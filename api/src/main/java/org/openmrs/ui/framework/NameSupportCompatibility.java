package org.openmrs.ui.framework;

import java.lang.reflect.Method;

import org.openmrs.PersonName;
import org.openmrs.layout.web.name.NameSupport;
import org.openmrs.util.OpenmrsClassLoader;

public class NameSupportCompatibility {
	
	public static boolean hasDefaultLayoutTemplate() {
		return getLayoutTemplate() != null;
	}
	
	public static String format(PersonName n) {

		Object nameTemplate = getLayoutTemplate();
		
		try {
			// need to use reflection since the format method was not added until later versions of openmrs
			Method format = nameTemplate.getClass().getDeclaredMethod("format", PersonName.class);
			return (String) format.invoke(nameTemplate, n);
		}
		catch (Exception e) {
			// fall through to just returning full name if no format method found or format fails
		}
		
		return n.getFullName();
	}
	
	private static Object getLayoutTemplate() {
		
		try {
			return NameSupport.getInstance().getDefaultLayoutTemplate() != null;
		}
		catch (NoClassDefFoundError e) {
			try {
				Class<?> cls = OpenmrsClassLoader.getInstance().loadClass("org.openmrs.layout.name.NameSupport");
				Method method = cls.getMethod("getInstance", null);
				Object nameSupportInstance = method.invoke(cls, null);
				method = nameSupportInstance.getClass().getMethod("getDefaultLayoutTemplate", null);
				return method.invoke(nameSupportInstance, null);
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		return null;
	}
}
