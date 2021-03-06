package org.openmrs.ui.framework.fragment;

import java.io.File;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.DevelopmentClassLoader;

public class ConventionBasedClasspathFragmentControllerProvider implements FragmentControllerProvider {
	
	private String basePackage;
	
	private List<File> developmentFolders;
	
	private List<String> developmentFolderNames;
	
	@Override
	public Object getController(String fragmentName) {
		StringBuilder className = new StringBuilder();
		className.append(basePackage).append('.').append(fragmentName.replaceAll("/", ".")).append("FragmentController");
		if (developmentFolders == null)
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
			Class<?> clazz = new DevelopmentClassLoader(developmentFolders, basePackage).loadClass(className);
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
	
	public List<File> getDevelopmentFolders() {
		return developmentFolders;
	}
	
	public void setDevelopmentFolders(List<File> developmentFolders) {
		this.developmentFolders = developmentFolders;
	}
	
	public List<String> getDevelopmentFolderNames() {
		return developmentFolderNames;
	}
	
	public void setDevelopmentFolderNames(List<String> developmentFolderNames) {
		this.developmentFolderNames = developmentFolderNames;
	}
}
