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
package org.openmrs.ui.framework.resource;

import java.io.File;

import org.openmrs.util.OpenmrsUtil;

/**
 * Standard way for resources to be provided via external files deployed to a server
 * If the specified path is absolute, load it as is from the specified path
 * If the specified path is relative, treat this as relative to the OpenMRS Application Data Directory
 */
public class FileResourceProvider implements ResourceProvider {

	public static final String RESOURCE_KEY = "file";

	/**
	 * @see ResourceProvider#getResource(String)
	 */
	@Override
	public File getResource(String path) {
		File resourceFile = null;
		if (path != null) {
			if (path.startsWith("/")) {
				resourceFile = new File(path);
			}
			else {
				resourceFile = new File(OpenmrsUtil.getApplicationDataDirectory(), path);
			}
		}
		return resourceFile.exists() ? resourceFile : null;
	}
}
