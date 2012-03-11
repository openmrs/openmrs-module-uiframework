package org.openmrs.ui.framework.page;

/**
 * Maps a user's page request to an internal page name, e.g. you might map the request
 * for the 'patient' page to 'clinicianPatientDashboard' if the user is logged in as a
 * Clinician.
 */
public interface PageRequestMapper {
	
	String mapRequest(PageRequest request);
	
}
