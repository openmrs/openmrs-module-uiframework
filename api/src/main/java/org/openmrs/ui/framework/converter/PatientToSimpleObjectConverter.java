package org.openmrs.ui.framework.converter;

import org.openmrs.Patient;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

public class PatientToSimpleObjectConverter implements Converter<Patient, SimpleObject> {
	
	@Autowired
	UiUtils ui;
	
	@Override
	public SimpleObject convert(Patient patient) {
		/*
		SimplePatient p = new SimplePatient(patient);
		SimpleObject ret = new SimpleObject();
		ret.put("age", p.getAge());
		ret.put("birthdate", p.getBirthdate());
		ret.put("birthdateEstimated", p.getBirthdateEstimated());
		ret.put("gender", p.getGender());
		ret.put("activeIdentifiers", ui.simplifyCollection(p.getActiveIdentifiers()));
		return ret;
		*/
		// TODO FIX THIS
		return new SimpleObject();
	}
	
}
