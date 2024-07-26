package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.springframework.core.convert.converter.Converter;

import static org.openmrs.ui.framework.UiFrameworkConstants.GET_GLOBAL_PROPERTIES;

/**
 * Note that this converts based on property name, not on id
 */
public class StringToGlobalPropertyConverter implements Converter<String, GlobalProperty> {
	
	@Override
	public GlobalProperty convert(String propertyName) {
		if (StringUtils.isBlank(propertyName)) {
			return null;
		}
		GlobalProperty globalPropertyObject;
		try {
			Context.addProxyPrivilege(GET_GLOBAL_PROPERTIES);
			globalPropertyObject = Context.getAdministrationService().getGlobalPropertyObject(propertyName);
		}
		finally {
			Context.removeProxyPrivilege(GET_GLOBAL_PROPERTIES);
		}
		return globalPropertyObject;
	}
}
