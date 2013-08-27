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
package org.openmrs.ui.framework.util;

import org.junit.Test;

import java.util.Calendar;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DateExtTest {

	@Test
	public void isDayBefore_shouldReturnTrueWhenYearChanges() {
		Calendar currentDate = Calendar.getInstance();
		currentDate.set(2000, Calendar.JANUARY, 01);
		Calendar pastDate = Calendar.getInstance();
		pastDate.set(1999, Calendar.DECEMBER, 31);
		assertThat(new DateExt(pastDate.getTime()).isDayBefore(currentDate.getTime()), is(true));
	}

	@Test
	public void isDayBefore_shouldReturnFalseWhen2YearsChange() {
		Calendar currentDate = Calendar.getInstance();
		currentDate.set(2000, Calendar.JANUARY, 01);
		Calendar pastDate = Calendar.getInstance();
		pastDate.set(1998, Calendar.DECEMBER, 31);
		assertThat(new DateExt(pastDate.getTime()).isDayBefore(currentDate.getTime()), is(false));
	}

	@Test
	public void isDayBefore_shouldReturnTrueForFebruary29WhenLeapYear() {
		Calendar currentDate = Calendar.getInstance();
		currentDate.set(2012, Calendar.MARCH, 01);
		Calendar pastDate = Calendar.getInstance();
		pastDate.set(2012, Calendar.FEBRUARY, 29);
		assertThat(new DateExt(pastDate.getTime()).isDayBefore(currentDate.getTime()), is(true));
	}

	@Test
	public void isDayBefore_shouldReturnFalseForFebruary28WhenLeapYear() {
		Calendar currentDate = Calendar.getInstance();
		currentDate.set(2012, Calendar.MARCH, 01);
		Calendar pastDate = Calendar.getInstance();
		pastDate.set(2012, Calendar.FEBRUARY, 28);
		assertThat(new DateExt(pastDate.getTime()).isDayBefore(currentDate.getTime()), is(false));
	}

	@Test
	 public void isSameDay_shouldReturnTrueWhenOnlyTimeDifferent() {
		Calendar currentDate = Calendar.getInstance();
		currentDate.set(2012, Calendar.MARCH, 01, 10, 00);
		Calendar pastDate = Calendar.getInstance();
		pastDate.set(2012, Calendar.MARCH, 01, 12, 12);
		assertThat(new DateExt(pastDate.getTime()).isSameDay(currentDate.getTime()), is(true));
	}

	@Test
	public void isSameDay_shouldReturnFalseWhenDayDifferent() {
		Calendar currentDate = Calendar.getInstance();
		currentDate.set(2012, Calendar.MARCH, 02, 10, 00);
		Calendar pastDate = Calendar.getInstance();
		pastDate.set(2012, Calendar.MARCH, 01, 10, 10);
		assertThat(new DateExt(pastDate.getTime()).isSameDay(currentDate.getTime()), is(false));
	}
}
