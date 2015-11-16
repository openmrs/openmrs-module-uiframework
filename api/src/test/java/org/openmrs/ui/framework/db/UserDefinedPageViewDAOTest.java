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

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.openmrs.ui.framework.UserDefinedPageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class UserDefinedPageViewDAOTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	@Qualifier("userDefinedPageviewDAO")
	UserDefinedPageViewDAO dao;
	
	/**
	 * @see {@link UserDefinedPageViewDAO#getPageViewByName(String)}
	 */
	@Test
	@Verifies(value = "should get a user defined page by name", method = "getPageViewByName(String)")
	public void getPageViewByName_shouldGetAUserDefinedPageByName() throws Exception {
		executeDataSet("ModuleTestData-userDefinedPageViews.xml");
		UserDefinedPageView userPage = dao.getPageViewByName("welcome");
		Assert.assertNotNull(userPage);
		Assert.assertEquals("bbc05786-9019-11e1-aaa4-00248140a5eb", userPage.getUuid());
	}
}
