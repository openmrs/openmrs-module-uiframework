package org.openmrs.ui2.core.page;

import org.openmrs.ui2.core.FormatterImpl;
import org.openmrs.ui2.core.UiUtils;

public class PageUiUtils extends UiUtils {
	
	public PageUiUtils(PageContext pageContext) {
		this.pageContext = pageContext;
		this.fragmentIncluder = pageContext;
		this.resourceIncluder = pageContext;
		this.formatter = new FormatterImpl();
		this.messager = pageContext;
		this.decoratable = pageContext;
		this.extensionManager = pageContext.getExtensionManager();
		this.conversionService = pageContext.getPageFactory().getConversionService();
	}
	
}
