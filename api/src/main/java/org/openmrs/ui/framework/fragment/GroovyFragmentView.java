package org.openmrs.ui.framework.fragment;

import groovy.lang.MissingPropertyException;
import groovy.lang.Writable;
import groovy.text.Template;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.ui.framework.FragmentException;
import org.openmrs.ui.framework.Model;
import org.openmrs.ui.framework.ViewException;
import org.openmrs.ui.framework.WebConstants;
import org.openmrs.ui.util.ExceptionUtil;

public class GroovyFragmentView implements FragmentView {
	
	Log log = LogFactory.getLog(getClass());
	
	private String viewName;
	
	private Template template;
	
	public GroovyFragmentView(String viewName, Template template) {
		this.viewName = viewName;
		this.template = template;
	}
	
	@Override
	public String render(FragmentContext context) {
		Model model = context.getMergedModel();
		model.put("config", context.getRequest().getConfiguration());
		model.put("session", context.getPageContext().getRequest().getSession());
		model.put("ui", new FragmentUiUtils(context));
		model.put("context", new Context()); // used for its static methods
		model.put("contextPath", WebConstants.CONTEXT_PATH);
		Writable boundTemplate = model == null ? template.make() : template.make(model);
		if (log.isTraceEnabled())
			log.trace("rendering groovy fragment view with model: " + model);
		// TODO add a way for the view to redirect. Perhaps this should happen via the context
		// instead of via a return value
		try {
			return boundTemplate.toString();
		}
		catch (MissingPropertyException ex) {
			StringBuilder sb = new StringBuilder();
			sb.append("In view '" + viewName + "', could not find property '" + ex.getProperty() + "'.\n");
			sb.append("Passed from controller: ");
			for (Map.Entry<String, Object> e : model.entrySet()) {
				sb.append("\n  " + e.getKey() + " -> " + e.getValue());
			}
			throw new ViewException(sb.toString());
		}
		catch (ViewException ex) {
			throw new ViewException("(in '" + viewName + "')\n" + ex.getMessage());
		}
		catch (ContextAuthenticationException ex) {
			throw ex;
		}
		catch (APIAuthenticationException ex) {
			throw ex;
		}
		catch (Exception ex) {
			APIAuthenticationException authEx = ExceptionUtil.findExceptionInChain(ex, APIAuthenticationException.class);
			if (authEx != null)
				throw authEx;

			throw new FragmentException("Error evaluating view: " + viewName, ex);
		}
	}
	
}
