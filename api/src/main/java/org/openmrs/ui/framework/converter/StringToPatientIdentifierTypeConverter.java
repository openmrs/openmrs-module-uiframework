package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.springframework.core.convert.converter.Converter;

public class StringToPatientIdentifierTypeConverter implements Converter<String, PatientIdentifierType> {
	
	@Override
	public PatientIdentifierType convert(String id) {
		if (StringUtils.isBlank(id))
			return null;
		return Context.getPatientService().getPatientIdentifierType(Integer.valueOf(id));
	}
}
