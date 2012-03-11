package org.openmrs.ui.framework.session;

/**
 * Base implementation of {@link SessionListener} with no-ops for all methods.
 */
public abstract class BaseSessionListener implements SessionListener {
	
	/**
	 * @see org.openmrs.ui.framework.session.SessionListener#afterSessionCreated(org.openmrs.ui.framework.session.Session)
	 */
	@Override
	public void afterSessionCreated(Session session) {
		// no-op
	}
	
	/**
	 * @see org.openmrs.ui.framework.session.SessionListener#beforeSessionDestroyed(org.openmrs.ui.framework.session.Session)
	 */
	@Override
	public void beforeSessionDestroyed(Session session) {
		// no-op
	}
	
}
