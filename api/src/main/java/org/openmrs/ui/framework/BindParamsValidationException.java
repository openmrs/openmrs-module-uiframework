package org.openmrs.ui.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class BindParamsValidationException extends RequestValidationException {
	
	private static final long serialVersionUID = 1L;
	
	private String prefix;
	
	private Errors errors;
	
	public BindParamsValidationException(String prefix, Errors errors) {
		this.prefix = prefix;
		this.errors = errors;
	}
	
	@Override
	public List<String> getGlobalErrorCodes() {
		List<String> ret = new ArrayList<String>();
		for (ObjectError error : errors.getGlobalErrors()) {
			ret.add(error.getCode());
		}
		return ret;
	}
	
	@Override
	public Map<String, List<String>> getFieldErrorCodes() {
		Map<String, List<String>> ret = new HashMap<String, List<String>>();
		for (FieldError error : errors.getFieldErrors()) {
			String field;
			if (prefix != null)
				field = prefix + "." + error.getField();
			else
				field = error.getField();
			List<String> holder = ret.get(field);
			if (holder == null) {
				holder = new ArrayList<String>();
				ret.put(field, holder);
			}
			holder.add(error.getCode());
		}
		return ret;
	}
	
}
