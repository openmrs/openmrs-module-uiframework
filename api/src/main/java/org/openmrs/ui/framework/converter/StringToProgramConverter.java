package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.ui.framework.converter.util.ConversionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;

/**
 * Converts from a {@link String} to a {@link Program}
 */
public class StringToProgramConverter implements Converter<String, Program> {
	
	@Autowired
	@Qualifier("programWorkflowService")
	private ProgramWorkflowService service;
	
	@Override
	public Program convert(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		} else if (ConversionUtil.onlyDigits(id)) {
			return service.getProgram(Integer.valueOf(id));
		} else {
			return service.getProgramByUuid(id);
		}
	}
	
}
