package org.openmrs.ui.framework.page;

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
import org.openmrs.ui.framework.resource.Resource;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PageContext implements ResourceIncluder, Messager, Decoratable, FragmentIncluder, ExtensionAware {
	
	private Locale locale;

    private MessageSource messageSource;
	
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

    private List<Resource> resourcesToInclude = new ArrayList<Resource>();

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
		ret.append(request.getProviderName() + "/" + request.getPageName() + ".page?");
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

    @Override
    public void includeResource(Resource resource) {
        resourcesToInclude.add(resource);
    }

    @Override
    public List<Resource> getResourcesToInclude(String resourceCategory) {
        List<Resource> ret = new ArrayList<Resource>();
        for (Resource candidate : resourcesToInclude) {
            if (resourceCategory.equals(candidate.getCategory())) {
                ret.add(candidate);
            }
        }
        // we count on Java providing a stable sort algorithm, so insertion order is maintained where priority is equal
        Collections.sort(ret, new Comparator<Resource>() {
            @Override
            public int compare(Resource left, Resource right) {
                return right.getPriority().compareTo(left.getPriority());
            }
        });
        return ret;
    }

    /**
	 * Requests that this page include the given javascript file.
	 * @param resource
	 */
	public void includeJavascript(Resource resource) {
        resource.setCategory(Resource.CATEGORY_JS);
        includeResource(resource);
	}
	
	/**
	 * Requests that this page include the given css file.
	 * @param resource
	 */
	public void includeCss(Resource resource) {
        resource.setCategory(Resource.CATEGORY_CSS);
        includeResource(resource);
    }
	
	/**
     * Also instantiates a MessageerImpl with this message source
	 * @param messageSource the messageSource to set
	 */
	public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
		this.messager = new MessagerImpl(getLocale(), messageSource);
	}

    public MessageSource getMessageSource() {
        return messageSource;
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
		
}
