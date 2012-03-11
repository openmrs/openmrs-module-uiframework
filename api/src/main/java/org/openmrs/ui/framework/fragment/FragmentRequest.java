package org.openmrs.ui.framework.fragment;

import java.util.HashMap;
import java.util.Map;

public class FragmentRequest {
	
	private String id;
	
	private FragmentConfiguration configuration;
	
	public FragmentRequest(String id) {
		this(id, null);
	}
	
	public FragmentRequest(String id, Map<String, Object> configuration) {
		this(id, new FragmentConfiguration(configuration));
	}
	
	public FragmentRequest(String id, FragmentConfiguration configuration) {
		this.id = id;
		if (configuration == null)
			configuration = new FragmentConfiguration();
		this.configuration = configuration;
	}
	
	public String toString() {
		return "Fragment Request for " + id;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
