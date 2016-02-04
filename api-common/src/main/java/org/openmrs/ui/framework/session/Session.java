package org.openmrs.ui.framework.session;

import javax.servlet.http.HttpSession;

import org.openmrs.Location;
import org.openmrs.Role;
import org.openmrs.ui.framework.WebConstants;

/**
 * The user's OpenMRS UI session. Stores contextual information like the user's role and chosen location,
 * as well as things like recently-viewed patients.
 */
public class Session {
	
	private HttpSession httpSession;
	
	public Session(HttpSession httpSession) {
		this.httpSession = httpSession;
	}
	
	public boolean isConfigured() {
		return getRole() != null && getLocation() != null;
	}
	
	public void clear() {
		setRole(null);
		setLocation(null);
	}
	
	public void setRole(Role role) {
		setAttribute(WebConstants.SESSION_ROLE_ATTRIBUTE, role);
	}
	
	public Role getRole() {
		Role ret = getAttribute(WebConstants.SESSION_ROLE_ATTRIBUTE, Role.class);
		//if (ret == null)
		//	ret = Context.isAuthenticated() ? Context.getUserService().getRole(RoleConstants.AUTHENTICATED) : Context.getUserService().getRole(RoleConstants.ANONYMOUS);
		return ret;
	}
	
	public void setLocation(Location location) {
		setAttribute(WebConstants.SESSION_LOCATION_ATTRIBUTE, location);
	}
	
	public Location getLocation() {
		return getAttribute(WebConstants.SESSION_LOCATION_ATTRIBUTE, Location.class);
	}
	
	public void setAttribute(String name, Object value) {
		httpSession.setAttribute(name, value);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String name, Class<T> asType) {
		return (T) httpSession.getAttribute(name);
	}
	
}
