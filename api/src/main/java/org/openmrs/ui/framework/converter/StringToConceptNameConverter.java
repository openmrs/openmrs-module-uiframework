package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.converter.util.ConversionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

/**
 *
 */
public class StringToConceptNameConverter implements Converter<String, ConceptName> {
	
	@Autowired
	private ConceptService conceptService;
	
	@Override
	public ConceptName convert(String id) {
		if (StringUtils.isBlank(id))
			return null;
		if (ConversionUtil.onlyDigits(id)) {
			return Context.getConceptService().getConceptName(Integer.valueOf(id));
		} else {
			return Context.getConceptService().getConceptNameByUuid(id);
		}
	}
	
}
