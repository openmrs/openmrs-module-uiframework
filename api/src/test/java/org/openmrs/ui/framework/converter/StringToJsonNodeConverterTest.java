package org.openmrs.ui.framework.converter;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class StringToJsonNodeConverterTest {
	
	@Test
	public void testConvert() throws Exception {
		String source = "{ \"foo\": true }";
		JsonNode converted = new StringToJsonNodeConverter().convert(source);
		assertThat(converted.get("foo").getBooleanValue(), is(true));
	}
	
}
