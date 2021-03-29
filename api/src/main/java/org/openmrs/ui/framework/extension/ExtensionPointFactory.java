package org.openmrs.ui.framework.extension;

import java.util.List;

/**
 * Implementations of this class provide a list of extension points that extensions may attach to.
 */
public interface ExtensionPointFactory {
	
	/**
	 * @return descriptors of extension points
	 */
	List<ExtensionPoint> getExtensionPoints();
	
}
