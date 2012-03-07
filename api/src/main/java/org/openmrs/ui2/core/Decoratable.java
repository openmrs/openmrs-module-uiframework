package org.openmrs.ui2.core;

import org.openmrs.ui2.core.fragment.FragmentRequest;

public interface Decoratable {
	
	/**
	 * Requests that this element be decorated with a fragment. (The output of this element
	 * will be passed to the decorator fragment as the 'content' property of its configuration.)
	 * @param fragmentRequest
	 */
	public void setDecorateWith(FragmentRequest fragmentRequest);
	
	/**
	 * @return the decorateWith
	 */
	public FragmentRequest getDecorateWith();
	
}
