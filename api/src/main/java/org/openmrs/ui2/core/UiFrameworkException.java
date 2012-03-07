package org.openmrs.ui2.core;

public class UiFrameworkException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public UiFrameworkException() {
		super();
	}
	
	public UiFrameworkException(String message) {
		super(message);
	}
	
	public UiFrameworkException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
}
