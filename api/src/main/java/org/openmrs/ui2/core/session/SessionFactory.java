package org.openmrs.ui2.core.session;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ui2.core.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Manages sessions for the OpenMRS UI Framework
 */
public class SessionFactory {
	
	private Log log = LogFactory.getLog(getClass());
	
	@Autowired(required = false)
	List<SessionListener> sessionListeners;
	
	public SessionFactory() {
		SessionFactory.instance = this;
	}
	
	/**
	 * Ensures that a {@link Session} exists on the given http session, creating it (and
	 * notifying {@link SessionListener}s) if necessary.
	 * @param httpSession
	 * @return
	 */
	public Session ensureSession(HttpSession httpSession) {
		synchronized (httpSession) {
			Session session = (Session) httpSession.getAttribute(WebConstants.SESSION_SESSION_ATTRIBUTE);
			if (session == null) {
				session = new Session(httpSession);
				httpSession.setAttribute(WebConstants.SESSION_SESSION_ATTRIBUTE, session);
				if (sessionListeners != null) {
					for (SessionListener listener : sessionListeners) {
						try {
							listener.afterSessionCreated(session);
						}
						catch (Exception ex) {
							log.error("Exception in SessionListener.afterSessionCreated", ex);
						}
					}
				}
			}
			return session;
		}
	}
	
	/**
	 * Gets the {@link Session} on the given http session. (This method will create one if it doesn't exist,
	 * so this method is actually equivalent to ensureSession, but each name is semantically better in a
	 * different situation.)
	 * @param httpSession
	 * @return
	 */
	public Session getSession(HttpSession httpSession) {
		Session ret = (Session) httpSession.getAttribute(WebConstants.SESSION_SESSION_ATTRIBUTE);
		if (ret != null)
			return ret;
		else
			return ensureSession(httpSession);
	}
	
	/**
	 * Removes the {@link Session} from the given http session, and notifies {@link SessionListener}s.
	 * @param httpSession
	 */
	public void destroySession(HttpSession httpSession) {
		Session session;
		try {
			session = (Session) httpSession.getAttribute(WebConstants.SESSION_SESSION_ATTRIBUTE);
		} catch (ClassCastException ex) {
			// this means the UI Framework module was reloaded
			httpSession.removeAttribute(WebConstants.SESSION_SESSION_ATTRIBUTE);
			return;
		}
		if (session != null) {
			if (sessionListeners != null) {
				for (SessionListener listener : sessionListeners) {
					try {
						listener.beforeSessionDestroyed(session);
					}
					catch (Exception ex) {
						log.error("Exception in SessionListener.beforeSessionDestroyed", ex);
					}
				}
			}
			session.setLocation(null);
			session.setRole(null);
			httpSession.removeAttribute(WebConstants.SESSION_SESSION_ATTRIBUTE);
		}
	}
	
	// We keep a static singleton instance because we need to access this from LoginServlet, which is outside of Spring
	private static SessionFactory instance;
	
	public static SessionFactory getInstance() {
		return instance;
	}
	
}
