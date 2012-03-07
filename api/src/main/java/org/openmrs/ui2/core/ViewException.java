package org.openmrs.ui2.core;

public class ViewException extends UiFrameworkException {
	
	private static final long serialVersionUID = 1L;
	
	public ViewException(String message) {
		super(message);
	}
	
	public ViewException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
