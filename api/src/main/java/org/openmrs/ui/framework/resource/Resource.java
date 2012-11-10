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

import org.openmrs.util.OpenmrsUtil;


/**
 * A resource that a page or fragment wants to include
 * @see ResourceFactory
 */
public class Resource {
	
	private String providerName;
	private String resourcePath;

    public Resource() {
        this(null, null);
    }

	/**
	 * Indicates you want the framework to search across all {@link ResourceProvider}s for one with the given path
	 * @param resourcePath
	 */
	public Resource(String resourcePath) {
		this(null, resourcePath);
	}
	
	public Resource(String providerName, String resourcePath) {
		this.providerName = providerName == null ? "*" : providerName;
		this.resourcePath = resourcePath;
	}

    /**
     * @return the providerName
     */
    public String getProviderName() {
    	return providerName;
    }
	
    /**
     * @param providerName the providerName to set
     */
    public void setProviderName(String providerName) {
    	this.providerName = providerName;
    }
	
    /**
     * @return the resourcePath
     */
    public String getResourcePath() {
    	return resourcePath;
    }
	
    /**
     * @param resourcePath the resourcePath to set
     */
    public void setResourcePath(String resourcePath) {
    	this.resourcePath = resourcePath;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
    	if (obj == null || !(obj instanceof Resource)) {
    		return false;
    	}
    	Resource other = (Resource) obj;
    	return OpenmrsUtil.nullSafeEquals(providerName, other.providerName) && OpenmrsUtil.nullSafeEquals(resourcePath, other.resourcePath); 
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (providerName + ":" + resourcePath).hashCode();
    }
}
