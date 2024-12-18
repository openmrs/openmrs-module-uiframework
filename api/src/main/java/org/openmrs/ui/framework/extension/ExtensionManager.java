package org.openmrs.ui.framework.extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Manager for extension points
 */
@Service
public class ExtensionManager {
	
	Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private List<ExtensionFactory> extensionFactories;
	
	private transient Map<String, Extension> cachedExtensions;
	
	@Autowired
	private List<ExtensionPointFactory> extensionPointFactories;
	
	// map from extension point id to a descriptor of that extension
	private transient Map<String, ExtensionPoint> cachedExtensionPoints;
	
	/**
	 * TODO: allow extensions to be turned off globally, even if they're on the classpath
	 * 
	 * @return (for now) all extensions
	 */
	private Map<String, Extension> activeExtensions() {
		// I noticed a bug where this is being generated and cached before all modules are loaded.
		// At some point we should investigate this, but for now performance is not an issue, so I'm
		// just clearing the cache every time this method is called
		cachedExtensions = null;
		if (cachedExtensions == null) {
			cachedExtensions = new LinkedHashMap<String, Extension>();
			for (ExtensionFactory factory : extensionFactories) {
				cachedExtensions.putAll(factory.getExtensions());
			}
		}
		return cachedExtensions;
	}
	
	/**
	 * @return all published {@link ExtensionPoint}s
	 */
	public Collection<ExtensionPoint> getExtensionPoints() {
		if (cachedExtensionPoints == null) {
			cachedExtensionPoints = new LinkedHashMap<String, ExtensionPoint>();
			for (ExtensionPointFactory factory : extensionPointFactories) {
				for (ExtensionPoint point : factory.getExtensionPoints()) {
					cachedExtensionPoints.put(point.getPointId(), point);
				}
			}
		}
		return Collections.unmodifiableCollection(cachedExtensionPoints.values());
	}
	
	/**
	 * @param pointId
	 * @return the published {@link ExtensionPoint} with the given pointId (or null if none exists)
	 */
	public ExtensionPoint getExtensionPoint(String pointId) {
		return cachedExtensionPoints.get(pointId);
	}
	
	/**
	 * @param extensionUniqueId an id for the extension (same as a key returned by
	 *            {@link #getExtensionsByClass(Class)})
	 * @return the {@link Extension} with the given id
	 */
	public Extension getExtension(String extensionUniqueId) {
		return activeExtensions().get(extensionUniqueId);
	}
	
	/**
	 * Gets all extensions of a given class (regardless of their specific point)
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends Extension> Map<String, T> getExtensionsByClass(Class<T> clazz) {
		Map<String, T> ret = new LinkedHashMap<String, T>();
		for (Map.Entry<String, Extension> ext : activeExtensions().entrySet()) {
			if (clazz.isAssignableFrom(ext.getValue().getClass()))
				ret.put(ext.getKey(), (T) ext.getValue());
		}
		return ret;
	}
	
	/**
	 * Gets an ordered list of extension unique ids, currently configured for the given point
	 * 
	 * @param pointId
	 * @return an ordered list of extension unique ids, as currently configured for the given point. If
	 *         the point has not been configured, this returns null.
	 */
	public List<String> getExtensionPointConfiguration(String pointId) {
		try {
			// we proxy this because this method ends up being called from the AuthenticationUI login page
			Context.addProxyPrivilege(PrivilegeConstants.GET_GLOBAL_PROPERTIES);
			String gp = Context.getAdministrationService().getGlobalProperty("ui2.extensionConfig." + pointId);
			return gp == null ? null : Arrays.asList(gp.split(","));
		}
		finally {
			Context.removeProxyPrivilege(PrivilegeConstants.GET_GLOBAL_PROPERTIES);
		}
	}
	
	/**
	 * Saves an ordering of extensions for a given point. If you pass an empty list for uniqueIds then
	 * the configuration for that extension point is cleared (so that it will return all extensions in
	 * the default order)
	 * 
	 * @param pointId
	 * @param uniqueIds
	 */
	public void saveExtensionPointConfiguration(String pointId, String... uniqueIds) {
		AdministrationService service = Context.getAdministrationService();
		GlobalProperty gp = service.getGlobalPropertyObject("ui2.extensionConfig." + pointId);
		if (uniqueIds.length == 0) {
			if (gp != null)
				service.purgeGlobalProperty(gp);
		} else {
			for (String id : uniqueIds) {
				if (activeExtensions().get(id) == null)
					throw new IllegalArgumentException("No extension found for id: " + id);
			}
			if (gp == null)
				gp = new GlobalProperty("ui2.extensionConfig." + pointId);
			gp.setPropertyValue(OpenmrsUtil.join(Arrays.asList(uniqueIds), ","));
			service.saveGlobalProperty(gp);
		}
	}
	
	/**
	 * Gets extensions for a given class and point combination. The class defines the set of all
	 * possible extensions, and the point defines which ones are enabled, and how they are ordered.
	 * 
	 * @param extensionClass
	 * @param pointId possibly null
	 * @return
	 */
	public <T extends Extension> List<T> getExtensions(Class<T> extensionClass, String pointId) {
		Map<String, T> candidates = getExtensionsByClass(extensionClass);
		List<T> ret = new ArrayList<T>();
		if (pointId == null) {
			ret.addAll(candidates.values());
			return ret;
		}
		// limit to just those that specify a given point, and haven't been disabled
		List<String> configuration = getExtensionPointConfiguration(pointId);
		if (configuration == null) {
			// just return all matching extensions that are enabled by default
			for (T ext : candidates.values()) {
				if (ext.isEnabledByDefault() && (ext.getPoint() == null || ext.getPoint().equals(pointId)))
					ret.add(ext);
			}
		} else {
			// return extensions in the order indicated by the configuration
			for (String extensionUniqueId : configuration) {
				T ext = candidates.get(extensionUniqueId);
				if (ext != null) {
					ret.add(ext);
				} else {
					log.warn("extension:" + extensionUniqueId + " configured for point:" + pointId + " not found");
				}
			}
		}
		return ret;
	}
	
}
