package org.openmrs.ui2.core.page;

public interface PageView {
	
	String render(PageContext context) throws PageAction;
	
	// some views may specify their own controllers
	String getControllerName();
	
}
