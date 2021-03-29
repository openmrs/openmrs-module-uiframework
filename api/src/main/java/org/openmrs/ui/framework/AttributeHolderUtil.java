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
package org.openmrs.ui.framework;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Helper methods for AttributeHolder implementations
 */
public class AttributeHolderUtil {
	
	/**
	 * Evaluates the list of expressions on attributeHolder and returns a list of the ones that
	 * fail. Supported expressions are:
	 * <ul>
	 * <li>simple attributeName, e.g. require("href", "label")</li>
	 * <li>any one of specified attributeNames, e.g. require("href", "label | icon")</li>
	 * </ul>
	 * 
	 * @param attributeHolder
	 * @param expressions
	 * @return
	 * @should handle a and b
	 * @should handle a and either b or c
	 */
	public static List<String> unsatisfiedExpressions(AttributeHolder attributeHolder, String[] expressions) {
		List<String> failed = new ArrayList<String>();
		for (String expression : expressions)
			if (!satisfied(attributeHolder, expression))
				failed.add(expression);
		return failed;
	}
	
	/**
	 * Determines whether or not expression is satisfied by attributeHolder
	 * 
	 * @param attributeHolder
	 * @param expression
	 * @return
	 */
	private static boolean satisfied(AttributeHolder attributeHolder, String expression) {
		if (StringUtils.isBlank(expression))
			return true;
		// for now we only handle attributeNames, and | for or
		String[] options = expression.split("\\|");
		for (String option : options)
			if (attributeHolder.getAttribute(option.trim()) != null)
				return true;
		return false;
	}
	
}
