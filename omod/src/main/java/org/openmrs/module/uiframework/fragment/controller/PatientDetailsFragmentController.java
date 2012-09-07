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
package org.openmrs.module.uiframework.fragment.controller;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.OpenmrsUtil;

public class PatientDetailsFragmentController {
	
	public void controller(FragmentModel model, @FragmentParam(value = "patientId") Patient patient) {
		Encounter firstEncounterFound = null;
		for (Encounter encounter : Context.getEncounterService().getEncountersByPatient(patient)) {
			if (firstEncounterFound == null
			        || OpenmrsUtil.compare(encounter.getEncounterDatetime(), firstEncounterFound.getEncounterDatetime()) < 0) {
				firstEncounterFound = encounter;
			}
		}
		model.addAttribute("patient", patient);
		model.addAttribute("firstEncounter", firstEncounterFound);
	}
}
