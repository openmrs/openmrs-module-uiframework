package org.openmrs.ui.framework.fragment;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.ui.framework.RequestValidationException;
import org.openmrs.ui.framework.UiFrameworkException;
import org.openmrs.ui.framework.UiFrameworkUtil;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.extension.ExtensionManager;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.interceptor.FragmentActionInterceptor;
import org.openmrs.ui.framework.page.PageAction;
import org.openmrs.ui.framework.page.PageContext;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.ui.framework.page.Redirect;
import org.openmrs.ui.framework.session.Session;
import org.openmrs.ui.framework.session.SessionFactory;
import org.openmrs.ui.util.ExceptionUtil;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles FragmentRequests
 */
public class FragmentFactory {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	ApplicationContext applicationContext;
	
	@Autowired
	ConversionService conversionService;
	
	@Autowired
	MessageSource messageSource;
	
	@Autowired
	ExtensionManager extensionManager;
	
	@Autowired
	SessionFactory sessionFactory;
	
	@Autowired(required = false)
	ServletContext servletContext;

	@Autowired(required = false)
	List<FragmentActionInterceptor> fragmentActionInterceptors;

    @Autowired(required = false)
    List<FragmentModelConfigurator> modelConfigurators;

    @Autowired(required = false)
    List<PossibleFragmentControllerArgumentProvider> possibleFragmentControllerArgumentProviders;

    @Autowired(required = false)
    List<PossibleFragmentActionArgumentProvider> possibleFragmentActionArgumentProviders;

	private boolean developmentMode = false;
	
	private static Map<String, FragmentControllerProvider> controllerProviders;
	
	private static Map<String, FragmentViewProvider> viewProviders;
	
	// a cache of views for production mode
	private Map<String, FragmentView> viewCache = new HashMap<String, FragmentView>();
	
	// a singleton one of these that can be reused
	private EmptyFragmentController emptyController = new EmptyFragmentController();
	
	public FragmentFactory() {
	}
	
	public String process(FragmentContext context) throws PageAction {
		if (context.getRequestDepth() > 100)
			throw new UiFrameworkException("Fragment inclusion > 100 levels deep. Check your code for infinite loops.");
		long startTime = System.currentTimeMillis();
		if (log.isDebugEnabled()) {
			log.debug("processing " + context.getRequest());
		}
		applyDefaultConfiguration(context);
        configureModel(context);
		// it's possible someone has pre-requested that this fragment be decorated
		if (context.getRequest().getConfiguration().containsKey("decorator")) {
			String decoratorProvider = (String) context.getRequest().getConfiguration().get("decoratorProvider");
			if (decoratorProvider == null) {
				decoratorProvider = "*";
			}
			String decoratorName = "decorator/" + context.getRequest().getConfiguration().get("decorator");
			@SuppressWarnings("unchecked")
			Map<String, Object> decoratorConfigurationMap = (Map<String, Object>) context.getRequest().getConfiguration()
			        .get("decoratorConfig");
			FragmentConfiguration decoratorConfiguration = new FragmentConfiguration(decoratorConfigurationMap);
			FragmentRequest decorator = new FragmentRequest(decoratorProvider, decoratorName, decoratorConfiguration);
			context.setDecorateWith(decorator);
		}
		String result = processThisFragment(context);
		if (context.getDecorateWith() == null)
			return result;
		FragmentRequest decoratorRequest = context.getDecorateWith();
		decoratorRequest.getConfiguration().put("content", result);
		decoratorRequest.getConfiguration().put("contentFragmentId", context.getRequest().getConfiguration().get("id"));
		FragmentContext decoratorContext = new FragmentContext(decoratorRequest, context);
        String ret = process(decoratorContext);
		if (log.isDebugEnabled()) {
			log.debug("\thandled " + context.getRequest() + " in " + (System.currentTimeMillis() - startTime) + " ms");
		}
		return ret;
	}

    private void configureModel(FragmentContext fragmentContext) {
        if (modelConfigurators != null) {
            for (FragmentModelConfigurator configurator : modelConfigurators) {
                configurator.configureModel(fragmentContext);
            }
        }
    }

    private void applyDefaultConfiguration(FragmentContext context) {
		FragmentConfiguration config = context.getRequest().getConfiguration();
		if (!config.containsKey("id"))
			config.put("id", UiUtils.randomId("fr"));
	}
	
	private String processThisFragment(FragmentContext context) throws PageAction {
		// determine what controller to use
		Object controller = getController(context.getRequest());
		if (controller == null)
			controller = emptyController;
		context.setController(controller);
		
		// let the controller handle the request
		Object result = handleRequestWithController(context);
		
		// if the return type is a FragmentRequest, that means we "redirect" to that fragment request instead
		if (result instanceof FragmentRequest) {
			FragmentRequest replacement = (FragmentRequest) result;
			avoidDuplicateDecoration(replacement, context.getRequest());
			FragmentContext replacementContext = new FragmentContext(replacement, context);
			return process(replacementContext);
		}
		
		// otherwise, the result must be a String
		String resultString = (String) result;
		
		// fragments are allowed to redirect to other pages
		if (resultString != null && resultString.startsWith("redirect:")) {
			String toApplicationUrl = resultString.substring("redirect:".length());
			throw new Redirect(toApplicationUrl);
		}
		
		// determine what view to use
		String viewName = resultString;
		FragmentView view = getView(context.getRequest(), viewName);
		context.setView(view);
		
		if (context.getController().equals(emptyController) && context.getView() == null) {
			throw new RuntimeException("Cannot find controller or view for fragment: " + context.getRequest().getFragmentId());
		}
		
		// Fragments are allowed to have no view (their controller can still affect the shared
		// page model, do redirects, etc) but if we have neither a controller nor a view, that's bad
		if (view == null) {
			return "";
		}
		
		// render the output
		String output = view.render(context);
		return output;
	}
	
	/**
	 * If one {@link FragmentRequest} "redirects" to another (by having its controller method return
	 * a new FragmentRequest) and the FragmentRequest that it returns includes exactly the same
	 * configuration as the original request, we need to remove the "decorator" attribute from the
	 * replacement configuration because the fragmework is already applying that decoration.
	 * 
	 * @param replacement
	 * @param original
	 */
	private void avoidDuplicateDecoration(FragmentRequest replacement, FragmentRequest original) {
		if (replacement.getConfiguration() != null && replacement.getConfiguration() == original.getConfiguration()) {
			FragmentConfiguration newConfig = new FragmentConfiguration(replacement.getConfiguration());
			newConfig.remove("decorator");
			replacement.setConfiguration(newConfig);
		}
	}
	
	// if you change the supported parameter classes, make sure to update the documentation on the wiki
	private Object handleRequestWithController(FragmentContext context) throws PageAction {
		Map<Class<?>, Object> possibleArguments = new LinkedHashMap<Class<?>, Object>();
		possibleArguments.put(FragmentContext.class, context);
		possibleArguments.put(PageContext.class, context.getPageContext());
		possibleArguments.put(FragmentConfiguration.class, context.getRequest().getConfiguration());
		possibleArguments.put(FragmentModel.class, context.getModel());
		possibleArguments.put(PageModel.class, context.getPageContext().getModel());
		possibleArguments.put(FragmentRequest.class, context.getRequest());
		possibleArguments.put(PageRequest.class, context.getPageContext().getRequest());
		possibleArguments.put(HttpServletRequest.class, context.getPageContext().getRequest().getRequest());
        possibleArguments.put(HttpSession.class, context.getPageContext().getRequest().getRequest().getSession());
		possibleArguments.put(FragmentFactory.class, this);
		possibleArguments.put(UiUtils.class, new FragmentUiUtils(context));
		possibleArguments.put(Session.class, context.getPageContext().getRequest().getSession());
		possibleArguments.put(ApplicationContext.class, applicationContext);
		possibleArguments.put(ServletContext.class, servletContext);
        if (possibleFragmentControllerArgumentProviders != null) {
            for (PossibleFragmentControllerArgumentProvider provider : possibleFragmentControllerArgumentProviders) {
                provider.addPossibleFragmentControllerArguments(possibleArguments);
            }
        }

        String httpRequestMethod = context.getPageContext().getRequest().getRequest().getMethod();
        return UiFrameworkUtil.executeControllerMethod(context.getController(), httpRequestMethod, possibleArguments, conversionService, applicationContext);
	}

    /**
	 * @param request
	 * @param viewName if not null, overrides what is specified in request
	 * @return
	 * @should get a view from the requested provider
	 * @should get a view from any provider if none is specified
	 * @should fail if an invalid provider name is specified
	 */
	FragmentView getView(FragmentRequest request, String viewName) {
		if (viewName == null)
			viewName = request.getFragmentId();
		String providerAndFragmentId = request.getProviderName() + ":" + viewName;
		if (!isDevelopmentMode()) {
			if (viewCache.containsKey(providerAndFragmentId)) {
				return viewCache.get(providerAndFragmentId);
			}
		}
		if ("*".equals(request.getProviderName())) {
			for (FragmentViewProvider p : viewProviders.values()) {
				FragmentView ret = p.getView(viewName);
				if (ret != null) {
					if (!isDevelopmentMode())
						viewCache.put(providerAndFragmentId, ret);
					return ret;
				}
			}
		} else {
			FragmentViewProvider provider = viewProviders.get(request.getProviderName());
			if (provider == null) {
				throw new UiFrameworkException("No view provider: " + request.getProviderName());
			}
			return provider.getView(viewName);
		}
		return null;
	}
	
	public Object getController(String providerName, String fragmentName) {
		FragmentRequest request = new FragmentRequest(providerName, fragmentName);
		return getController(request);
	}
	
	/**
	 * TODO cache the results in production mode?
	 * 
	 * @param request
	 * @return
	 * @should get a controller from the specified provider
	 * @should get a controller from any provider if none specified
	 * @should fail if an invalid provider is specified
	 */
	Object getController(FragmentRequest request) {
		if (controllerProviders != null) {
			if ("*".equals(request.getProviderName())) {
				for (FragmentControllerProvider p : controllerProviders.values()) {
					Object ret = p.getController(request.getFragmentId());
					if (ret != null)
						return ret;
				}
			} else {
				FragmentControllerProvider provider = controllerProviders.get(request.getProviderName());
				if (provider == null) {
					throw new UiFrameworkException("No controller provider: " + request.getProviderName());
				}
				return provider.getController(request.getFragmentId());
			}
		}
		return null;
	}
	
	/**
	 * @return the developmentMode
	 */
	public boolean isDevelopmentMode() {
		return developmentMode;
	}
	
	/**
	 * @param developmentMode the developmentMode to set
	 */
	public void setDevelopmentMode(boolean developmentMode) {
		this.developmentMode = developmentMode;
	}
	
	/**
	 * @return the controllerProviders
	 */
	public Map<String, FragmentControllerProvider> getControllerProviders() {
		return controllerProviders;
	}
	
	/**
	 * @param newControllerProviders the controllerProviders to set
	 */
	public void setControllerProviders(Map<String, FragmentControllerProvider> newControllerProviders) {
		controllerProviders = newControllerProviders;
	}
	
	/**
	 * Adds the given controller providers to the existing ones. (I.e. this is not a proper setter.)
	 * 
	 * @param additional
	 * @see #addControllerProvider(String, FragmentControllerProvider)
	 */
	public void setAdditionalControllerProviders(Map<String, FragmentControllerProvider> additional) {
		for (Map.Entry<String, FragmentControllerProvider> e : additional.entrySet()) {
			addControllerProvider(e.getKey(), e.getValue());
		}
	}
	
	/**
	 * Registers a Controller Provider. If a system property exists called
	 * "uiFramework.development.${ key }", and the controller provider has a "developmentFolder"
	 * property, the value of "${systemProperty}/omod/target/classes" will be set for that property
	 * 
	 * @param key
	 * @param provider
	 */
	public void addControllerProvider(String key, FragmentControllerProvider provider) {
		if (controllerProviders == null)
			controllerProviders = new LinkedHashMap<String, FragmentControllerProvider>();
		
		String devRootFolder = System.getProperty("uiFramework.development." + key);
		if (devRootFolder != null) {
			File devFolder = new File(devRootFolder + File.separator + "omod" + File.separator + "target" + File.separator
			        + "classes");
			if (devFolder.exists() && devFolder.isDirectory()) {
				try {
					PropertyUtils.setProperty(provider, "developmentFolder", devFolder);
				}
				catch (Exception ex) {
					// pass
				}
			} else {
				log.warn("Failed to set development mode for FragmentControllerProvider " + key + " because "
				        + devFolder.getAbsolutePath() + " does not exist or is not a directory");
			}
		}
		
		controllerProviders.put(key, provider);
	}
	
	/**
	 * @return the viewProviders
	 */
	public Map<String, FragmentViewProvider> getViewProviders() {
		return viewProviders;
	}
	
	/**
	 * @param newViewProviders the viewProviders to set
	 */
	public void setViewProviders(Map<String, FragmentViewProvider> newViewProviders) {
		viewProviders = newViewProviders;
	}
	
	/**
	 * Adds the given view providers to the existing ones. (I.e. this is not a proper setter.)
	 * 
	 * @param additional
	 * @see #addViewProvider(String, FragmentViewProvider)
	 */
	public void setAdditionalViewProviders(Map<String, FragmentViewProvider> additional) {
		for (Map.Entry<String, FragmentViewProvider> e : additional.entrySet()) {
			addViewProvider(e.getKey(), e.getValue());
		}
	}
	
	/**
	 * If a system property exists called "uiFramework.development.${ key }", and the view provider
	 * has a "developmentFolder" property, the value of
	 * "${systemProperty}/omod/src/main/webapp/fragments" will be set for that property
	 * 
	 * @param key
	 * @param provider
	 */
	public void addViewProvider(String key, FragmentViewProvider provider) {
		if (viewProviders == null)
			viewProviders = new LinkedHashMap<String, FragmentViewProvider>();
		
		String devRootFolder = System.getProperty("uiFramework.development." + key);
		if (devRootFolder != null) {
			File devFolder = new File(devRootFolder + File.separator + "omod" + File.separator + "src" + File.separator
			        + "main" + File.separator + "webapp" + File.separator + "fragments");
			if (devFolder.exists() && devFolder.isDirectory()) {
				try {
					PropertyUtils.setProperty(provider, "developmentFolder", devFolder);
				}
				catch (Exception ex) {
					// pass
				}
			} else {
				log.warn("Failed to set development mode for FragmentViewProvider " + key + " because "
				        + devFolder.getAbsolutePath() + " does not exist or is not a directory");
			}
		}
		
		viewProviders.put(key, provider);
	}
	
	public Object invokeFragmentAction(String providerName, String fragmentName, String action, HttpServletRequest httpRequest) {
		log.info("Invoking " + providerName + ":" + fragmentName + " . " + action);
		FragmentActionRequest request = new FragmentActionRequest(this, httpRequest);
		
		// try to find the requested fragment controller
		Object controller = getController(providerName, fragmentName);
		if (controller == null) {
			throw new UiFrameworkException("Cannot find fragment controller for " + providerName + ":" + fragmentName);
		}
		
		// find the correct action method
		Method method = null;
		for (Method candidate : controller.getClass().getMethods()) {
			if (candidate.getName().equals(action)) {
				method = candidate;
				break;
			}
		}
		if (method == null) {
			throw new UiFrameworkException("Error getting " + controller.getClass() + "." + action + " method");
		}

		// invoke all fragment action interceptors
		if (fragmentActionInterceptors != null) {
			for (FragmentActionInterceptor interceptor : fragmentActionInterceptors) {
				interceptor.beforeHandleRequest(request, method);
			}
		}
		
		// determine method arguments
		Map<Class<?>, Object> possibleArguments = new LinkedHashMap<Class<?>, Object>();
		possibleArguments.put(FragmentActionRequest.class, request);
		possibleArguments.put(HttpServletRequest.class, httpRequest);
		possibleArguments.put(HttpSession.class, httpRequest.getSession());
		possibleArguments.put(UiUtils.class, request.getUiUtils());
		possibleArguments.put(Session.class, sessionFactory.getSession(httpRequest.getSession()));
		possibleArguments.put(ApplicationContext.class, applicationContext);
		possibleArguments.put(ServletContext.class, servletContext);
        if (possibleFragmentActionArgumentProviders != null) {
            for (PossibleFragmentActionArgumentProvider provider : possibleFragmentActionArgumentProviders) {
                provider.addPossibleFragmentActionArguments(possibleArguments);
            }
        }

        Object[] params = null;
		try {
			params = UiFrameworkUtil.determineControllerMethodParameters(controller, method, possibleArguments, conversionService, applicationContext);
		}
		catch (RequestValidationException ex) {
			// this means we caught something via a @Validate annotation
			for (String errorCode : ex.getGlobalErrorCodes())
				request.getErrors().reject(errorCode);
			for (Map.Entry<String, List<String>> e : ex.getFieldErrorCodes().entrySet()) {
				for (String errorCode : e.getValue())
					request.getErrors().rejectValue(e.getKey(), errorCode);
			}
		}

		if (request.hasErrors())
			return new FailureResult(request.getErrors());
		
		// invoke method
		Object result;
		try {
			result = method.invoke(controller, params);
		}
		catch (Exception ex) {
			// if this error is a RequestValidationException (likely wrapped in an InvocationTargetException), the action
			// a validation exception (as opposed to this happen via a @Validate annotation, caught above)
			RequestValidationException validationEx = ExceptionUtil.findExceptionInChain(ex, RequestValidationException.class);
			if (validationEx != null) {
				for (String errorCode : validationEx.getGlobalErrorCodes())
					request.getErrors().reject(errorCode);
				for (Map.Entry<String, List<String>> e : validationEx.getFieldErrorCodes().entrySet()) {
					for (String errorCode : e.getValue())
						request.getErrors().rejectValue(e.getKey(), errorCode);
				}
				return new FailureResult(request.getErrors());
			}
			
			// it's possible that the underlying exception is that the user was logged out or lacks privileges
			// and we want to special-case that
			APIAuthenticationException authEx = ExceptionUtil.findExceptionInChain(ex, APIAuthenticationException.class);
			if (authEx != null) {
				throw authEx;
			}
			
			// we don't know how to handle other types of exceptions
			log.error("Error invoking fragment action with parameters: " + describeParamsForErrorMessage(method, params));
			throw new UiFrameworkException("Error invoking fragment action " + method, ex);
		}
		
		return result;
	}

    private String describeParamsForErrorMessage(Method method, Object[] params) {
        if (method.getParameterTypes().length != params.length) {
            return "Parameter length mismatch: expected " + method.getParameterTypes().length + " but passed " + params.length;
        }
        List<String> lines = new ArrayList<String>();
        for (int i = 0; i < method.getParameterTypes().length; ++i) {
            Class<?> expected = method.getParameterTypes()[i];
            String temp = "" + i + ": ";
            temp += "Expected: " + expected.getName() + " (cl: " + expected.getClassLoader() + ")";
            temp += " | Actual: ";
            if (params[i] == null) {
                temp += "null";
            } else {
                temp += params[i].getClass().getName() + " (cl: " + params[i].getClass().getClassLoader() + ")";
            }
            lines.add(temp);
        }
        return "\n" + OpenmrsUtil.join(lines, "\n");
    }

    public boolean fragmentExists(String providerName, String fragmentName) {
		Object controller = getController(providerName, fragmentName);
		if (controller != null) {
			return true;
		}
		
		FragmentView view = getView(new FragmentRequest(providerName, fragmentName), fragmentName);
		return (view != null);
	}
	
	public <T> T convert(Object obs, Class<T> toType) {
		return conversionService.convert(obs, toType);
	}
	
	public <T> Map<String, T> getSpringBeansOfType(Class<T> ofType) {
		return applicationContext.getBeansOfType(ofType);
	}
	
	public MessageSource getMessageSource() {
		return messageSource;
	}
	
	public ExtensionManager getExtensionManager() {
		return extensionManager;
	}
	
	public ConversionService getConversionService() {
		return conversionService;
	}
	
	public ServletContext getServletContext() {
		return servletContext;
	}

    public void setModelConfigurators(List<FragmentModelConfigurator> modelConfigurators) {
        this.modelConfigurators = modelConfigurators;
    }

    public List<FragmentModelConfigurator> getModelConfigurators() {
        return modelConfigurators;
    }

    public void setPossibleFragmentControllerArgumentProviders(List<PossibleFragmentControllerArgumentProvider> possibleFragmentControllerArgumentProviders) {
        this.possibleFragmentControllerArgumentProviders = possibleFragmentControllerArgumentProviders;
    }

    public void setPossibleFragmentActionArgumentProviders(List<PossibleFragmentActionArgumentProvider> possibleFragmentActionArgumentProviders) {
        this.possibleFragmentActionArgumentProviders = possibleFragmentActionArgumentProviders;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
