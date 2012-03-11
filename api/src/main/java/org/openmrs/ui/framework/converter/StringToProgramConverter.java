package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.springframework.core.convert.converter.Converter;

/**
 * Converts from a {@link String} to a {@link Program}
 */
public class StringToProgramConverter implements Converter<String, Program> {
	
	/**
	 * Treats the string as the integer primary key of the Program
	 */
	@Override
	public Program convert(String id) {
		if (StringUtils.isBlank(id))
			return null;
		return Context.getProgramWorkflowService().getProgram(Integer.valueOf(id));
	}
	
}
