package org.openmrs.ui.framework;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ui.framework.annotation.BindParams;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.mock.web.MockHttpServletRequest;

public class UiFrameworkUtilTest {
	
	ConversionService conversionService;
	
    @Before
	public void beforeEachTest() throws Exception {
		ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
		conversionService = bean.getObject();
	}
	
	@Test
	public void test() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter("helper.name", "Helper");
		req.addParameter("helper.numbers", new String[] { "1", "2", "3" });
		
		Map<Class<?>, Object> argumentsByType = new HashMap<Class<?>, Object>();
		argumentsByType.put(HttpServletRequest.class, req);
		
		Method method = new MockController().getClass().getMethod("action", MockDomainObject.class);
		
		Object[] temp = UiFrameworkUtil.determineControllerMethodParameters(method, argumentsByType, conversionService);
		MockDomainObject bound = (MockDomainObject) temp[0];
		
		Assert.assertNotNull(bound);
		Assert.assertEquals("Helper", bound.getName());
		Assert.assertNotNull(bound.getNumbers());
		Assert.assertEquals(3, bound.getNumbers().size());
		Assert.assertTrue(bound.getNumbers().contains(1));
		Assert.assertTrue(bound.getNumbers().contains(2));
		Assert.assertTrue(bound.getNumbers().contains(3));
	}
	
	public class MockController {
		
		public void action(@BindParams("helper") MockDomainObject helper) {
			// intentionally blank
		}
	}
	
}