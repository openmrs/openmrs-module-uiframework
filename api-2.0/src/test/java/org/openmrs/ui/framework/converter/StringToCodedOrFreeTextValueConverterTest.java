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
import static org.openmrs.ui.framework.CodedOrFreeTextValue.CONCEPT_NAME_PREFIX;
import static org.openmrs.ui.framework.CodedOrFreeTextValue.CONCEPT_PREFIX;
import static org.openmrs.ui.framework.CodedOrFreeTextValue.NON_CODED_PREFIX;

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.ui.framework.CodedOrFreeTextValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

//@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
//@Ignore("@DirtiesContext causes tests to fail")
public class StringToCodedOrFreeTextValueConverterTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private StringToCodedOrFreeTextValueConverter converter;
	
	/**
	 * @verifies convert a valid concept id string
	 * @see StringToCodedOrFreeTextValueConverter#convert(String)
	 */
	@Test
	public void convert_shouldConvertAValidConceptIdString() throws Exception {
		int conceptId = 3;
		CodedOrFreeTextValue codedValue = converter.convert(CONCEPT_PREFIX + conceptId);
		Concept concept = codedValue.getCodedValue();
		assertEquals(conceptId, concept.getConceptId().intValue());
	}
	
	/**
	 * @verifies convert a valid concept uuid string
	 * @see StringToCodedOrFreeTextValueConverter#convert(String)
	 */
	@Test
	public void convert_shouldConvertAValidConceptUuidString() throws Exception {
		String conceptUuid = "0cbe2ed3-cd5f-4f46-9459-26127c9265ab";
		CodedOrFreeTextValue codedValue = converter.convert(CONCEPT_PREFIX + conceptUuid);
		Concept concept = codedValue.getCodedValue();
		assertEquals(conceptUuid, concept.getUuid());
	}
	
	/**
	 * @verifies convert a valid concept name id string
	 * @see StringToCodedOrFreeTextValueConverter#convert(String)
	 */
	@Test
	public void convert_shouldConvertAValidConceptNameIdString() throws Exception {
		int conceptNameId = 1439;
		CodedOrFreeTextValue codedValue = converter.convert(CONCEPT_NAME_PREFIX + conceptNameId);
		ConceptName name = codedValue.getCodedNameValue();
		assertEquals(conceptNameId, name.getConceptNameId().intValue());
	}
	
	/**
	 * @verifies convert a valid concept name uuid string
	 * @see StringToCodedOrFreeTextValueConverter#convert(String)
	 */
	@Test
	public void convert_shouldConvertAValidConceptNameUuidString() throws Exception {
		String conceptNameUuid = "9bc5693a-f558-40c9-8177-145a4b119ca7";
		CodedOrFreeTextValue codedValue = converter.convert(CONCEPT_NAME_PREFIX + conceptNameUuid);
		ConceptName name = codedValue.getCodedNameValue();
		assertEquals(conceptNameUuid, name.getUuid());
	}
	
	/**
	 * @verifies handle free text
	 * @see StringToCodedOrFreeTextValueConverter#convert(String)
	 */
	@Test
	public void convert_shouldHandleFreeText() throws Exception {
		String text = "some random text";
		CodedOrFreeTextValue codedValue = converter.convert(NON_CODED_PREFIX + text);
		assertEquals(text, codedValue.getValue());
	}
	
	/**
	 * @verifies not return null
	 * @see StringToCodedOrFreeTextValueConverter#convert(String)
	 */
	@Test
	public void convert_shouldNotReturnNull() throws Exception {
		String conceptUuid = "some invalid concept uuid";
		CodedOrFreeTextValue codedValue = converter.convert(CONCEPT_PREFIX + conceptUuid);
		assertNotNull(codedValue);
		assertNull(codedValue.getValue());//the wrapped value can be null
	}
}
