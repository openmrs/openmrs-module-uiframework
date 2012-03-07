package org.openmrs.ui2.core.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.springframework.core.convert.converter.Converter;

public class StringToPersonAttributeConverter implements Converter<String, PersonAttribute> {
	
	@Override
	public PersonAttribute convert(String id) {
		if (StringUtils.isBlank(id))
			return null;
		return Context.getPersonService().getPersonAttribute(Integer.valueOf(id));
	}
	
}
