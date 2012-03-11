package org.openmrs.ui.framework.page;

public interface PageView {
	
	String render(PageContext context) throws PageAction;
	
	// some views may specify their own controllers
	String getControllerName();
	
}
