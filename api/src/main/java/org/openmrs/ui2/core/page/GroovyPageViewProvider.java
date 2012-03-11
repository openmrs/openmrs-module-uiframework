package org.openmrs.ui2.core.page;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.util.OpenmrsUtil;

/**
 * Generates views based on Groovy templates, stored in ".gsp" files.
 * You may use this view provider in "development mode" by setting "developmentFilePath", in which case
 * views will be dynamically loaded from that filesystem directory.
 * In normal operation, views are loaded from your module's classpath, and the compiled templates are cached.
 */
public class GroovyPageViewProvider implements PageViewProvider {
	
	Log log = LogFactory.getLog(getClass());
	
	//config properties
	private String resourcePrefix = "web/module/pages/";
	private File developmentFolder;
	
	// internal data
	SimpleTemplateEngine engine = new SimpleTemplateEngine();
	
	@Override
	public PageView getView(String name) {
		try {
			String gsp = getViewContents(name);
			if (gsp == null)
				return null;
			
			String controllerName = null;
			// <!--CONTROLLER:{non-whitespace, not greedy}--> allowing for whitespaces between elements
			Pattern p = Pattern.compile("<!--\\s*CONTROLLER\\s*:\\s*(\\S*?)\\s*-->"); // TODO BW: Can be moved to static attr or constructor
			Matcher m = p.matcher(gsp);
			if (m.find())
				controllerName = m.group(1);

			Template template = engine.createTemplate(gsp);
			// TODO maybe configure this template with the request, session, etc. See groovy.servlet.TemplateServlet for guidance
			return new GroovyPageView(template, controllerName);
		}
		catch (Exception ex) {
			log.error("Error creating GroovyPageView", ex);
			throw new RuntimeException("Error creating GroovyPageView", ex);
		}
	}
	
	
	/**
	 * @param name
	 * @return if there's a matching view file for the given name, returns its contents 
	 * @throws Exception
	 */
	public String getViewContents(String name) throws Exception {
		if (developmentFolder != null) {
			// we're in development mode, and we want to dynamically reload views from this filesystem directory
			File file = new File(developmentFolder, name + ".gsp");
			if (!file.exists())
				return null;
			return OpenmrsUtil.getFileAsString(file);
		}
		else {
			// we're not in development mode, so we get the view from the module's classpath 
			URL resource = getClass().getClassLoader().getResource(resourcePrefix + name + ".gsp");
			if (resource == null)
				return null;
			InputStream inputStream = resource.openStream();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			OpenmrsUtil.copyFile(inputStream, outputStream);
			// TODO: what character encoding do URL input streams use?
			String fileContents = outputStream.toString();
			OpenmrsUtil.closeStream(inputStream);
			return fileContents;
		}
	}

	
    /**
     * @return the resourcePrefix
     */
    public String getResourcePrefix() {
    	return resourcePrefix;
    }

	
    /**
     * @param resourcePrefix the resourcePrefix to set
     */
    public void setResourcePrefix(String resourcePrefix) {
    	this.resourcePrefix = resourcePrefix;
    }

    
    /**
     * @return the developmentFolder
     */
    public File getDevelopmentFolder() {
    	return developmentFolder;
    }
	
    
    /**
     * @param developmentFolder the developmentFolder to set
     */
    public void setDevelopmentFolder(File developmentFolder) {
    	this.developmentFolder = developmentFolder;
    }
	
}
