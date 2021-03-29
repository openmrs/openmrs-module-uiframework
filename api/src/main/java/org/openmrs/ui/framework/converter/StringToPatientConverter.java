package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.ui.framework.converter.util.ConversionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

public class StringToPatientConverter implements Converter<String, Patient> {
	
	@Autowired
	private PatientService patientService;
	
	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}
	
	@Override
	public Patient convert(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		} else if (ConversionUtil.onlyDigits(id)) {
			return patientService.getPatient(Integer.valueOf(id));
		} else {
			return patientService.getPatientByUuid(id);
		}
	}
	
}
