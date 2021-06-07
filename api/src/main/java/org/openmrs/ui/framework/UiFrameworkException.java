package org.openmrs.ui.framework;

import org.owasp.encoder.Encode;

public class UiFrameworkException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UiFrameworkException() {
		super();
	}

	public UiFrameworkException(String message) {
		super(getEncodedMessage(message));
	}

	public UiFrameworkException(String message, Throwable throwable) {
		super(getEncodedMessage(message), throwable);
	}

	private static String getEncodedMessage(String message) {
		return message == null ? null : Encode.forHtml(message);
	}
}
