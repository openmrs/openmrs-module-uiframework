package org.openmrs.ui.framework.extension;

/**
 * Business objects that provide access to the Extension Manager implement this interface
 */
public interface ExtensionAware {
	
	ExtensionManager getExtensionManager();
	
}
