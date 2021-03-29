package org.openmrs.ui.framework.converter;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.springframework.core.convert.converter.Converter;

import java.io.IOException;

public class StringToArrayNodeConverter implements Converter<String, ArrayNode> {
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public ArrayNode convert(String source) {
		try {
			return objectMapper.readValue(source, ArrayNode.class);
		}
		catch (IOException e) {
			throw new IllegalArgumentException("Could not convert to ArrayNode: " + source, e);
		}
	}
	
}
