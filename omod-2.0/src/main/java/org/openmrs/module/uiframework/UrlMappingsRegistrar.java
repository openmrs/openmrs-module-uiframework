/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.uiframework;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

/**
 * Registers servlet mappings for http requests that end with .page or .action
 */
@Component
public class UrlMappingsRegistrar implements ServletContextAware {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private static boolean skipAddMappings = false;
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		log.debug("Registering URL mappings");
		
		//Dynamic servlet registration can only be done once i.e. at application startup
		//In theory it means this module can only be installed at application startup
		if (!skipAddMappings) {
			servletContext.getServletRegistration("openmrs").addMapping("*.page", "*.action");
			servletContext.getFilterRegistration("compressionFilter").addMappingForUrlPatterns(null, true, "*.page",
			    "*.action");
			skipAddMappings = true;
		}
	}
	
}
