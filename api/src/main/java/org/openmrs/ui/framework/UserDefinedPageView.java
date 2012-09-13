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
package org.openmrs.ui.framework;

import org.openmrs.BaseOpenmrsObject;

/**
 * An {@link UserDefinedPageView} object represents a user defined page view
 */
public class UserDefinedPageView extends BaseOpenmrsObject {
	
	private Integer pageViewId;
	
	private String name;
	
	//Only groovy templates are supported currently
	private String templateType = WebConstants.DEFAULT_USER_DEFINED_TEMPLATE_TYPE;
	
	private String templateText;
	
	/**
	 * Default constructor
	 */
	public UserDefinedPageView() {
	}
	
	/**
	 * Convenience constructor that takes in a name and definition
	 * 
	 * @param name
	 * @param definition
	 */
	public UserDefinedPageView(String name, String templateText) {
		this.name = name;
		this.templateText = templateText;
	}
	
	/**
	 * @return the id
	 */
	public Integer getPageViewId() {
		return pageViewId;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setPageViewId(Integer id) {
		this.pageViewId = id;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the templateType
	 */
	public String getTemplateType() {
		return templateType;
	}
	
	/**
	 * @param templateType the templateType to set
	 */
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}
	
	/**
	 * @return the templateText
	 */
	public String getTemplateText() {
		return templateText;
	}
	
	/**
	 * @param templateText the templateText to set
	 */
	public void setTemplateText(String templateText) {
		this.templateText = templateText;
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return getPageViewId();
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	@Override
	public void setId(Integer id) {
		setPageViewId(id);
	}
	
}
