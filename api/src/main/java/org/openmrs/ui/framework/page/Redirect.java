package org.openmrs.ui.framework.page;

import javax.servlet.http.HttpServletRequest;

public class Redirect extends PageAction {
	
	private static final long serialVersionUID = 1L;
	
	private String url;
	
	/**
	 * @param applicationUrl an URL starting after the server-plus-context-path part. (e.g.
	 *            "yourmodule/home.page")
	 */
	public Redirect(String applicationUrl) {
		this.url = applicationUrl;
	}
	
	/**
	 * Redirects to the given page
	 * 
	 * @param pageName
	 * @param queryString
	 */
	public Redirect(String providerName, String pageName, String queryString) {
		this.url = providerName + "/" + pageName + ".page";
		if (queryString != null)
			this.url += "?" + queryString;
	}
	
	/**
	 * The equivalent of redirecting back to the url specified in request. Query string parameters
	 * will be included but any POST parameter will not be.
	 * 
	 * @param request
	 */
	public Redirect(HttpServletRequest request) {
		String ret = request.getServletPath();
		if (request.getPathInfo() != null)
			ret += request.getPathInfo();
		if (request.getQueryString() != null)
			ret += "?" + request.getQueryString();
		this.url = ret.replaceAll("//", "/");
	}
	
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
}
