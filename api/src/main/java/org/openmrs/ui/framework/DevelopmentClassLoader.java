package org.openmrs.ui.framework;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.openmrs.util.OpenmrsClassLoader;
import org.openmrs.util.OpenmrsUtil;

/**
 * This is a placeholder solution for allowing Page and Fragment controller classes to
 * be reloaded in development mode. It probably leaks lots of memory. 
 */
public class DevelopmentClassLoader extends ClassLoader {
	
	ClassLoader parent;
	
	String reloadablePackage;
	
	List<File> classDirectories;
	
	/**
	 * Constructs a class loader that will automatically load classes from one specific
	 * package from the given location on disk, and delegate to the regular OpenmrsClassLoader
	 * for all other classes
	 * @param classDirectories
	 * @param reloadablePackage
	 */
	public DevelopmentClassLoader(List<File> classDirectories, String reloadablePackage) {
		this.classDirectories = classDirectories;
		this.reloadablePackage = reloadablePackage;
		this.parent = OpenmrsClassLoader.getInstance();
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (!name.startsWith(reloadablePackage))
			return parent.loadClass(name);
		
		for (File classDirectory : classDirectories) {
			File classFile = new File(classDirectory, File.separator + name.replace('.', File.separatorChar) + ".class");
			try {
				if (classFile.exists()) {
					byte[] bytes = getBytes(classFile);
					return defineClass(name, bytes, 0, bytes.length);
				}
			}
			catch (IOException ex) {}
		}
		
		throw new ClassNotFoundException();
	}
	
	private byte[] getBytes(File file) throws IOException {
		FileInputStream fileInput = new FileInputStream(file);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		OpenmrsUtil.copyFile(fileInput, output);
		return output.toByteArray();
	}
}
