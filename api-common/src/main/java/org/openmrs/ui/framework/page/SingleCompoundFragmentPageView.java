package org.openmrs.ui.framework.page;

import org.openmrs.ui.framework.ProviderAndName;
import org.openmrs.ui.framework.fragment.CompoundFragmentView;
import org.openmrs.ui.framework.fragment.FragmentRequest;
import org.openmrs.ui.framework.fragment.FragmentView;

public class SingleCompoundFragmentPageView implements PageView {
	
	private String controllerProviderAndName;
	
	private CompoundFragmentView fragmentView;
	
	public SingleCompoundFragmentPageView(String controllerProviderAndName, CompoundFragmentView fragmentView) {
		this.controllerProviderAndName = controllerProviderAndName;
		this.fragmentView = fragmentView;
	}
	
	/**
	 * @see org.openmrs.ui.framework.page.PageView#getController()
	 */
	@Override
	public ProviderAndName getController() {
		if (controllerProviderAndName != null) {
			String provider;
			String controller;
			String[] temp = controllerProviderAndName.split(":");
			if (temp.length == 1) {
				provider = "*";
				controller = temp[0];
			} else {
				provider = temp[0];
				controller = temp[1];
			}
			return new ProviderAndName(provider, controller);
		} else {
			return null;
		}
	}
	
	@Override
	public String render(PageContext context) throws PageAction {
		return fragmentView.render(context);
	}
	
}
