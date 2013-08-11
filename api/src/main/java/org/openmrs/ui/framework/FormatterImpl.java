package org.openmrs.ui.framework;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.GlobalProperty;
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
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.GlobalPropertyListener;
import org.openmrs.api.context.Context;
import org.openmrs.layout.web.address.AddressSupport;
import org.openmrs.layout.web.address.AddressTemplate;
import org.springframework.context.MessageSource;

public class FormatterImpl implements Formatter, GlobalPropertyListener {

    private MessageSource messageSource;
    private AdministrationService administrationService;

    private static final String ADDRESS_LAYOUT_TEMPLATE_NAME_GP = "layout.address.format";

    private static AddressTemplate oldAddressTemplate = null;

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
		String override = getLocalization(locale, "Concept", c.getUuid());
		return override != null ? override : c.getName(locale).getName();
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
		if (o.getValueDatetime() != null) {
			// limitation of Obs.getValueAsString() and java date locale
			return format(o.getValueDatetime(), locale);
		}
		return o.getValueAsString(locale);
	}
	
	private String format(PatientIdentifier pi, Locale locale) {
		return format(pi.getIdentifierType(), locale) + ": " + pi.getIdentifier();
	}

    private String format(PersonAddress personAddress, Locale locale) {
        List<String> addressFieldValues = new ArrayList<String>();
        List<String> addressHierarchyLevels = getAddressHierarchyLevels();
        try {
            if (addressHierarchyLevels.size() > 0) {
                for (String addressLevel : addressHierarchyLevels) {
                    String tokenValue = BeanUtils.getProperty(personAddress, addressLevel);
                    if (StringUtils.isNotBlank(tokenValue)) {
                        addressFieldValues.add(tokenValue);
                    }
                }
            } else {
                AddressTemplate addressTemplate;
                if (isOneNineOrLater()) {
                    Object templates = MethodUtils.invokeExactMethod(AddressSupport.getInstance(), "getAddressTemplate",
                            null);
                    addressTemplate = ((List<AddressTemplate>) templates).get(0);
                } else {
                    if (oldAddressTemplate == null) {
                        String templateName = administrationService.getGlobalProperty(
                                ADDRESS_LAYOUT_TEMPLATE_NAME_GP);
                        if (templateName != null) {
                            oldAddressTemplate = AddressSupport.getInstance().getLayoutTemplateByName(templateName);
                        }
                        if (oldAddressTemplate == null) {
                            oldAddressTemplate = AddressSupport.getInstance().getDefaultLayoutTemplate();
                        }
                    }
                    addressTemplate = oldAddressTemplate;
                }

                for (List<Map<String, String>> line : addressTemplate.getLines()) {
                    for (Map<String, String> lineToken : line) {
                        if (lineToken.get("isToken").equals(addressTemplate.getLayoutToken())) {
                            String tokenValue = BeanUtils.getProperty(personAddress, lineToken.get("codeName"));
                            if (StringUtils.isNotBlank(tokenValue)) {
                                addressFieldValues.add(tokenValue);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            //wrap into a runtime exception
            throw new APIException("Error while getting patient address", e);
        }

        return StringUtils.join(addressFieldValues, ", ");
    }

    private static List<String> getAddressHierarchyLevels() {
        List<String> l = new ArrayList<String>();

        try {
            Class<?> svcClass = Context.loadClass("org.openmrs.module.addresshierarchy.service.AddressHierarchyService");
            Object svc = Context.getService(svcClass);
            List<Object> levels = (List<Object>) svcClass.getMethod("getOrderedAddressHierarchyLevels", Boolean.class,
                    Boolean.class).invoke(svc, true, true);
            Class<?> levelClass = Context.loadClass("org.openmrs.module.addresshierarchy.AddressHierarchyLevel");
            Class<?> fieldClass = Context.loadClass("org.openmrs.module.addresshierarchy.AddressField");
            for (Object o : levels) {
                Object addressField = levelClass.getMethod("getAddressField").invoke(o);
                String fieldName = (String) fieldClass.getMethod("getName").invoke(addressField);
                l.add(fieldName);
            }
            if (l.size() > 1) {
                Collections.reverse(l);
            }
        }
        catch (ClassNotFoundException cnfe) {
            //ignore, the module isn't installed
        }
        catch (Exception e) {
            throw new APIException("Error obtaining address hierarchy levels", e);
        }

        return l;
    }

    private boolean isOneNineOrLater() {
        try {
            Context.loadClass("org.openmrs.api.VisitService");
            return true;
        }
        catch (ClassNotFoundException e) {
            //ignore, this is 1.8
        }

        return false;
    }

    @Override
    public boolean supportsPropertyName(String propertyName) {
        return ADDRESS_LAYOUT_TEMPLATE_NAME_GP.equals(propertyName);
    }

    @Override
    public void globalPropertyChanged(GlobalProperty globalProperty) {
        oldAddressTemplate = null;
    }

    @Override
    public void globalPropertyDeleted(String propertyName) {
        oldAddressTemplate = null;
    }
}
