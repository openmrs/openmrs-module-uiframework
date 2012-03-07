package org.openmrs.ui2.core.extension;

/**
 * An extension that can only be included in a patient context (for example a patient dashboard)
 */
public abstract class PatientExtension implements Extension {
	
	private String point;
	
	private boolean enabledByDefault = true;
	
	private String label; // intended for use as a title on a tab or a decorator
	
	private String description;
	
	/**
	 * @see org.openmrs.ui2.core.extension.Extension#getPoint()
	 */
	@Override
	public String getPoint() {
		return point;
	}
	
	/**
	 * @see org.openmrs.ui2.core.extension.Extension#isEnabledByDefault()
	 */
	@Override
	public boolean isEnabledByDefault() {
		return enabledByDefault;
	}
	
	/**
	 * @param point the point to set
	 */
	public void setPoint(String point) {
		this.point = point;
	}
	
	/**
	 * @param enabledByDefault the enabledByDefault to set
	 */
	public void setEnabledByDefault(boolean enabledByDefault) {
		this.enabledByDefault = enabledByDefault;
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
	
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
}
