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

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.WebConstants;
import org.openmrs.ui.framework.page.PageAction;
import org.openmrs.ui.framework.page.PageFactory;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.ui.framework.page.Redirect;
import org.openmrs.ui.framework.session.Session;
import org.openmrs.ui.framework.session.SessionFactory;
import org.openmrs.ui.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Lets clients access pages via:
 * (in 2.x) .../openmrs/pageName.page
 * (in 1.x) .../openmrs/pages/pageName.form
 */
@Controller
public class PageController {

	public final static String SHOW_HTML_VIEW = "/module/uiframework/showHtml";
	
	@Autowired
	SessionFactory sessionFactory;
	
	@Autowired
	@Qualifier("corePageFactory")
	PageFactory pageFactory;
	
	/**
	 * Since the 1.x web application only lets spring handle certain file extensions (not including .page) we
	 * need to let people access pages like: ".../openmrs/pages/home.form"
	 */
	@RequestMapping("/pages/{pageName}")
	public String helper1x(@PathVariable("pageName") String pageName, HttpServletRequest request,
	                       HttpServletResponse response, Model model, HttpSession httpSession) {
		return handlePage(pageName, request, response, model, httpSession);
	}
	
	@RequestMapping("/{pageName}.page")
	public String handlePage(@PathVariable("pageName") String pageName, HttpServletRequest request,
	        HttpServletResponse response, Model model, HttpSession httpSession) {
		Session session;
		try {
			session = sessionFactory.getSession(httpSession);
		} catch (ClassCastException ex) {
			// this means that the UI Framework module was reloaded
			sessionFactory.destroySession(httpSession);
			session = sessionFactory.getSession(httpSession);
		}
		PageRequest pageRequest = new PageRequest(pageName, request, response, session);
		try {
			String html = pageFactory.handle(pageRequest);
			model.addAttribute("html", html);
			return SHOW_HTML_VIEW;
		}
		catch (Redirect redirect) {
			String ret = "";
			if (!redirect.getUrl().startsWith("/"))
				ret += "/";
			ret += redirect.getUrl();
			if (ret.startsWith("/" + WebConstants.CONTEXT_PATH)) {
				ret = ret.substring(WebConstants.CONTEXT_PATH.length() + 1);
			}
			return "redirect:" + ret;
		}
		catch (PageAction action) {
			throw new RuntimeException("Not Yet Implemented: " + action.getClass(), action);
		}
		catch (RuntimeException ex) {
			// special-case if this is due to the user not being logged in
			APIAuthenticationException authEx = ExceptionUtil.findExceptionInChain(ex, APIAuthenticationException.class);
			if (authEx != null) {
				throw authEx;
			}

			// The following should go in an @ExceptionHandler. I tried this, and it isn't getting invoked for some reason.
			// And it's not worth debugging that.
			
			StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw));
			model.addAttribute("fullStacktrace", sw.toString());
			
			Throwable t = ex;
			while (t.getCause() != null && !t.equals(t.getCause()))
				t = t.getCause();
			sw = new StringWriter();
			t.printStackTrace(new PrintWriter(sw));
			model.addAttribute("rootStacktrace", sw.toString());
			
			return "/module/uiframework/uiError";
		}
	}
	
}
