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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.ui.framework.CodedValueOrFreeText;

public class StringToCodedValueOrFreeTextConverterTest extends BaseModuleContextSensitiveTest {
	
	private ConceptService cs;
	
	private StringToCodedValueOrFreeTextConverter converter;
	
	@Before
	public void setup() {
		cs = Context.getConceptService();
		converter = new StringToCodedValueOrFreeTextConverter();
	}
	
	/**
	 * @verifies convert a valid concept id string
	 * @see StringToCodedValueOrFreeTextConverter#convert(String)
	 */
	@Test
	public void convert_shouldConvertAValidConceptIdString() throws Exception {
		int conceptId = 3;
		String toConvert = StringToCodedValueOrFreeTextConverter.CONCEPT_PREFIX + conceptId;
		CodedValueOrFreeText codedValue = converter.convert(toConvert);
		Concept concept = codedValue.getValueAsType();
		assertEquals(conceptId, concept.getConceptId().intValue());
	}
	
	/**
	 * @verifies convert a valid concept uuid string
	 * @see StringToCodedValueOrFreeTextConverter#convert(String)
	 */
	@Test
	public void convert_shouldConvertAValidConceptUuidString() throws Exception {
		String conceptUuid = "0cbe2ed3-cd5f-4f46-9459-26127c9265ab";
		String toConvert = StringToCodedValueOrFreeTextConverter.CONCEPT_PREFIX + conceptUuid;
		CodedValueOrFreeText codedValue = converter.convert(toConvert);
		Concept concept = codedValue.getValueAsType();
		assertEquals(conceptUuid, concept.getUuid());
	}
	
	/**
	 * @verifies convert a valid concept name id string
	 * @see StringToCodedValueOrFreeTextConverter#convert(String)
	 */
	@Test
	public void convert_shouldConvertAValidConceptNameIdString() throws Exception {
		int conceptNameId = 1439;
		String toConvert = StringToCodedValueOrFreeTextConverter.CONCEPT_NAME_PREFIX + conceptNameId;
		CodedValueOrFreeText codedValue = converter.convert(toConvert);
		ConceptName name = codedValue.getValueAsType();
		assertEquals(conceptNameId, name.getConceptNameId().intValue());
	}
	
	/**
	 * @verifies convert a valid concept name uuid string
	 * @see StringToCodedValueOrFreeTextConverter#convert(String)
	 */
	@Test
	public void convert_shouldConvertAValidConceptNameUuidString() throws Exception {
		String conceptNameUuid = "9bc5693a-f558-40c9-8177-145a4b119ca7";
		String toConvert = StringToCodedValueOrFreeTextConverter.CONCEPT_NAME_PREFIX + conceptNameUuid;
		CodedValueOrFreeText codedValue = converter.convert(toConvert);
		ConceptName name = codedValue.getValueAsType();
		assertEquals(conceptNameUuid, name.getUuid());
	}
	
	/**
	 * @verifies handle free text
	 * @see StringToCodedValueOrFreeTextConverter#convert(String)
	 */
	@Test
	public void convert_shouldHandleFreeText() throws Exception {
		String text = "some random text";
		CodedValueOrFreeText codedValue = converter.convert(text);
		assertEquals(text, codedValue.getValueAsType());
	}
	
	/**
	 * @verifies not return null
	 * @see StringToCodedValueOrFreeTextConverter#convert(String)
	 */
	@Test
	public void convert_shouldNotReturnNull() throws Exception {
		String conceptUuid = "some invalid concept uuid";
		String toConvert = StringToCodedValueOrFreeTextConverter.CONCEPT_PREFIX + conceptUuid;
		CodedValueOrFreeText codedValue = converter.convert(toConvert);
		assertNotNull(codedValue);
		assertNull(codedValue.getValue());//the wrapped value can be null
	}
}
