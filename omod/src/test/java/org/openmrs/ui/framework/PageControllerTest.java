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

import static org.mockito.Mockito.mock;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.module.uiframework.PageController;
import org.openmrs.ui.framework.page.FileDownload;
import org.openmrs.ui.framework.page.PageFactory;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.ui.framework.session.Session;
import org.openmrs.ui.framework.session.SessionFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ExtendedModelMap;

/**
 *
 */
public class PageControllerTest {
	
	@Test
	public void shouldHandleFileDownloadReturnType() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpSession session = new MockHttpSession();
		Session uiSession = new Session(session);
		
		PageFactory pageFactory = mock(PageFactory.class);
		Mockito.when(pageFactory.handle(Mockito.any(PageRequest.class)))
			.thenThrow(new FileDownload("download.txt", "text/plain", "File contents".getBytes()));
		
		SessionFactory sessionFactory = mock(SessionFactory.class);
		Mockito.when(sessionFactory.getSession(session)).thenReturn(uiSession);
		
		PageController controller = new PageController();
		controller.setPageFactory(pageFactory);
		controller.setSessionFactory(sessionFactory);
		
		controller.handlePage("download", request, response, new ExtendedModelMap(), session);
		
		Assert.assertEquals("text/plain", response.getContentType());
		Assert.assertEquals("File contents", response.getContentAsString());
		Assert.assertEquals("attachment; filename=download.txt", response.getHeader("Content-Disposition"));
	}
	
}
