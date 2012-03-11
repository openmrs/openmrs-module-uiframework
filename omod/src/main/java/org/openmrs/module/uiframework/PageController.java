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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.ui.framework.page.PageAction;
import org.openmrs.ui.framework.page.PageFactory;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.ui.framework.page.Redirect;
import org.openmrs.ui.framework.session.Session;
import org.openmrs.ui.framework.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 *
 */
@Controller
public class PageController {

	@Autowired
	SessionFactory sessionFactory;
	
	@Autowired
	@Qualifier("corePageFactory")
	PageFactory pageFactory;
	
	@RequestMapping("/pages/{pageName}")
	public String helper1x(@PathVariable("pageName") String pageName, HttpServletRequest request,
	                       HttpServletResponse response, Model model, HttpSession httpSession) {
		String ret = handlePage(pageName, request, response, model, httpSession);
		if ("showHtml".equals(ret))
			return "/module/ui2framework/showHtml";
		else
			return ret;
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
			return "showHtml";
		}
		catch (Redirect redirect) {
			String ret = "redirect:";
			if (!redirect.getUrl().startsWith("/"))
				;
			ret += "/";
			ret += redirect.getUrl();
			return ret;
		}
		catch (PageAction action) {
			throw new RuntimeException("Not Yet Implemented: " + action.getClass(), action);
		}
		catch (RuntimeException ex) {
			if (!Context.isAuthenticated()) {
				// most likely this is an uncaught exception due to the user not being logged in
				// TODO consider whether this is a good idea
				throw new ContextAuthenticationException(ex);
			} else {
				throw ex;
			}
		}
	}
}
