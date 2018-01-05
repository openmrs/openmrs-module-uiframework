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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.ui.framework.UiFrameworkException;
import org.openmrs.ui.framework.WebConstants;
import org.openmrs.ui.framework.page.FileDownload;
import org.openmrs.ui.framework.page.PageAction;
import org.openmrs.ui.framework.page.PageFactory;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.ui.framework.page.Redirect;
import org.openmrs.ui.framework.session.Session;
import org.openmrs.ui.framework.session.SessionFactory;
import org.openmrs.ui.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Lets clients access pages via:
 * (in 1.8.4+) .../openmrs/provider/subfolder/pageName.page
 * (in 1.8-1.8.3) .../openmrs/pages/provider/subfolder/pageName.form
 */
@Controller
public class PageController {

	public final static String SHOW_HTML_VIEW = "/module/uiframework/showHtml";
	
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	SessionFactory sessionFactory;
	
	@Autowired
	@Qualifier("corePageFactory")
	PageFactory pageFactory;

	/**
	 * Since the 1.x web application only lets spring handle certain file extensions (not including .page) we
	 * need to let people access pages like: ".../openmrs/pages/home.form"
	 */
	@RequestMapping("/pages/**")
	public String handleUrlStartingWithPage(HttpServletRequest request, HttpServletResponse response, Model model, HttpSession httpSession) {
        // everything after the contextPath, e.g. "/pages/mymodule/examples.form", "/pages/emr/registration/checkin.form"
        String path = request.getServletPath();
        path = path.substring("/pages/".length(), path.lastIndexOf("."));
		return handlePath(path, request, response, model, httpSession);
	}

    @RequestMapping("**/*.page")
    public String handleUrlWithDotPage(HttpServletRequest request, HttpServletResponse response, Model model, HttpSession httpSession) {
        // everything after the contextPath, e.g. "/mymodule/examples.page", "/emr/registration/checkin.page"
        String path = request.getServletPath();
        path = path.substring(1, path.lastIndexOf(".page"));
        return handlePath(path, request, response, model, httpSession);
    }

    /**
     * @param path should be of the form "provider/optional/subdirectories/pageName"
     * @param request
     * @param response
     * @param model
     * @param httpSession
     * @return
     */
    public String handlePath(String path, HttpServletRequest request, HttpServletResponse response, Model model, HttpSession httpSession) {
        // handle the case where the url has two slashes, e.g. host/openmrs//emr/patient.page
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        int index = path.indexOf("/");
        if (index < 0) {
            throw new IllegalArgumentException("page request must have at least provider/pageName, but this does not: " + request.getRequestURI());
        }
        String providerName = path.substring(0, index);
        String pageName = path.substring(index + 1);

        Session session;
        try {
            session = sessionFactory.getSession(httpSession);
        } catch (ClassCastException ex) {
            // this means that the UI Framework module was reloaded
            sessionFactory.destroySession(httpSession);
            session = sessionFactory.getSession(httpSession);
        }
        PageRequest pageRequest = new PageRequest(providerName, pageName, request, response, session);
        try {
            String html = pageFactory.handle(pageRequest);
            model.addAttribute("html", html);
            return SHOW_HTML_VIEW;
        } catch (Redirect redirect) {
            String ret = "";
            if (!redirect.getUrl().startsWith("/"))
                ret += "/";
            ret += redirect.getUrl();
            if (ret.startsWith("/" + WebConstants.CONTEXT_PATH + "/")) {
                ret = ret.substring(WebConstants.CONTEXT_PATH.length() + 1);
            }
            
            setRedirectUrl(request);
            
            return "redirect:" + ret;
        } catch (FileDownload download) {
            response.setContentType(download.getContentType());
            response.setHeader("Content-Disposition", "attachment; filename=" + download.getFilename());
            try {
                IOUtils.copy(new ByteArrayInputStream(download.getFileContent()), response.getOutputStream());
                response.flushBuffer();
            } catch (IOException ex) {
                throw new UiFrameworkException("Error trying to write file content to response", ex);
            }
            return null;
        } catch (PageAction action) {
            throw new RuntimeException("Not Yet Implemented: " + action.getClass(), action);
        } catch (RuntimeException ex) {
            // special-case if this is due to the user not being logged in
            APIAuthenticationException authEx = ExceptionUtil.findExceptionInChain(ex, APIAuthenticationException.class);
            if (authEx != null) {
            	setRedirectUrl(request);
                throw authEx;
            }
            

            // The following should go in an @ExceptionHandler. I tried this, and it isn't getting invoked for some reason.
            // And it's not worth debugging that.

            log.error(ex.getMessage(),ex);
            
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
    
    private void setRedirectUrl(HttpServletRequest request) {
    	StringBuffer url = request.getRequestURL();
    	String queryStr = request.getQueryString();
    	if (StringUtils.isNotBlank(queryStr)) {
    		url = url.append("?").append(queryStr);
    	}
        request.getSession().setAttribute("_REFERENCE_APPLICATION_REDIRECT_URL_", url);
    }

    /**
     * @param pageFactory the pageFactory to set
     */
    public void setPageFactory(PageFactory pageFactory) {
	    this.pageFactory = pageFactory;
    }
    
    /**
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
	    this.sessionFactory = sessionFactory;
    }
    
}
