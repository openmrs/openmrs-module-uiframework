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
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.converter.util.ConversionUtil;
import org.springframework.core.convert.converter.Converter;

public class StringToPersonConverter implements Converter<String, Person> {
	
	@Override
	public Person convert(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		} else if (ConversionUtil.onlyDigits(id)) {
			return Context.getPersonService().getPerson(Integer.valueOf(id));
		}
		return Context.getPersonService().getPersonByUuid(id);
	}
	
}
