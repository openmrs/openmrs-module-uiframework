package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.springframework.core.convert.converter.Converter;

public class StringToConceptConverter implements Converter<String, Concept> {
	
	@Override
	public Concept convert(String id) {
		if (StringUtils.isBlank(id))
			return null;
		return Context.getConceptService().getConcept(Integer.valueOf(id));
	}
	
}
