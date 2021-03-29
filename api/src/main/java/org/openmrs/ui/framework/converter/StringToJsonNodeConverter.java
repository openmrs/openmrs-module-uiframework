package org.openmrs.ui.framework.converter;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.core.convert.converter.Converter;

import java.io.IOException;

public class StringToJsonNodeConverter implements Converter<String, JsonNode> {
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public JsonNode convert(String source) {
		try {
			return objectMapper.readValue(source, JsonNode.class);
		}
		catch (IOException e) {
			throw new IllegalArgumentException("Could not convert to JsonNode: " + source, e);
		}
	}
	
}
