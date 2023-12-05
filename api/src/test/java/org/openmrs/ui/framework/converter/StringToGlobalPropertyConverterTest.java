package org.openmrs.ui.framework.converter;

import junit.framework.Assert;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class StringToGlobalPropertyConverterTest extends BaseModuleContextSensitiveTest {
	
	@Test
	public void convert_shouldConvertStringToGlobalProperty() {
		StringToGlobalPropertyConverter converter = new StringToGlobalPropertyConverter();
		GlobalProperty prop = converter.convert("locale.allowed.list");
		Assert.assertEquals("en_GB", prop.getPropertyValue());
	}
	
	@Test
	public void convert_shouldReturnNullIfPassedEmptyString() {
		StringToGlobalPropertyConverter converter = new StringToGlobalPropertyConverter();
		GlobalProperty prop = converter.convert("");
		Assert.assertNull(prop);
	}
	
	@Test
	public void convert_shouldReturnNullIfPassedNull() {
		StringToGlobalPropertyConverter converter = new StringToGlobalPropertyConverter();
		GlobalProperty prop = converter.convert(null);
		Assert.assertNull(prop);
	}
	
	@Test
	public void convert_shouldReturnNullIfNoMatchingProperty() {
		StringToGlobalPropertyConverter converter = new StringToGlobalPropertyConverter();
		GlobalProperty prop = converter.convert("bogus.property");
		Assert.assertNull(prop);
	}
}
