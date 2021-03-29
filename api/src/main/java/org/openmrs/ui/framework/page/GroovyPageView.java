package org.openmrs.ui.framework.page;

import groovy.lang.Writable;
import groovy.text.Template;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.ui.framework.ProviderAndName;
import org.openmrs.ui.framework.ViewException;
import org.openmrs.ui.framework.WebConstants;
import org.openmrs.ui.util.ExceptionUtil;
import org.openmrs.util.OpenmrsUtil;

import java.util.Map;

public class GroovyPageView implements PageView {
	
	Log log = LogFactory.getLog(getClass());
	
	private String controllerProviderAndName;
	
	private Template template;
	
	public GroovyPageView(Template template, String controllerProviderAndName) {
		this.template = template;
		this.controllerProviderAndName = controllerProviderAndName;
	}
	
	@Override
	public String render(PageContext context) {
		Map<String, Object> model = context.getModel();
		model.put("ui", new PageUiUtils(context));
		model.put("context", new Context()); // used for its static methods
		model.put("contextPath", WebConstants.CONTEXT_PATH);
		model.put("session", context.getRequest().getSession());
		model.put("param", context.getRequest().getRequest().getParameterMap());
		Writable boundTemplate = model == null ? template.make() : template.make(model);
		if (log.isTraceEnabled())
			log.trace("rendering groovy fragment view with model: " + model);
		try {
			return boundTemplate.toString();
		}
		catch (RuntimeException ex) {
			APIAuthenticationException authEx = ExceptionUtil.findExceptionInChain(ex, APIAuthenticationException.class);
			if (authEx != null)
				throw authEx;
			
			ContextAuthenticationException cAuthEx = ExceptionUtil.findExceptionInChain(ex,
			    ContextAuthenticationException.class);
			if (cAuthEx != null)
				throw cAuthEx;
			
			String message = "Error rendering page view for " + context.getRequest().getPageName() + ". ";
			message += "Model properties:\n" + OpenmrsUtil.join(model.keySet(), " \n");
			throw new ViewException(message, ex);
		}
		
	}
	
	/**
	 * @see org.openmrs.ui.framework.page.PageView#getController()
	 */
	@Override
	public ProviderAndName getController() {
		if (controllerProviderAndName != null) {
			String provider;
			String controller;
			String[] temp = controllerProviderAndName.split(":");
			if (temp.length == 1) {
				provider = "*";
				controller = temp[0];
			} else {
				provider = temp[0];
				controller = temp[1];
			}
			return new ProviderAndName(provider, controller);
		} else {
			return null;
		}
	}
	
}
