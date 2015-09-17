/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Role;
import org.openmrs.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Convert from {@link String} to {@link org.openmrs.Role}, interpreting it as a Role.role
 */
@Component
public class StringToRoleConverter implements Converter<String, Role> {
	
	@Autowired
	@Qualifier("userService")
	UserService service;
	
	/**
	 * @see Converter#convert(Object)
	 */
	@Override
	public Role convert(String source) {
		Role role = null;
		if (StringUtils.isNotBlank(source)) {
			role = service.getRole(source);
			if (role == null) {
				role = service.getRoleByUuid(source);
			}
		}
		return role;
	}
	
}
