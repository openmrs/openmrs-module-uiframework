package org.openmrs.ui.framework;

import java.util.Set;

public interface ResourceIncluder {
	
	public void includeJavascript(String file);
	
	public void includeCss(String file);
	
	public Set<String> getJavascriptToInclude();
	
	public Set<String> getCssToInclude();
	
}
