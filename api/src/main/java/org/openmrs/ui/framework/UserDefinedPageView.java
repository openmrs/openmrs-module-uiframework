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

import java.util.Date;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.User;

/**
 * An {@link UserDefinedPageView} object encapsulates data a custom user template that is stored in
 * the database, the template scripts are executed on the fly when the page is requested. Only
 * groovy templates are currently supported
 */
public class UserDefinedPageView extends BaseOpenmrsObject {
	
	private Integer pageViewId;
	
	private String name;
	
	//Only groovy templates are supported currently
	private String templateType = WebConstants.DEFAULT_USER_DEFINED_TEMPLATE_TYPE;
	
	private String templateText;
	
	private User creator;
	
	private Date dateCreated;
	
	/**
	 * Default constructor
	 */
	public UserDefinedPageView() {
	}
	
	/**
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
	 * @return the creator
	 */
	public User getCreator() {
		return creator;
	}
	
	/**
	 * @param creator the creator to set
	 */
	public void setCreator(User creator) {
		this.creator = creator;
	}
	
	/**
	 * @return the dateCreated
	 */
	public Date getDateCreated() {
		return dateCreated;
	}
	
	/**
	 * @param dateCreated the dateCreated to set
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
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
