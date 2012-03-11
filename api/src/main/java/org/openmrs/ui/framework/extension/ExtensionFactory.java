package org.openmrs.ui.framework.extension;

import java.util.Map;

/**
 * Implementations of this class should provide a list of extensions that may connect to
 * extension points in the system.
 */
public interface ExtensionFactory {
	
	/**
	 * @return a group of extensions, mapped by unique ids. (Often this will just be the extension subclass
	 * name, but it may differ for extensions with configurable parameters.)
	 */
	Map<String, Extension> getExtensions();
	
}
