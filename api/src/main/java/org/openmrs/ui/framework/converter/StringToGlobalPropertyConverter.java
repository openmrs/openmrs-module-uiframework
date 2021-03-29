package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.springframework.core.convert.converter.Converter;

/**
 * Note that this converts based on property name, not on id
 */
public class StringToGlobalPropertyConverter implements Converter<String, GlobalProperty> {
	
	@Override
	public GlobalProperty convert(String propertyName) {
		if (StringUtils.isBlank(propertyName)) {
			return null;
		}
		return Context.getAdministrationService().getGlobalPropertyObject(propertyName);
	}
}
