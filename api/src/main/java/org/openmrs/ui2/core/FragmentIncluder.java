package org.openmrs.ui2.core;

import org.openmrs.ui2.core.fragment.FragmentRequest;
import org.openmrs.ui2.core.page.PageAction;

public interface FragmentIncluder {
	
	public String includeFragment(FragmentRequest request) throws PageAction;
	
}
