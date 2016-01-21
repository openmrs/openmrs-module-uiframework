package org.openmrs.ui.framework.fragment;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.text.TemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.util.OpenmrsUtil;

public class GroovyFragmentViewProvider implements FragmentViewProvider {
	
	Log log = LogFactory.getLog(getClass());
	
	//config properties
	private ClassLoader viewClassLoader;
	private String resourcePrefix = "web/module/fragments/";
	private File developmentFolder;
	
	// internal data
	TemplateEngine engine = new SimpleTemplateEngine(getClass().getClassLoader());
	
	// cache these
	Map<String, GroovyFragmentView> cache = new HashMap<String, GroovyFragmentView>();
	
	@Override
	public FragmentView getView(String name) {
		try {
			String gsp = getViewContents(name);
			if (gsp == null)
				return null;

			if (developmentFolder != null) {
				// we are in development mode, so we do not cache view templates
				Template template = engine.createTemplate(gsp);
				GroovyFragmentView view = new GroovyFragmentView(name, template);
				return view;
			}
			else {
				// cache for performance, since compiling templates is expensive
				GroovyFragmentView cached = cache.get(name);
				if (cached == null) {
					if (log.isDebugEnabled())
						log.debug("generating Groovy Template for view: " + name);
					Template template = engine.createTemplate(gsp);
					GroovyFragmentView view = new GroovyFragmentView(name, template);
					cache.put(name, view);
					cached = view;
				}
				return cached;
			}
		}
		catch (Exception ex) {
			log.error("Error creating GroovyFragmentView for " + name, ex);
			throw new RuntimeException("Error creating GroovyFragmentView for " + name, ex);
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
    
}
