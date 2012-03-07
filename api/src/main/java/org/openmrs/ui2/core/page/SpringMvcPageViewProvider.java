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
package org.openmrs.ui2.core.page;

import java.util.HashMap;
import java.util.Map;

/**
 * Exposes urls mapped via Spring MVC as pages. Configure this by setting pages to a map from the
 * page name you want to expose (e.g. "xyz") to the Spring request mapping that should satisfy it
 * (e.g. "/module/xyz/xyz.form").
 * Any part of the content that is outside of the comments <!-- START 2.x PAGE CONTENT --> and
 * <!-- END 2.x PAGE CONTENT --> will be trimmed before the page is returned. (Both these are optional.)
 */
public class SpringMvcPageViewProvider implements PageViewProvider {
	
	public final static String TRIM_START = "<!-- START 2.x PAGE CONTENT -->";
	
	public final static String TRIM_END = "<!-- END 2.x PAGE CONTENT -->";
	
	// page name -> spring request mapping
	private Map<String, String> pages;
	
	// spring request mapping -> page name
	private transient Map<String, String> reversePageMap;
	
	/**
	 * @return the pages
	 */
	public Map<String, String> getPages() {
		return pages;
	}
	
	/**
	 * @param pages the pages to set
	 */
	public void setPages(Map<String, String> pages) {
		this.pages = pages;
		// also store the reverse of this map
		reversePageMap = new HashMap<String, String>();
		for (Map.Entry<String, String> e : pages.entrySet())
			reversePageMap.put(e.getValue(), e.getKey());
	}
	
	/**
	 * @see org.openmrs.ui2.core.page.PageViewProvider#getView(java.lang.String)
	 */
	@Override
	public PageView getView(String name) {
		String springRequestMapping = pages.get(name);
		if (springRequestMapping != null)
			return new SpringMvcView(springRequestMapping, TRIM_START, TRIM_END, reversePageMap);
		else
			return null;
	}
	
}
