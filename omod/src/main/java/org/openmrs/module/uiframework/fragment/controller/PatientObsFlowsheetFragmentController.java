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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.OpenmrsConstants.PERSON_TYPE;

public class PatientObsFlowsheetFragmentController {
	
	public void controller(FragmentModel model, @FragmentParam(value = "patientId") Patient patient,
	                       @FragmentParam(value = "conceptIds") ArrayList<Concept> questionConcepts,
	                       @FragmentParam(value = "withinDays", required = false) Integer withinDays,
	                       @FragmentParam(value = "maximumNumber", required = false) Integer maximumNumber) {
		
		List<Person> persons = new ArrayList<Person>();
		persons.add(patient);
		
		List<PERSON_TYPE> personTypes = new ArrayList<PERSON_TYPE>();
		personTypes.add(PERSON_TYPE.PATIENT);
		
		//we want the latest obs to be displayed at the top
		List<String> sortList = new ArrayList<String>();
		sortList.add("obsDatetime");
		
		Date fromDate = null;
		if (withinDays != null) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -withinDays);
			fromDate = startOfDay(cal.getTime());
		}
		
		List<Obs> observations = Context.getObsService().getObservations(persons, null, questionConcepts, null, personTypes,
		    null, sortList, null, null, fromDate, null, false);
		if (maximumNumber != null)
			observations = observations.subList(0, maximumNumber);
		
		Map<Concept, List<Obs>> conceptObsMap = new HashMap<Concept, List<Obs>>();
		for (Obs o : observations) {
			if (!conceptObsMap.containsKey(o.getConcept()))
				conceptObsMap.put(o.getConcept(), new ArrayList<Obs>());
			
			conceptObsMap.get(o.getConcept()).add(o);
		}
		
		model.addAttribute("conceptObsMap", conceptObsMap);
	}
	
	/**
	 * This method was actually added in core as of 1.9
	 */
	private static Date startOfDay(Date date) {
		if (date == null)
			return null;
		
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.getTime();
	}
}
