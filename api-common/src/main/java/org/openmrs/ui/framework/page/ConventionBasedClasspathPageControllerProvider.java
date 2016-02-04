package org.openmrs.ui.framework.page;

import java.io.File;

import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.DevelopmentClassLoader;

public class ConventionBasedClasspathPageControllerProvider implements PageControllerProvider {
	
	private String basePackage;
	
	private File developmentFolder;
	
	@Override
	public Object getController(String pageName) {
		StringBuilder className = new StringBuilder();
		className.append(basePackage).append('.').append(pageName.replaceAll("/", ".")).append("PageController");
		if (developmentFolder == null)
			return getClassIfExistsInProductionMode(capitalizeClassName(className).toString());
		else
			return getClassIfExistsInDevelopmentMode(capitalizeClassName(className).toString());
	}
	
	private Object getClassIfExistsInProductionMode(String className) {
		// Note: I tried caching controllers in a map after fetching them, and did not see a
		// significant performance improvement.
		try {
			Class<?> clazz = Context.loadClass(className);
			return clazz.newInstance();
		}
		catch (ClassNotFoundException ex) {
			return null;
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private Object getClassIfExistsInDevelopmentMode(String className) {
		try {
			Class<?> clazz = new DevelopmentClassLoader(developmentFolder, basePackage).loadClass(className);
			return clazz.newInstance();
		}
		catch (ClassNotFoundException ex) {
			return null;
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private StringBuilder capitalizeClassName(StringBuilder className) {
		int lastDot = className.lastIndexOf(".");
		className.setCharAt(lastDot + 1, Character.toUpperCase(className.charAt(lastDot + 1)));
		return className;
	}
	
	/**
	 * @return the basePackage
	 */
	public String getBasePackage() {
		return basePackage;
	}
	
	/**
	 * @param basePackage the basePackage to set
	 */
	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
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
