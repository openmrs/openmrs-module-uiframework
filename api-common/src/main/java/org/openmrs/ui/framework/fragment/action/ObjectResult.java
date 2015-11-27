package org.openmrs.ui.framework.fragment.action;

/**
 * Return type from a fragment action method that contains a wrapped object to be displayed
 * directly to the client.
 * Subclasses of this type may indicate specific serialization formats.
 * This serves the same purpose as Spring MVC's ResponseBody annotation.
 */
public class ObjectResult implements FragmentActionResult {
	
	protected Object resultObject;
	
	public ObjectResult(Object wrapped) {
		this.resultObject = wrapped;
	}
	
	public String toString() {
		if (resultObject != null)
			return resultObject.toString();
		else
			return "";
	}
	
	/**
	 * @return the resultObject
	 */
	public Object getWrapped() {
		return resultObject;
	}
	
	/**
	 * @param resultObject the resultObject to set
	 */
	public void setWrapped(Object wrapped) {
		this.resultObject = wrapped;
	}
	
}
