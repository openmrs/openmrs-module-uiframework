package org.openmrs.ui2.core.fragment;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.text.TemplateEngine;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GroovyFragmentViewProvider implements FragmentViewProvider {
	
	Log log = LogFactory.getLog(getClass());
	
	//config properties
	private File templateDirectory;
	
	// internal data
	TemplateEngine engine = new SimpleTemplateEngine();
	
	// cache these
	Map<String, CachedView> cache = new HashMap<String, CachedView>();
	
	@Override
	public FragmentView getView(String name) {
		File file = new File(templateDirectory, name + ".gsp");
		if (!file.exists())
			return null;
		try {
			CachedView cached = cache.get(name);
			if (cached != null) {
				if (file.lastModified() <= cached.compiledOn) {
					log.trace("using cached view");
					return cached.view;
				}
				log.trace("gsp changed since view was cached");
				cache.remove(name);
			}
			long ts = file.lastModified();
			Template template = engine.createTemplate(file);
			GroovyFragmentView view = new GroovyFragmentView(name, template);
			cache.put(name, new CachedView(view, ts));
			
			return view;
		}
		catch (Exception ex) {
			log.error("Error creating GroovyFragmentView for " + name, ex);
			throw new RuntimeException("Error creating GroovyFragmentView for " + name, ex);
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
	
	class CachedView {
		
		public GroovyFragmentView view;
		
		public long compiledOn;
		
		public CachedView(GroovyFragmentView view, long compiledOn) {
			this.view = view;
			this.compiledOn = compiledOn;
		}
	}
	
}
