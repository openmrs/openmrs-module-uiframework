package org.openmrs.ui.framework.converter;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.core.convert.converter.Converter;

import java.io.IOException;

public class StringToObjectNodeConverter implements Converter<String, ObjectNode> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ObjectNode convert(String source) {
        try {
            return objectMapper.readValue(source, ObjectNode.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not convert to ObjectNode: " + source, e);
        }
    }

}
