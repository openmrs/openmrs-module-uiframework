package org.openmrs.ui2.core.fragment.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/**
 * Return type from a fragment action method that indicates failure.
 */
public class FailureResult implements FragmentActionResult {
	
	private Errors errors;
	
	private String singleError;
	
	public FailureResult(Errors errors) {
		this.errors = errors;
	}
	
	public FailureResult(String singleError) {
		this.singleError = singleError;
	}
	
	/**
	 * @return the errors
	 */
	public Errors getErrors() {
		return errors;
	}
	
	/**
	 * @param errors the errors to set
	 */
	public void setErrors(Errors errors) {
		this.errors = errors;
	}
	
	/**
	 * @return the singleError
	 */
	public String getSingleError() {
		return singleError;
	}
	
	/**
	 * @param singleError the singleError to set
	 */
	public void setSingleError(String singleError) {
		this.singleError = singleError;
	}
	
	public List<String> getGlobalErrors() {
		if (singleError != null)
			return Collections.singletonList(singleError);
		
		List<String> ret = new ArrayList<String>();
		if (errors != null) {
			for (ObjectError err : errors.getGlobalErrors()) {
				ret.add(err.getDefaultMessage());
			}
		}
		return ret;
	}
	
	public Map<String, List<String>> getFieldErrorMap() {
		Map<String, List<String>> ret = new LinkedHashMap<String, List<String>>();
		if (errors != null) {
			for (FieldError err : errors.getFieldErrors()) {
				List<String> forField = ret.get(err.getField());
				if (forField == null) {
					forField = new ArrayList<String>();
					ret.put(err.getField(), forField);
				}
				forField.add(err.getDefaultMessage());
			}
		}
		return ret;
	}
	
}
