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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.PatientService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 */
public class StringToPatientConverterTest {
	
	private StringToPatientConverter converter;
	
	private PatientService patientService;
	
	@Before
	public void setUp() throws Exception {
		patientService = mock(PatientService.class);
		converter = new StringToPatientConverter();
		converter.setPatientService(patientService);
	}
	
	@Test
	public void testConvertByPatientId() throws Exception {
		converter.convert("1234");
		verify(patientService).getPatient(1234);
	}
	
	@Test
	public void testConvertByPatientUuid() throws Exception {
		String uuid = "8d793bee-c2cc-11de-8d13-0010c6dffd0f";
		converter.convert(uuid);
		verify(patientService).getPatientByUuid(uuid);
	}
}
