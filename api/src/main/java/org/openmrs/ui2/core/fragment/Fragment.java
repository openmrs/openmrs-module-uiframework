package org.openmrs.ui2.core.fragment;

/**
 * A fragment that may be included in a page
 */
public interface Fragment {
	
	String handle(FragmentRequest request);
	
}
