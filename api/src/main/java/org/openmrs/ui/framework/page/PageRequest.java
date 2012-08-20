package org.openmrs.ui.framework.page;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.ui.framework.AttributeExpressionException;
import org.openmrs.ui.framework.AttributeHolder;
import org.openmrs.ui.framework.AttributeHolderUtil;
import org.openmrs.ui.framework.session.Session;

/**
 * A request for a provider and page (which may be mapped by a {@link PageRequestMapper} to a
 * different providerNameOverride and pageNameOverride)
 */
public class PageRequest implements AttributeHolder {
	
	private String providerName;
	
	private String pageName;
	
	private String providerNameOverride;
	
	private String pageNameOverride;
	
	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	private Session session;
	
	public PageRequest(String providerName, String pageName, HttpServletRequest request, HttpServletResponse response,
	    Session session) {
		this.providerName = providerName;
		this.pageName = pageName;
		this.request = request;
		this.response = response;
		this.session = session;
	}
	
	/**
	 * @return the providerName
	 */
	public String getProviderName() {
		return providerName;
	}
	
	/**
	 * @param providerName the providerName to set
	 */
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	
	/**
	 * @return the pageName
	 */
	public String getPageName() {
		return pageName;
	}
	
	/**
	 * @param pageName the pageName to set
	 */
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	
	/**
	 * @return the request
	 */
	public HttpServletRequest getRequest() {
		return request;
	}
	
	/**
	 * @param request the request to set
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	/**
	 * @return the response
	 */
	public HttpServletResponse getResponse() {
		return response;
	}
	
	/**
	 * @param response the response to set
	 */
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	/**
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}
	
	/**
	 * @param session the session to set
	 */
	public void setSession(Session session) {
		this.session = session;
	}
	
	/**
	 * @return the providerNameOverride
	 */
	public String getProviderNameOverride() {
		return providerNameOverride;
	}
	
	/**
	 * @param providerNameOverride the providerNameOverride to set
	 */
	public void setProviderNameOverride(String providerNameOverride) {
		this.providerNameOverride = providerNameOverride;
	}
	
	/**
	 * @return the pageNameOverride
	 */
	public String getPageNameOverride() {
		return pageNameOverride;
	}
	
	/**
	 * @param pageNameOverride the pageNameOverride to set
	 */
	public void setPageNameOverride(String pageNameOverride) {
		this.pageNameOverride = pageNameOverride;
	}
	
	/**
	 * @see org.openmrs.ui.framework.AttributeHolder#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String name) {
		return request.getParameter(name);
	}
	
	/**
	 * @see org.openmrs.ui.framework.AttributeHolder#require(java.lang.String[])
	 */
	@Override
	public void require(String... expressions) throws AttributeExpressionException {
		List<String> failed = AttributeHolderUtil.unsatisfiedExpressions(this, expressions);
		if (failed.size() > 0) {
			throw new AttributeExpressionException(expressions, failed);
		}
	}
	
	/**
	 * @return providerNameOverride ?: providerName
	 */
	public String getMappedProviderName() {
		return providerNameOverride != null ? providerNameOverride : providerName;
	}
	
	/**
	 * @return pageNameOverride ?: pageName
	 */
	public String getMappedPageName() {
		return pageNameOverride != null ? pageNameOverride : pageName;		
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
	    return "Request for " + providerName + ":" + pageName + " (mapped to " + providerNameOverride + ":" + pageNameOverride + ")";
	}
}
