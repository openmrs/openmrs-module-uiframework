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
import java.net.URL;
import java.util.Map;

import org.openmrs.module.ModuleClassLoader;
import org.openmrs.ui.framework.UiFrameworkException;


/**
 * Standard way for a module to provide resources. Supports "development mode" when a developmentFolder is specified.
 */
public class ModuleResourceProvider implements ResourceProvider {
	
	private File developmentFolder;
	private ModuleClassLoader moduleClassLoader;
	private String resourcePrefix = "web/module/resources/";
	private Map<String, String> resourceShortcuts;
	
	/**
	 * @see org.openmrs.ui.framework.resource.ResourceProvider#getResource(java.lang.String)
	 */
	@Override
	public File getResource(String path) {
		if (resourceShortcuts != null && resourceShortcuts.containsKey(path))
			path = resourceShortcuts.get(path);
		
		if (developmentFolder != null) {
    		// we're in development mode, and we want to dynamically reload resource from this filesystem directory
			File file = new File(developmentFolder, path);
			return file.exists() ? file : null;
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
     * @return the developmentFolder
     */
    public File getDevelopmentFolder() {
    	return developmentFolder;
    }

    /**
     * @param developmentFolder the developmentFolder to set
     */
    public void setDevelopmentFolder(File developmentFolder) {
    	this.developmentFolder = developmentFolder;
    }

    /**
     * @param resourceShortcuts the resourceShortcuts to set
     */
    public void setResourceShortcuts(Map<String, String> resourceShortcuts) {
	    this.resourceShortcuts = resourceShortcuts;
    }
    
}
