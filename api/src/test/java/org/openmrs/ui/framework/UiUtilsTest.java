/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.ui.framework;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.page.PageContext;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.ui.framework.page.Redirect;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class UiUtilsTest {
	
	UiUtils ui;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void before() {
		this.ui = new BasicUiUtils();
		PowerMockito.mockStatic(Context.class);
	}
	
	private class TestHandler implements FailedAuthenticationHandler {
		
		static final String ATTRUBUTE_NAME = "test_attrib";
		
		static final String ATTRUBUTE_VALUE = "test_attrib_value";
		
		static final String REDIRECT_URL = "/some_module/some.page";
		
		public String handle(PageRequest pageRequest, List<String> requiredPrivileges) {
			//For testing purposes
			if (pageRequest != null) {
				pageRequest.getRequest().setAttribute(ATTRUBUTE_NAME, ATTRUBUTE_VALUE);
			}
			return getRedirectUrl();
		}
		
		public String getRedirectUrl() {
			return REDIRECT_URL;
		}
	}
	
	/**
	 * @see UiUtils#pageLink(String, String, Map)
	 * @verifies handle page name
	 */
	@Test
	public void pageLink_shouldHandlePageName() throws Exception {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("one", "1");
		params.put("two", "2");
		String link = ui.pageLink("mymoduleid", "myPage", params);
		Assert.assertTrue(link.endsWith("/mymoduleid/myPage.page?one=1&two=2&"));
	}
	
	/**
	 * @see UiUtils#pageLink(String, String, Map)
	 * @verifies handle page name with question mark and query string
	 */
	@Test
	public void pageLink_shouldHandlePageNameWithQuestionMarkAndQueryString() throws Exception {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("one", "1");
		params.put("two", "2");
		String link = ui.pageLink("mymoduleid", "myPage?three=3&four=4", params);
		Assert.assertTrue(link.endsWith("/mymoduleid/myPage.page?one=1&two=2&three=3&four=4"));
	}
	
	/**
	 * @see UiUtils#pageLink(String, String, Map)
	 * @verifies handle page name with anchor
	 */
	@Test
	public void pageLink_shouldHandlePageNameWithAnchor() throws Exception {
		String link = ui.pageLink("mymoduleid", "myPage#mySection", null);
		Assert.assertTrue(link.endsWith("/mymoduleid/myPage.page#mySection"));
	}
	
	/**
	 * @see UiUtils#pageLink(String, String, Map)
	 * @verifies handle page name with anchor and query string
	 */
	@Test
	public void pageLink_shouldHandlePageNameWithAnchorAndQueryString() throws Exception {
		String link = ui.pageLink("mymoduleid", "myPage?param=val#mySection", null);
		Assert.assertTrue(link.endsWith("/mymoduleid/myPage.page?param=val#mySection"));
	}
	
	/**
	 * @verifies replace the current date with today text
	 * @see UiUtils#formatDatePretty(java.util.Date)
	 */
	@Test
	public void formatDatePretty_shouldReplaceTheCurrentDateWithTodayText() throws Exception {
		final Locale locale = Locale.ENGLISH;
		UiUtils ui = Mockito.mock(UiUtils.class);
		Mockito.when(ui.message(eq("uiframework.today"))).thenReturn("Today");
		Mockito.when(ui.formatDatePretty(any(Date.class))).thenCallRealMethod();
		Assert.assertEquals("Today", ui.formatDatePretty(new Date()));
	}
	
	/**
	 * @verifies replace the previous date with yesterday text
	 * @see UiUtils#formatDatePretty(java.util.Date)
	 */
	@Test
	public void formatDatePretty_shouldReplaceThePreviousDateWithYesterdayText() throws Exception {
		final Locale locale = Locale.ENGLISH;
		UiUtils ui = Mockito.mock(UiUtils.class);
		Mockito.when(ui.message(eq("uiframework.yesterday"))).thenReturn("Yesterday");
		Mockito.when(ui.formatDatePretty(any(Date.class))).thenCallRealMethod();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		Assert.assertEquals("Yesterday", ui.formatDatePretty(cal.getTime()));
	}
	
	@Test
	public void urlBind_shouldProperlyBindPatientAndVisit() {
		
		UiUtils ui = Mockito.mock(UiUtils.class);
		Mockito.when(ui.urlBind(anyString(), any(Visit.class))).thenCallRealMethod();
		Mockito.when(ui.urlBind(anyString(), any(Patient.class))).thenCallRealMethod();
		
		Patient patient = new Patient(2);
		patient.setUuid("patient_uuid");
		
		Visit visit = new Visit(3);
		visit.setUuid("visit_uuid");
		visit.setPatient(patient);
		
		String url = "someUrl.page?patientId={{patientId}}&patientId={{patient.id}}&patient={{patient.uuid}}&patientid={{patient.patientId}}&visitId={{visitId}}&visit.id={{visit.id}}&visit={{visit.uuid}}&visit={{visit.visitId}}";
		Assert.assertEquals(
		    "someUrl.page?patientId=2&patientId=2&patient=patient_uuid&patientid=2&visitId=3&visit.id=3&visit=visit_uuid&visit=3",
		    ui.urlBind(url, visit));
		
	}
	
	/**
	 * @see UiUtils#requirePrivileges(List, String, String, String)
	 * @verifies fail if no privilege is specified
	 */
	@Test
	public void requirePrivileges_shouldFailIfNoPrivilegeIsSpecified() throws Exception {
		expectedException.expect(ViewException.class);
		expectedException.expectMessage("At least one privilege is required");
		ui.requirePrivileges(new ArrayList<String>(), null, null, null);
	}
	
	/**
	 * @see UiUtils#requirePrivileges(List,String,String,String)
	 * @verifies fail if the preferred handler throws an exception
	 */
	@Test
	public void requirePrivileges_shouldFailIfThePreferredHandlerThrowsAnException() throws Exception {
		String privilege = "some privilege";
		assertFalse(Context.hasPrivilege(privilege));
		FailedAuthenticationHandler handler = mock(FailedAuthenticationHandler.class);
		doThrow(APIException.class).when(handler).handle(any(PageRequest.class), anyList());
		String beanId = "some bean";
		when(Context.getRegisteredComponent(eq(beanId), eq(FailedAuthenticationHandler.class))).thenReturn(handler);
		expectedException.expect(ViewException.class);
		expectedException.expectMessage("An error occurred while invoking the failed authentication handler");
		ui.requirePrivileges(Collections.singletonList(privilege), null, null, beanId);
	}
	
	/**
	 * @see UiUtils#requirePrivileges(List,String,String,String)
	 * @verifies redirect the user to the specified redirect view
	 */
	@Test
	public void requirePrivileges_shouldRedirectTheUserToTheSpecifiedRedirectView() throws Exception {
		String privilege = "some privilege";
		assertFalse(Context.hasPrivilege(privilege));
		ui.pageContext = mock(PageContext.class);
		String provider = "some_module";
		String page = "some_page";
		String expectedUrl = provider + "/" + page + ".page";
		String actualUrl = null;
		try {
			ui.requirePrivileges(Collections.singletonList(privilege), provider, page, null);
		}
		catch (Redirect ex) {
			actualUrl = ex.getUrl();
		}
		assertEquals(expectedUrl, actualUrl);
	}
	
	/**
	 * @see UiUtils#requirePrivileges(List,String,String,String)
	 * @verifies always pass if the user has the privileges
	 */
	@Test
	public void requirePrivileges_shouldAlwaysPassIfTheUserHasThePrivileges() throws Exception {
		String privilege = "some privilege";
		when(Context.hasPrivilege(eq(privilege))).thenReturn(true);
		ui.requirePrivileges(Collections.singletonList(privilege), null, null, null);
	}
	
	/**
	 * @see UiUtils#requirePrivileges(List,String,String,String)
	 * @verifies use the specified handler
	 */
	@Test
	public void requirePrivileges_shouldUseTheSpecifiedHandler() throws Exception {
		String privilege = "some privilege";
		assertFalse(Context.hasPrivilege(privilege));
		String beanId = "some bean";
		MockHttpServletRequest request = new MockHttpServletRequest();
		ui.pageContext = new PageContext(new PageRequest(null, null, request, null, null));
		when(Context.getRegisteredComponent(eq(beanId), eq(FailedAuthenticationHandler.class)))
		        .thenReturn(new TestHandler());
		String actualUrl = null;
		try {
			ui.requirePrivileges(Collections.singletonList(privilege), null, null, beanId);
		}
		catch (Redirect ex) {
			actualUrl = ex.getUrl();
		}
		assertEquals(TestHandler.REDIRECT_URL, actualUrl);
		assertEquals(TestHandler.ATTRUBUTE_VALUE, request.getAttribute(TestHandler.ATTRUBUTE_NAME));
	}
	
	/**
	 * @see UiUtils#requirePrivileges(List,String,String,String)
	 * @verifies use the default handler if none is registered
	 */
	@Test
	public void requirePrivileges_shouldUseTheDefaultHandlerIfNoneIsRegistered() throws Exception {
		String privilege = "some privilege";
		assertFalse(Context.hasPrivilege(privilege));
		ui.pageContext = mock(PageContext.class);
		DefaultFailedAuthenticationHandler defaultHandler = new DefaultFailedAuthenticationHandler();
		when(Context.getRegisteredComponent(anyString(), eq(FailedAuthenticationHandler.class))).thenReturn(null);
		String actualUrl = null;
		try {
			ui.requirePrivileges(Collections.singletonList(privilege), null, null, "some none existing bean");
		}
		catch (Redirect ex) {
			actualUrl = ex.getUrl();
		}
		assertEquals(defaultHandler.getRedirectUrl(), actualUrl);
	}
	
	/**
	 * @see UiUtils#requirePrivileges(List,String,String,String)
	 * @verifies use the default handler if none is specified
	 */
	@Test
	public void requirePrivileges_shouldUseTheDefaultHandlerIfNoneIsSpecified() throws Exception {
		String privilege = "some privilege";
		assertFalse(Context.hasPrivilege(privilege));
		ui.pageContext = mock(PageContext.class);
		DefaultFailedAuthenticationHandler defaultHandler = new DefaultFailedAuthenticationHandler();
		when(Context.getRegisteredComponent(anyString(), eq(FailedAuthenticationHandler.class))).thenReturn(null);
		String actualUrl = null;
		try {
			ui.requirePrivileges(Collections.singletonList(privilege), null, null, null);
		}
		catch (Redirect ex) {
			actualUrl = ex.getUrl();
		}
		assertEquals(defaultHandler.getRedirectUrl(), actualUrl);
	}
	
	/**
	 * @see UiUtils#requirePrivileges(List,String,String,String)
	 * @verifies use any handler if none is specified and only one has been registered
	 */
	@Test
	public void requirePrivileges_shouldUseAnyHandlerIfNoneIsSpecifiedAndOnlyOneHasBeenRegistered() throws Exception {
		String privilege = "some privilege";
		assertFalse(Context.hasPrivilege(privilege));
		String beanId = "some bean";
		MockHttpServletRequest request = new MockHttpServletRequest();
		ui.pageContext = new PageContext(new PageRequest(null, null, request, null, null));
		FailedAuthenticationHandler handler = new TestHandler();
		when(Context.getRegisteredComponent(anyString(), eq(FailedAuthenticationHandler.class))).thenReturn(null);
		List<FailedAuthenticationHandler> handlers = Collections.singletonList(handler);
		when(Context.getRegisteredComponents(eq(FailedAuthenticationHandler.class))).thenReturn(handlers);
		String actualUrl = null;
		try {
			ui.requirePrivileges(Collections.singletonList(privilege), null, null, beanId);
		}
		catch (Redirect ex) {
			actualUrl = ex.getUrl();
		}
		assertEquals(TestHandler.REDIRECT_URL, actualUrl);
		assertEquals(TestHandler.ATTRUBUTE_VALUE, request.getAttribute(TestHandler.ATTRUBUTE_NAME));
	}
	
	/**
	 * @see UiUtils#requirePrivileges(List,String,String,String)
	 * @verifies use the default handler url if the preferred handler returns none
	 */
	@Test
	public void requirePrivileges_shouldUseTheDefaultHandlerUrlIfThePreferredHandlerReturnsNone() throws Exception {
		String privilege = "some privilege";
		assertFalse(Context.hasPrivilege(privilege));
		String beanId = "some bean";
		MockHttpServletRequest request = new MockHttpServletRequest();
		TestHandler handler = mock(TestHandler.class);
		when(handler.getRedirectUrl()).thenReturn("");
		when(handler.handle(any(PageRequest.class), anyList())).thenCallRealMethod();
		ui.pageContext = new PageContext(new PageRequest(null, null, request, null, null));
		when(Context.getRegisteredComponent(eq(beanId), eq(FailedAuthenticationHandler.class))).thenReturn(handler);
		String actualUrl = null;
		try {
			ui.requirePrivileges(Collections.singletonList(privilege), null, null, beanId);
		}
		catch (Redirect ex) {
			actualUrl = ex.getUrl();
		}
		assertEquals(new DefaultFailedAuthenticationHandler().getRedirectUrl(), actualUrl);
		assertEquals(TestHandler.ATTRUBUTE_VALUE, request.getAttribute(TestHandler.ATTRUBUTE_NAME));
	}
}
