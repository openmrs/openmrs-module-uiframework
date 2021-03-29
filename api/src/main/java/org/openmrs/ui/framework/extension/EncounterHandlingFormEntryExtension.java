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
package org.openmrs.ui.framework.extension;

import java.util.Map;

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.ui.framework.AttributeHolder;

/**
 * Subclass of FormEntryExtension for technologies that are able to re-open an existing
 * {@link Encounter} (either in view or edit mode, however they prefer)
 */
public interface EncounterHandlingFormEntryExtension extends FormEntryExtension {
	
	/**
	 * Gets the pages that may be used to open an already-entered encounter. The framework will
	 * append encounterId=x to this page
	 * 
	 * @param context contains person, patient, (TBD)
	 * @return
	 */
	Map<Form, String> getOpenPages(AttributeHolder context);
	
}
