package org.openmrs.ui.framework.converter;

import org.codehaus.jackson.node.ArrayNode;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class StringToArrayNodeConverterTest {

    @Test
    public void testConvert() throws Exception {
        String source = "[ true, false ]";
        ArrayNode converted = new StringToArrayNodeConverter().convert(source);
        assertThat(converted.get(0).getBooleanValue(), is(true));
        assertThat(converted.get(1).getBooleanValue(), is(false));
    }

}
