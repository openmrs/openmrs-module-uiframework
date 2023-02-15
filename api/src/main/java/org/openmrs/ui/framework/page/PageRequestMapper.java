package org.openmrs.ui.framework.page;

/**
 * Maps a user's page request to an internal page name, e.g. you might map the request for the
 * 'patient' page to 'clinicianPatientDashboard' if the user is logged in as a Clinician.
 */
public interface PageRequestMapper {
	
	/**
	 * Implementations should call {@link PageRequest#setProviderNameOverride(String)} and
	 * {@link PageRequest#setPageNameOverride(String)}, and return true if they want to remap a request,
	 * or return false if they didn't remap it.
	 * 
	 * @param request may have its providerNameOverride and pageNameOverride set
	 * @return true if this page was mapped (by overriding the provider and/or page), false otherwise
	 */
	boolean mapRequest(PageRequest request);
	
}
