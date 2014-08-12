package org.openmrs.ui.framework.fragment;

import org.openmrs.ui.framework.UiUtils;

import java.util.Map;

/**
 * Utility methods that you'd use in a view. These often delegate back to methods on the fragment
 * and page contexts, but they're packaged here to make things more intuitive while writing a view
 */
public class FragmentUiUtils extends UiUtils {
	
	private FragmentContext context;
	
	public FragmentUiUtils(FragmentContext context) {
		this.pageContext = context.getPageContext();
		this.context = context;
		this.resourceIncluder = pageContext;
        this.formatter = context.getFormatter();
		this.messager = pageContext;
		this.fragmentIncluder = context;
		this.decoratable = context;
		this.extensionManager = pageContext.getExtensionManager();
		this.conversionService = pageContext.getPageFactory().getConversionService();
	}
	
	public String startForm(String action) {
		return super.startForm(getProviderName(), getControllerName(), action);
	}
	
	public String startForm(String action, Map<String, CharSequence> args) {
		return super.startForm(getProviderName(), getControllerName(), action, args);
	}
	
	public String actionLink(String action) {
		return actionLink(getProviderName(), getControllerName(), action);
	}
	
	public String actionLink(String action, Map<String, ?> args) {
		return actionLink(getProviderName(), getControllerName(), action, args);
	}
	
	public String thisFragmentId() {
		return context.getRequest().getFragmentId();
	}
	
	private String getProviderName() {
		// TODO find a way to get the actual provider
		return context.getRequest().getProviderName();
	}
	
	private String getControllerName() {
		// TODO find a way to get the actual controller name
		return context.getRequest().getFragmentId();
	}
	
}
