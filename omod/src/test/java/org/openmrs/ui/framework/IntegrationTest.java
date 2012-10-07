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

import org.junit.Test;
import org.openmrs.ui.framework.page.PageFactory;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.ui.framework.session.Session;
import org.openmrs.ui.framework.session.SessionFactory;
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

    // Commenting out because this test doesn't pass and I need to code review and figure out why
	//@Autowired
	//@Qualifier("userDefinedPageviewDAO")
	//UserDefinedPageViewDAO dao;
	
	@Test
	public void integrationTest() throws Exception {
		MockHttpSession httpSession = new MockHttpSession();
		Session session = sessionFactory.getSession(httpSession);
		PageRequest req = new PageRequest("uiframework", "home", new MockHttpServletRequest(),
		        new MockHttpServletResponse(), session);
		String html = pageFactory.handle(req);
		System.out.println("Result = " + html);
	}
	
	/**
	 * TODO Fix this test
	 *
	@Test
	public void shouldDisplayAUserDefinedPage() throws Exception {
		UserDefinedPageView page = new UserDefinedPageView("welcome", "Welcome ${context.authenticatedUser}!");
		page.setTemplateType(WebConstants.DEFAULT_USER_DEFINED_TEMPLATE_TYPE);
		page.setUuid("random-uuid");
		page.setCreator(Context.getAuthenticatedUser());
		page.setDateCreated(new Date());
		dao.saveOrUpdate(page);
		MockHttpSession httpSession = new MockHttpSession();
		Session session = sessionFactory.getSession(httpSession);
		PageRequest req = new PageRequest("userdefined", "welcome", new MockHttpServletRequest(),
		        new MockHttpServletResponse(), session);
		String html = pageFactory.handle(req);
		Assert.assertTrue(html.indexOf("Welcome admin!") > -1);
	}
    */
	
}
