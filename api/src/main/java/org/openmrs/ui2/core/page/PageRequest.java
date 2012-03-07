package org.openmrs.ui2.core.page;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.ui2.core.AttributeExpressionException;
import org.openmrs.ui2.core.AttributeHolder;
import org.openmrs.ui2.core.AttributeHolderUtil;
import org.openmrs.ui2.core.session.Session;

public class PageRequest implements AttributeHolder {
	
	private String pageName;
	
	private String internalPageName;
	
	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	private Session session;
	
	public PageRequest(String pageName, HttpServletRequest request, HttpServletResponse response, Session session) {
		this.pageName = pageName;
		this.request = request;
		this.response = response;
		this.session = session;
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
	 * @return the internalPageName
	 */
	public String getInternalPageName() {
		return internalPageName;
	}
	
	/**
	 * @param internalPageName the internalPageName to set
	 */
	public void setInternalPageName(String internalPageName) {
		this.internalPageName = internalPageName;
	}
	
	/**
	 * @see org.openmrs.ui2.core.AttributeHolder#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String name) {
		return request.getParameter(name);
	}
	
	/**
	 * @see org.openmrs.ui2.core.AttributeHolder#require(java.lang.String[])
	 */
	@Override
	public void require(String... expressions) throws AttributeExpressionException {
		List<String> failed = AttributeHolderUtil.unsatisfiedExpressions(this, expressions);
		if (failed.size() > 0) {
			throw new AttributeExpressionException(expressions, failed);
		}
	}
	
}
