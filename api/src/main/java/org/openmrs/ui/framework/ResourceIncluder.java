package org.openmrs.ui.framework;

import java.util.Set;

import org.openmrs.ui.framework.resource.Resource;

public interface ResourceIncluder {
	
	public void includeJavascript(Resource resource);
	
	public void includeCss(Resource resource);
	
	public Set<Resource> getJavascriptToInclude();
	
	public Set<Resource> getCssToInclude();
	
}
