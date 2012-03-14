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
package org.openmrs.module.uiframework.page.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.ui.framework.page.PageAction;

/**
 * This page displays a single fragment, specified via request parameters
 */
public class FragmentPageController {
	
	public void controller(HttpServletRequest request, Map<String, Object> model) throws PageAction {
		String fragment = request.getParameter("fragment");
		if (fragment == null)
			throw new RuntimeException("Fragment page requires a 'fragment' parameter");
		model.put("fragment", fragment);
		Map<String, String> configuration = new HashMap<String, String>();
		for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
			String name = e.nextElement();
			if ("fragment".equals(name))
				continue;
			// TODO deal with multi-valued parameters
			configuration.put(name, request.getParameter(name));
		}
		model.put("configuration", configuration);
	}
	
}
