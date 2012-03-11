package org.openmrs.ui.framework.page;

import org.openmrs.ui.framework.fragment.CompoundFragmentView;
import org.openmrs.ui.framework.fragment.FragmentRequest;
import org.openmrs.ui.framework.fragment.FragmentView;

public class SingleCompoundFragmentPageView implements PageView {
	
	private String controllerName;
	
	private CompoundFragmentView fragmentView;
	
	public SingleCompoundFragmentPageView(String controllerName, CompoundFragmentView fragmentView) {
		this.controllerName = controllerName;
		this.fragmentView = fragmentView;
	}
	
	@Override
	public String getControllerName() {
		return controllerName;
	}
	
	@Override
	public String render(PageContext context) throws PageAction {
		return fragmentView.render(context);
	}
	
}
