package org.openmrs.ui.framework.fragment.action;

/**
 * Return type from a fragment action method that indicates success.
 * Optionally you may provide a message which will be put in the session and displayed on the next page load
 */
public class SuccessResult implements FragmentActionResult {
	
	private String message;
	
	public SuccessResult() {
	}
	
	public SuccessResult(String message) {
		this.message = message;
	}
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
}
