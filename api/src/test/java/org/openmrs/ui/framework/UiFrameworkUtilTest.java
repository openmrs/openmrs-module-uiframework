package org.openmrs.ui.framework;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.page.PageRequest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class UiFrameworkUtilTest {

    ConversionService conversionService;
	MockController controller;
    ApplicationContext applicationContext;
	
    @Before
	public void beforeEachTest() throws Exception {
		ConversionServiceFactoryBean bean = new ConversionServiceFactoryBean();
		bean.afterPropertiesSet();
		conversionService = bean.getObject();
		controller = new MockController();
        applicationContext = mock(ApplicationContext.class);
	}
	
	@Test
	public void test_determineControllerMethodParameters_bindCollection() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter("helper.name", "Helper");
		req.addParameter("helper.numbers", new String[] { "1", "2", "3" });
		
		Map<Class<?>, Object> argumentsByType = new HashMap<Class<?>, Object>();
		argumentsByType.put(HttpServletRequest.class, req);
		
		Method method = MockController.class.getMethod("action", MockDomainObject.class);
		
		Object[] temp = UiFrameworkUtil.determineControllerMethodParameters(controller, method, argumentsByType, conversionService, applicationContext);
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
		
		Method method = MockController.class.getMethod("action", MockDomainObject.class);
		
		Object[] temp = UiFrameworkUtil.determineControllerMethodParameters(controller, method, argumentsByType, conversionService, applicationContext);
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
		
		Method method = MockController.class.getMethod("controller", String[].class, Integer.class);
		
		Object[] temp = UiFrameworkUtil.determineControllerMethodParameters(controller, method, argumentsByType, conversionService, applicationContext);

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
		
		Method method = MockController.class.getMethod("integerList", List.class);
		
		Object[] temp = UiFrameworkUtil.determineControllerMethodParameters(controller, method, argumentsByType, conversionService, applicationContext);

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
		
		Method method = MockController.class.getMethod("controller", String[].class, Integer.class);
		
		try {
			UiFrameworkUtil.determineControllerMethodParameters(controller, method, argumentsByType, conversionService, applicationContext);
			Assert.fail("Should have caught that a required parameter was missing");
		} catch (MissingRequiredParameterException ex) {
			// pass
		}
		
		req.setParameter("properties", "name");
		try {
			UiFrameworkUtil.determineControllerMethodParameters(controller, method, argumentsByType, conversionService, applicationContext);
		} catch (MissingRequiredParameterException ex) {
			Assert.fail("Should not have required the second parameter");
		}
		
		req.setParameter("number", "");
		Object[] temp = UiFrameworkUtil.determineControllerMethodParameters(controller, method, argumentsByType, conversionService, applicationContext);
		Assert.assertNull(temp[1]);
		
		req.setParameter("number", new String[] { "", "" });
		temp = UiFrameworkUtil.determineControllerMethodParameters(controller, method, argumentsByType, conversionService, applicationContext);
		Assert.assertNull(temp[1]);
	}
	
	@Test
	public void test_determineControllerMethodParameters_requestParamDefault() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();

		Map<Class<?>, Object> argumentsByType = new HashMap<Class<?>, Object>();
		argumentsByType.put(HttpServletRequest.class, req);
		
		Method method = MockController.class.getMethod("withDefault", int.class);
		
		UiFrameworkUtil.determineControllerMethodParameters(controller, method, argumentsByType, conversionService, applicationContext);
		// this should succeed
	}

	@Test
	public void test_determineControllerMethodParameters_requestParamNull() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter("parameter2", "");
		
		Map<Class<?>, Object> argumentsByType = new HashMap<Class<?>, Object>();
		argumentsByType.put(HttpServletRequest.class, req);
		
		Method method = MockController.class.getMethod("search", String.class, String.class);
		
		Object[] temp = UiFrameworkUtil.determineControllerMethodParameters(controller, method, argumentsByType, conversionService, applicationContext);

		@SuppressWarnings("unchecked")
		String	methodParameter1 = (String) temp[0];
		String	methodParameter2 = (String) temp[1];

		Assert.assertNull(methodParameter1);
		Assert.assertEquals("", methodParameter2);
	}

	@Test
    public void test_determineControllerMethodParameters_fromCookie() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCookies(new Cookie("number", "3"));
        PageRequest pr = new PageRequest(null, null, req, null, null);

        Map<Class<?>, Object> argumentsByType = new HashMap<Class<?>, Object>();
        argumentsByType.put(HttpServletRequest.class, req);
        argumentsByType.put(PageRequest.class, pr);

        Method method = MockController.class.getMethod("fromCookie", Integer.class);
        Object[] vals = UiFrameworkUtil.determineControllerMethodParameters(controller, method, argumentsByType, conversionService, applicationContext);
        assertThat(vals[0], is((Object) 3));
    }
	
	@Test
	public void test_determineControllerMethodParameters_methodParam() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter("name", "Testing");
		
		Map<Class<?>, Object> argumentsByType = new HashMap<Class<?>, Object>();
		argumentsByType.put(HttpServletRequest.class, req);
		
		Method method = MockController.class.getMethod("fromMethod", MockDomainObject.class);
		
		Object[] temp = UiFrameworkUtil.determineControllerMethodParameters(controller, method, argumentsByType, conversionService, applicationContext);
		MockDomainObject bound = (MockDomainObject) temp[0];
		
		Assert.assertNotNull(bound);
		Assert.assertEquals("Testing", bound.getName());
	}
	
	@Test
	public void test_determineControllerMethodParameters_methodParamSubclass() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter("name", "Testing");
		
		Map<Class<?>, Object> argumentsByType = new HashMap<Class<?>, Object>();
		argumentsByType.put(HttpServletRequest.class, req);
		
		Method method = MockController.class.getMethod("fromMethodSubclass", MockDomainObject.class);
		
		Object[] temp = UiFrameworkUtil.determineControllerMethodParameters(controller, method, argumentsByType, conversionService, applicationContext);
		MockDomainObject bound = (MockDomainObject) temp[0];
		
		Assert.assertNotNull(bound);
		Assert.assertEquals("Testing", bound.getName());
		Assert.assertEquals(MockDomainSubclass.class, bound.getClass());
	}

    @Test
    public void test_executeControllerMethodShouldDependOnHttpRequestMethod() throws Exception {
        Map<Class<?>, Object> argumentsByType = new HashMap<Class<?>, Object>();

        Object result = UiFrameworkUtil.executeControllerMethod(new MockFormController(), "GET", argumentsByType, conversionService, applicationContext);
        Assert.assertEquals("Got it", result);

        result = UiFrameworkUtil.executeControllerMethod(new MockFormController(), "POST", argumentsByType, conversionService, applicationContext);
        Assert.assertEquals("Posted it", result);

        result = UiFrameworkUtil.executeControllerMethod(new MockFormController(), "HEAD", argumentsByType, conversionService, applicationContext);
        Assert.assertEquals("Fallback", result);

        result = UiFrameworkUtil.executeControllerMethod(new MockFormController(), null, argumentsByType, conversionService, applicationContext);
        Assert.assertEquals("Fallback", result);
    }

    @Test
    public void test_invokeMethodWithArgumentsShouldHandleBindParamsAnnotation() throws Exception {
        final String expectedName = "expectedName";
        final Integer one = new Integer(1);

        Object controller = new Object() {
            public void post(@BindParams MockDomainObject command, Integer nextArg) {
                assertThat(command.getName(), is(expectedName));
                assertThat(nextArg, is(one));
            }
            public void postAndValidate(@BindParams MockDomainObject command, Errors errors, Integer nextArg) {
                assertThat(command.getName(), is(expectedName));
                assertThat(errors, is(notNullValue()));
                assertThat(errors.hasErrors(), is(false));
                assertThat(nextArg, is(one));
            }
        };
        Method postMethod1 = controller.getClass().getMethod("post", MockDomainObject.class, Integer.class);
        Method postMethod2 = controller.getClass().getMethod("postAndValidate", MockDomainObject.class, Errors.class, Integer.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("name", expectedName);

        HashMap<Class<?>, Object> possibleArguments = new HashMap<Class<?>, Object>();
        possibleArguments.put(HttpServletRequest.class, request);
        possibleArguments.put(Integer.class, one);

        UiFrameworkUtil.invokeMethodWithArguments(controller, postMethod1, possibleArguments, conversionService, applicationContext);
        UiFrameworkUtil.invokeMethodWithArguments(controller, postMethod2, possibleArguments, conversionService, applicationContext);
    }

    public class MockFormController {

        public String controller() {
            return "Fallback";
        }

        public String get() {
            return "Got it";
        }

        public String post() {
            return "Posted it";
        }

    }
	
	public class MockController {
		
		public void controller(@RequestParam("properties") String[] properties, @RequestParam(value="number", required=false) Integer number) {
			// intentionally blank
		}
		
		public void action(@BindParams("helper") MockDomainObject helper) {
			// intentionally blank
		}

		public void search(@RequestParam(value="parameter1", required=false) String parameter1, @RequestParam(value="parameter2", required=false) String parameter2) {
			// intentionally blank
		}
		
		public void integerList(@RequestParam("numbers") List<Integer> numbers) {
			// intentionally blank
		}
		
		public void withDefault(@RequestParam(value="something", defaultValue="5") int number) {
			// intentionally blank
		}

        public void fromCookie(@CookieValue("number") Integer number) {
            // intentionally blank
        }
		
		public void fromMethod(@MethodParam("initializeMethodParameter") MockDomainObject obj) {
			// intentionally blank
		}
		
		public void fromMethodSubclass(@MethodParam("createSubclass") MockDomainObject obj) {
			// intentionally blank
		}
		
		public MockDomainObject initializeMethodParameter(@RequestParam("name") String name) {
			MockDomainObject ret = new MockDomainObject();
			ret.setName(name);
			return ret;
		}
		
		public MockDomainSubclass createSubclass(@RequestParam("name") String name) {
			MockDomainSubclass ret = new MockDomainSubclass();
			ret.setName(name);
			return ret;
		}
		
	}
	
}