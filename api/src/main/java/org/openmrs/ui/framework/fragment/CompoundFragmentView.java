package org.openmrs.ui.framework.fragment;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.ui.framework.page.PageAction;
import org.openmrs.ui.framework.page.PageContext;

public class CompoundFragmentView implements FragmentView {
	
	List<FragmentRequest> fragments;
	
	public CompoundFragmentView() {
		this.fragments = new ArrayList<FragmentRequest>();
	}
	
	public CompoundFragmentView(List<FragmentRequest> fragments) {
		this.fragments = fragments;
	}
	
	public void addFragment(FragmentRequest request) {
		fragments.add(request);
	}
	
	@Override
	public String render(FragmentContext context) throws PageAction {
		StringBuilder ret = new StringBuilder();
		for (FragmentRequest request : fragments) {
			String html = context.includeFragment(request);
			if (html != null)
				ret.append(html);
		}
		return ret.toString();
	}
	
	// TODO figure out how to get rid of this. Maybe make PageContext and FragmentContext implement a common interface?
	public String render(PageContext context) throws PageAction {
		StringBuilder ret = new StringBuilder();
		for (FragmentRequest request : fragments) {
			String html = context.includeFragment(request);
			if (html != null)
				ret.append(html);
		}
		return ret.toString();
	}
	
	/**
	 * @return the fragments
	 */
	public List<FragmentRequest> getFragments() {
		return fragments;
	}
	
	/**
	 * @param fragments the fragments to set
	 */
	public void setFragments(List<FragmentRequest> fragments) {
		this.fragments = fragments;
	}
	
}
