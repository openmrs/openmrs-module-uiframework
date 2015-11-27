package org.openmrs.ui.framework;

/**
 * An exception while rendering/invoking a fragment
 */
public class FragmentException extends UiFrameworkException {
	
	private static final long serialVersionUID = 1L;
	
	public FragmentException(String message) {
		super(message);
	}
	
	public FragmentException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
}
