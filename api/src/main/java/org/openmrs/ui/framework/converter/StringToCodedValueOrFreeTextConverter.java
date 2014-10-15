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
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.CodedValueOrFreeText;
import org.openmrs.ui.framework.converter.util.ConversionUtil;
import org.springframework.core.convert.converter.Converter;

/**
 * Converts a String it to a code value or returns it as a free text. The supported types it can
 * convert to are Concept and ConceptName, the string to be converted has to start with 'CONCEPT:'
 * to convert to a concept or 'CONCEPT_NAME:' to convert to a concept name otherwise it will return
 * the passed in string as the wrapped value. Note that this converter never returns null but the
 * wrapped value can be null.
 */
public class StringToCodedValueOrFreeTextConverter implements Converter<String, CodedValueOrFreeText> {
	
	public static final String CONCEPT_PREFIX = "CONCEPT:";
	
	public static final String CONCEPT_NAME_PREFIX = "CONCEPT_NAME:";
	
	/**
	 * @see Converter#convert(Object)
	 * @param str
	 * @return an instance of CodedValueOrFreeText
	 * @should convert a valid concept id string
	 * @should convert a valid concept uuid string
	 * @should convert a valid concept name id string
	 * @should convert a valid concept name uuid string
	 * @should not return null
	 * @should handle free text
	 */
	@Override
	public CodedValueOrFreeText convert(String str) {
		
		Object value = null;
		
		if (StringUtils.isNotBlank(str)) {
			ConceptService cs = Context.getConceptService();
			if (str.startsWith(CONCEPT_PREFIX)) {
				String uuidOrConceptId = str.substring(str.indexOf(CONCEPT_PREFIX) + CONCEPT_PREFIX.length());
				if (StringUtils.isNotBlank(uuidOrConceptId)) {
					if (ConversionUtil.onlyDigits(uuidOrConceptId)) {
						value = cs.getConcept(Integer.valueOf(uuidOrConceptId));
					} else {
						value = cs.getConceptByUuid(uuidOrConceptId);
					}
				}
				
			} else if (str.startsWith(CONCEPT_NAME_PREFIX)) {
				String uuidOrConceptNameId = str.substring(str.indexOf(CONCEPT_NAME_PREFIX) + CONCEPT_NAME_PREFIX.length());
				
				if (StringUtils.isNotBlank(uuidOrConceptNameId)) {
					if (ConversionUtil.onlyDigits(uuidOrConceptNameId)) {
						value = cs.getConceptName(Integer.valueOf(uuidOrConceptNameId));
					} else {
						value = cs.getConceptNameByUuid(uuidOrConceptNameId);
					}
				}
				
			} else {
				value = str;
			}
		}
		
		return new CodedValueOrFreeText(value);
	}
}
