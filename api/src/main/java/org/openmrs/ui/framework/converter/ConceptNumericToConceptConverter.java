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

import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Workaround for problem where the Spring conversion service reloads ConceptNumeric instances as
 * plain Concept instances.
 */
@Component
public class ConceptNumericToConceptConverter implements Converter<ConceptNumeric, Concept> {
	
	/**
	 * @see org.springframework.core.convert.converter.Converter#convert(Object)
	 */
	@Override
	public Concept convert(ConceptNumeric c) {
		return c;
	}
}
