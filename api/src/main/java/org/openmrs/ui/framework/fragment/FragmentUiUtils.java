package org.openmrs.ui.framework.fragment;

import java.util.Map;

import org.openmrs.ui.framework.FormatterImpl;
import org.openmrs.ui.framework.UiUtils;

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
		this.formatter = new FormatterImpl();
		this.messager = pageContext;
		this.fragmentIncluder = context;
		this.decoratable = context;
		this.extensionManager = pageContext.getExtensionManager();
		this.conversionService = pageContext.getPageFactory().getConversionService();
	}
	
	public String startForm(String action) {
		return super.startForm(getControllerName(), action);
	}
	
	public String startForm(String action, Map<String, CharSequence> args) {
		return super.startForm(getControllerName(), action, args);
	}
	
	public String actionLink(String action) {
		return actionLink(getControllerName(), action);
	}
	
	public String actionLink(String action, Map<String, ?> args) {
		return actionLink(getControllerName(), action, args);
	}
	
	public String thisFragmentId() {
		return context.getRequest().getId();
	}
	
	private String getControllerName() {
		// TODO fix this
		return context.getRequest().getId();
	}
	
}
