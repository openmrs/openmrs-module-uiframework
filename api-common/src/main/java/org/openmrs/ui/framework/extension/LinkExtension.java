package org.openmrs.ui.framework.extension;

import org.openmrs.ui.framework.Link;
import org.openmrs.ui.framework.session.Session;

/**
 * inherits getLabel, getLink, and getIcon methods from {@link Link} 
 */
public class LinkExtension extends Link implements Extension {
	
	private String point;
	
	private boolean enabledByDefault = true;
	
	private String description;
	
	public LinkExtension() {
	}
	
	public LinkExtension(String point, String label, String link, String icon, String description) {
		super(label, link, icon);
		this.point = point;
		this.description = description;
	}
	
	/**
	 * @see org.openmrs.ui.framework.extension.Extension#getPoint()
	 */
	@Override
	public String getPoint() {
		return point;
	}
	
	/**
	 * @see org.openmrs.ui.framework.extension.Extension#isEnabledByDefault()
	 */
	@Override
	public boolean isEnabledByDefault() {
		return enabledByDefault;
	}
	
	/**
	 * Subclasses should override this method if they don't always want to be enabled.
	 * @see org.openmrs.ui.framework.extension.Extension#isEnabled(org.openmrs.ui.framework.session.Session)
	 */
	@Override
	public boolean isEnabled(Session session) {
		return true;
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
	
}
