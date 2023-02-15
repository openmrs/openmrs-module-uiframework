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

import org.openmrs.Form;
import org.openmrs.ui.framework.AttributeHolder;

/**
 *
 */
public interface FormEntryExtension extends Extension {
	
	/**
	 * Gets the pages that may be used to enter specific forms (i.e. enter a new form from scratch)
	 * 
	 * @param context contains person, patient, (TBD)
	 * @return
	 */
	Map<Form, String> getEnterPages(AttributeHolder context);
	
}
