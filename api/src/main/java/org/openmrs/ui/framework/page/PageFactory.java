package org.openmrs.ui.framework.page;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openmrs.ui.framework.Model;
import org.openmrs.ui.framework.ProviderAndName;
import org.openmrs.ui.framework.UiFrameworkException;
import org.openmrs.ui.framework.UiFrameworkUtil;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.extension.ExtensionManager;
import org.openmrs.ui.framework.fragment.FragmentContext;
import org.openmrs.ui.framework.fragment.FragmentFactory;
import org.openmrs.ui.framework.fragment.FragmentRequest;
import org.openmrs.ui.framework.interceptor.PageRequestInterceptor;
import org.openmrs.ui.framework.session.Session;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionService;

public class PageFactory {
	
	private static Map<String, PageControllerProvider> controllerProviders;
	
	private static Map<String, PageViewProvider> viewProviders;
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	ApplicationContext applicationContext;
	
	@Autowired
	MessageSource messageSource;
	
	@Autowired
	@Qualifier("coreFragmentFactory")
	FragmentFactory fragmentFactory;
	
	@Autowired(required = false)
	List<PageRequestMapper> requestMappers;
	
	@Autowired
	ExtensionManager extensionManager;
	
	@Autowired
	ConversionService conversionService;
	
	@Autowired(required = false)
	List<PageRequestInterceptor> pageRequestInterceptors;
	
	@Autowired(required = false)
	List<PageModelConfigurator> modelConfigurators;
	
	@Autowired(required = false)
	List<PossiblePageControllerArgumentProvider> possiblePageControllerArgumentProviders;
	
	// a singleton one of these that can be reused
	private EmptyPageController emptyController = new EmptyPageController();
	
	public String handle(PageRequest request) throws PageAction {
		long startTime = System.currentTimeMillis();
		// create a context for processing this page
		PageContext context = new PageContext(request);
		context.setMessageSource(messageSource);
		context.setPageFactory(this);
		context.setFragmentFactory(fragmentFactory);
		context.setExtensionManager(extensionManager);
		overridePageProviderAndName(request);
		if (modelConfigurators != null) {
			for (PageModelConfigurator pageModelConfigurator : modelConfigurators) {
				pageModelConfigurator.configureModel(context);
			}
			
		}
		String result = process(context);
		if (log.isDebugEnabled()) {
			log.debug(">>> Page >>> handled " + request + " in " + (System.currentTimeMillis() - startTime) + " ms");
		}
		
		// generally all pages are dynamic and should not be cached
		request.getResponse().setHeader("Cache-Control", "no-cache,no-store,must-revalidate"); // HTTP 1.1
		request.getResponse().setHeader("Pragma", "no-cache"); // HTTP 1.0
		request.getResponse().setDateHeader("Expires", 0); // prevents caching at any proxy server
		
		return result;
	}
	
	/**
	 * Allow modules to override page handling via {@link PageRequestMapper}s. Sets this internal page
	 * provider and name on request
	 * 
	 * @param request
	 */
	private void overridePageProviderAndName(PageRequest request) {
		if (requestMappers != null) {
			for (PageRequestMapper mapper : requestMappers) {
				boolean mapped = mapper.mapRequest(request);
				if (mapped) {
					break;
				}
			}
		}
	}
	
	public String process(PageContext context) throws PageAction {
		String result = processThisFragment(context);
		if (context.getDecorateWith() == null)
			return result;
		
		FragmentRequest decoratorRequest = context.getDecorateWith();
		decoratorRequest.getConfiguration().put("content", result);
		FragmentContext decoratorContext = new FragmentContext(decoratorRequest, context);
		result = fragmentFactory.process(decoratorContext);
		return result;
	}
	
	private String processThisFragment(PageContext context) throws PageAction {
		// determine what controller to use
		Object controller = getController(context.getRequest());
		PageView view = null;
		if (controller == null) {
			// some views can specify their controller
			try {
				view = getView(null, context.getRequest());
				ProviderAndName controllerProviderAndName = view.getController();
				if (controllerProviderAndName != null) {
					controller = getController(controllerProviderAndName.getProvider(), controllerProviderAndName.getName());
				}
			}
			catch (Exception ex) {
				// this probably means we didn't find a view. Pass now to fail later 
			}
			// TODO determine the controller from the view
			if (controller == null) {
				// go with the blank controller
				controller = emptyController;
			}
		}
		context.setController(controller);
		
		// invoke all page request interceptors
		if (pageRequestInterceptors != null) {
			for (PageRequestInterceptor interceptor : pageRequestInterceptors) {
				interceptor.beforeHandleRequest(context);
			}
		}
		
		// let the controller handle the request
		// TODO: refactor because fragment controllers can now also return a FragmentRequest
		Object resultObject = handleRequestWithController(context);
		
		if (resultObject instanceof PageAction) {
			throw (PageAction) resultObject;
		}
		
		String result = (String) resultObject;
		
		// check if there was redirect (other than via a thrown PageAction)
		if (result != null && result.startsWith("redirect:")) {
			String toApplicationUrl = result.substring("redirect:".length());
			throw new Redirect(toApplicationUrl);
		}
		
		// If the controller returns a simple string, we interpret that as a view in the requested provider.
		// The controller should return "*:viewName" to search all providers.
		if (result != null && result.indexOf(':') <= 0) {
			result = context.getRequest().getMappedProviderName() + ":" + result;
		}
		
		// determine what view to use
		// (if the controller requests the default view, and we have it from earlier, we use that)
		if (result != null || view == null) {
			view = getView(result, context.getRequest());
		}
		context.setView(view);
		
		String output = view.render(context);
		return output;
	}
	
	/**
	 * Invokes the appropriate method on the controller to handle this page request NB. If you change
	 * the supported parameter classes, make sure to update the documentation on the wiki
	 * 
	 * @param context the page context
	 * @return the controller output
	 * @throws PageAction
	 */
	private Object handleRequestWithController(PageContext context) throws PageAction {
		Map<Class<?>, Object> possibleArguments = new HashMap<Class<?>, Object>();
		possibleArguments.put(PageContext.class, context);
		possibleArguments.put(PageModel.class, context.getModel());
		possibleArguments.put(Model.class, context.getModel());
		possibleArguments.put(Map.class, context.getModel());
		possibleArguments.put(PageRequest.class, context.getRequest());
		possibleArguments.put(HttpServletRequest.class, context.getRequest().getRequest());
		possibleArguments.put(HttpSession.class, context.getRequest().getRequest().getSession());
		possibleArguments.put(HttpServletResponse.class, context.getRequest().getResponse());
		possibleArguments.put(Session.class, context.getRequest().getSession());
		possibleArguments.put(ApplicationContext.class, applicationContext);
		possibleArguments.put(UiUtils.class, new PageUiUtils(context));
		if (possiblePageControllerArgumentProviders != null) {
			for (PossiblePageControllerArgumentProvider provider : possiblePageControllerArgumentProviders) {
				provider.addPossiblePageControllerArguments(possibleArguments);
			}
		}
		
		String httpRequestMethod = context.getRequest().getRequest().getMethod();
		return UiFrameworkUtil.executeControllerMethod(context.getController(), httpRequestMethod, possibleArguments,
		    conversionService, applicationContext);
	}
	
	/**
	 * @param request
	 * @return controller class, or null if none is available
	 * @should get a controller from the specified provider
	 * @should get a controller from any provider if none specified
	 */
	Object getController(PageRequest request) {
		return getController(request.getMappedProviderName(), request.getMappedPageName());
	}
	
	private Object getController(String providerName, String pageName) {
		if (controllerProviders == null) {
			return null;
		}
		if ("*".equals(providerName)) {
			for (PageControllerProvider p : controllerProviders.values()) {
				Object ret = p.getController(pageName);
				if (ret != null)
					return ret;
			}
			return null;
		} else {
			PageControllerProvider provider = controllerProviders.get(providerName);
			if (provider == null) {
				return null;
			}
			return provider.getController(pageName);
		}
	}
	
	/**
	 * @param viewProviderAndName null indicates we should use the default view for request, otherwise
	 *            this should be "providerName:viewName"
	 * @param request
	 * @return
	 * @should get a view from the requested provider
	 * @should get a view from any provider if none is specified
	 * @should fail if an invalid provider name is specified
	 */
	PageView getView(String viewProviderAndName, PageRequest request) {
		String providerName;
		String viewName;
		if (viewProviderAndName == null) {
			providerName = request.getMappedProviderName();
			viewName = request.getMappedPageName();
		} else {
			String[] temp = viewProviderAndName.split(":");
			if (temp.length != 2) {
				throw new UiFrameworkException("Expected \"providerName:viewName\" but was \"" + viewProviderAndName + "\"");
			}
			providerName = temp[0];
			viewName = temp[1];
		}
		
		if ("*".equals(providerName)) {
			for (PageViewProvider p : viewProviders.values()) {
				PageView ret = p.getView(viewName);
				if (ret != null) {
					return ret;
				}
			}
			// pages are required to have views, so we throw an exception if we couldn't find one
			throw new UiFrameworkException("Could not find page view '" + viewName + "' in any of the view providers ("
			        + OpenmrsUtil.join(viewProviders.keySet(), ", ") + ")");
		} else {
			PageViewProvider provider = viewProviders.get(providerName);
			if (provider == null) {
				throw new UiFrameworkException("No viewProvider named " + providerName);
			}
			PageView ret = provider.getView(viewName);
			if (ret == null) {
				// pages are required to have views, so we throw an exception if we couldn't find one
				throw new UiFrameworkException("viewProvider " + providerName + " does not have a view named " + viewName);
			}
			return ret;
		}
	}
	
	/**
	 * @return the controllerProviders
	 */
	public Map<String, PageControllerProvider> getControllerProviders() {
		return controllerProviders;
	}
	
	/**
	 * @param newControllerProviders the controllerProviders to set
	 */
	public void setControllerProviders(Map<String, PageControllerProvider> newControllerProviders) {
		controllerProviders = newControllerProviders;
	}
	
	/**
	 * Adds the given controller providers to the existing ones. (I.e. this is not a proper setter.)
	 * 
	 * @param additional
	 * @see #addControllerProvider(String, PageControllerProvider)
	 */
	public void setAdditionalControllerProviders(Map<String, PageControllerProvider> additional) {
		for (Map.Entry<String, PageControllerProvider> e : additional.entrySet()) {
			addControllerProvider(e.getKey(), e.getValue());
		}
	}
	
	/**
	 * Registers a Page Controller Provider.
	 * 
	 * @see UiFrameworkUtil#checkAndSetDevelopmentModeForProvider(String, Object)
	 */
	public void addControllerProvider(String key, PageControllerProvider provider) {
		if (controllerProviders == null) {
			controllerProviders = new LinkedHashMap<String, PageControllerProvider>();
		}
		
		UiFrameworkUtil.checkAndSetDevelopmentModeForProvider(key, provider);
		
		controllerProviders.put(key, provider);
	}
	
	/**
	 * @return the viewProviders
	 */
	public Map<String, PageViewProvider> getViewProviders() {
		return viewProviders;
	}
	
	/**
	 * @param newViewProviders the viewProviders to set
	 */
	public void setViewProviders(Map<String, PageViewProvider> newViewProviders) {
		viewProviders = newViewProviders;
	}
	
	/**
	 * Adds the given view providers to the existing ones. (I.e. this is not a proper setter.)
	 * 
	 * @param additional
	 * @see #addViewProvider(String, PageViewProvider)
	 */
	public void setAdditionalViewProviders(Map<String, PageViewProvider> additional) {
		for (Map.Entry<String, PageViewProvider> e : additional.entrySet()) {
			addViewProvider(e.getKey(), e.getValue());
		}
	}
	
	/**
	 * Registers a Page View Provider
	 * 
	 * @see UiFrameworkUtil#checkAndSetDevelopmentModeForProvider(String, Object)
	 */
	public void addViewProvider(String key, PageViewProvider provider) {
		if (viewProviders == null) {
			viewProviders = new LinkedHashMap<String, PageViewProvider>();
		}
		
		UiFrameworkUtil.checkAndSetDevelopmentModeForProvider(key, provider);
		
		viewProviders.put(key, provider);
	}
	
	/**
	 * @return the requestMappers
	 */
	public List<PageRequestMapper> getRequestMappers() {
		return requestMappers;
	}
	
	/**
	 * @param requestMappers the requestMappers to set
	 */
	public void setRequestMappers(List<PageRequestMapper> requestMappers) {
		this.requestMappers = requestMappers;
	}
	
	public ConversionService getConversionService() {
		return conversionService;
	}
	
	public List<PageModelConfigurator> getModelConfigurators() {
		return modelConfigurators;
	}
	
	/**
	 * Sets the model configurators for this page factory. Usually these are autowired but this is used
	 * for testing.
	 * 
	 * @param modelConfigurators the model configurators
	 */
	public void setModelConfigurators(List<PageModelConfigurator> modelConfigurators) {
		this.modelConfigurators = modelConfigurators;
	}
	
	/**
	 * Sets the page controller argument providers for this page factory. Usually these are autowired
	 * but this is used for testing.
	 * 
	 * @param possiblePageControllerArgumentProviders the page controller argument providers
	 */
	public void setPossiblePageControllerArgumentProviders(
	        List<PossiblePageControllerArgumentProvider> possiblePageControllerArgumentProviders) {
		this.possiblePageControllerArgumentProviders = possiblePageControllerArgumentProviders;
	}
	
	/**
	 * Sets the page request interceptors for this page factory. Usually these are autowired but this is
	 * used for testing.
	 * 
	 * @param pageRequestInterceptors the page request interceptors
	 */
	public void setPageRequestInterceptors(List<PageRequestInterceptor> pageRequestInterceptors) {
		this.pageRequestInterceptors = pageRequestInterceptors;
	}
}
