/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.util;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.UiFrameworkConstants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.joda.time.DateTimeZone.UTC;
import static org.openmrs.ui.framework.UiFrameworkConstants.GET_GLOBAL_PROPERTIES;

/**
 * Helps provide tools to support recommended OpenMRS time zones conventions.
 * 
 * @see https://wiki.openmrs.org/display/docs/Time+Zones+Conventions
 */
public class TimeZoneUtil {
	
	/**
	 * Convert a date to the client timezone, and format it, to be readable for the user.
	 * 
	 * @param date The date.
	 * @param format the format to be used on the date
	 * @return string with the date in the client timezone, formatted and ready to be displayed.
	 */
	public static String toTimezone(Date date, String format) {
		Context.addProxyPrivilege(GET_GLOBAL_PROPERTIES);
		String clientTimezone = Context.getAuthenticatedUser().getUserProperty(
		    Context.getAdministrationService().getGlobalProperty(UiFrameworkConstants.UP_CLIENT_TIMEZONE));
		Context.removeProxyPrivilege(GET_GLOBAL_PROPERTIES);
		return toTimezone(date, format, clientTimezone);
	}
	
	/**
	 * Formats a date while expressing it in the specified timezone.
	 * 
	 * @param date The date.
	 * @param format the format to be used on the date
	 * @param timezone The tz database name, eg. "Europe/Zurich", if for some reason that param is null,
	 *            it will use UTC.
	 * @return string with the date in the client timezone, formatted and ready to be displayed.
	 */
	public static String toTimezone(Date date, String format, String timezone) {
		if (StringUtils.isEmpty(timezone)) {
			timezone = UTC.toString();
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, Context.getLocale());
		dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
		return dateFormat.format(date);
	}
	
	/**
	 * Formats a date as its RFC 3339 string representation.
	 * 
	 * @param date The date.
	 * @return The date formated as RFC 3339.
	 */
	public static String toRFC3339(Date date) {
		return ISODateTimeFormat.dateTime().print(new DateTime(date.getTime(), UTC));
	}
	
	/**
	 * Gets the Calendar instance for the date set in UTC. This always returns a GregorianCalendar
	 * subclass.
	 * 
	 * @param date The date.
	 * @return The GregorianCalendar set in UTC for the date.
	 */
	public static Calendar toUTCCalendar(Date date) {
		return new DateTime(date.getTime(), UTC).toGregorianCalendar();
	}
	
	/**
	 * Get a Date out of its ISO 8601 string representation.
	 * 
	 * @param isoDateString A date formatted as ISO 8601.
	 * @return The Date object.
	 * @Throws IllegalArgumentException – if string parameter does not conform to lexical value space
	 */
	public static Date fromISO8601(String isoDateString) throws IllegalArgumentException {
		DateTimeFormatter parser = ISODateTimeFormat.dateTime();
		return parser.parseDateTime(isoDateString).toDate();
	}
	
}
