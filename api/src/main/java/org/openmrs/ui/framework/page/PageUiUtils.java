package org.openmrs.ui.framework.page;

import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.FormatterImpl;
import org.openmrs.ui.framework.UiUtils;

public class PageUiUtils extends UiUtils {
	
	public PageUiUtils(PageContext pageContext) {
		this.pageContext = pageContext;
		this.fragmentIncluder = pageContext;
		this.resourceIncluder = pageContext;
		this.formatter = new FormatterImpl(pageContext.getMessageSource(), Context.getAdministrationService());
		this.messager = pageContext;
		this.decoratable = pageContext;
		this.extensionManager = pageContext.getExtensionManager();
		this.conversionService = pageContext.getPageFactory().getConversionService();
	}
	
}
