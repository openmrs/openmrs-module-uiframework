package org.openmrs.ui.framework.page;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.Decoratable;
import org.openmrs.ui.framework.FragmentIncluder;
import org.openmrs.ui.framework.Messager;
import org.openmrs.ui.framework.MessagerImpl;
import org.openmrs.ui.framework.ResourceIncluder;
import org.openmrs.ui.framework.WebConstants;
import org.openmrs.ui.framework.extension.ExtensionAware;
import org.openmrs.ui.framework.extension.ExtensionManager;
import org.openmrs.ui.framework.fragment.FragmentContext;
import org.openmrs.ui.framework.fragment.FragmentFactory;
import org.openmrs.ui.framework.fragment.FragmentRequest;
import org.springframework.context.MessageSource;

public class PageContext implements ResourceIncluder, Messager, Decoratable, FragmentIncluder, ExtensionAware {
	
	private Locale locale;
	
	private Messager messager;
	
	private PageFactory pageFactory;
	
	private FragmentFactory fragmentFactory;
	
	private ExtensionManager extensionManager;
	
	private PageRequest request;
	
	private Object controller;
	
	private PageView view;
	
	private PageModel model;
	
	private FragmentRequest decorateWith;
	
	private String pageTitle;
	
	private Set<String> javascriptToInclude = new LinkedHashSet<String>();
	
	private Set<String> cssToInclude = new LinkedHashSet<String>();
	
	// TODO move this
	private static Map<String, String> resourceReplacements = new HashMap<String, String>();
	static {
		resourceReplacements.put("jquery.js", "jquery-1.7.1.min.js");
		resourceReplacements.put("jquery-ui.js", "jquery-ui-1.8.18.custom.min.js");
		resourceReplacements.put("jquery-ui.css", "cupertino/jquery-ui-1.8.18.custom.css");
		
		resourceReplacements.put("/scripts/jquery/jquery-1.3.2.min.js", resourceReplacements.get("jquery.js"));
		resourceReplacements.put("/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js", resourceReplacements.get("jquery-ui.js"));
		resourceReplacements.put("/scripts/jquery-ui/css/redmond/jquery-ui-1.7.2.custom.css", resourceReplacements.get("jquery-ui.css"));
		resourceReplacements.put("/scripts/openmrsmessages.js", "/scripts/openmrsmessages.js.withjstl");
	}
	
	public PageContext(PageRequest request) {
		this.request = request;
		this.model = new PageModel();
	}
	
	/**
	 * Set up a FragmentContext for including a fragment within this page
	 * @param fragmentRequest
	 * @return
	 */
	public FragmentContext createFragmentContext(FragmentRequest fragmentRequest) {
		FragmentContext ret = new FragmentContext(fragmentRequest, this);
		return ret;
	}
	
	@Override
	public String includeFragment(FragmentRequest request) throws PageAction {
		FragmentContext fragmentContext = new FragmentContext(request, this);
		return fragmentFactory.process(fragmentContext);
	}
	
	@Override
	public String message(String code, Object... args) {
		return messager.message(code, args);
	}
	
	public String getUrl(boolean includeContextPath) {
		StringBuilder ret = new StringBuilder();
		if (includeContextPath)
			ret.append("/" + WebConstants.CONTEXT_PATH + "/");
		ret.append("pages/" + request.getPageName() + ".page?");
		Map<String, String[]> params = request.getRequest().getParameterMap();
		for (Map.Entry<String, String[]> e : params.entrySet()) {
			for (String val : e.getValue()) {
				ret.append(e.getKey() + "=" + val + "&");
			}
		}
		return ret.toString();
	}
	
	public String getUrlExcludingParameters(boolean includeContextPath, String... parameters) {
		Set<String> exclude = new HashSet<String>(Arrays.asList(parameters));
		StringBuilder ret = new StringBuilder();
		if (includeContextPath)
			ret.append(WebConstants.CONTEXT_PATH + "/");
		ret.append(request.getPageName() + ".page?");
		Map<String, String[]> params = request.getRequest().getParameterMap();
		for (Map.Entry<String, String[]> e : params.entrySet()) {
			if (exclude.contains(e.getKey()))
				continue;
			for (String val : e.getValue()) {
				ret.append(e.getKey() + "=" + val + "&");
			}
		}
		if (ret.charAt(ret.length() - 1) == '&')
			ret.deleteCharAt(ret.length() - 1);
		return ret.toString();
	}
	
	/**
	 * Requests that this fragment be decorated with another one. (The output of this fragment
	 * will be passed to the decorator as "content".)
	 * @param fragmentRequest
	 */
	@Override
	public void setDecorateWith(FragmentRequest fragmentRequest) {
		this.decorateWith = fragmentRequest;
	}
	
	/**
	 * @return the decorateWith
	 */
	@Override
	public FragmentRequest getDecorateWith() {
		return decorateWith;
	}
	
	/**
	 * @return the request
	 */
	public PageRequest getRequest() {
		return request;
	}
	
	/**
	 * @param request the request to set
	 */
	public void setRequest(PageRequest request) {
		this.request = request;
	}
	
	/**
	 * @return the model
	 */
	public PageModel getModel() {
		return model;
	}
	
	/**
	 * @param model the model to set
	 */
	public void setModel(PageModel model) {
		this.model = model;
	}
	
	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale == null ? Context.getLocale() : locale;
	}
	
	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	/**
	 * Requests that this page include the given javascript file.
	 * @param file
	 */
	@Override
	public void includeJavascript(String file) {
		javascriptToInclude.add(translateResource(file));
	}
	
	/**
	 * Requests that this page include the given css file.
	 * @param file
	 */
	@Override
	public void includeCss(String file) {
		cssToInclude.add(translateResource(file));
	}
	
	@Override
	public Set<String> getJavascriptToInclude() {
		return javascriptToInclude;
	}
	
	@Override
	public Set<String> getCssToInclude() {
		return cssToInclude;
	}
	
	/**
	 * @param messageSource the messageSource to set
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messager = new MessagerImpl(getLocale(), messageSource);
	}
	
	/**
	 * @return the controller
	 */
	public Object getController() {
		return controller;
	}
	
	/**
	 * @param controller the controller to set
	 */
	public void setController(Object controller) {
		this.controller = controller;
	}
	
	/**
	 * @return the view
	 */
	public PageView getView() {
		return view;
	}
	
	/**
	 * @param view the view to set
	 */
	public void setView(PageView view) {
		this.view = view;
	}
	
	/**
	 * @return the pageFactory
	 */
	public PageFactory getPageFactory() {
		return pageFactory;
	}
	
	/**
	 * @param pageFactory the pageFactory to set
	 */
	public void setPageFactory(PageFactory pageFactory) {
		this.pageFactory = pageFactory;
	}
	
	/**
	 * @return the fragmentFactory
	 */
	public FragmentFactory getFragmentFactory() {
		return fragmentFactory;
	}
	
	/**
	 * @param fragmentFactory the fragmentFactory to set
	 */
	public void setFragmentFactory(FragmentFactory fragmentFactory) {
		this.fragmentFactory = fragmentFactory;
	}
	
	/**
	 * @see ExtensionAware#getExtensionManager()
	 */
	public ExtensionManager getExtensionManager() {
		return extensionManager;
	}
	
	/**
	 * @param extensionManager the extensionManager to set
	 */
	public void setExtensionManager(ExtensionManager extensionManager) {
		this.extensionManager = extensionManager;
	}
	
	/**
	 * @return the pageTitle
	 */
	public String getPageTitle() {
		return pageTitle;
	}
	
	/**
	 * @param pageTitle the pageTitle to set
	 */
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
	
	public static String translateResource(String requestedName) {
		String replacement = resourceReplacements.get(requestedName);
		return replacement == null ? requestedName : replacement;
	}
	
}
