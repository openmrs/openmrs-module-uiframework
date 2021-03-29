package org.openmrs.ui.framework.extension;

import org.openmrs.ui.framework.UiFrameworkConstants;
import org.openmrs.ui.framework.session.Session;

/**
 * Allows to map an extension like kenyaemr/standardEmrPage so that it is used instead of
 * appui/standardEmrPage. The extension point is
 * {@link UiFrameworkConstants#MAP_RESOURCE_EXTENSION_POINT_ID}.
 */
public class MapResourceExtension implements Extension {
	
	private String point;
	
	private String description;
	
	private String providerId;
	
	private String resourceId;
	
	private String providerIdToMap;
	
	private String resourceIdToMap;
	
	public MapResourceExtension() {
	}
	
	@Override
	public String getPoint() {
		return point;
	}
	
	public void setPoint(String point) {
		this.point = point;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public boolean isEnabledByDefault() {
		return true;
	}
	
	@Override
	public boolean isEnabled(Session session) {
		return true;
	}
	
	public String getProviderId() {
		return providerId;
	}
	
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	
	public String getResourceId() {
		return resourceId;
	}
	
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	public String getProviderIdToMap() {
		return providerIdToMap;
	}
	
	public void setProviderIdToMap(String providerIdToMap) {
		this.providerIdToMap = providerIdToMap;
	}
	
	public String getResourceIdToMap() {
		return resourceIdToMap;
	}
	
	public void setResourceIdToMap(String resourceIdToMap) {
		this.resourceIdToMap = resourceIdToMap;
	}
	
}
