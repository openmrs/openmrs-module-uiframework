package org.openmrs.ui2.core.converter;

import org.openmrs.OpenmrsObject;
import org.springframework.core.convert.converter.Converter;

public class OpenmrsObjectToStringConverter implements Converter<OpenmrsObject, String> {
	
	@Override
	public String convert(OpenmrsObject obj) {
		return obj.getId().toString();
	}
	
}
