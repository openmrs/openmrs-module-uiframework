package org.openmrs.ui2.core.page;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.util.OpenmrsUtil;

public class GroovyPageViewProvider implements PageViewProvider {
	
	Log log = LogFactory.getLog(getClass());
	
	//config properties
	private File templateDirectory;
	
	// internal data
	SimpleTemplateEngine engine = new SimpleTemplateEngine();
	
	@Override
	public PageView getView(String name) {
		File file = new File(templateDirectory, name + ".gsp");
		if (!file.exists())
			return null;
		try {
			// get the file contents as text so we can determine the controller this view requests
			String controllerName = null;
			String fileContents = OpenmrsUtil.getFileAsString(file);
			// <!--CONTROLLER:{non-whitespace, not greedy}--> allowing for whitespaces between elements
			Pattern p = Pattern.compile("<!--\\s*CONTROLLER\\s*:\\s*(\\S*?)\\s*-->"); // TODO BW: Can be moved to static attr or constructor
			Matcher m = p.matcher(fileContents);
			if (m.find())
				controllerName = m.group(1);
			Template template = engine.createTemplate(fileContents);
			// TODO maybe configure this template with the request, session, etc. See groovy.servlet.TemplateServlet for guidance
			return new GroovyPageView(template, controllerName);
		}
		catch (Exception ex) {
			log.error("Error creating GroovyPageView", ex);
			throw new RuntimeException("Error creating GroovyPageView", ex);
		}
	}
	
	/**
	 * @return the templateDirectory
	 */
	public File getTemplateDirectory() {
		return templateDirectory;
	}
	
	/**
	 * @param templateDirectory the templateDirectory to set
	 */
	public void setTemplateDirectory(File templateDirectory) {
		this.templateDirectory = templateDirectory;
	}
	
}
