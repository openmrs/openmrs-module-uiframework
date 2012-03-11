package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.springframework.core.convert.converter.Converter;

public class StringToEncounterConverter implements Converter<String, Encounter> {
	
	@Override
	public Encounter convert(String id) {
		if (StringUtils.isBlank(id))
			return null;
		return Context.getEncounterService().getEncounter(Integer.valueOf(id));
	}
	
}
