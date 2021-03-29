package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.api.EncounterService;
import org.openmrs.ui.framework.converter.util.ConversionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

public class StringToEncounterConverter implements Converter<String, Encounter> {
	
	@Autowired
	private EncounterService encounterService;
	
	public void setEncounterService(EncounterService encounterService) {
		this.encounterService = encounterService;
	}
	
	@Override
	public Encounter convert(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		} else if (ConversionUtil.onlyDigits(id)) {
			return encounterService.getEncounter(Integer.valueOf(id));
		} else {
			return encounterService.getEncounterByUuid(id);
		}
	}
	
}
