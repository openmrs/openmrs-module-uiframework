package org.openmrs.ui.framework.extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Allows core and module to declare {@link Extension}s and {@link ExtensionPoint}s as Spring beans,
 * either via {@link Component} annotations, or in xml.
 */
@Component
public class SpringBeanExtensionFactory implements ExtensionFactory, ExtensionPointFactory {
	
	/**
	 * Spring bean names, mapped to the extensions they represent
	 */
	@Autowired(required = false)
	Map<String, Extension> extensions;
	
	/**
	 * All Spring-managed extension points
	 */
	@Autowired(required = false)
	List<ExtensionPoint> extensionPoints;
	
	@Override
	public Map<String, Extension> getExtensions() {
		return extensions != null ? extensions : new HashMap<String, Extension>();
	}
	
	@Override
	public List<ExtensionPoint> getExtensionPoints() {
		return extensionPoints != null ? extensionPoints : new ArrayList<ExtensionPoint>();
	}
	
}
