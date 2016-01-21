package org.openmrs.ui.framework.page;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.util.OpenmrsUtil;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

/**
 * Generates views based on Groovy templates, stored in ".gsp" files.
 * You may use this view provider in "development mode" by setting "developmentFilePath", in which case
 * views will be dynamically loaded from that filesystem directory.
 * In normal operation, views are loaded from your module's classpath, and the compiled templates are cached.
 */
public class GroovyPageViewProvider implements PageViewProvider {
	
	Log log = LogFactory.getLog(getClass());
	
	//config properties
	private ClassLoader viewClassLoader;
	private String resourcePrefix = "web/module/pages/";

	private Map<String, String> pageDirectoryMap;
	
	private List<File> pageDirectories;
	
	// internal data
	SimpleTemplateEngine engine = new SimpleTemplateEngine(getClass().getClassLoader());

    // cache these
    Map<String, GroovyPageView> cache = new HashMap<String, GroovyPageView>();

    @Override
	public PageView getView(String name) {
        GroovyPageView cached = cache.get(name);
        if (cached != null) {
            return cached;
        }
        try {
            String gsp = getViewContents(name);
            if (gsp == null)
                return null;

            String controllerProviderAndName = null;
            // <!--CONTROLLER:{non-whitespace, not greedy}--> allowing for whitespaces between elements
            Pattern p = Pattern.compile("<!--\\s*CONTROLLER\\s*:\\s*(\\S*?)\\s*-->"); // TODO BW: Can be moved to static attr or constructor
            Matcher m = p.matcher(gsp);
            if (m.find())
                controllerProviderAndName = m.group(1);

            Template template = engine.createTemplate(gsp);
            GroovyPageView compiledView = new GroovyPageView(template, controllerProviderAndName);
            if (pageDirectories == null) {
                // cache for performance, since compiling templates is expensive.
                // Also we suspect that compiling groovy templates leaks permgen memory
                cache.put(name, compiledView);
            }
            return compiledView;
        } catch (Exception ex) {
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
		if (pageDirectories != null) {
			for (File pageDirectory : pageDirectories) {
				// we're in development mode, and we want to dynamically reload views from this filesystem directory
				File file = new File(pageDirectory, name + ".gsp");
				if (file.exists()) {
					return OpenmrsUtil.getFileAsString(file);
				}
			}
			return null;
		}
		// we're not in development mode, so we get the view from the module's classpath 
		else {
			URL resource = (viewClassLoader != null ? viewClassLoader : getClass().getClassLoader()).getResource(resourcePrefix + name + ".gsp");
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
     * @return the viewClassLoader
     */
    public ClassLoader getViewClassLoader() {
    	return viewClassLoader;
    }

    /**
     * @param viewClassLoader the viewClassLoader to set
     */
    public void setViewClassLoader(ClassLoader viewClassLoader) {
    	this.viewClassLoader = viewClassLoader;
    }
    
	public Map<String, String> getPageDirectoryMap() {
		return pageDirectoryMap;
	}
	
	public void setPageDirectoryMap(Map<String, String> pageDirectoryMap) {
		this.pageDirectoryMap = pageDirectoryMap;
	}

	public List<File> getPageDirectories() {
		return pageDirectories;
	}

	public void setPageDirectories(List<File> pageDirectories) {
		this.pageDirectories = pageDirectories;
	}
}