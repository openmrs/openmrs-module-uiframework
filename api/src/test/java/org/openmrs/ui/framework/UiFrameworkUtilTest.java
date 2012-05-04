package org.openmrs.ui.framework;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ui.framework.annotation.BindParams;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;

public class UiFrameworkUtilTest {
	
	ConversionService conversionService;
	
    @Before
	public void beforeEachTest() throws Exception {
		ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
		bean.afterPropertiesSet();
		conversionService = bean.getObject();
	}
	
	@Test
	public void test_determineControllerMethodParameters_bindCollection() throws Exception {
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
	
	@Test
	public void test_determineControllerMethodParameters_bindMap() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter("helper.map['foo']", "123");
		req.addParameter("helper.map[bar]", "456");
		
		Map<Class<?>, Object> argumentsByType = new HashMap<Class<?>, Object>();
		argumentsByType.put(HttpServletRequest.class, req);
		
		Method method = new MockController().getClass().getMethod("action", MockDomainObject.class);
		
		Object[] temp = UiFrameworkUtil.determineControllerMethodParameters(method, argumentsByType, conversionService);
		MockDomainObject bound = (MockDomainObject) temp[0];
		
		Assert.assertNotNull(bound);
		Assert.assertNotNull(bound.getMap());
		Assert.assertEquals(2, bound.getMap().size());
		Assert.assertEquals(Integer.valueOf(123), bound.getMap().get("foo"));
		Assert.assertEquals(Integer.valueOf(456), bound.getMap().get("bar"));
	}
	
	@Test
	public void test_determineControllerMethodParameters_requestParamCollection() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter("properties", new String[] { "name", "description" });
		req.addParameter("number", "5");
		
		Map<Class<?>, Object> argumentsByType = new HashMap<Class<?>, Object>();
		argumentsByType.put(HttpServletRequest.class, req);
		
		Method method = new MockController().getClass().getMethod("controller", String[].class, Integer.class);
		
		Object[] temp = UiFrameworkUtil.determineControllerMethodParameters(method, argumentsByType, conversionService);

		String[] props = (String[]) temp[0];
		Assert.assertNotNull(props);
		Assert.assertEquals(2, props.length);
		Assert.assertEquals("name", props[0]);
		Assert.assertEquals("description", props[1]);
		
		Integer number = (Integer) temp[1];
		Assert.assertNotNull(number);
		Assert.assertEquals(Integer.valueOf(5), number);
	}
	
	@Test
	public void test_determineControllerMethodParameters_requestParamList() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter("numbers", new String[] { "1", "2", "3" });
		
		Map<Class<?>, Object> argumentsByType = new HashMap<Class<?>, Object>();
		argumentsByType.put(HttpServletRequest.class, req);
		
		Method method = new MockController().getClass().getMethod("integerList", List.class);
		
		Object[] temp = UiFrameworkUtil.determineControllerMethodParameters(method, argumentsByType, conversionService);

		@SuppressWarnings("unchecked")
        List<Integer> list = (List<Integer>) temp[0];
		Assert.assertNotNull(list);
		Assert.assertEquals(3, list.size());
		for (int i = 0; i < 3; ++i)
			Assert.assertEquals(Integer.valueOf(i + 1), list.get(i));
	}
	
	@Test
	public void test_determineControllerMethodParameters_requestParamRequired() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();

		Map<Class<?>, Object> argumentsByType = new HashMap<Class<?>, Object>();
		argumentsByType.put(HttpServletRequest.class, req);
		
		Method method = new MockController().getClass().getMethod("controller", String[].class, Integer.class);
		
		try {
			UiFrameworkUtil.determineControllerMethodParameters(method, argumentsByType, conversionService);
			Assert.fail("Should have caught that a required parameter was missing");
		} catch (MissingRequiredParameterException ex) {
			// pass
		}
		
		req.setParameter("properties", "name");
		try {
			UiFrameworkUtil.determineControllerMethodParameters(method, argumentsByType, conversionService);
		} catch (MissingRequiredParameterException ex) {
			Assert.fail("Should not have required the second parameter");
		}
		
		req.setParameter("number", "");
		Object[] temp = UiFrameworkUtil.determineControllerMethodParameters(method, argumentsByType, conversionService);
		Assert.assertNull(temp[1]);
		
		req.setParameter("number", new String[] { "", "" });
		temp = UiFrameworkUtil.determineControllerMethodParameters(method, argumentsByType, conversionService);
		Assert.assertNull(temp[1]);
	}
	
	@Test
	public void test_determineControllerMethodParameters_requestParamDefault() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();

		Map<Class<?>, Object> argumentsByType = new HashMap<Class<?>, Object>();
		argumentsByType.put(HttpServletRequest.class, req);
		
		Method method = new MockController().getClass().getMethod("withDefault", int.class);
		
		UiFrameworkUtil.determineControllerMethodParameters(method, argumentsByType, conversionService);
		// this should succeed
	}
	
	public class MockController {
		
		public void controller(@RequestParam("properties") String[] properties, @RequestParam(value="number", required=false) Integer number) {
			// intentionally blank
		}
		
		public void action(@BindParams("helper") MockDomainObject helper) {
			// intentionally blank
		}
		
		public void integerList(@RequestParam("numbers") List<Integer> numbers) {
			// intentionally blank
		}
		
		public void withDefault(@RequestParam(value="something", defaultValue="5") int number) {
			// intentionally blank
		}
	}
	
}