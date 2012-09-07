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

import java.util.Collections;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.activelist.ActiveListItem;
import org.openmrs.activelist.Problem;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.ActiveListItemByStartDateComparator;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

public class PatientProblemsFragmentController {
	
	public void controller(FragmentModel model, @FragmentParam(value = "patientId") Patient patient) {
		//TODO should be able to use problem added and problem resolved GP
		//but for demo purposes we are using the active list service
		List<ActiveListItem> problems = Context.getActiveListService().getActiveListItems(patient, Problem.ACTIVE_LIST_TYPE);
		
		//sort the problems by date started descending	
		Collections.sort(problems, Collections.reverseOrder(new ActiveListItemByStartDateComparator()));
		model.addAttribute("problems", problems);
	}
}
