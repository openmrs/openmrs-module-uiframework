/*
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
import org.openmrs.Provider;
import org.openmrs.api.ProviderService;
import org.openmrs.ui.framework.converter.util.ConversionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Convert from {@link String} to {@link org.openmrs.Provider}, interpreting it as a
 * Provider.providerId
 */
@Component
public class StringToProviderConverter implements Converter<String, Provider> {
	
	@Autowired
	@Qualifier("providerService")       
	ProviderService service;
	
	/**
	 * @see Converter#convert(Object)
	 */
	@Override
	public Provider convert(String source) {
		if (StringUtils.isBlank(source)) {
			return null;
		} else if (ConversionUtil.onlyDigits(source)) {
			return service.getProvider(Integer.valueOf(source));
		}
		return service.getProviderByUuid(source);
	}
	
}
