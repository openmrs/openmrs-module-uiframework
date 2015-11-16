package org.openmrs.ui.framework;

import org.openmrs.ui.framework.resource.Resource;

import java.util.List;

/**
 * Methods for including resources (which are grouped by category, e.g. CSS, JAVASCRIPT)
 */
public interface ResourceIncluder {

    public void includeResource(Resource resource);

    public List<Resource> getResourcesToInclude(String resourceCategory);
	
}
