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
package org.openmrs.ui.framework.fragment;

import java.util.Map;

import org.openmrs.ui.framework.page.SpringMvcView;

/**
 * Exposes urls mapped via Spring MVC as fragments. Configure this by setting fragments to a map
 * from the fragment name you want to expose (e.g. "xyz") to the Spring request mapping that should
 * satisfy it, with query params (e.g. "/module/xyz/xyz.form?inPopup=true"). Note: when specifying
 * an url with multiple parameters in a Spring xml file, you need to replace "&" with "&amp;", for
 * example:
 * "/module/htmlformentry/htmlFormEntry.form?pageFragment=true&amp;mode=VIEW&amp;which=last" Any
 * part of the content that is outside of the comments <!-- START 2.x FRAGMENT CONTENT --> and <!--
 * END 2.x FRAGMENT CONTENT --> will be trimmed before the fragment is returned. (Both these are
 * optional.)
 */
public class SpringMvcPageAsFragmentViewProvider implements FragmentViewProvider {
	
	public final static String TRIM_START = "<!-- START 2.x FRAGMENT CONTENT -->";
	
	public final static String TRIM_END = "<!-- END 2.x FRAGMENT CONTENT -->";
	
	// fragment name -> spring request mapping
	private Map<String, String> fragments;
	
	/**
	 * @return the fragments
	 */
	public Map<String, String> getFragments() {
		return fragments;
	}
	
	/**
	 * @param fragments the fragments to set
	 */
	public void setFragments(Map<String, String> fragments) {
		this.fragments = fragments;
	}
	
	/**
	 * @see org.openmrs.ui.framework.fragment.FragmentViewProvider#getView(java.lang.String)
	 */
	@Override
	public FragmentView getView(String name) {
		String springRequestMapping = fragments.get(name);
		if (springRequestMapping != null)
			return new SpringMvcView(springRequestMapping, TRIM_START, TRIM_END, null);
		else
			return null;
	}
	
}
