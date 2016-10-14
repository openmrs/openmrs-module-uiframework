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
import java.util.List;
import java.util.Map;

import org.openmrs.module.ModuleClassLoader;


/**
 * Standard way for a module to provide resources. Supports "development mode" when a developmentFolder is specified.
 */
public class ModuleResourceProvider implements ResourceProvider {

	private ModuleClassLoader moduleClassLoader;
	private String resourcePrefix = "web/module/resources/";
	private Map<String, String> resourceShortcuts;
	
	private Map<String, String> resourceDirectoryMap;
	
	private List<File> resourceDirectories;
	
	/**
	 * @see org.openmrs.ui.framework.resource.ResourceProvider#getResource(java.lang.String)
	 */
	@Override
	public File getResource(String path) {
		if (resourceShortcuts != null && resourceShortcuts.containsKey(path))
			path = resourceShortcuts.get(path);
		
		if (resourceDirectories != null) {
			for (File resourceDirectory : resourceDirectories) {
	    		// we're in development mode, and we want to dynamically reload resource from this filesystem directory
				File file = new File(resourceDirectory, path);
				if (file.exists()) {
					return file;
				}
			}
			return null;
    	}
    	else {
    		ModuleClassLoader mcl = moduleClassLoader != null ? moduleClassLoader : (ModuleClassLoader) getClass().getClassLoader();
    		
    		// force OpenMRS to expand this resource from the jar, if available.
    		// ideally we'd only look in this module, but this will also look in required modules...
    		mcl.findResource(resourcePrefix + path);
    		
    		File folderForModule = ModuleClassLoader.getLibCacheFolderForModule(mcl.getModule());
    		File resourceFile = new File(folderForModule, resourcePrefix + path);
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

    /**
     * @param resourceShortcuts the resourceShortcuts to set
     */
    public void setResourceShortcuts(Map<String, String> resourceShortcuts) {
	    this.resourceShortcuts = resourceShortcuts;
    }
    
	public Map<String, String> getResourceDirectoryMap() {
		return resourceDirectoryMap;
	}
	
	public void setResourceDirectoryMap(Map<String, String> resourceDirectoryMap) {
		this.resourceDirectoryMap = resourceDirectoryMap;
	}

	public List<File> getResourceDirectories() {
		return resourceDirectories;
	}

	public void setResourceDirectories(List<File> resourceDirectories) {
		this.resourceDirectories = resourceDirectories;
	}
}