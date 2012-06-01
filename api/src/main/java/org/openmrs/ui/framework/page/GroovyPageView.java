package org.openmrs.ui.framework.page;

import groovy.lang.Writable;
import groovy.text.Template;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.ui.framework.FragmentException;
import org.openmrs.ui.framework.WebConstants;
import org.openmrs.ui.util.ExceptionUtil;

public class GroovyPageView implements PageView {
	
	Log log = LogFactory.getLog(getClass());
	
	private String controllerName;
	
	private Template template;
	
	public GroovyPageView(Template template, String controllerName) {
		this.template = template;
		this.controllerName = controllerName;
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
		} catch (RuntimeException ex) {
			APIAuthenticationException authEx = ExceptionUtil.findExceptionInChain(ex, APIAuthenticationException.class);
			if (authEx != null)
				throw authEx;
			
			ContextAuthenticationException cAuthEx = ExceptionUtil.findExceptionInChain(ex, ContextAuthenticationException.class);
			if (cAuthEx != null)
				throw cAuthEx;  

			throw ex;
		}

	}
	
	@Override
	public String getControllerName() {
		return controllerName;
	}
	
}
