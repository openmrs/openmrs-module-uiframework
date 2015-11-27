package org.openmrs.ui.framework;

import org.openmrs.PersonName;
import org.openmrs.layout.name.NameSupport;
import org.openmrs.layout.name.NameTemplate;

import java.lang.reflect.Method;

import org.springframework.stereotype.Component;
import org.openmrs.annotation.OpenmrsProfile;

/**
 * Factors out helper classes for {@link FormatterImpl()} since packages are different for the platforms.
 */
@Component
@OpenmrsProfile(openmrsPlatformVersion = "2.0.0")
public class FormatterImplHelper {

	public static String getFormattedName (PersonName n) {
        try {
	        Method format = FormatterImplHelper.getFormatMethod();
	        if (format != null) {
	            return (String) format.invoke(FormatterImplHelper.getNameTemplate(), n);
	        }
        }
        catch (Exception e) {
                // fall through to just returning full name if no format method found or format fails
        }
        return null;
	}

	public static NameTemplate getNameTemplate () {
        NameTemplate nameTemplate = NameSupport.getInstance().getDefaultLayoutTemplate();
        return nameTemplate;
	}

	public static Method getFormatMethod () {

        try {
            // need to use reflection since the format method was not added until later versions of openmrs
            Method format = NameTemplate.class.getDeclaredMethod("format", PersonName.class);
            return format;
        }
        catch (Exception e) {
                // fall through to just returning full name if no format method found or format fails
        }
        
        return null;
    }

}
=======
package org.openmrs.ui.framework;

import org.openmrs.PersonName;
import org.openmrs.layout.name.NameSupport;
import org.openmrs.layout.name.NameTemplate;

import java.lang.reflect.Method;

/**
 * Factors out helper classes for {@link FormatterImpl()} since packages are different for the platforms.
 */
public class FormatterImplHelper {

	public static String getFormattedName (PersonName n) {
        try {
	        Method format = FormatterImplHelper.getFormatMethod();
	        if (format != null) {
	            return (String) format.invoke(FormatterImplHelper.getNameTemplate(), n);
	        }
        }
        catch (Exception e) {
                // fall through to just returning full name if no format method found or format fails
        }
        return null;
	}

	public static NameTemplate getNameTemplate () {
        NameTemplate nameTemplate = NameSupport.getInstance().getDefaultLayoutTemplate();
        return nameTemplate;
	}

	public static Method getFormatMethod () {

        try {
            // need to use reflection since the format method was not added until later versions of openmrs
            Method format = NameTemplate.class.getDeclaredMethod("format", PersonName.class);
            return format;
        }
        catch (Exception e) {
                // fall through to just returning full name if no format method found or format fails
        }
        
        return null;
    }

}
