package org.openmrs.ui2.core.fragment;

import org.openmrs.ui2.core.page.PageAction;

public interface FragmentView {
	
	String render(FragmentContext context) throws PageAction;
	
}
