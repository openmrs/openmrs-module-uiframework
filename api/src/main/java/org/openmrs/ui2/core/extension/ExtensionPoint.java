package org.openmrs.ui2.core.extension;

/**
 * The descriptor for a point that extensions may connect to
 */
public class ExtensionPoint {
	
	private String pointId;
	
	private String description;
	
	private Class<? extends Extension> requiredClass;
	
	public ExtensionPoint() {
	}
	
	public ExtensionPoint(String pointId, String description, Class<? extends Extension> requiredClass) {
		this.pointId = pointId;
		this.description = description;
		this.requiredClass = requiredClass;
	}
	
	/**
	 * @return the pointId
	 */
	public String getPointId() {
		return pointId;
	}
	
	/**
	 * @param pointId the pointId to set
	 */
	public void setPointId(String pointId) {
		this.pointId = pointId;
	}
	
	/**
	 * @return the requiredClass
	 */
	public Class<? extends Extension> getRequiredClass() {
		return requiredClass;
	}
	
	/**
	 * @param requiredClass the requiredClass to set
	 */
	public void setRequiredClass(Class<? extends Extension> requiredClass) {
		this.requiredClass = requiredClass;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
}
