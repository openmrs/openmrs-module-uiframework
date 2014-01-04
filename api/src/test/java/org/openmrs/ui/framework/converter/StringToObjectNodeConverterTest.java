package org.openmrs.ui.framework.converter;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class StringToObjectNodeConverterTest {

    @Test
    public void testConvert() throws Exception {
        String source = "{ \"foo\": true }";
        ObjectNode converted = new StringToObjectNodeConverter().convert(source);
        assertThat(converted.get("foo").getBooleanValue(), is(true));
    }

}
