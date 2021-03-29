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
package org.openmrs.module.uiframework;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.openmrs.util.OpenmrsUtil;

/**
 * Modeled after ModuleResourcesServlet in the OpenMRS 1.x web application, and used to provide
 * access to the {@link ResourceFactory} in a web context.
 */
public class ResourceServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private Pattern pathPattern;
	
	public ResourceServlet() {
		pathPattern = Pattern.compile("/.+?/.+?/(.+?)/(.*)");
	}
	
	/**
	 * Used for caching purposes
	 * 
	 * @see javax.servlet.http.HttpServlet#getLastModified(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected long getLastModified(HttpServletRequest req) {
		File f = getFile(req);
		
		if (f == null)
			return super.getLastModified(req);
		
		return f.lastModified();
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("In service method for module servlet: " + request.getPathInfo());
		
		File f = getFile(request);
		if (f == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		response.setDateHeader("Last-Modified", f.lastModified());
		response.setContentLength(new Long(f.length()).intValue());
		String mimeType = getServletContext().getMimeType(f.getName());
		response.setContentType(mimeType);
		
		FileInputStream is = new FileInputStream(f);
		try {
			OpenmrsUtil.copyFile(is, response.getOutputStream());
		}
		finally {
			OpenmrsUtil.closeStream(is);
		}
	}
	
	/**
	 * @param request
	 * @return the requested file
	 */
	protected File getFile(HttpServletRequest request) {
		ResourceFactory factory = ResourceFactory.getInstance();
		
		String path = request.getPathInfo();
		try {
			// path is like "/uiframework/resource/providerName/path/to/resource.png"
			// the first and second elements are this module's id and this servlet's name; we don't care about them
			Matcher matcher = pathPattern.matcher(path);
			if (!matcher.matches())
				throw new IllegalArgumentException("Pattern does not match");
			String providerName = matcher.group(1);
			String resourcePath = matcher.group(2);
			if ("*".equals(providerName))
				providerName = null;
			return factory.getResource(providerName, resourcePath);
			
		}
		catch (Exception ex) {
			if (log.isDebugEnabled())
				log.debug("Invalid resource path: " + path, ex);
			return null;
		}
	}
}
