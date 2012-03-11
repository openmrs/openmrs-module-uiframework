package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.springframework.core.convert.converter.Converter;

public class StringToRelationshipTypeConverter implements Converter<String, RelationshipType> {
	
	@Override
	public RelationshipType convert(String id) {
		if (StringUtils.isBlank(id))
			return null;
		return Context.getPersonService().getRelationshipType(Integer.valueOf(id));
	}
	
}
