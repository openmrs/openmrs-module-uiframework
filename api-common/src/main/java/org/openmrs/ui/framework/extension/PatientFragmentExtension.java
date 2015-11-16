package org.openmrs.ui.framework.extension;

import java.util.Map;

import org.openmrs.ui.framework.session.Session;

/**
 * An extension only usable in a Patient context (e.g. on a dashboard), that includes a fragment,
 * possibly with a configuration.
 */
public class PatientFragmentExtension extends PatientExtension {
	
	private String fragment;
	
	private Map<String, Object> fragmentConfig;
	
	public PatientFragmentExtension() {
	}
	
	public PatientFragmentExtension(String label, String description, String fragment, Map<String, Object> fragmentConfig) {
		this.fragment = fragment;
		this.fragmentConfig = fragmentConfig;
		this.setLabel(label);
		this.setDescription(description);
	}
	
	/**
	 * Subclasses should override this method if they want to only be conditionally enabled.
	 * @see org.openmrs.ui.framework.extension.Extension#isEnabled(org.openmrs.ui.framework.session.Session)
	 */
	@Override
	public boolean isEnabled(Session session) {
		return true;
	}
	
	/**
	 * @return the fragment name
	 */
	public String getFragment() {
		return fragment;
	}
	
	/**
	 * @param fragment the fragment name to set
	 */
	public void setFragment(String fragment) {
		this.fragment = fragment;
	}
	
	/**
	 * @return the fragmentConfig
	 */
	public Map<String, Object> getFragmentConfig() {
		return fragmentConfig;
	}
	
	/**
	 * @param fragmentConfig the fragmentConfig to set
	 */
	public void setFragmentConfig(Map<String, Object> fragmentConfig) {
		this.fragmentConfig = fragmentConfig;
	}
	
}
