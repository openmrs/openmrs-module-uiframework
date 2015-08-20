/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Role;
import org.openmrs.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component("uiframeworkStringToRoleConverter")
public class StringToRoleConverter implements Converter<String, Role> {
	
	@Autowired
	@Qualifier("userService")
	public UserService service;
	
	@Override
	public Role convert(String source) {
		Role ret = null;
		if (StringUtils.isNotBlank(source)) {
			ret = service.getRole(source);
			if (ret == null) {
				ret = service.getRoleByUuid(source);
			}
		}
		
		return ret;
	}
}
