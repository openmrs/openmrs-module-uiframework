package org.openmrs.ui.framework.page;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.ui.framework.Model;
import org.openmrs.ui.framework.UiFrameworkUtil;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.WebConstants;
import org.openmrs.ui.framework.extension.ExtensionManager;
import org.openmrs.ui.framework.fragment.FragmentContext;
import org.openmrs.ui.framework.fragment.FragmentFactory;
import org.openmrs.ui.framework.fragment.FragmentRequest;
import org.openmrs.ui.framework.resource.Resource;
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
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	ApplicationContext applicationContext;
	
	@Autowired
	MessageSource messageSource;
	
	@Autowired
	@Qualifier("coreFragmentFactory")
	FragmentFactory fragmentFactory;
	
	@Autowired(required=false)
	List<PageRequestMapper> requestMappers;
	
	@Autowired
	ExtensionManager extensionManager;
	
	@Autowired
	ConversionService conversionService;
	
	private static Map<String, PageControllerProvider> controllerProviders;
	
	private static Map<String, PageViewProvider> viewProviders;
	
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
		mapInternalPageName(request);
		String result = process(context);
		log.info(">>> Page >>> handled " + request.getPageName() + " (as " + request.getInternalPageName() + ") in "
		        + (System.currentTimeMillis() - startTime) + " ms");
		return result;
	}
	
	/**
	 * Determines what internal page name should be used for a given external request,
	 * by delegating to the registered requestMappers.
	 * Sets this internal page name on request
	 * @param request
	 */
	private void mapInternalPageName(PageRequest request) {
		String internal = null;
		if (requestMappers != null) {
			for (PageRequestMapper mapper : requestMappers) {
				String mapped = mapper.mapRequest(request);
				if (mapped != null) {
					internal = mapped;
					break;
				}
			}
		}
		if (internal == null)
			internal = request.getPageName();
		request.setInternalPageName(internal);
	}
	
	public String process(PageContext context) throws PageAction {
		String result = processThisFragment(context);
		if (context.getDecorateWith() == null)
			return toHtml(result, context);
		
		FragmentRequest decoratorRequest = context.getDecorateWith();
		decoratorRequest.getConfiguration().put("content", result);
		FragmentContext decoratorContext = new FragmentContext(decoratorRequest, context);
		result = fragmentFactory.process(decoratorContext);
		return toHtml(result, context);
	}
	
	private String processThisFragment(PageContext context) throws PageAction {
		// determine what controller to use
		Object controller = getController(context.getRequest());
		PageView view = null;
		if (controller == null) {
			// some views can specify their controller
			try {
				view = getView(null, context.getRequest());
				String controllerName = view.getControllerName();
				if (controllerName != null) {
					controller = getController(controllerName);
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
		
		// determine what view to use
		// (if the controller requests the default view, and we have it from earlier, we use that)
		if (result != null || view == null) {
			view = getView(result, context.getRequest());
		}
		context.setView(view);
		
		String output = view.render(context);
		return output;
	}
	
	// if you change the supported parameter classes, make sure to update the documentation on the wiki
	private Object handleRequestWithController(PageContext context) throws PageAction {
		Map<Class<?>, Object> possibleArguments = new HashMap<Class<?>, Object>();
		possibleArguments.put(PageContext.class, context);
		possibleArguments.put(PageModel.class, context.getModel());
		possibleArguments.put(Model.class, context.getModel());
		possibleArguments.put(Map.class, context.getModel());
		possibleArguments.put(PageRequest.class, context.getRequest());
		possibleArguments.put(HttpServletRequest.class, context.getRequest().getRequest());
		possibleArguments.put(Session.class, context.getRequest().getSession());
		possibleArguments.put(ApplicationContext.class, applicationContext);
		possibleArguments.put(UiUtils.class, new PageUiUtils(context));
		return UiFrameworkUtil.executeControllerMethod(context.getController(), possibleArguments, conversionService);
	}
	
	private String toHtml(String body, PageContext context) {
		StringBuilder ret = new StringBuilder();
		ret.append("<html>\n");
		ret.append("<head>\n");
		if (context.getPageTitle() != null)
			ret.append("<title>" + context.getPageTitle() + "</title>\n");
		for (Resource resource : context.getJavascriptToInclude()) {
			ret.append("<script type=\"text/javascript\" src=\"/" + WebConstants.CONTEXT_PATH + "/ms/uiframework/resource/" + resource.getProviderName() + "/" + resource.getResourcePath() + "\"></script>\n");
		}
		for (Resource resource : context.getCssToInclude()) {
			ret.append("<link rel=\"stylesheet\" href=\"/" + WebConstants.CONTEXT_PATH + "/ms/uiframework/resource/" + resource.getProviderName() + "/" + resource.getResourcePath() + "\" type=\"text/css\"/>\n");
		}
		ret.append("</head>\n");
		ret.append("<body>\n");
		ret.append("<script>var OPENMRS_CONTEXT_PATH = '" + WebConstants.CONTEXT_PATH + "';</script>");
		ret.append(body);
		ret.append("</body>\n");
		ret.append("</html>");
		return ret.toString();
	}
	
	private Object getController(String controllerName) {
		// TODO BW: could be slow in prod. possibly use a cached version should be used for non debug modes?
		if (controllerProviders != null) {
			for (PageControllerProvider p : controllerProviders.values()) {
				Object ret = p.getController(controllerName);
				if (ret != null)
					return ret;
			}
		}
		return null;
	}
	
	private Object getController(PageRequest request) {
		return getController(request.getInternalPageName());
	}
	
	private PageView getView(String viewName, PageRequest request) {
		if (viewName == null)
			viewName = request.getInternalPageName();
		for (PageViewProvider p : viewProviders.values()) {
			PageView ret = p.getView(viewName);
			if (ret != null)
				return ret;
		}
		// pages are required to have views, so we throw an exception if we couldn't find one
		throw new RuntimeException("Could not find page view '" + viewName + "' in any of the view providers (" + OpenmrsUtil.join(viewProviders.keySet(), ", ") + ")");
	}
	
	/**
	 * @return the controllerProviders
	 */
	public Map<String, PageControllerProvider> getControllerProviders() {
		return controllerProviders;
	}
	
	/**
	 * @param controllerProviders the controllerProviders to set
	 */
	public void setControllerProviders(Map<String, PageControllerProvider> newControllerProviders) {
		controllerProviders = newControllerProviders;
	}
	
	/**
	 * Adds the given controller providers to the existing ones. (I.e. this is not a proper setter.)
	 * @param additional
	 * @see #addControllerProvider(String, PageControllerProvider)
	 */
	public void setAdditionalControllerProviders(Map<String, PageControllerProvider> additional) {
		for (Map.Entry<String, PageControllerProvider> e : additional.entrySet()) {
			addControllerProvider(e.getKey(), e.getValue());
		}
	}
	
	/**
	 * Registers a Controller Provider.
	 * 
	 * If a system property exists called "uiFramework.development.${ key }", and the controller provider has
	 * a "developmentFolder" property, the value of "${systemProperty}/omod/target/classes" will be set
	 * for that property 
	 * 
	 * @param key
	 * @param provider
	 */
	public void addControllerProvider(String key, PageControllerProvider provider) {
		if (controllerProviders == null)
			controllerProviders = new LinkedHashMap<String, PageControllerProvider>();
		
		String devRootFolder = System.getProperty("uiFramework.development." + key);
		if (devRootFolder != null) {
			File devFolder = new File(devRootFolder + File.separator + "omod" + File.separator + "target" + File.separator + "classes");
			if (devFolder.exists() && devFolder.isDirectory()) {
				try {
					PropertyUtils.setProperty(provider, "developmentFolder", devFolder);
				} catch (Exception ex) {
					// pass
				}
			} else {
				log.warn("Failed to set development mode for PageControllerProvider " + key + " because " + devFolder.getAbsolutePath() + " does not exist or is not a directory");
			}
		}
		
		controllerProviders.put(key, provider);
	}
	
	/**
	 * @return the viewProviders
	 */
	public Map<String, PageViewProvider> getViewProviders() {
		return viewProviders;
	}
	
	/**
	 * @param viewProviders the viewProviders to set
	 */
	public void setViewProviders(Map<String, PageViewProvider> newViewProviders) {
		viewProviders = newViewProviders;
	}
	
	
	/**
	 * Adds the given view providers to the existing ones. (I.e. this is not a proper setter.)
	 * @param additional
	 * @see #addViewProvider(String, PageViewProvider)
	 */
	public void setAdditionalViewProviders(Map<String, PageViewProvider> additional) {
		for (Map.Entry<String, PageViewProvider> e : additional.entrySet()) {
			addViewProvider(e.getKey(), e.getValue());
		}
	}
			
		
	/**
	 * If a system property exists called "uiFramework.development.${ key }", and the view provider has
	 * a "developmentFolder" property, the value of "${systemProperty}/omod/src/main/webapp/pages" will be set
	 * for that property 
	 * 
	 * @param key
	 * @param provider
	 */
	public void addViewProvider(String key, PageViewProvider provider) {
		if (viewProviders == null)
			viewProviders = new LinkedHashMap<String, PageViewProvider>();
		
		String devRootFolder = System.getProperty("uiFramework.development." + key);
		if (devRootFolder != null) {
			File devFolder = new File(devRootFolder + File.separator + "omod" + File.separator + "src" + File.separator + "main" + File.separator + "webapp" + File.separator + "pages");
			if (devFolder.exists() && devFolder.isDirectory()) {
				try {
					PropertyUtils.setProperty(provider, "developmentFolder", devFolder);
				} catch (Exception ex) {
					// pass
				}
			} else {
				log.warn("Failed to set development mode for PageViewProvider " + key + " because " + devFolder.getAbsolutePath() + " does not exist or is not a directory");
			}
		}
		
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
	
}
