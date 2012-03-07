package org.openmrs.ui2.core.fragment;

import org.openmrs.ui2.core.page.PageAction;

/**
 * Used for fragments that do not have a controller, and are view-only
 */
public class EmptyFragmentController {
	
	public String controller() {
		return null;
	}
	
}
