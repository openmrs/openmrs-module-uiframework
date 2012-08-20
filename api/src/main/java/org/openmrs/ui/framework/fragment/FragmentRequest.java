package org.openmrs.ui.framework.fragment;

import java.util.Map;

public class FragmentRequest {
	
	private String providerName;
	
	private String fragmentId;
	
	private FragmentConfiguration configuration;
	
	public FragmentRequest(String providerName, String fragmentId) {
		this(providerName, fragmentId, (FragmentConfiguration) null);
	}
	
	public FragmentRequest(String providerName, String fragmentId, Map<String, Object> configuration) {
		this(providerName, fragmentId, new FragmentConfiguration(configuration));
	}
	
	public FragmentRequest(String providerName, String fragmentId, FragmentConfiguration configuration) {
		this.providerName = providerName == null ? "*" : providerName;
		this.fragmentId = fragmentId;
		this.configuration = configuration == null ? new FragmentConfiguration() : configuration;
	}
	
	public String toString() {
		return "Fragment Request for " + providerName + " : " + fragmentId;
	}
	
	/**
	 * @return the providerName
	 */
	public String getProviderName() {
		return providerName;
	}
	
	/**
	 * @param providerName the providerName to set
	 */
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	
	/**
	 * @return the fragmentId
	 */
	public String getFragmentId() {
		return fragmentId;
	}
	
	/**
	 * @param fragmentId the fragmentId to set
	 */
	public void setFragmentId(String fragmentId) {
		this.fragmentId = fragmentId;
	}
	
	/**
	 * @return the configuration
	 */
	public FragmentConfiguration getConfiguration() {
		return configuration;
	}
	
	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(FragmentConfiguration configuration) {
		this.configuration = configuration;
	}
	
}
