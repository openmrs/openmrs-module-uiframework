package org.openmrs.ui.framework;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.annotation.InjectBeans;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.annotation.Validate;
import org.openmrs.ui.framework.fragment.FragmentConfiguration;
import org.openmrs.ui.framework.fragment.FragmentControllerProvider;
import org.openmrs.ui.framework.fragment.FragmentViewProvider;
import org.openmrs.ui.framework.page.PageAction;
import org.openmrs.ui.framework.page.PageControllerProvider;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.ui.framework.page.PageViewProvider;
import org.openmrs.ui.framework.resource.ResourceProvider;
import org.openmrs.util.HandlerUtil;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class UiFrameworkUtil {
	
	private static Log log = LogFactory.getLog(UiFrameworkUtil.class);
	
	public static Object executeControllerMethod(Object controller, String httpRequestMethod, Map<Class<?>, Object> possibleArguments,
                                                 ConversionService conversionService, ApplicationContext applicationContext) throws PageAction {
		Method controllerMethod = null;
        controllerMethod = findControllerMethodForHttpRequestMethod(controller, httpRequestMethod);
        if (controllerMethod == null) {
            controllerMethod = findGenericControllerMethod(controller);
        }
		if (controllerMethod == null)
			throw new UiFrameworkException("Cannot find controller method for request method " + httpRequestMethod + " in " + controller.getClass());
		
		return invokeMethodWithArguments(controller, controllerMethod, possibleArguments, conversionService, applicationContext);
	}

    private static Method findGenericControllerMethod(Object controller) {
        for (Method candidate : controller.getClass().getMethods()) {
            if (candidate.getName().equals("controller")) {
                return candidate;
            }
        }
        return null;
    }

    private static Method findControllerMethodForHttpRequestMethod(Object controller, String httpRequestMethod) {
        if (httpRequestMethod != null) {
            String lookForMethodName = httpRequestMethod.toLowerCase();
            for (Method candidate : controller.getClass().getMethods()) {
                if (candidate.getName().equals(lookForMethodName)) {
                    return candidate;
                }
            }
        }
        return null;
    }

    public static Object invokeMethodWithArguments(Object target, Method method, Map<Class<?>, Object> possibleArguments,
                                                   ConversionService conversionService, ApplicationContext applicationContext) throws PageAction {
		try {
			Class<?>[] types = method.getParameterTypes();
			Object[] params = new Object[types.length];
			for (int i = 0; i < types.length; ++i) {
				Object result = determineArgumentValue(target, possibleArguments, new MethodParameter(method, i), conversionService, applicationContext);
                if (result instanceof BindingResult) {
                    params[i] = ((BindingResult) result).getTarget();
                    if ((i + 1 < types.length) && Errors.class.isAssignableFrom(types[i + 1])) {
                        params[i + 1] = result;
                        i += 1; // skip the next one
                    }
                } else {
                    params[i] = result;
                }
			}
			return method.invoke(target, params);
		}
		catch (Exception ex) {
			if (ex instanceof InvocationTargetException
			        && ((InvocationTargetException) ex).getTargetException() instanceof Exception) {
				ex = (Exception) ((InvocationTargetException) ex).getTargetException();
			}
			if (ex instanceof RuntimeException) {
				throw (RuntimeException) ex;
			}
			if (ex.getCause() != null && ex.getCause() instanceof RuntimeException)
				throw (RuntimeException) ex.getCause();
			if (ex instanceof PageAction)
				throw (PageAction) ex;
			throw new UiFrameworkException("Exception in controller method", ex);
		}
	}
	
	/**
	 * Determines what parameters to use when calling the given controller or action method
	 * 
	 * @param controller if any @MethodParam annotations are present, the method will be called on this
	 * @param method the controller or action method whose parameters we need to determine
	 * @param argumentsByType parameters to look up by type
	 * @param conversionService
	 * @param applicationContext
     * @return an array of the right number of objects to do controllerMethod.invoke
	 */
	public static Object[] determineControllerMethodParameters(Object controller, Method method, Map<Class<?>, Object> argumentsByType,
                                                               ConversionService conversionService, ApplicationContext applicationContext) throws RequestValidationException {
		Class<?>[] types = method.getParameterTypes();
		int numParams = types.length;
		Object[] ret = new Object[numParams];
		for (int i = 0; i < numParams; ++i) {
			Object result = determineArgumentValue(controller, argumentsByType, new MethodParameter(method, i), conversionService, applicationContext);
			if (result instanceof BindingResult) {
				ret[i] = ((BindingResult) result).getTarget();
				if ((i + 1 < numParams) && Errors.class.isAssignableFrom(types[i + 1])) {
					ret[i + 1] = result;
					i += 1; // skip the next one
				}
			} else {
				ret[i] = result;
			}
		}
		return ret;
	}
	
	/**
	 * Determines the appropriate value to a method argument, for a given type and annotations
	 * 
     * @param controller if any @MethodParam annotations are present, the method will be called on this object
     * @param valuesByType
     * @param methodParam
     * @param conversionService
     * @param applicationContext
     * @return
	 */
	public static Object determineArgumentValue(Object controller, Map<Class<?>, Object> valuesByType, MethodParameter methodParam,
                                                ConversionService conversionService, ApplicationContext applicationContext) {
		
		// first, try to handle by type
		Object byType = valuesByType.get(methodParam.getParameterType());
		if (byType != null)
			return byType;
		
		// next try to handle by annotations, in their appropriate order
		// * top priority are @FragmentParam, @RequestParam, @CookieValue, and @SpringBean (which should be mutually exclusive)
		// * next comes @MethodParam, assuming neither of the above annotations set a value
        // * next comes @InjectBeans, which can be used alone or in conjunction with any of the above
		// * next comes @BindParam, which can be used alone or in conjunction with any of the above
		// * next comes @Validate, which can be used with any of the above (but not alone)
		
		Object ret = null;

		// if @FragmentParam is specified, get the parameter value from the FragmentConfiguration
		if (ret == null && methodParam.getParameterAnnotation(FragmentParam.class) != null) {

			FragmentConfiguration fragConfig = (FragmentConfiguration) valuesByType.get(FragmentConfiguration.class);
			if (fragConfig == null)
				throw new IllegalArgumentException("Tried to use a @FragmentParam annotation in a context that has no FragmentConfiguration");

			FragmentParam fp = methodParam.getParameterAnnotation(FragmentParam.class);
			String param = fp.value();
			Object value = fragConfig.getAttribute(param);
			if (value == null && !ValueConstants.DEFAULT_NONE.equals(fp.defaultValue()))
				value = fp.defaultValue();
			if (value == null && fp.required())
				throw new UiFrameworkException(param + " is required");
			try {
				ret = conversionService.convert(value, TypeDescriptor.forObject(value), new TypeDescriptor(methodParam));
			}
			catch (ConversionException ex) {
				throw new UiFrameworkException(param + " couldn't be converted to " + methodParam.getParameterType(), ex);
			}
		}
		
		// if @RequestParam is specified, get the parameter value from the HttpServletRequest
		if (ret == null && methodParam.getParameterAnnotation(RequestParam.class) != null) {
			HttpServletRequest request = (HttpServletRequest) valuesByType.get(HttpServletRequest.class);
			if (request == null)
				throw new UiFrameworkException(
				        "Cannot use @RequestParam when we don't have an underlying HttpServletRequest");
			
			RequestParam rp = methodParam.getParameterAnnotation(RequestParam.class);
			String param = rp.value();
			
			if (request instanceof MultipartHttpServletRequest) {
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
				MultipartFile file = multipartRequest.getFile(param);
				if (file != null && !file.isEmpty()) {
					ret = file;
				}
			}
			
			if (ret == null) {
				String[] values = request.getParameterValues(param);
				if (empty(values)) {
					values = new String[0];
				}
				// check whether the developer has specified a default value
				if (!ValueConstants.DEFAULT_NONE.equals(rp.defaultValue()) && values.length == 0) {
					ret = rp.defaultValue();
				} else if (rp.required() && values.length == 0) {
					throw new MissingRequiredParameterException(param);
				} else {
					ret = values;
				}
			}
			
			try {
				ret = conversionService.convert(ret, TypeDescriptor.forObject(ret), new TypeDescriptor(methodParam));
			}
			catch (ConversionException ex) {
				APIAuthenticationException authEx = findCause(ex, APIAuthenticationException.class);
				if (authEx != null)
					throw authEx;
				else
					throw new UiFrameworkException(param + " couldn't be converted to " + methodParam.getParameterType(), ex);
			}
		}

        // if @CookieValue is specified, look in a cookie in the request
        if (ret == null && methodParam.getParameterAnnotation(CookieValue.class) != null) {
            CookieValue cv = methodParam.getParameterAnnotation(CookieValue.class);
            String cookieName = cv.value();
            PageRequest pageRequest = (PageRequest) valuesByType.get(PageRequest.class);
            if (pageRequest == null) {
                throw new UiFrameworkException("Cannot use @CookieValue when we don't have an underlying PageRequest");
            }
            String cookieValue = pageRequest.getCookieValue(cookieName);
            if (cookieValue == null && !ValueConstants.DEFAULT_NONE.equals(cv.defaultValue())) {
                cookieValue = cv.defaultValue();
            }
            if (cookieValue == null && cv.required()) {
                throw new MissingRequiredCookieException(cookieName);
            }
            try {
                ret = conversionService.convert(cookieValue, TypeDescriptor.forObject(cookieValue), new TypeDescriptor(methodParam));
            }
            catch (ConversionException ex) {
                APIAuthenticationException authEx = findCause(ex, APIAuthenticationException.class);
                if (authEx != null)
                    throw authEx;
                else
                    throw new UiFrameworkException("Cookie " + cookieName + " (value " + cookieValue + ") couldn't be converted to " + methodParam.getParameterType(), ex);
			}
		}
		
		// if @SpringBean is specified, get the parameter value from Spring's ApplicationContext
		if (ret == null && methodParam.getParameterAnnotation(SpringBean.class) != null) {
			ApplicationContext spring = (ApplicationContext) valuesByType.get(ApplicationContext.class);
			if (spring == null)
				throw new UiFrameworkException(
					"Cannot use @SpringBean when we don't have an underlying ApplicationContext");
			
			SpringBean sb = methodParam.getParameterAnnotation(SpringBean.class);
			if ("".equals(sb.value())) {
				// autowire by type
				try {
					ret = spring.getBean(methodParam.getParameterType());
				}
				catch (NoSuchBeanDefinitionException ex) {
					throw new UiFrameworkException("Tried to autowire a " + methodParam.getParameterType()
						+ " by type, but did not find exactly one matching Spring bean", ex);
				}
			} else {
				// autowire by id
				try {
					ret = spring.getBean(sb.value());
				}
				catch (NoSuchBeanDefinitionException ex) {
					throw new UiFrameworkException("Could not find Spring bean with id " + sb.value());
				}
			}
		}

		// if no value has been set yet and @MethodParam is specified, call that to get the parameter value
		if (ret == null && methodParam.getParameterAnnotation(MethodParam.class) != null) {
			if (controller == null)
				throw new UiFrameworkException("Cannot use @MethodParam if we do not have a controller to call the method on");
			MethodParam mp = methodParam.getParameterAnnotation(MethodParam.class);
			
			Method method = null;
			for (Method candidate : controller.getClass().getMethods()) {
				if (candidate.getName().equals(mp.value()) && methodParam.getParameterType().isAssignableFrom(candidate.getReturnType())) {
					method = candidate;
					break;
				}
			}
			if (method == null) {
				throw new UiFrameworkException("Cannot find a method with name " + mp.value() + " and return type " + methodParam.getParameterType() + " as specified by @MethodParam");
			}
			
			try {
				ret = invokeMethodWithArguments(controller, method, valuesByType, conversionService, applicationContext);
			} catch (Exception ex) {
				throw new UiFrameworkException("Error evaluating " + mp.value() + " method specified by @MethodParam", ex);
			}
		}

        // If @InjectBeans is present, use Spring to wire @Autowired properties
        if (methodParam.getParameterAnnotation(InjectBeans.class) != null) {
            if (ret == null) {
                try {
                    ret = methodParam.getParameterType().newInstance();
                }
                catch (Exception ex) {
                    throw new UiFrameworkException("Failed to instantiate a new " + methodParam.getParameterType().getSimpleName()
                            + " for @InjectBeans annotated parameter", ex);
                }
            }

            applicationContext.getAutowireCapableBeanFactory().autowireBean(ret);
        }
		
		// If @BindParams is present, bind all relevent request parameters
		String bindingPrefix = null;
		if (methodParam.getParameterAnnotation(BindParams.class) != null) {
			if (ret == null) {
				try {
					ret = methodParam.getParameterType().newInstance();
				}
				catch (Exception ex) {
					throw new UiFrameworkException("Failed to instantiate a new " + methodParam.getParameterType().getSimpleName()
					        + " for @BindParams annotated parameter", ex);
				}
			}

			BindParams bp = methodParam.getParameterAnnotation(BindParams.class);
			
			BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(ret, bp.value());
			bindingResult.getPropertyAccessor().setConversionService(conversionService);
			if (StringUtils.isNotEmpty(bp.value())) {
				bindingPrefix = bp.value();
			}
			
			HttpServletRequest request = (HttpServletRequest) valuesByType.get(HttpServletRequest.class);
			if (request == null)
				throw new UiFrameworkException(
					"Cannot use @RequestParam when we don't have an underlying HttpServletRequest");
			
			if (request instanceof MultipartHttpServletRequest) {
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
				for (Iterator<String> i = multipartRequest.getFileNames(); i.hasNext();) {
					String paramName = i.next();
					if (bindingPrefix == null) {
						bindFileRequestParameter(bindingResult, multipartRequest, paramName, paramName);
					} else if (paramName.startsWith(bindingPrefix + ".")) {
						bindFileRequestParameter(bindingResult, multipartRequest, paramName, paramName
							.substring(bindingPrefix.length() + 1));
					}
				}
			}
			
			for (@SuppressWarnings("unchecked")
			Enumeration<String> i = request.getParameterNames(); i.hasMoreElements();) {
				String paramName = i.nextElement();
				if (bindingPrefix == null) {
					bindRequestParameter(bindingResult, request, paramName, paramName);
				} else if (paramName.startsWith(bindingPrefix + ".")) {
					bindRequestParameter(bindingResult, request, paramName, paramName
						.substring(bindingPrefix.length() + 1));
				}
			}
			
			ret = bindingResult;
		}
		
		// apply @Validate annotation if present
		if (ret != null && methodParam.getParameterAnnotation(Validate.class) != null) {
			Validate val = methodParam.getParameterAnnotation(Validate.class);
			
			BindingResult result;
			if (ret instanceof BindingResult) {
				result = (BindingResult) ret;
			} else {
				result = new BeanPropertyBindingResult(ret, "");
			}
			
			// use the preferred validator that is an instance of the @Validate annotation's value
			Validator validator = HandlerUtil.getPreferredHandler(val.value(), result.getTarget().getClass());
			try {
				validator.validate(result.getTarget(), result);
			}
			catch (Exception ex) {
				throw new UiFrameworkException("Validator threw exception ", ex);
			}
			
			if (result.hasErrors())
				throw new BindParamsValidationException(bindingPrefix, result);
		}
		
		return ret;
	}
	
	/**
     * @param values
     * @return true if the array is null, empty, or only contains empty strings
     */
    private static boolean empty(String[] values) {
	    if (values == null || values.length == 0)
	    	return true;
	    for (String s : values) {
	    	if (StringUtils.isNotEmpty(s)) {
	    		return false;
	    	}
	    }
	    return true;
    }

	private static boolean bindFileRequestParameter(BeanPropertyBindingResult bindingResult,
	        MultipartHttpServletRequest request, String requestParamName, String beanPropertyName) {
		PropertyAccessor accessor = bindingResult.getPropertyAccessor();
		if (!accessor.isWritableProperty(beanPropertyName))
			return false;
		
		accessor.setPropertyValue(beanPropertyName, request.getFile(requestParamName));
		return true;
	}
	
	private static boolean bindRequestParameter(BeanPropertyBindingResult bindingResult, HttpServletRequest request,
	        String requestParamName, String beanPropertyName) {
		PropertyAccessor accessor = bindingResult.getPropertyAccessor();
		if (!accessor.isWritableProperty(beanPropertyName))
			return false;
		
		accessor.setPropertyValue(beanPropertyName, request.getParameterValues(requestParamName));
		return true;
	}
	
	/**
	 * If a cause of the exception chain is of the given class, return it. (Otherwise, return null.)
	 * 
	 * @param <T>
	 * @param t
	 * @param lookFor
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public static <T> T findCause(Throwable t, Class<T> lookFor) {
		while (t.getCause() != null && t.getCause() != t) {
			t = t.getCause();
			if (lookFor.isAssignableFrom(t.getClass()))
				return (T) t;
		}
		return null;
	}

	/**
     * Convenience method that provides a workaround for TRUNK-3580
     * 
     * @param msg
     * @return localized message, or the default (last) code if there is no mapped code
     */
    public static String getMessage(MessageSourceResolvable msg) {
    	String ret = Context.getMessageSourceService().getMessage(msg, Context.getLocale());
    	String[] codes = msg.getCodes();
		if (codes.length > 1 && ret.equals(codes[0])) {
    		ret = codes[codes.length - 1];
    	}
    	return ret;
    }

	/**
	 * Sets the developmentFolder property on an appropriate factory object in order to facilitate
	 * developers automatically having changes to pages and controllers picked up by the application live during development
	 *
	 * This supports a couple of different mechanisms, based on properties set at runtime.  These
	 * properties can be specified either in the OpenMRS runtime properties file, or as system properties
	 *
	 * 1. For developers who tend to check out their code into a single folder, organized by module id, they can specify:
	 *
	 * uiFramework.developmentFolder=/path/to/where/developer/keeps/their/modules/checked/out
	 *
	 * Any modules whose code exists in this folder, under either "moduleId" or "openmrs-module-moduleId" will be included
	 * if they are uiframework-enabled and started
	 *
	 * 2. As an optional restriction to this, one can specify uiFramework.developmentModules=comma,separated,module,ids
	 *
	 * This will have the effect of limiting the modules included using option 1 to only those specified using option 2
	 *
	 * 3. As an alternative (or addition) to this, in the event that the developer wants more specific control over the module/folder combinations,
	 * they can specify one or more properties in the format of:
	 *
	 * uiFramework.development.${moduleId}=/path/to/where/the/code/to/this/module/resides
	 *
	 * @return true if development mode has been set for the given provider
	 */
	public static boolean checkAndSetDevelopmentModeForProvider(String key, Object provider) {

		boolean addedInDevMode = false;

		// Option 1:  User can specify uiFramework.developmentFolder and (optionally) uiFramework.developmentModules (comma-separated)
		// Setting development mode this way is implicit and passive, so no need to warn if no match is found

		String devModeDir = getRuntimeOrSystemProperty("uiFramework.developmentFolder");
		if (devModeDir != null) {
			String devModeModules = getRuntimeOrSystemProperty("uiFramework.developmentModules");
			boolean moduleAllowed = false;
			if (devModeModules != null) {
				for (String devModeModule : devModeModules.split(",")) {
					if (devModeModule.trim().equalsIgnoreCase(key)) {
						moduleAllowed = true;
					}
				}
			}
			else {
				moduleAllowed = true;
			}
			if (moduleAllowed) {
				addedInDevMode = addPossibleDevFolder(devModeDir + File.separator + "openmrs-module-" + key, key, provider);
				if (!addedInDevMode) {
					addedInDevMode = addPossibleDevFolder(devModeDir + File.separator + key, key, provider);
				}
			}
		}

		// Option 2:  User can specify uiFramework.development.moduleId=/path/to/module/code/on/filesystem to enable dev mode for a given module and path explicitly
		// Setting development mode this way is explicit and active, so warn if the development mode that was set is not found

		if (!addedInDevMode) {
			String devRootFolder = getRuntimeOrSystemProperty("uiFramework.development." + key);
			if (devRootFolder != null) {
				addedInDevMode = addPossibleDevFolder(devRootFolder, key, provider);
				if (!addedInDevMode) {
					log.warn("Failed to set development mode for " + provider.getClass().getSimpleName() + " in " + key + " because it does not exist or is not a directory");
				}
			}
		}

		return addedInDevMode;
	}

	/**
	 * Checks whether or not the passed baseFolder exists and has the property subdirectory structure to server as a dev folder
	 * If it does, it indicates that this should server as a resource provider in development mode, and returns true
	 * If it does not, or if problems occur while trying to perform the operation, it returns false
	 */
	private static boolean addPossibleDevFolder(String baseFolder, String key, Object provider) {

		// Allow a developer to specify the modules directory e.g omod_1.x, mod_2.x.
		// The default directory is still "omod"
		String moduleDir = "omod";
		if (StringUtils.isNotEmpty(baseFolder)) {
			String fSeparator = File.separator;
			if (baseFolder.endsWith(fSeparator))
				baseFolder = baseFolder.substring(0, baseFolder.length() - 1);

			String[] pathSubs = baseFolder.split(fSeparator);
			if (pathSubs != null && pathSubs.length > 0) {
				int len = pathSubs.length;
				String mod = pathSubs[len - 1];
				if (!mod.contains(key)) {
					moduleDir = "";
				}
			}
		}

		// Get the appropriate folderPath to check, given the type of provider passed in
		String folderPath = baseFolder;
		if (provider instanceof ResourceProvider) {
			folderPath += "src" + File.separator + "main" + File.separator + "webapp" + File.separator + "resources";
		}
		else if (provider instanceof PageViewProvider) {
			folderPath += "src" + File.separator + "main" + File.separator + "webapp" + File.separator + "pages";
		}
		else if (provider instanceof FragmentViewProvider) {
			folderPath += "src" + File.separator + "main" + File.separator + "webapp" + File.separator + "fragments";
		}
		else if (provider instanceof PageControllerProvider || provider instanceof FragmentControllerProvider) {
			folderPath += "target" + File.separator + "classes";
		}
		else {
			throw new IllegalArgumentException("Provider is not of an expected type.  Found: " + provider.getClass());
		}

		File devFolder = new File(folderPath);
		if (devFolder.exists() && devFolder.isDirectory()) {
			try {
				PropertyUtils.setProperty(provider, "developmentFolder", devFolder);
				log.warn("Folder " + devFolder.getAbsolutePath() + " successfully set as developmentFolder mode folder for provider " + key);
				return true;
			}
			catch (Exception ex) {
				// pass
			}
		}
		return false;
	}

	/**
	 * @return the property value with the given key from the OpenMRS runtime properties, of if not found, from a system property
	 */
	private static String getRuntimeOrSystemProperty(String key) {
		String ret = Context.getRuntimeProperties().getProperty(key);
		if (ret == null) {
			ret = System.getProperty(key);
		}
		return ret;
	}

    public static DateFormat getDateFormat(AdministrationService administrationService, Locale locale) {
        if (administrationService != null) {
            return new SimpleDateFormat(
                    administrationService.getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATE_FORMAT), locale);
        } else {
            return new SimpleDateFormat("dd.MMM.yyyy", locale);
        }
    }

    public static DateFormat getDateTimeFormat(AdministrationService administrationService, Locale locale) {
        if (administrationService != null) {
            return new SimpleDateFormat(
                    administrationService.getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATETIME_FORMAT), locale);
        } else {
            return new SimpleDateFormat("dd.MMM.yyyy, HH:mm:ss", locale);
        }
    }
}
