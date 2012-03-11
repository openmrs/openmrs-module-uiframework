package org.openmrs.ui.framework.extension;

import org.openmrs.ui.framework.session.Session;

/**
 * An extension, either provided by core code or a module, that is capable of connecting to
 * an {@link ExtensionPoint}. The administrator of an OpenMRS system can configured whether or
 * not the extension is actually shown at that point.
 * 
 * Most extensions should plug in "by class" rather than "by point", e.g. a widget that can go on a
 * patient dashboard should be a {@link PatientFragmentExtension}, and return null from its
 * {@link #getPoint()} method, so that it can be plugged into any patient dashboard.
 */
public interface Extension {
	
	/**
	 * @return the specific point that this extension binds to (which should usually be null, since most extensions
	 *         should be able to bind to any point of their given type)
	 */
	String getPoint();
	
	/**
	 * @return a description of this point, intended for system administrators when they are
	 * configuring which extensions are active. A null return value may indicate that this extension
	 * is not really user-configurable. (And callers of this method should expect null values.)
	 */
	String getDescription();
	
	/**
	 * If you are creating an extension that may be used on its own, but is more typically included in
	 * another extension, you can have the container be enabled by default, and the child disabled by
	 * default. (For example core provides a "patientDemographics" fragment that also contains a "personNames"
	 * fragment. The former is enabled by default, and the latter isn't.)
	 * @return whether this extension should be enabled on compatible {@link ExtensionPoint}s, if the
	 * system administrator has not set a specific configuration on the point  
	 */
	boolean isEnabledByDefault();
	
	/**
	 * Extensions are given the chance to indicate whether or not they're enabled in the context of the given
	 * session.
	 * 
	 * @param session
	 * @return
	 */
	boolean isEnabled(Session session);
	
}
