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
 * A resource that a page or fragment wants to include.
 * Each resource has an optional priority. (A higher number means it should be included first, and the default is 0.)
 * @see ResourceFactory
 */
public class Resource implements Comparable<Resource> {

    public final static String CATEGORY_CSS = "css";
    public final static String CATEGORY_JS = "js";

    private String category;
	private String providerName;
	private String resourcePath;
    private Integer priority = 0;

    public Resource() {
        this(null, null, null, null);
    }

	/**
	 * Indicates you want the framework to search across all {@link ResourceProvider}s for one with the given path
	 * @param resourcePath
	 */
	public Resource(String resourcePath) {
		this(null, null, resourcePath, null);
	}
	
	public Resource(String providerName, String resourcePath) {
        this(null, providerName, resourcePath, null);
	}

    public Resource(String category, String providerName, String resourcePath, Integer priority) {
        this.category = category;
        this.providerName = providerName == null ? "*" : providerName;
        this.resourcePath = resourcePath;
        this.priority = priority != null ? priority : 0;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
    	if (other == null || !(other instanceof Resource)) {
    		return false;
    	}
        return compareTo((Resource) other) == 0;
    }

    @Override
    public int compareTo(Resource other) {
        int temp = OpenmrsUtil.compareWithNullAsLowest(other.priority, priority);
        if (temp == 0) {
            temp = OpenmrsUtil.compareWithNullAsLowest(resourcePath, other.resourcePath);
        }
        if (temp == 0) {
            temp = OpenmrsUtil.compareWithNullAsLowest(providerName, other.providerName);
        }
        if (temp == 0) {
            temp = OpenmrsUtil.compareWithNullAsLowest(category, other.category);
        }
        return temp;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (providerName + ":" + resourcePath).hashCode();
    }
}
