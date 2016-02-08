/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.uiframework;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.ui.framework.DefaultFailedAuthenticationHandler;
import org.openmrs.ui.framework.page.PageFactory;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.ui.framework.session.Session;
import org.openmrs.ui.framework.session.SessionFactory;
import org.springframework.ui.Model;

public class PageControllerTest {
	
	/**
	 * @see PageController#handlePath(String, HttpServletRequest, HttpServletResponse, Model,
	 *      HttpSession)
	 * @verifies return the login url if not authenticated
	 */
	@Test
	public void handlePath_shouldReturnTheLoginUrlIfNotAuthenticated() throws Exception {
		Session session = Mockito.mock(Session.class);
		SessionFactory sessionFactory = Mockito.mock(SessionFactory.class);
		Mockito.when(sessionFactory.getSession(Mockito.any(HttpSession.class))).thenReturn(session);
		PageController controller = new PageController();
		controller.setSessionFactory(sessionFactory);
		PageFactory pageFactory = Mockito.mock(PageFactory.class);
		Mockito.when(pageFactory.handle(Mockito.any(PageRequest.class))).thenThrow(ContextAuthenticationException.class);
		controller.setPageFactory(pageFactory);
		
		String url = controller.handlePath("someProvider/somePage", null, null, null, null);
		Assert.assertEquals("redirect:" + new DefaultFailedAuthenticationHandler().getRedirectUrl(), url);
	}
}
