package org.openmrs.ui.framework.page;

import groovy.lang.Writable;
import groovy.text.Template;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;

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
		model.put("param", context.getRequest().getRequest().getParameterMap());
		Writable boundTemplate = model == null ? template.make() : template.make(model);
		if (log.isTraceEnabled())
			log.trace("rendering groovy fragment view with model: " + model);
		return boundTemplate.toString();
	}
	
	@Override
	public String getControllerName() {
		return controllerName;
	}
	
}
