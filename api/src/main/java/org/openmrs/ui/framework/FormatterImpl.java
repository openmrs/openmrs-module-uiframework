package org.openmrs.ui.framework;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.springframework.context.MessageSource;

public class FormatterImpl implements Formatter {

    private MessageSource messageSource;
    private AdministrationService administrationService;

    public FormatterImpl(MessageSource messageSource, AdministrationService administrationService) {
        this.messageSource = messageSource;
        this.administrationService = administrationService;
    }

    @Override
    public String format(Object o, Locale locale) {
		if (o == null)
			return "";
		if (o instanceof Date) {
			return format((Date) o, locale);
		} else if (o instanceof Role) {
			return format((Role) o, locale);
		} else if (o instanceof Concept) {
			return format((Concept) o, locale);
		} else if (o instanceof Person) {
			return format((Person) o, locale);
		} else if (o instanceof User) {
			return format((User) o, locale);
		} else if (o instanceof PatientIdentifierType) {
			return format((PatientIdentifierType) o, locale);
        } else if (o instanceof PersonAttribute) {
            return format((PersonAttribute) o, locale);
		} else if (o instanceof OpenmrsMetadata) { // this should be after branches for specific metadata
			return format((OpenmrsMetadata) o, locale);
		} else if (o instanceof Obs) {
			return format((Obs) o, locale);
		} else if (o instanceof PatientIdentifier) {
			return format((PatientIdentifier) o, locale);
		} else if (o instanceof PersonAddress) {
			return format((PersonAddress) o, locale);
		} else {
			return o.toString();
		}
	}
	
	private String format(Date d, Locale locale) {
        if (administrationService != null) {
            if (hasTimeComponent(d)) {
                return new SimpleDateFormat(administrationService.getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATETIME_FORMAT), locale).format(d);
            } else {
                return new SimpleDateFormat(administrationService.getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATE_FORMAT), locale).format(d);
            }
        } else {
            if (hasTimeComponent(d)) {
                return new SimpleDateFormat("dd.MMM.yyyy, HH:mm:ss", locale).format(d);
            } else {
                return new SimpleDateFormat("dd.MMM.yyyy", locale).format(d);
            }
        }
    }

    private boolean hasTimeComponent(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(Calendar.HOUR_OF_DAY) != 0 || cal.get(Calendar.MINUTE) != 0 || cal.get(Calendar.SECOND) != 0 || cal.get(Calendar.MILLISECOND) != 0;
    }

    private String format(Role role, Locale locale) {
        String override = getLocalization(locale, "Role", role.getUuid());
		return override != null ? override : role.getRole();
	}
	
	private String format(OpenmrsMetadata md, Locale locale) {
        String override = getLocalization(locale, md.getClass().getSimpleName(), md.getUuid());
        return override != null ? override : md.getName();
	}

    private String getLocalization(Locale locale, String shortClassName, String uuid) {
        if (messageSource == null) {
            return null;
        }

        // in case this is a hibernate proxy, strip off anything after an underscore
        // ie: EncounterType_$$_javassist_26 needs to be converted to EncounterType
        int underscoreIndex = shortClassName.indexOf("_$");
        if (underscoreIndex > 0) {
            shortClassName = shortClassName.substring(0, underscoreIndex);
        }

        String code = "ui.i18n." + shortClassName + ".name." + uuid;
        String localization = messageSource.getMessage(code, null, locale);
        if (localization == null || localization.equals(code)) {
            return null;
        } else {
            return localization;
        }
    }

    private String format(Concept c, Locale locale) {
		return c.getName(locale).getName();
	}
	
	private String format(Person p, Locale locale) {
		if (p == null)
			return null;
		PersonName n = p.getPersonName();
		return n == null ? messageSource.getMessage("uiframework.formatter.noNamePerson", null, locale) : n.getFullName();
	}
	
	private String format(User u, Locale locale) {
        String un = u.getUsername();
        if (un == null) {
            un = u.getSystemId();
        }
        return format(u.getPerson(), locale) + " (" + un + ")";
	}

    private String format(PersonAttribute pa, Locale locale) {
        return format(pa.getHydratedObject(), locale);
    }

	private String format(Obs o, Locale locale) {
		if(o.getValueDatetime() != null){
			return format(o.getValueDatetime(), locale);
		}
		return o.getValueAsString(locale);
	}
	
	private String format(PatientIdentifier pi, Locale locale) {
		return format(pi.getIdentifierType(), locale) + ": " + pi.getIdentifier();
	}

	private String format(PersonAddress personAddress, Locale locale) {
		List<String> address = new ArrayList<String>();
		if (!StringUtils.isBlank(personAddress.getStateProvince())) {
			address.add(personAddress.getStateProvince());
		}
		if (!StringUtils.isBlank(personAddress.getCityVillage())) {
			address.add(personAddress.getCityVillage());
		}
		if (!StringUtils.isBlank(personAddress.getAddress1())) {
			address.add(personAddress.getAddress1());
		}
		if (!StringUtils.isBlank(personAddress.getAddress2())) {
			address.add(personAddress.getAddress2());
		}
		return StringUtils.join(address, ", ");
	}
}
