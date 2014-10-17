package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.ui.framework.converter.util.ConversionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

public class StringToConceptConverter implements Converter<String, Concept> {
	
	@Autowired
	private ConceptService conceptService;
	
	@Override
	public Concept convert(String id) {
		if (StringUtils.isBlank(id))
			return null;
		if (ConversionUtil.onlyDigits(id)) {
			return conceptService.getConcept(Integer.valueOf(id));
		} else {
			return conceptService.getConceptByUuid(id);
		}
	}
	
}
