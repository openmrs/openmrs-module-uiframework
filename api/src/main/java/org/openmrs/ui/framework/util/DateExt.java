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

import java.util.Calendar;
import java.util.Date;

/**
 * Wraps Date providing helper methods.
 */
public class DateExt {

	final Date date;

	public DateExt(Date date) {
		this.date = date;
	}

	public boolean isSameDay(Date anotherDate) {
		Calendar day = Calendar.getInstance();
		day.setTime(date);
		day.set(Calendar.HOUR_OF_DAY, 0);
		day.set(Calendar.MINUTE, 0);
		day.set(Calendar.SECOND, 0);
		day.set(Calendar.MILLISECOND, 0);

		Calendar anotherDay = Calendar.getInstance();
		anotherDay.setTime(anotherDate);
		anotherDay.set(Calendar.HOUR_OF_DAY, 0);
		anotherDay.set(Calendar.MINUTE, 0);
		anotherDay.set(Calendar.SECOND, 0);
		anotherDay.set(Calendar.MILLISECOND, 0);

		return day.equals(anotherDay);
	}

	public boolean isDayBefore(Date anotherDate) {
		Calendar day = Calendar.getInstance();
		day.setTime(date);
		day.set(Calendar.HOUR_OF_DAY, 0);
		day.set(Calendar.MINUTE, 0);
		day.set(Calendar.SECOND, 0);
		day.set(Calendar.MILLISECOND, 0);

		Calendar anotherDay = Calendar.getInstance();
		anotherDay.setTime(anotherDate);
		anotherDay.add(Calendar.DAY_OF_YEAR, -1);
		anotherDay.set(Calendar.HOUR_OF_DAY, 0);
		anotherDay.set(Calendar.MINUTE, 0);
		anotherDay.set(Calendar.SECOND, 0);
		anotherDay.set(Calendar.MILLISECOND, 0);

		return day.equals(anotherDay);
	}

	public Date getDateWithoutTime() {
		Calendar day = Calendar.getInstance();
		day.setTime(date);
		day.set(Calendar.HOUR_OF_DAY, 0);
		day.set(Calendar.MINUTE, 0);
		day.set(Calendar.SECOND, 0);
		day.set(Calendar.MILLISECOND, 0);

		return day.getTime();
	}
}
