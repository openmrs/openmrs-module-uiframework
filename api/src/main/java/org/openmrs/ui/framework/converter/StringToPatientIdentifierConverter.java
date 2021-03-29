package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
import org.springframework.core.convert.converter.Converter;

public class StringToPatientIdentifierConverter implements Converter<String, PatientIdentifier> {
	
	@Override
	public PatientIdentifier convert(String id) {
		if (StringUtils.isBlank(id))
			return null;
		return Context.getPatientService().getPatientIdentifier(Integer.valueOf(id));
	}
	
}
