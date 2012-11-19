package org.openmrs.ui.framework;

import java.util.List;
import java.util.Map;

public class MissingRequiredParameterException extends RequestValidationException {
	
	private static final long serialVersionUID = 1L;
	
	private String requiredParameter;
	
	public MissingRequiredParameterException(String requiredParameter) {
		this.requiredParameter = requiredParameter;
	}

	@Override
	public Map<String, List<String>> getFieldErrorCodes() {
		return singleFieldError(requiredParameter, "error.required");
	}
	
}
