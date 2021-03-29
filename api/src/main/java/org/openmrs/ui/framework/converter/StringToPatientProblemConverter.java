package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.activelist.ProblemModifier;
import org.openmrs.annotation.OpenmrsProfile;
import org.springframework.core.convert.converter.Converter;

@OpenmrsProfile(openmrsPlatformVersion = "[1.9.9 - 1.12.*]")
public class StringToPatientProblemConverter implements Converter<String, ProblemModifier> {
	
	@Override
	public ProblemModifier convert(String text) {
		if (StringUtils.isBlank(text))
			return null;
		return ProblemModifier.getValue(text);
	}
}
