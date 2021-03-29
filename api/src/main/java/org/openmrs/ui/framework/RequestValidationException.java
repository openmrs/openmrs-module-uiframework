package org.openmrs.ui.framework;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Thrown when a request is invalid, for example it is missing required parameters
 */
public abstract class RequestValidationException extends UiFrameworkException {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Override this if you want to signal any global errors
	 * 
	 * @return
	 */
	public List<String> getGlobalErrorCodes() {
		return Collections.emptyList();
	}
	
	/**
	 * Override this if you want to signal any field errors
	 * 
	 * @return
	 */
	public Map<String, List<String>> getFieldErrorCodes() {
		return Collections.emptyMap();
	}
	
	/**
	 * Utility method for subclasses that want to report a single field error
	 * 
	 * @param fieldName
	 * @param errorCode
	 * @return
	 */
	protected static Map<String, List<String>> singleFieldError(String fieldName, String errorCode) {
		Map<String, List<String>> ret = new HashMap<String, List<String>>();
		ret.put(fieldName, Collections.singletonList(errorCode));
		return ret;
	}
	
}
