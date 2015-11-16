package org.openmrs.ui.framework;

import org.openmrs.PersonName;
import org.openmrs.layout.web.name.NameSupport;
import org.openmrs.layout.web.name.NameTemplate;

import java.lang.reflect.Method;

import org.springframework.stereotype.Component;
import org.openmrs.annotation.OpenmrsProfile;

/**
 * Contains default formatting for most OpenMRS classes, which can be override with {@link FormatterFactory} instances.
 * Do not construct this class directly, but rather use {@link FormatterService#getFormatter()}.
 */
@Component
@OpenmrsProfile(openmrsVersion = "[1.9.9 - 1.11.3]")
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
