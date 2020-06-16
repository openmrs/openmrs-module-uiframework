/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.ui.framework.resource;

import org.openmrs.api.APIException;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Standard way for resources to be provided via external files deployed to a server
 * If the specified path is absolute, load it as is from the specified path
 * If the specified path is relative, treat this as relative to the OpenMRS Application Data Directory
 */
public class ConfigurationResourceProvider implements ResourceProvider {

	public static final String RESOURCE_KEY = "file";

	private static final Logger log = LoggerFactory.getLogger(ConfigurationResourceProvider.class);

	/**
	 * @see ResourceProvider#getResource(String)
	 */
	@Override
	public File getResource(String path) {
		if (path == null) {
			return null;
		}

		final String configurationDirectory;
		try {
			configurationDirectory = OpenmrsUtil.getDirectoryInApplicationDataDirectory("configuration")
					.getCanonicalPath();
		}
		catch (IOException e) {
			throw new APIException("Could not determine canonical path for configuration directory", e);
		}

		final File resourceFile;
		final File file = new File(path);

		if (file.isAbsolute()) {
			resourceFile = file;
		} else {
			String firstDir = null;
			int idx = path.indexOf(File.separatorChar, 1);
			if (idx > 0) {
				int start = path.charAt(0) == File.separatorChar ? 1 : 0;
				firstDir = path.substring(start, idx);
			}

			if (!"configuration".equals(firstDir)) {
				resourceFile = new File(configurationDirectory, path);
			} else {
				resourceFile = new File(OpenmrsUtil.getApplicationDataDirectory(), path);
			}
		}

		// guard against loading files outside of the configuration directory
		try {
			if (!resourceFile.getCanonicalPath().startsWith(configurationDirectory)) {
				log.warn("Attempted to load invalid resource: {}", file);
				return null;
			}
		}
		catch (IOException e) {
			log.error("Error occurred while trying to load file: {}", path, e);
			return null;
		}

		return resourceFile.exists() ? resourceFile : null;
	}
}
