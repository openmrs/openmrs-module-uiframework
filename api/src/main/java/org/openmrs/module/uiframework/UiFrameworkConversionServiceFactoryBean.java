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
package org.openmrs.module.uiframework;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.GenericConversionService;


/**
 * We ovverride Spring's implementation so we can autowire all spring-managed converters.
 * This allows other modules (besides uiframework) to provide converters.
 */
public class UiFrameworkConversionServiceFactoryBean extends ConversionServiceFactoryBean {
	
	@Autowired(required=false)
	Set<Converter<?, ?>> springManagedConverters;
	
	/**
	 * @see org.springframework.context.support.ConversionServiceFactoryBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() {
		if (springManagedConverters != null && springManagedConverters.size() > 0)
			super.setConverters(springManagedConverters);
	    super.afterPropertiesSet();
	}
}
