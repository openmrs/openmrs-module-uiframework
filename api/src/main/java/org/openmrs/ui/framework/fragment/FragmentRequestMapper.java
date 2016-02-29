package org.openmrs.ui.framework.fragment;

/**
 * Maps a request to a fragment e.g. you might map the request
 * for the 'header' fragment in the 'appui' to a different fragment 'myheader' in 'mymodule'
 */
public interface FragmentRequestMapper {
	
	/**
	 * Implementations should call {@link FragmentRequest#setProviderNameOverride(String)} and
	 * {@link FragmentRequest#setFragmentIdOverride(String)}, and return true if they want to remap a request,
	 * or return false if they didn't remap it.
	 * @param request may have its providerNameOverride and fragmentIdOverride set
	 * @return true if this fragment was mapped (by overriding the provider and/or fragment), false otherwise
	 */
	boolean mapRequest(FragmentRequest request);
	
}
