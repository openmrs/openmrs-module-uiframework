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
package org.openmrs.ui.framework.page;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.ui.framework.UserDefinedPageView;
import org.openmrs.ui.framework.WebConstants;
import org.openmrs.ui.framework.db.UserDefinedPageViewDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * A Groovy {@link PageViewProvider} for user defined pages, it looks up views from the database
 */
public class UserDefinedPageViewProvider implements PageViewProvider {
	
	private static final Log log = LogFactory.getLog(UserDefinedPageViewProvider.class);
	
	// internal data
	SimpleTemplateEngine engine = new SimpleTemplateEngine();
	
	@Autowired
	@Qualifier("userDefinedPageviewDAO")
	UserDefinedPageViewDAO dao;
	
	@Override
	public PageView getView(String name) {
		
		if (log.isDebugEnabled())
			log.debug("Looking up Page view for: '" + name + "'");
		
		try {
			String controllerName = null;
			
			UserDefinedPageView userDefined = dao.getPageViewByName(name);
			if (userDefined == null)
				return null;
			
			//TODO Add support for other templates e.g velocity
			if (!WebConstants.DEFAULT_USER_DEFINED_TEMPLATE_TYPE.equalsIgnoreCase(userDefined.getTemplateType()))
				throw new APIException("Only groovy templates are supported");
			
			String definition = userDefined.getTemplateText();
			// <!--CONTROLLER:{non-whitespace, not greedy}--> allowing for whitespaces between elements
			Pattern p = Pattern.compile("<!--\\s*CONTROLLER\\s*:\\s*(\\S*?)\\s*-->");
			Matcher m = p.matcher(definition);
			if (m.find())
				controllerName = m.group(1);
			Template template = engine.createTemplate(definition);
			
			return new GroovyPageView(template, controllerName);
		}
		catch (Exception ex) {
			throw new RuntimeException("Error creating GroovyPageView", ex);
		}
	}
}
