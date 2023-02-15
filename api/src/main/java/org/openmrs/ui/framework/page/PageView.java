package org.openmrs.ui.framework.page;

import org.openmrs.ui.framework.ProviderAndName;

public interface PageView {
	
	String render(PageContext context) throws PageAction;
	
	/**
	 * Some views may specify their own controllers (e.g. to support the idea of a user-defined view for
	 * a customized patient page, that uses a pre-existing patient page controller)
	 * 
	 * @return
	 */
	ProviderAndName getController();
	
}
