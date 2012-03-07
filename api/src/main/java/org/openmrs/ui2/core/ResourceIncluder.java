package org.openmrs.ui2.core;

import java.util.Set;

public interface ResourceIncluder {
	
	public void includeJavascript(String file);
	
	public void includeCss(String file);
	
	public Set<String> getJavascriptToInclude();
	
	public Set<String> getCssToInclude();
	
}
