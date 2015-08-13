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

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.page.PageRequest;

public class DefaultFailedAuthenticationHandler implements FailedAuthenticationHandler {
	
	public String handle(PageRequest pageRequest, List<String> requiredPrivileges, String redirectUrl) {
		if (StringUtils.isNotBlank(redirectUrl)) {
			//Currently there is no action required on our part
			return null;
		}
		
		if (Context.isAuthenticated()) {
			//Logout the user so that they can log in with another account
			Context.logout();
			pageRequest.getRequest().getSession().invalidate();
		}
		
		return getRedirectUrl();
	}
	
	public String getRedirectUrl() {
		//This is the only login page we can guarantee exists for now,
		//but how about after removing the legacy UI from the platform?
		return "/login.htm";
	}
}
