package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.springframework.core.convert.converter.Converter;

public class StringToPersonConverter implements Converter<String, Person> {
	
	@Override
	public Person convert(String id) {
		if (StringUtils.isBlank(id))
			return null;
		return Context.getPersonService().getPerson(Integer.valueOf(id));
	}
	
}
