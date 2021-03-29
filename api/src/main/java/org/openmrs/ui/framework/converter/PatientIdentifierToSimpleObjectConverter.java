package org.openmrs.ui.framework.converter;

import org.openmrs.PatientIdentifier;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

public class PatientIdentifierToSimpleObjectConverter implements Converter<PatientIdentifier, SimpleObject> {
	
	@Autowired
	UiUtils ui;
	
	@Override
	public SimpleObject convert(PatientIdentifier pid) {
		SimpleObject ret = new SimpleObject();
		ret.put("id", pid.getId());
		ret.put("identifierType", ui.simplifyObject(pid.getIdentifierType()));
		ret.put("identifier", pid.getIdentifier());
		ret.put("location", ui.simplifyObject(pid.getLocation()));
		ret.put("preferred", pid.isPreferred());
		return ret;
	}
	
}
