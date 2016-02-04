package org.openmrs.ui.framework.fragment;

import org.openmrs.ui.framework.page.PageAction;

public interface FragmentView {
	
	String render(FragmentContext context) throws PageAction;
	
}
