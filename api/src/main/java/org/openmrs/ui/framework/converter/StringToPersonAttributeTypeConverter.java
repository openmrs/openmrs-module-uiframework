package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.springframework.core.convert.converter.Converter;

public class StringToPersonAttributeTypeConverter implements Converter<String, PersonAttributeType> {
	
	@Override
	public PersonAttributeType convert(String id) {
		if (StringUtils.isBlank(id))
			return null;
		return Context.getPersonService().getPersonAttributeType(Integer.valueOf(id));
	}
	
}
