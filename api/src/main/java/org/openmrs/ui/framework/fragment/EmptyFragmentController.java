package org.openmrs.ui.framework.fragment;

import org.openmrs.ui.framework.page.PageAction;

/**
 * Used for fragments that do not have a controller, and are view-only
 */
public class EmptyFragmentController {
	
	public String controller() {
		return null;
	}
	
}
