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

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.ui.framework.FormatterImpl;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiFrameworkException;
import org.openmrs.ui.framework.ViewException;
import org.openmrs.ui.framework.WebConstants;
import org.openmrs.ui.framework.fragment.FragmentFactory;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.ObjectResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.openmrs.ui.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Lets clients access pages via:
 * (in 2.x) .../openmrs/(fragmentName)/(action).action
 * (in 1.x) .../openmrs/action/(fragmentName)/(action).form
 */
@Controller
public class FragmentActionController {

	private final Logger log = LoggerFactory.getLogger(FragmentActionController.class);
	
	public final static String SHOW_HTML_VIEW = "/module/uiframework/showHtml";
	
	@Autowired
	@Qualifier("coreFragmentFactory")
	FragmentFactory fragmentFactory;
	
	@RequestMapping("/action/{fragmentName}/{action}")
	public String helper1x(@PathVariable("fragmentName") String fragmentName, @PathVariable("action") String action,
	                       @RequestParam(value = "returnFormat", required = false) String returnFormat,
	                       @RequestParam(value = "successUrl", required = false) String successUrl,
	                       @RequestParam(value = "failureUrl", required = false) String failureUrl,
	                       HttpServletRequest request, Model model, HttpServletResponse response) throws Exception {
		return handleAction(fragmentName, action, returnFormat, successUrl, failureUrl, request, model, response);
	}
	
	@RequestMapping("/action/{directoryName}/{fragmentName}/{action}")
	public String helper1x(@PathVariable("directoryName") String directoryName,
	                       @PathVariable("fragmentName") String fragmentName,
	                       @PathVariable("action") String action,
	                       @RequestParam(value = "returnFormat", required = false) String returnFormat,
	                       @RequestParam(value = "successUrl", required = false) String successUrl,
	                       @RequestParam(value = "failureUrl", required = false) String failureUrl, HttpServletRequest request,
	                       Model model, HttpServletResponse response) throws Exception {
		return handleAction(directoryName + "/" + fragmentName, action, returnFormat, successUrl, failureUrl, request, model, response);
	}

	@RequestMapping("/{directoryName}/{fragmentName}/{action}.action")
	public String handleAction(@PathVariable("directoryName") String directoryName,
	                           @PathVariable("fragmentName") String fragmentName, @PathVariable("action") String action,
	                           @RequestParam(value = "returnFormat", required = false) String returnFormat,
	                           @RequestParam(value = "successUrl", required = false) String successUrl,
	                           @RequestParam(value = "failureUrl", required = false) String failureUrl,
	                           HttpServletRequest request, Model model, HttpServletResponse response) throws Exception {
		return handleAction(directoryName + "/" + fragmentName, action, returnFormat, successUrl, failureUrl, request,
		    model, response);
	}
	
	@RequestMapping("/{fragmentName}/{action}.action")
	public String handleAction(@PathVariable("fragmentName") String fragmentName, @PathVariable("action") String action,
	                           @RequestParam(value = "returnFormat", required = false) String returnFormat,
	                           @RequestParam(value = "successUrl", required = false) String successUrl,
	                           @RequestParam(value = "failureUrl", required = false) String failureUrl,
	                           HttpServletRequest request, Model model, HttpServletResponse response) throws Exception {
		
		if (returnFormat == null) {
			String acceptHeader = request.getHeader("Accept");
			if (StringUtils.isNotEmpty(acceptHeader)) {
				if (acceptHeader.startsWith("application/json")) {
					returnFormat = "json";
				}
			}
		}
		
		Object resultObject;
		try {
			resultObject = fragmentFactory.invokeFragmentAction(fragmentName, action, request);
		}
		catch (Exception ex) {
			// it's possible that the underlying exception is that the user was logged out or lacks privileges
			// and we want to special-case that
			Exception authEx = ExceptionUtil.findExceptionInChain(ex, APIAuthenticationException.class);
			if (authEx != null) {
				resultObject = new FailureResult("#APIAuthenticationException#" + authEx.getMessage());
			} else {
				authEx = ExceptionUtil.findExceptionInChain(ex, ContextAuthenticationException.class);
				if (authEx != null) {
					resultObject = new FailureResult("#APIAuthenticationException#" + authEx.getMessage());
				} else {
					// we don't know how to handle other types of exceptions
					log.error("error", ex);
					throw new UiFrameworkException("Error invoking fragment action", ex);
				}
			}
		}
		
		if (!StringUtils.isEmpty(returnFormat)) {
			// this is an ajax request, so we need to return an object
			
			// turn the non-object result types into ObjectResults
			if (resultObject == null) {
				resultObject = new SuccessResult();
			} else if (resultObject instanceof SuccessResult) {
				SuccessResult success = (SuccessResult) resultObject;
				resultObject = SimpleObject.create("success", "true", "message", success.getMessage());
			} else if (resultObject instanceof FailureResult) {
				FailureResult failure = (FailureResult) resultObject;
				response.setStatus(403);
				resultObject = SimpleObject.create("failure", "true", "globalErrors", failure.getGlobalErrors(),
				    "fieldErrors", failure.getFieldErrorMap());
			} else if (resultObject instanceof ObjectResult) {
				resultObject = ((ObjectResult) resultObject).getWrapped();
			}
			
			Object result;
			if (returnFormat.equals("json")) {
				result = toJson(resultObject);
			} else {
				result = resultObject.toString();
			}
			model.addAttribute("html", result);
			return SHOW_HTML_VIEW;
			
		} else {
			// this is a regular post, so we will return a page
			
			if (successUrl == null)
				successUrl = getSuccessUrl(request);
			if (failureUrl == null)
				failureUrl = getFailureUrl(request, successUrl);
			
			successUrl = removeContextPath(successUrl);
			failureUrl = removeContextPath(failureUrl);
			
			if (resultObject == null || resultObject instanceof SuccessResult) {
				if (resultObject != null) {
					SuccessResult result = (SuccessResult) resultObject;
					if (result.getMessage() != null)
						request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, result.getMessage());
				}
				return "redirect:/" + successUrl;
			} else if (resultObject instanceof FailureResult) {
				// TODO harmonize this with the return-type version
				FailureResult failureResult = (FailureResult) resultObject;
				String errorMessage = null;
				if (failureResult.getSingleError() != null) {
					errorMessage = failureResult.getSingleError();
				} else if (failureResult.getErrors() != null) {
					Errors errors = failureResult.getErrors();
					StringBuilder sb = new StringBuilder();
					sb.append("<ul>");
					for (ObjectError err : errors.getGlobalErrors()) {
						sb.append("<li>" + Context.getMessageSourceService().getMessage(err, Context.getLocale()) + "</li>");
					}
					for (FieldError err : errors.getFieldErrors()) {
						sb.append("<li>" + err.getField() + ": " + Context.getMessageSourceService().getMessage(err, Context.getLocale())
						        + "</li>");
					}
					sb.append("</ul>");
					errorMessage = sb.toString();
				}
				request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, errorMessage);
				return redirectHelper(failureUrl, model);
			} else if (resultObject instanceof ObjectResult) {
				// the best we can do is just display a formatted version of the wrapped object
				String formatted = new FormatterImpl().format(((ObjectResult) resultObject).getWrapped());
				model.addAttribute("html", formatted);
				return SHOW_HTML_VIEW;
				
			} else {
				throw new RuntimeException("Don't know how to handle fragment action result type: "
				        + resultObject.getClass());
			}
		}
	}
	
	private String removeContextPath(String url) {
		if (url != null) {
			if (url.startsWith(WebConstants.CONTEXT_PATH))
				url = url.substring(WebConstants.CONTEXT_PATH.length());
			else if (url.startsWith("/" + WebConstants.CONTEXT_PATH))
				url = url.substring(WebConstants.CONTEXT_PATH.length() + 1);
		}
		return url;
	}
	
	/**
	 * Exposes any query parameter from url
	 * 
	 * @param urlString
	 * @param model
	 * @throws MalformedURLException
	 */
	private String redirectHelper(String urlString, Model model) throws MalformedURLException {
		URL url = new URL(urlString);
		if (url.getQuery() != null) {
			for (StringTokenizer st = new StringTokenizer(url.getQuery(), "&"); st.hasMoreTokens();) {
				String item = st.nextToken();
				int ind = item.indexOf('=');
				String name = item.substring(0, ind);
				String value = URLDecoder.decode(item.substring(ind + 1));
				model.addAttribute(name, value);
			}
		}
		return "redirect:" + removeContextPath(url.getPath());
	}
	
	private String getSuccessUrl(HttpServletRequest request) {
		String referrer = request.getHeader("referer");
		if (referrer != null) {
			return referrer;
		} else {
			throw new RuntimeException("Don't know what to use as success url");
		}
	}
	
	private String getFailureUrl(HttpServletRequest request, String successUrl) {
		String referrer = request.getHeader("referer");
		if (referrer != null)
			return referrer;
		else
			return successUrl;
	}
	
	private String toJson(Object object) {
		try {
			if (object instanceof ObjectResult) {
				object = ((ObjectResult) object).getWrapped();
			}
			ObjectMapper mapper = new ObjectMapper();
			StringWriter sw = new StringWriter();
			mapper.writeValue(sw, object);
			return sw.toString();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw new ViewException("Error generating JSON", ex);
		}
	}
	
}
