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
package org.openmrs.ui.framework.db;

import org.openmrs.ui.framework.UserDefinedPageView;

public interface UserDefinedPageViewDAO extends SingleClassDAO<UserDefinedPageView> {
	
	/**
	 * Gets a {@link UserDefinedPageView} matching the specified name
	 * 
	 * @param pageName the name of the page view to get
	 * @return a user defined page view
	 * @should get a user defined page by name
	 */
	public UserDefinedPageView getPageViewByName(String pageName);
}
