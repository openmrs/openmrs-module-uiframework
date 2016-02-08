/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *  
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.ui.framework;

import java.util.List;

import org.openmrs.ui.framework.page.PageRequest;

public interface FailedAuthenticationHandler {
	
	/**
	 * The return value is the url to send the user to because of the failed privilege check, note
	 * that the returned url from this method will be ignored if the caller already specified one
	 * 
	 * @param pageRequest PageContext object
	 * @param requiredPrivileges The privileges that are required to access the resource
	 * @param redirectUrl The redirect url the caller specified if any
	 * @return the redirect url that was specified by the caller
	 */
	String handle(PageRequest pageRequest, List<String> requiredPrivileges, String redirectUrl);
}
