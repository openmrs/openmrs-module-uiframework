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
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.openmrs.module.ModuleClassLoader;
import org.openmrs.util.OpenmrsClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standard way for a module to provide resources. Supports "development mode" when a
 * developmentFolder is specified.
 */
public class ModuleResourceProvider implements ResourceProvider {
	
	private static final Logger log = LoggerFactory.getLogger(ModuleResourceProvider.class);
	
	private List<File> developmentFolders;
	
	private List<String> developmentFolderNames;
	
	private ModuleClassLoader moduleClassLoader;
	
	private String resourcePrefix = "web/module/resources/";
	
	private Map<String, String> resourceShortcuts;
	
	/**
	 * @see org.openmrs.ui.framework.resource.ResourceProvider#getResource(java.lang.String)
	 */
	@Override
	public File getResource(String path) {
		if (resourceShortcuts != null && resourceShortcuts.containsKey(path)) {
			path = resourceShortcuts.get(path);
		}
		
		// module resources should be fetched by a path relative to the module, so a request for a file by absolute path
		// is an error
		if (path == null || new File(path).isAbsolute()) {
			return null;
		}
		
		if (developmentFolders != null) {
			for (File developmentFolder : developmentFolders) {
				// we're in development mode, and we want to dynamically reload resource from this filesystem directory
				File file = new File(developmentFolder, path);
				if (file.exists()) {
					return file;
				}
			}
			return null;
		} else {
			ModuleClassLoader mcl = moduleClassLoader != null ? moduleClassLoader : (ModuleClassLoader) getClass()
			        .getClassLoader();
			
			// force OpenMRS to expand this resource from the jar, if available.
			// ideally we'd only look in this module, but this will also look in required modules...
			mcl.findResource(resourcePrefix + path);
			
			File folderForModule = ModuleClassLoader.getLibCacheFolderForModule(mcl.getModule());
			File resourceFile = new File(folderForModule, resourcePrefix + path);
			
			// guard against loading files outside of the lib cache folder
			try {
				if (!resourceFile.getCanonicalPath().startsWith(OpenmrsClassLoader.getLibCacheFolder().getCanonicalPath())) {
					log.warn("Attempted to load invalid resource: {}", resourceFile);
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
	
	/**
	 * @return the moduleClassLoader
	 */
	public ModuleClassLoader getModuleClassLoader() {
		return moduleClassLoader;
	}
	
	/**
	 * @param moduleClassLoader the moduleClassLoader to set
	 */
	public void setModuleClassLoader(ModuleClassLoader moduleClassLoader) {
		this.moduleClassLoader = moduleClassLoader;
	}
	
	public List<File> getDevelopmentFolders() {
		return developmentFolders;
	}
	
	public void setDevelopmentFolders(List<File> developmentFolders) {
		this.developmentFolders = developmentFolders;
	}
	
	public List<String> getDevelopmentFolderNames() {
		return developmentFolderNames;
	}
	
	public void setDevelopmentFolderNames(List<String> developmentFolderNames) {
		this.developmentFolderNames = developmentFolderNames;
	}
	
	/**
	 * @param resourceShortcuts the resourceShortcuts to set
	 */
	public void setResourceShortcuts(Map<String, String> resourceShortcuts) {
		this.resourceShortcuts = resourceShortcuts;
	}
	
}
