package org.openmrs.ui.framework;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.Patient;
import org.openmrs.Visit;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;

public class UiUtilsTest {
	
	UiUtils ui;
	
	@Before
	public void before() {
		this.ui = new BasicUiUtils();
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
	public void formatDatePretty_shouldReplaceThePreviousDateWithYesterdayText()
	    throws Exception {
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

        String url = "someUrl.page?patientId={{patientId}}&patientId={{patient.id}}&patient={{patient.uuid}}&visitId={{visitId}}&visit.id={{visit.id}}&visit={{visit.uuid}}";
        Assert.assertEquals("someUrl.page?patientId=2&patientId=2&patient=patient_uuid&visitId=3&visit.id=3&visit=visit_uuid", ui.urlBind(url, visit));

    }
}
