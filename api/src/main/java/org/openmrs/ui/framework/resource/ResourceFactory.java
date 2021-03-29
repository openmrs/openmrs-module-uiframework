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

import org.apache.commons.io.FilenameUtils;
import org.openmrs.ui.framework.UiFrameworkUtil;
import org.openmrs.util.OpenmrsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registry for {@link ResourceProvider}s, and provides methods for getting resources. Since (as of
 * 1.9) there's no way to wire spring beans to a module servlet, the first instance of this bean
 * that is instantiated at module startup is statically-accessible.
 */
public class ResourceFactory {
	
	private static ResourceFactory instance;
	
	private static Map<String, ResourceProvider> resourceProviders;
	
	private static Set<String> resourceProvidersInDevelopmentMode = new HashSet<String>();
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	public ResourceFactory() {
		// hack to allow our module servlet to access this
		if (instance == null)
			instance = this;
	}
	
	/**
	 * This method is a hack to allow our module servlet to access this. Don't count on it.
	 */
	public static ResourceFactory getInstance() {
		if (instance == null)
			new ResourceFactory();
		return instance;
	}
	
	/**
	 * @param providerName if null, look in all providers (if multiple providers have the resource,
	 *            an arbitrary one is returned)
	 * @param resourcePath
	 * @return the requested resource, from the requested provider, or null if not found
	 */
	public File getResource(String providerName, String resourcePath) {
		if (resourcePath == null) {
			return null;
		}
		
		if (providerName == null) {
			for (ResourceProvider provider : resourceProviders.values()) {
				File ret = provider.getResource(resourcePath);
				if (ret != null) {
					return ret;
				}
			}
			// not found in any registered provider
			return null;
		} else {
			ResourceProvider provider = resourceProviders.get(providerName);
			return provider.getResource(resourcePath);
		}
	}
	
	/**
	 * @param resourcePath
	 * @return the resource with the given path, from any provider that has it
	 */
	public File getResource(String resourcePath) {
		return getResource(null, resourcePath);
	}
	
	/**
	 * @see #getResource(String, String)
	 * @param providerName
	 * @param resourcePath
	 * @return the resource's contents, as a String
	 * @throws IOException
	 */
	public String getResourceAsString(String providerName, String resourcePath) throws IOException {
		File file = getResource(providerName, resourcePath);
		if (file == null) {
			return null;
		}
		return OpenmrsUtil.getFileAsString(file);
	}
	
	/**
	 * @return the resourceProviders
	 */
	public Map<String, ResourceProvider> getResourceProviders() {
		return resourceProviders;
	}
	
	/**
	 * @param resourceProviders the resourceProviders to set
	 */
	public void setResourceProviders(Map<String, ResourceProvider> resourceProviders) {
		ResourceFactory.resourceProviders = resourceProviders;
	}
	
	public boolean isResourceProviderInDevelopmentMode(String providerName) {
		return resourceProvidersInDevelopmentMode.contains(providerName);
	}
	
	/**
	 * Adds the given resource providers to the existing ones. (I.e. this is not a proper setter.)
	 * 
	 * @param additional
	 * @see #addResourceProvider(String, ResourceProvider)
	 * @param additional
	 */
	public void setAdditionalResourceProviders(Map<String, ResourceProvider> additional) {
		for (Map.Entry<String, ResourceProvider> e : additional.entrySet()) {
			addResourceProvider(e.getKey(), e.getValue());
		}
	}
	
	/**
	 * Registers a Resource Provider.
	 * 
	 * @see UiFrameworkUtil#checkAndSetDevelopmentModeForProvider(String, Object)
	 */
	public void addResourceProvider(String key, ResourceProvider provider) {
		if (resourceProviders == null) {
			resourceProviders = new LinkedHashMap<String, ResourceProvider>();
		}
		
		boolean addedInDevMode = UiFrameworkUtil.checkAndSetDevelopmentModeForProvider(key, provider);
		if (addedInDevMode) {
			resourceProvidersInDevelopmentMode.add(key);
		}
		
		resourceProviders.put(key, provider);
	}
	
}
