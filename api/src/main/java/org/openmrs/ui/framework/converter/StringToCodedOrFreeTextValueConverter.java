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

import static org.openmrs.ui.framework.CodedOrFreeTextValue.CONCEPT_NAME_PREFIX;
import static org.openmrs.ui.framework.CodedOrFreeTextValue.CONCEPT_PREFIX;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.CodedOrFreeTextValue;
import org.openmrs.ui.framework.converter.util.ConversionUtil;
import org.springframework.core.convert.converter.Converter;

/**
 * Converts a String to a concept name, concept, the string to be converted has to start with
 * 'CONCEPT:' for a concept or 'CONCEPT_NAME:' for a concept name otherwise it will return the
 * passed in string as the wrapped value. Note that this converter never returns null but the
 * wrapped value can be null.
 */
public class StringToCodedOrFreeTextValueConverter implements Converter<String, CodedOrFreeTextValue> {
	
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
	public CodedOrFreeTextValue convert(String str) {
		String nonCodedValue = null;
		if (StringUtils.isNotBlank(str)) {
			ConceptService cs = Context.getConceptService();
			if (str.startsWith(CONCEPT_PREFIX)) {
				String uuidOrConceptId = str.substring(str.indexOf(CONCEPT_PREFIX) + CONCEPT_PREFIX.length());
				if (StringUtils.isNotBlank(uuidOrConceptId)) {
					Concept concept;
					if (ConversionUtil.onlyDigits(uuidOrConceptId)) {
						concept = cs.getConcept(Integer.valueOf(uuidOrConceptId));
					} else {
						concept = cs.getConceptByUuid(uuidOrConceptId);
					}
					return new CodedOrFreeTextValue(concept);
				}
				
			} else if (str.startsWith(CONCEPT_NAME_PREFIX)) {
				String uuidOrConceptNameId = str.substring(str.indexOf(CONCEPT_NAME_PREFIX) + CONCEPT_NAME_PREFIX.length());
				
				if (StringUtils.isNotBlank(uuidOrConceptNameId)) {
					ConceptName name;
					if (ConversionUtil.onlyDigits(uuidOrConceptNameId)) {
						name = cs.getConceptName(Integer.valueOf(uuidOrConceptNameId));
					} else {
						name = cs.getConceptNameByUuid(uuidOrConceptNameId);
					}
					return new CodedOrFreeTextValue(name);
				}
				
			} else {
				nonCodedValue = str;
			}
		}
		
		return new CodedOrFreeTextValue(nonCodedValue);
	}
}
