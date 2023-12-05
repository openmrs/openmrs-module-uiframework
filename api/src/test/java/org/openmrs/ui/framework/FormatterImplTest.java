/*
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

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptNumeric;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Role;
import org.openmrs.api.AdministrationService;
import org.openmrs.User;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.context.MessageSource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openmrs.util.TimeZoneUtil.toTimezone;

/**
 *
 */
public class FormatterImplTest {
	
	AdministrationService administrationService;
	
	MockMessageSource messageSource;
	
	FormatterImpl formatter;
	
	@Before
	public void setUp() {
		administrationService = mock(AdministrationService.class);
		messageSource = new MockMessageSource();
		formatter = new MockFormatter(messageSource, administrationService);
	}
	
	@Test
	public void testFormattingAMetadata() throws Exception {
		Locale locale = Locale.ENGLISH;
		
		EncounterType encounterType = new EncounterType();
		encounterType.setName("Emergency Check-In");
		
		String output = formatter.format(encounterType, locale);
		
		assertThat(output, is(encounterType.getName()));
	}
	
	@Test
	public void testFormattingWithOverriddenMetadataName() throws Exception {
		Locale locale = Locale.ENGLISH;
		String displayName = "Emergency Check-In";
		String uuid = "a-fake-uuid";
		
		messageSource.addMessage("ui.i18n.EncounterType.name." + uuid, displayName);
		
		EncounterType encounterType = new EncounterType();
		encounterType.setUuid(uuid);
		
		String output = formatter.format(encounterType, locale);
		
		assertThat(output, is(displayName));
	}
	
	@Test
	public void testFormattingProxiedObjectWithOverriddenMetadataName() throws Exception {
		Locale locale = Locale.ENGLISH;
		String displayName = "Emergency Check-In";
		String uuid = "a-fake-uuid";
		
		messageSource.addMessage("ui.i18n.EncounterType.name." + uuid, displayName);
		
		EncounterType_$$_javassist_26 encounterType = new EncounterType_$$_javassist_26();
		encounterType.setUuid(uuid);
		
		String output = formatter.format(encounterType, locale);
		
		assertThat(output, is(displayName));
	}
	
	@Test
	public void testFormattingProxiedObjectWithCustomFormatter() throws Exception {
		Locale locale = Locale.ENGLISH;
		Formatter customFormatter = mock(Formatter.class);
		formatter.registerClassFormatter(CustomClass.class.getName(), customFormatter);
		
		CustomClass_$$_javassist_26 instance = new CustomClass_$$_javassist_26();
		
		formatter.format(instance, locale);
		
		verify(customFormatter).format(instance, locale);
	}
	
	@Test
	public void testFormattingRole() throws Exception {
		Locale locale = Locale.ENGLISH;
		
		Role role = new Role();
		role.setRole("Admin");
		
		String output = formatter.format(role, locale);
		assertThat(output, is(role.getRole()));
	}
	
	@Test
	public void testFormattingRoleWithOverriddenMetadataName() throws Exception {
		Locale locale = Locale.ENGLISH;
		
		String displayName = "Administrator";
		String uuid = "a-fake-uuid";
		
		messageSource.addMessage("ui.i18n.Role.name." + uuid, displayName);
		
		Role role = new Role();
		role.setRole("Admin");
		role.setUuid(uuid);
		
		String output = formatter.format(role, locale);
		assertThat(output, is(displayName));
	}
	
	@Test
	public void testFormattingADateWithNoTimeWithClientTimezone() throws Exception {
		when(administrationService.getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATETIME_FORMAT))
		        .thenReturn("dd.MMM.yyyy");
		when(administrationService.getGlobalProperty(UiFrameworkConstants.GP_TIMEZONE_CONVERSIONS)).thenReturn("true");
		when(administrationService.getGlobalProperty(UiFrameworkConstants.UP_CLIENT_TIMEZONE)).thenReturn("clientTimezone");
		Locale locale = Locale.ENGLISH;
		Date date = new DateTime("2011-08-16T00:00:00Z").toDate();
		String output = formatter.format(date, locale);
		assertThat(output, is("16.Aug.2011"));
	}
	
	@Test
	public void testFormattingTimeWithClientTimezone() throws Exception {
		Date date = new DateTime("2011-08-16T07:22:05Z").toDate();
		String output = toTimezone(date, "HH:mm:ss", "Pacific/Kiritimati");
		assertThat(output, is("21:22:05"));
	}
	
	@Test
	public void testFormattingADateWithNoTime() throws Exception {
		when(administrationService.getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATE_FORMAT, "dd.MMM.yyyy"))
		        .thenReturn("dd.MMM.yyyy");
		when(administrationService.getGlobalProperty(UiFrameworkConstants.GP_TIMEZONE_CONVERSIONS)).thenReturn("false");
		Locale locale = Locale.ENGLISH;
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2003-02-01");
		
		String output = formatter.format(date, locale);
		assertThat(output, is("01.Feb.2003"));
	}
	
	@Test
	public void testFormattingADateWithATime() throws Exception {
		when(administrationService.getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATETIME_FORMAT,
		    "dd.MMM.yyyy, HH:mm:ss")).thenReturn("dd.MMM.yyyy, HH:mm:ss");
		when(administrationService.getGlobalProperty(UiFrameworkConstants.GP_TIMEZONE_CONVERSIONS)).thenReturn("false");
		Locale locale = Locale.ENGLISH;
		Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2003-02-01 14:25:07.123");
		
		String output = formatter.format(date, locale);
		assertThat(output, is("01.Feb.2003, 14:25:07"));
	}
	
	@Test
	public void testFormattingConceptNumeric() throws Exception {
		
		Locale locale = Locale.ENGLISH;
		
		ConceptNumeric conceptNumeric = new ConceptNumeric();
		conceptNumeric.setUnits("mg");
		ConceptDatatype numericDatatype = new ConceptDatatype();
		numericDatatype.setHl7Abbreviation("NM");
		conceptNumeric.setDatatype(numericDatatype);
		conceptNumeric.setAllowDecimal(true);
		
		Obs numericObs = new Obs();
		numericObs.setConcept(conceptNumeric);
		numericObs.setValueNumeric(1.0);
		
		String output = formatter.format(numericObs, locale);
		assertThat(output, is("1.0 mg"));
		
	}
	
	@Test
	public void testFormattingConceptNumeric_shouldNotFailIfNoUnits() throws Exception {
		
		Locale locale = Locale.ENGLISH;
		
		ConceptNumeric conceptNumeric = new ConceptNumeric();
		ConceptDatatype numericDatatype = new ConceptDatatype();
		numericDatatype.setHl7Abbreviation("NM");
		conceptNumeric.setDatatype(numericDatatype);
		conceptNumeric.setAllowDecimal(true);
		
		Obs numericObs = new Obs();
		numericObs.setConcept(conceptNumeric);
		numericObs.setValueNumeric(1.0);
		
		String output = formatter.format(numericObs, locale);
		assertThat(output, is("1.0"));
		
	}
	
	@Test
	public void testFormattingClass() throws Exception {
		String output = formatter.format(Date.class, Locale.ENGLISH);
		assertThat(output, is("java.util.Date"));
	}
	
	@Test
	public void testFormattingWholeNumber() throws Exception {
		String output = formatter.format(100d, Locale.ENGLISH);
		assertThat(output, is("100"));
	}
	
	@Test
	public void testFormattingDecimalNumber() throws Exception {
		String output = formatter.format(0.5, Locale.ENGLISH);
		assertThat(output, is("0.5"));
	}
	
	private class EncounterType_$$_javassist_26 extends EncounterType {
		
	}
	
	private class CustomClass {
		
	}
	
	private class CustomClass_$$_javassist_26 extends CustomClass {
		
	}
	
	private class MockFormatter extends FormatterImpl {
		
		public MockFormatter(MessageSource messageSource, AdministrationService administrationService) {
			super(messageSource, administrationService);
		}
		
		@Override
		protected User getAuthenticatedUser() {
			User mockedUser = new User();
			mockedUser.setUserProperty(administrationService.getGlobalProperty(UiFrameworkConstants.UP_CLIENT_TIMEZONE),
			    "Pacific/Kiritimati");
			return mockedUser;
		}
	}
}
