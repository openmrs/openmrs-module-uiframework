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
package org.openmrs.module.uiframework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.Activator;


/**
 * Activator for the UI Framework module
 */
@SuppressWarnings("deprecation")
public class UiFrameworkActivator implements Activator {

	private Log log = LogFactory.getLog(getClass());
	
	/**
     * @see org.openmrs.module.Activator#shutdown()
     */
    @Override
    public void shutdown() {
	    log.info("Stopping UI Framework module");
    }

	/**
     * @see org.openmrs.module.Activator#startup()
     */
    @Override
    public void startup() {
    	log.info("Starting UI Framework module");
    }
	
	
	
}
