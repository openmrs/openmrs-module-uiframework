package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.springframework.core.convert.converter.Converter;

public class StringToFormConverter implements Converter<String, Form> {
	
	@Override
	public Form convert(String id) {
		if (StringUtils.isBlank(id))
			return null;
		return Context.getFormService().getForm(Integer.valueOf(id));
	}
	
}
