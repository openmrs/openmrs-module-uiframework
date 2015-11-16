package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.activelist.ProblemModifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.openmrs.annotation.OpenmrsProfile;

@Component
@OpenmrsProfile(openmrsVersion = "[1.9.9 - 1.11.3]")
public class StringToPatientProblemConverter implements Converter<String, ProblemModifier> {
	
	@Override
	public ProblemModifier convert(String text) {
		if (StringUtils.isBlank(text))
			return null;
		return ProblemModifier.getValue(text);
	}
}
