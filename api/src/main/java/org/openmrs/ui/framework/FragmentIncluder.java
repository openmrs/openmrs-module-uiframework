package org.openmrs.ui.framework;

import org.openmrs.ui.framework.fragment.FragmentRequest;
import org.openmrs.ui.framework.page.PageAction;

public interface FragmentIncluder {
	
	public String includeFragment(FragmentRequest request) throws PageAction;
	
}
