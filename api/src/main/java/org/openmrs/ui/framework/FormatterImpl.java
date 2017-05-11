package org.openmrs.ui.framework;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.Obs;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.formatter.FormatterFactory;
import org.openmrs.ui.framework.formatter.FormatterService;
import org.springframework.context.MessageSource;

/**
 * Contains default formatting for most OpenMRS classes, which can be override with {@link FormatterFactory} instances.
 * Do not construct this class directly, but rather use {@link FormatterService#getFormatter()}.
 */
public class FormatterImpl implements Formatter {

    private MessageSource messageSource;
    private AdministrationService administrationService;

    /**
     * Map from fully-qualified classname, to the formatter to use for this class
     */
    private Map<String, Formatter> classFormatters = new HashMap<String, Formatter>();

    private static final String ADDRESS_LAYOUT_TEMPLATE_NAME_GP = "layout.address.format";

    public FormatterImpl(MessageSource messageSource, AdministrationService administrationService) {
        this.messageSource = messageSource;
        this.administrationService = administrationService;
    }

    @Override
    public String format(Object o, Locale locale) {
		if (o == null)
			return "";

        String className = o.getClass().getName();
        Formatter classFormatter = classFormatters.get(getCleanClassName(className));
        if (classFormatter != null) {
            return classFormatter.format(o, locale);
        } else if (o instanceof Date) {
			return format((Date) o, locale);
		} else if (o instanceof Role) {
			return format((Role) o, locale);
		} else if (o instanceof Concept) {
			return format((Concept) o, locale);
		} else if (o instanceof Person) {
			return format((Person) o, locale);
		} else if (o instanceof User) {
            return format((User) o, locale);
        } else if (o instanceof Provider) {
            return format((Provider) o, locale);
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
		} else if (o instanceof PersonName) {
            return format((PersonName) o, locale);
        } else if (o instanceof PersonAddress) {
            return format((PersonAddress) o, locale);
        } else if (o instanceof Number) {
            return format((Number) o, locale);
        } else if (o instanceof Class) {
            return ((Class) o).getName();
		} else {
			return o.toString();
		}
	}

    private String format(Number n, Locale locale) {
        if (wholeNumber(n)) {
            return "" + n.intValue();
        } else {
            return "" + n;
        }
    }

    private boolean wholeNumber(Number n) {
        return n != null && n.intValue() == n.doubleValue();
    }

    private String format(Date d, Locale locale) {
        DateFormat df;
        if (hasTimeComponent(d)) {
            df = UiFrameworkUtil.getDateTimeFormat(administrationService, locale);
        } else {
            df = UiFrameworkUtil.getDateFormat(administrationService, locale);
        }
        return df.format(d);
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
        shortClassName = getCleanClassName(shortClassName);


        String code = "ui.i18n." + shortClassName + ".name." + uuid;
        String localization = messageSource.getMessage(code, null, locale);
        if (localization == null || localization.equals(code)) {
            return null;
        } else {
            return localization;
        }
    }

    private String getCleanClassName(String shortClassName) {
        // in case this is a hibernate proxy, strip off anything after an underscore
        // ie: EncounterType_$$_javassist_26 needs to be converted to EncounterType
        int underscoreIndex = shortClassName.indexOf("_$");
        if (underscoreIndex > 0) {
            shortClassName = shortClassName.substring(0, underscoreIndex);
        }
        return shortClassName;
    }

    private String format(Concept c, Locale locale) {
		String override = getLocalization(locale, "Concept", c.getUuid());
		if (override != null) {
			return override;
		}
		
		ConceptName conceptName = c.getName(locale);
		if (conceptName == null) {
			//just get name in any locale
			conceptName = c.getName();
		}
		return conceptName.getName();
	}

    private String format(Person p, Locale locale) {
        if (p == null) {
            return null;
        }

        return format(p.getPersonName(), locale);
    }

	private String format(PersonName n, Locale locale) {
        // no name, return message
        if (n == null) {
            return messageSource.getMessage("uiframework.formatter.noNamePerson", null, locale);
        }
        // format via name template if available
        else if (NameSupportCompatibility.hasDefaultLayoutTemplate()) {
        	return NameSupportCompatibility.format(n);
        }
	    // otherwise, just return full name
        return n.getFullName();
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
		if (o.getValueTime() != null && o.getConcept().getDatatype().isTime()) {
			return UiFrameworkUtil.getTimeFormat(administrationService, locale).format(o.getValueTime());
		}
		else if (o.getValueDatetime() != null) {
			// limitation of Obs.getValueAsString() and java date locale
			return format(o.getValueDatetime(), locale);
		}
        if (o.getConcept() instanceof ConceptNumeric) {
            String units = ((ConceptNumeric) o.getConcept()).getUnits();
		    return o.getValueAsString(locale) + (StringUtils.isNotBlank(units) ? " " + units : "");
        }
        else {
        	if (o.hasGroupMembers()) {
        		StringBuilder sb = new StringBuilder();
    			for (Obs groupMember : o.getGroupMembers()) {
    				if (sb.length() > 0) {
    					sb.append(", ");
    				}
    				ConceptName conceptName = groupMember.getConcept().getName(locale);
    				if (conceptName == null) {
    					//just get available name in any locale
    					conceptName = groupMember.getConcept().getName();
    				}
    				sb.append(conceptName.getName());
    				sb.append(": ");
    				sb.append(groupMember.getValueAsString(locale));
    			}
    			return sb.toString();
        	}
        	else {
        		return o.getValueAsString(locale);
        	}
        }
	}

	private String format(PatientIdentifier pi, Locale locale) {
		return format(pi.getIdentifierType(), locale) + ": " + pi.getIdentifier();
	}

    private String format(PersonAddress personAddress, Locale locale) {
        List<String> personAddressLines = new ArrayList<String>();
        try {
            Class<?> addressSupportClass = null;
            try {
            	addressSupportClass = Context.loadClass("org.openmrs.layout.web.address.AddressSupport");
            }
            catch (ClassNotFoundException ex) {
            	addressSupportClass = Context.loadClass("org.openmrs.layout.address.AddressSupport");
            }
            
            Object addressSupport = addressSupportClass.getMethod("getInstance").invoke(null);
            Object addressTemplate = null;
            if (isOneNineOrLater()) {
                Object templates = MethodUtils.invokeExactMethod(addressSupport, "getAddressTemplate", null);
                addressTemplate = ((List<?>) templates).get(0);
            } else {
                String templateName = administrationService.getGlobalProperty(ADDRESS_LAYOUT_TEMPLATE_NAME_GP);
                if (templateName != null) {
                    addressTemplate = MethodUtils.invokeExactMethod(addressSupport, "getLayoutTemplateByName", templateName);
                }
                if (addressTemplate == null) {
                    addressTemplate = MethodUtils.invokeExactMethod(addressSupport, "getDefaultLayoutTemplate", null);
                }
            }

            List<List<Map<String, String>>> lines = (List<List<Map<String, String>>>) MethodUtils.invokeExactMethod(
                    addressTemplate, "getLines", null);
            String layoutToken = (String) MethodUtils.invokeExactMethod(addressTemplate, "getLayoutToken", null);
            for (List<Map<String, String>> line : lines) {
                String addressLine = "";
                Boolean hasToken = false;
                for (Map<String, String> lineToken : line) {
                    if (lineToken.get("isToken").equals(layoutToken)) {
                        String tokenValue = BeanUtils.getProperty(personAddress, lineToken.get("codeName"));
                        if (StringUtils.isNotBlank(tokenValue)) {
                            addressLine += tokenValue;
                            hasToken = true;
                        }
                    }
                    else if (StringUtils.isNotBlank(lineToken.get("displayText"))) {
                        addressLine += lineToken.get("displayText");
                    }
                }
                if (StringUtils.isNotBlank(addressLine) && hasToken) {
                    personAddressLines.add(addressLine);
                }
            }
        }
        catch (Exception e) {
            //wrap into a runtime exception
            throw new APIException("Error while getting patient address", e);
        }

        return StringUtils.join(personAddressLines, "\n");
    }

    /**
     * We are using this hacky code to check if it is OpenMRS 1.9 or later until
     * https://tickets.openmrs.org/browse/TRUNK-3751 is done and back ported to 1.8.x and 1.9.x
     */
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

    public void registerClassFormatter(String forClass, Formatter formatter) {
        classFormatters.put(forClass, formatter);
    }

}
