package org.openmrs.ui.framework.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openmrs.ui.framework.WebConstants;
import org.openmrs.util.TimeZoneUtil;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;

public class StringToDateConverter implements Converter<String, Date> {
	
	@Override
	public Date convert(String ymdhmsms) {
		ymdhmsms = ymdhmsms.trim();
		if (ymdhmsms.isEmpty()) {
			return null;
		}

		//try to parse date with format ISO 8601
		try {
			return TimeZoneUtil.fromISO8601(ymdhmsms);
		}
		catch (Exception ex) {}

		try {
			SimpleDateFormat sdf = new SimpleDateFormat(WebConstants.DATE_FORMAT_TIMESTAMP);
			return sdf.parse(ymdhmsms);
		}
		catch (ParseException ex) {}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATETIME);
			return sdf.parse(ymdhmsms);
		}
		catch (ParseException ex) {}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE);
			return sdf.parse(ymdhmsms);
		}
		catch (ParseException ex) {}
		throw new ConversionFailedException(TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Date.class),
		        ymdhmsms, null);
	}
	
}
