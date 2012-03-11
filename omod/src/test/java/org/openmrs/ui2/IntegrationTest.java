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
package org.openmrs.ui2;

import org.junit.Test;
import org.openmrs.ui2.core.page.PageFactory;
import org.openmrs.ui2.core.page.PageRequest;
import org.openmrs.ui2.core.session.Session;
import org.openmrs.ui2.core.session.SessionFactory;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;


/**
 *
 */
public class IntegrationTest extends BaseModuleWebContextSensitiveTest {
	
	@Autowired
	PageFactory pageFactory;
	
	@Autowired
	SessionFactory sessionFactory;
	
	@Test
	public void integrationTest() throws Exception {
		MockHttpSession httpSession = new MockHttpSession();
		Session session = sessionFactory.getSession(httpSession);
		PageRequest req = new PageRequest("home", new MockHttpServletRequest(), new MockHttpServletResponse(), session);
		String html = pageFactory.handle(req);
		System.out.println("Result = " + html);
	}
	
}
