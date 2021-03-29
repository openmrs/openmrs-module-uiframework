package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.springframework.core.convert.converter.Converter;

public class StringToUserConverter implements Converter<String, User> {
	
	@Override
	public User convert(String id) {
		if (StringUtils.isBlank(id))
			return null;
		return Context.getUserService().getUser(Integer.valueOf(id));
		
	}
	
}
