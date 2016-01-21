package org.openmrs.ui.framework.fragment;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.DevelopmentClassLoader;

public class ConventionBasedClasspathFragmentControllerProvider implements FragmentControllerProvider {
	
	private String basePackage;

	private Map<String, String> classDirectoryMap;
	
	private List<File> classDirectories;
	
	@Override
	public Object getController(String fragmentName) {
		StringBuilder className = new StringBuilder();
		className.append(basePackage).append('.').append(fragmentName.replaceAll("/", ".")).append("FragmentController");
		if (classDirectories == null)
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
			Class<?> clazz = new DevelopmentClassLoader(classDirectories, basePackage).loadClass(className);
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

	public Map<String, String> getClassDirectoryMap() {
		return classDirectoryMap;
	}
	
	public void setClassDirectoryMap(Map<String, String> classDirectoryMap) {
		this.classDirectoryMap = classDirectoryMap;
	}
	
	public List<File> getClassDirectories() {
		return classDirectories;
	}

	public void setClassDirectories(List<File> classDirectories) {
		this.classDirectories = classDirectories;
	}
}