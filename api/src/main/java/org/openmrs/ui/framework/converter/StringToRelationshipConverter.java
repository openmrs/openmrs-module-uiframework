package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Relationship;
import org.openmrs.api.context.Context;
import org.springframework.core.convert.converter.Converter;

public class StringToRelationshipConverter implements Converter<String, Relationship> {
	
	@Override
	public Relationship convert(String id) {
		if (StringUtils.isBlank(id))
			return null;
		return Context.getPersonService().getRelationship(Integer.valueOf(id));
	}
	
}
