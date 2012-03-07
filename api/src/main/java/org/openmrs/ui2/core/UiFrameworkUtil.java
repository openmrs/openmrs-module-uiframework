package org.openmrs.ui2.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.ui2.core.annotation.BindParams;
import org.openmrs.ui2.core.annotation.FragmentParam;
import org.openmrs.ui2.core.annotation.SpringBean;
import org.openmrs.ui2.core.annotation.Validate;
import org.openmrs.ui2.core.fragment.FragmentConfiguration;
import org.openmrs.ui2.core.page.PageAction;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public class UiFrameworkUtil {
	
	private static Log log = LogFactory.getLog(UiFrameworkUtil.class);
	
	public static Object executeControllerMethod(Object controller, Map<Class<?>, Object> possibleArguments,
	        ConversionService conversionService) throws PageAction {
		Method controllerMethod = null;
		for (Method candidate : controller.getClass().getMethods()) {
			if (candidate.getName().equals("controller")) {
				controllerMethod = candidate;
				break;
			}
		}
		if (controllerMethod == null)
			throw new UiFrameworkException("Cannot find controller method in " + controller.getClass()
			        + " that returns void or String");
		
		return invokeMethodWithArguments(controller, controllerMethod, possibleArguments, conversionService);
	}
	
	public static Object invokeMethodWithArguments(Object target, Method method, Map<Class<?>, Object> possibleArguments,
	        ConversionService conversionService) throws PageAction {
		try {
			Class<?>[] types = method.getParameterTypes();
			Annotation[][] annotations = method.getParameterAnnotations();
			Object[] params = new Object[types.length];
			for (int i = 0; i < types.length; ++i) {
				params[i] = determineArgumentValue(possibleArguments, types[i], annotations[i], conversionService);
			}
			return method.invoke(target, params);
		}
		catch (Exception ex) {
			if (ex instanceof InvocationTargetException
			        && ((InvocationTargetException) ex).getTargetException() instanceof Exception) {
				ex = (Exception) ((InvocationTargetException) ex).getTargetException();
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
	 * @param method the controller or action method whose parameters we need to determine
	 * @param argumentsByType parameters to look up by type
	 * @param conversionService
	 * @return an array of the right number of objects to do controllerMethod.invoke
	 */
	public static Object[] determineControllerMethodParameters(Method method, Map<Class<?>, Object> argumentsByType,
	        ConversionService conversionService) throws RequestValidationException {
		Class<?>[] types = method.getParameterTypes();
		Annotation[][] annotations = method.getParameterAnnotations();
		int numParams = types.length;
		Object[] ret = new Object[numParams];
		for (int i = 0; i < numParams; ++i) {
			Object result = determineArgumentValue(argumentsByType, types[i], annotations[i], conversionService);
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
	 * @param possibleArguments
	 * @param class1
	 * @param annotations
	 * @return
	 */
	public static Object determineArgumentValue(Map<Class<?>, Object> valuesByType, Class<?> argClass,
	        Annotation[] annotations, ConversionService conversionService) {
		
		// first, try to handle by type
		Object byType = valuesByType.get(argClass);
		if (byType != null)
			return byType;
		
		// next try to handle by annotation
		Object ret = null;
		for (Annotation ann : annotations) {
			if (ann instanceof FragmentParam) {
				FragmentParam fp = (FragmentParam) ann;
				String param = fp.value();
				FragmentConfiguration fragConfig = (FragmentConfiguration) valuesByType.get(FragmentConfiguration.class);
				if (fragConfig == null)
					throw new IllegalArgumentException(
					        "Tried to use a @FragmentParam annotation in a context that has no FragmentConfiguration");
				Object value = fragConfig.getAttribute(param);
				if (value == null && !ValueConstants.DEFAULT_NONE.equals(fp.defaultValue()))
					value = fp.defaultValue();
				if (value == null && fp.required())
					throw new UiFrameworkException(param + " is required");
				try {
					ret = conversionService.convert(value, argClass);
				}
				catch (ConversionException ex) {
					throw new UiFrameworkException(param + " couldn't be converted to " + argClass, ex);
				}
				
			} else if (ann instanceof RequestParam) {
				HttpServletRequest request = (HttpServletRequest) valuesByType.get(HttpServletRequest.class);
				if (request == null)
					throw new UiFrameworkException(
					        "Cannot use @RequestParam when we don't have an underlying HttpServletRequest");
				RequestParam rp = (RequestParam) ann;
				String param = rp.value();
				
				if (request instanceof MultipartHttpServletRequest) {
					MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
					MultipartFile file = multipartRequest.getFile(param);
					if (!file.isEmpty()) {
						ret = file;
					}
				}
				
				if (ret == null) {
					String value = request.getParameter(param);
					if (StringUtils.isEmpty(value) && !ValueConstants.DEFAULT_NONE.equals(rp.defaultValue()))
						value = rp.defaultValue();
					if (StringUtils.isEmpty(value) && rp.required())
						throw new MissingRequiredParameterException(param);
					
					ret = value;
				}
				
				try {
					ret = conversionService.convert(ret, argClass);
				}
				catch (ConversionException ex) {
					APIAuthenticationException authEx = findCause(ex, APIAuthenticationException.class);
					if (authEx != null)
						throw authEx;
					else
						throw new UiFrameworkException(param + " couldn't be converted to " + argClass, ex);
				}
				
			} else if (ann instanceof SpringBean) {
				SpringBean sb = (SpringBean) ann;
				ApplicationContext spring = (ApplicationContext) valuesByType.get(ApplicationContext.class);
				if (spring == null)
					throw new UiFrameworkException(
					        "Cannot use @SpringBean when we don't have an underlying ApplicationContext");
				if ("".equals(sb.value())) {
					// autowire by type
					try {
						ret = spring.getBean(argClass);
					}
					catch (NoSuchBeanDefinitionException ex) {
						throw new UiFrameworkException("Tried to autowire a " + argClass
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
		}
		
		// see if there are any @BindParams annotations
		String bindingPrefix = null;
		for (Annotation ann : annotations) {
			if (ann instanceof BindParams) {
				if (ret == null) {
					try {
						ret = argClass.newInstance();
					}
					catch (Exception ex) {
						throw new UiFrameworkException("Failed to instantiate a new " + argClass.getSimpleName()
						        + " for @BindParams annotated parameter", ex);
					}
				}
				BindParams bp = (BindParams) ann;
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
		}
		
		if (ret != null) {
			// see if there are any @Validate annotations
			for (Annotation ann : annotations) {
				if (ann instanceof Validate) {
					Validate val = (Validate) ann;
					BindingResult result;
					if (ret instanceof BindingResult) {
						result = (BindingResult) ret;
					} else {
						result = new BeanPropertyBindingResult(ret, "");
					}
					if (val.value() != null) {
						try {
							((Validator) val.value().newInstance()).validate(result.getTarget(), result);
						}
						catch (Exception ex) {
							throw new UiFrameworkException("Error validating ", ex);
						}
					} else {
						log.debug("No validation performed");
					}
					if (result.hasErrors())
						throw new BindParamsValidationException(bindingPrefix, result);
				}
			}
		}
		
		return ret;
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
		
		accessor.setPropertyValue(beanPropertyName, request.getParameter(requestParamName));
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
	public static <T> T findCause(Throwable t, Class<T> lookFor) {
		while (t.getCause() != null && t.getCause() != t) {
			t = t.getCause();
			if (lookFor.isAssignableFrom(t.getClass()))
				return (T) t;
		}
		return null;
	}
	
}
