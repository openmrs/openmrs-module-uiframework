package org.openmrs.ui2.core.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.springframework.core.convert.converter.Converter;

public class StringToPatientConverter implements Converter<String, Patient> {
	
	@Override
	public Patient convert(String id) {
		if (StringUtils.isBlank(id))
			return null;
		return Context.getPatientService().getPatient(Integer.valueOf(id));
	}
	
}
