package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

import java.util.regex.Pattern;

public class StringToPatientConverter implements Converter<String, Patient> {

    private Pattern onlyDigits = Pattern.compile("\\d+");

    @Autowired
    private PatientService patientService;

    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

	@Override
	public Patient convert(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
        } else if (onlyDigits.matcher(id).matches()) {
            return patientService.getPatient(Integer.valueOf(id));
        }else {
            return patientService.getPatientByUuid(id);
        }
	}

}
