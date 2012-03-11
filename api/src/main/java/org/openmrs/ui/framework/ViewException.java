package org.openmrs.ui.framework;

public class ViewException extends UiFrameworkException {
	
	private static final long serialVersionUID = 1L;
	
	public ViewException(String message) {
		super(message);
	}
	
	public ViewException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
