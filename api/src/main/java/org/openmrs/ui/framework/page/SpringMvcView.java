/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.ui.framework.page;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.ui.framework.WebConstants;
import org.openmrs.ui.framework.fragment.FragmentContext;
import org.openmrs.ui.framework.fragment.FragmentView;

/**
 * A view that will effectively do a jsp:include of the given page (with this application's servlet).
 * This is able to implement _both_ {@link PageView} and {@link FragmentView} because it ignores most
 * of the PageContext and FragmentContext, and just uses the configured requestMapping.
 */
public class SpringMvcView implements PageView, FragmentView {
	
	private String requestMapping;
	
	private String trimContentStart;
	
	private String trimContentEnd;
	
	private Map<String, String> urlsToShortenToPages;
	
	public SpringMvcView(String requestMapping, String trimContentStart, String trimContentEnd,
	    Map<String, String> urlsToShorten) {
		this.requestMapping = requestMapping;
		this.trimContentStart = trimContentStart;
		this.trimContentEnd = trimContentEnd;
		this.urlsToShortenToPages = urlsToShorten;
	}
	
	/**
	 * @see org.openmrs.ui.framework.page.PageView#getControllerName()
	 */
	@Override
	public String getControllerName() {
		// TODO force this to be controller-less 
		return null;
	}
	
	/**
	 * @see org.openmrs.ui.framework.page.PageView#render(org.openmrs.ui.framework.page.PageContext)
	 */
	@Override
	public String render(PageContext context) throws PageAction {
		return doRender(context, true);
	}
	
	/**
	 * @see org.openmrs.ui.framework.fragment.FragmentView#render(org.openmrs.ui.framework.fragment.FragmentContext)
	 */
	@Override
	public String render(FragmentContext context) throws PageAction {
		return doRender(context.getPageContext(), false);
	}
	
	/**
	 * 
	 * Does an include based on the HttpServletRequest and response underlying the original page request
	 * 
	 * @param context
	 * @return
	 */
	private String doRender(PageContext context, boolean allowRedirects) {
		HttpServletRequest request = context.getRequest().getRequest();
		RequestDispatcher dispatcher = request.getRequestDispatcher(requestMapping);
		ContentCapturingHttpServletResponse response = new ContentCapturingHttpServletResponse(context.getRequest()
		        .getResponse());
		try {
			dispatcher.include(request, response);
			
			if (response.getRedirect() != null) {
				String redirect = response.getRedirect();
				
				// if the we're redirecting to login.htm, handle this as a special case so we preserve the requested url after login
				if (redirect.equals(WebConstants.CONTEXT_PATH + "/login.htm"))
					throw new ContextAuthenticationException("included Spring MVC page redirects to login");
				
				if (allowRedirects) {
					redirect = maybeReplaceWithPage(redirect);
					// send this redirect through to the underlying HttpServletResponse
					context.getRequest().getResponse().sendRedirect(response.getRedirect());
					return "redirect:" + response.getRedirect();
				} else {
					System.out.println("Tried to redirect to " + response.getRedirect() + " but redirects not allowed");
				}
			}
			
			String ret = response.getContentAsString();
			
			ret = trimContent(ret);
			
			// TODO remove inclusion of javascript and css from returned html and include it in page context
			
			return ret;
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * if the redirect is to an url that we're exposing as a page, replace it before returning
	 * 
	 * @param redirect
	 * @return
	 */
	private String maybeReplaceWithPage(String redirect) {
		if (urlsToShortenToPages == null)
			return redirect;
		
		// redirect starts with the context path, but the map entries won't
		String temp = redirect.substring(WebConstants.CONTEXT_PATH.length());
		String[] urlAndQuery = temp.split("\\?", 2);
		String replacedWithoutQuery = urlsToShortenToPages.get(urlAndQuery[0]);
		if (replacedWithoutQuery != null)
			return WebConstants.CONTEXT_PATH + "/" + replacedWithoutQuery + ".page?" + urlAndQuery[1];
		else
			return redirect;
	}
	
	/**
	 * To aid 1.x/2.x compatibility, allow a comment to indicate when the content to include starts and ends
	 * 
	 * @return
	 * @should trim content before starting comment
	 * @should trim content after ending comment
	 * @should trim content between comments
	 * @should not trim content if there are no comments
	 */
	String trimContent(String string) {
		int index = string.indexOf(trimContentStart);
		if (index > 0)
			string = string.substring(index + trimContentStart.length());
		index = string.indexOf(trimContentEnd);
		if (index > 0)
			string = string.substring(0, index);
		return string;
	}
	
	/**
	 * Idea copied from org.apache.taglibs.standard.tag.common.core.ImportSupport$ImportResponseWrapper
	 * Captures content written to the writer, as well as redirects.
	 */
	class ContentCapturingHttpServletResponse extends HttpServletResponseWrapper {
		
		private StringWriter sw = new StringWriter();
		
		private String redirect;
		
		/**
		 * @param response
		 */
		public ContentCapturingHttpServletResponse(HttpServletResponse response) {
			super(response);
		}
		
		public String getContentAsString() {
			return sw.toString();
		}
		
		/**
		 * @see javax.servlet.ServletResponseWrapper#getWriter()
		 */
		@Override
		public PrintWriter getWriter() throws IOException {
			return new PrintWriter(sw);
		}
		
		/**
		 * @see javax.servlet.ServletResponseWrapper#getOutputStream()
		 */
		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			throw new RuntimeException("Can only get a writer");
		}
		
		/**
		 * @see javax.servlet.http.HttpServletResponseWrapper#sendRedirect(java.lang.String)
		 */
		@Override
		public void sendRedirect(String location) throws IOException {
			this.redirect = location;
			//super.sendRedirect(location);
		}
		
		public String getRedirect() {
			return redirect;
		}
		
	}
	
}
