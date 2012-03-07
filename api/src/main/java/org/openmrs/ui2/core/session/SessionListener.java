package org.openmrs.ui2.core.session;

/**
 * Implement this interface and load your subclass as a Spring bean, in order to be notified when sessions
 * are created and destroyed.
 * 
 * You should subclass {@link BaseSessionListener} rather than implementing this interface directly, as it
 * may have methods added to it in the future.
 */
public interface SessionListener {
	
	/**
	 * Will be called just after the session has been created
	 * @param session
	 */
	void afterSessionCreated(Session session);
	
	/**
	 * Will be called just before the session is destroyed
	 * @param session
	 */
	void beforeSessionDestroyed(Session session);
	
}
