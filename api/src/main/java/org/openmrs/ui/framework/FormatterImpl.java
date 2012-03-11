package org.openmrs.ui.framework;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.Context;

public class FormatterImpl implements Formatter {
	
	@Override
	public String format(Object o) {
		return formatAsText(o);
	}
	
	@Override
	public String formatAsText(Object o) {
		if (o == null)
			return "";
		if (o instanceof Date) {
			return format((Date) o);
		} else if (o instanceof Role) {
			return format((Role) o);
		} else if (o instanceof Concept) {
			return format((Concept) o);
		} else if (o instanceof Person) {
			return format((Person) o);
		} else if (o instanceof User) {
			return format((User) o);
		} else if (o instanceof PatientIdentifierType) {
			return format((PatientIdentifierType) o);
		} else if (o instanceof OpenmrsMetadata) { // this should be after branches for specific metadata
			return format((OpenmrsMetadata) o);
		} else if (o instanceof Obs) {
			return format((Obs) o);
		} else if (o instanceof PatientIdentifier) {
			return format((PatientIdentifier) o);
		} else {
			return o.toString();
		}
	}
	
	private String format(Date d) {
		//return Context.getDateFormat().format(d);
		return new SimpleDateFormat("dd-MMM-yyyy", Context.getLocale()).format(d);
	}
	
	private String format(Role role) {
		return role.getRole();
	}
	
	private String format(OpenmrsMetadata md) {
		return md.getName();
	}
	
	private String format(Concept c) {
		return c.getName().getName();
	}
	
	private String format(Person p) {
		if (p == null)
			return null;
		PersonName n = p.getPersonName();
		return n == null ? "No Name" : n.getFullName();
	}
	
	private String format(User u) {
		return format(u.getPerson()) + " (" + u.getUsername() + ")";
	}
	
	private String format(Obs o) {
		return o.getValueAsString(Context.getLocale());
	}
	
	private String format(PatientIdentifierType pit) {
		return pit.getName();
	}
	
	private String format(PatientIdentifier pi) {
		return format(pi.getIdentifierType()) + ": " + pi.getIdentifier();
	}
	
}
