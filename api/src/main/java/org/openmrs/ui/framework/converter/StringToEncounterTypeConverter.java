package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.springframework.core.convert.converter.Converter;

public class StringToEncounterTypeConverter implements Converter<String, EncounterType> {
	
	@Override
	public EncounterType convert(String id) {
		if (StringUtils.isBlank(id))
			return null;
		return Context.getEncounterService().getEncounterType(Integer.valueOf(id));
	}
	
}
