package org.openmrs.ui.framework.fragment;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ui.framework.UiFrameworkException;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.openmrs.ui.framework.page.PageAction;
import org.openmrs.ui.framework.page.PageContext;
import org.openmrs.ui.framework.page.PageFactory;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.ui.framework.session.Session;
import org.openmrs.ui.framework.session.SessionFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class FragmentFactoryTest {
	
	FragmentFactory factory;
	
	@Before
	public void beforeEachTest() throws Exception {
		
		factory = new FragmentFactory();
		factory.setSessionFactory(new SessionFactory());
		
		Map<String, FragmentControllerProvider> cps = new HashMap<String, FragmentControllerProvider>();
		cps.put("somemodule", new MockControllerProvider("somefragment"));
		cps.put("othermodule", new MockControllerProvider("otherfragment"));
		factory.setControllerProviders(cps);
		
		Map<String, FragmentViewProvider> vps = new HashMap<String, FragmentViewProvider>();
		vps.put("somemodule", new MockViewProvider("somefragment"));
		vps.put("othermodule", new MockViewProvider("otherfragment"));
		factory.setViewProviders(vps);
		
		FragmentModelConfigurator configurator = new FragmentModelConfigurator() {
			
			@Override
			public void configureModel(FragmentContext pageContext) {
				pageContext.getModel().put("someCustomVariable", "Success!!!");
			}
		};
		factory.setModelConfigurators(Collections.singletonList(configurator));
	}
	
	/**
	 * @see FragmentFactory#getController(FragmentRequest)
	 * @verifies get a controller from the specified provider
	 */
	@Test
	public void getController_shouldGetAControllerFromTheSpecifiedProvider() throws Exception {
		assertNotNull(factory.getController(new FragmentRequest("somemodule", "somefragment")));
		assertNull(factory.getController(new FragmentRequest("somemodule", "otherfragment")));
		assertNull(factory.getController(new FragmentRequest("othermodule", "somefragment")));
	}
	
	/**
	 * @see FragmentFactory#getController(FragmentRequest)
	 * @verifies get a controller from any provider if none specified
	 */
	@Test
	public void getController_shouldGetAControllerFromAnyProviderIfNoneSpecified() throws Exception {
		assertNotNull(factory.getController(new FragmentRequest("*", "somefragment")));
		assertNotNull(factory.getController(new FragmentRequest("*", "otherfragment")));
		assertNull(factory.getController(new FragmentRequest("*", "nothingwiththisname")));
	}
	
	/**
	 * @see FragmentFactory#getController(FragmentRequest)
	 * @verifies fail if an invalid provider is specified
	 */
	@Test(expected = UiFrameworkException.class)
	public void getController_shouldFailIfAnInvalidProviderIsSpecified() throws Exception {
		factory.getController(new FragmentRequest("unknownmodule", "somefragment"));
	}
	
	/**
	 * @see FragmentFactory#getView(FragmentRequest,String)
	 * @verifies get a view from the requested provider
	 */
	@Test
	public void getView_shouldGetAViewFromTheRequestedProvider() throws Exception {
		assertNotNull(factory.getView(new FragmentRequest("somemodule", "somefragment"), null));
		assertNull(factory.getView(new FragmentRequest("somemodule", "otherfragment"), null));
		assertNull(factory.getView(new FragmentRequest("othermodule", "somefragment"), null));
	}
	
	/**
	 * @see FragmentFactory#getView(FragmentRequest,String)
	 * @verifies get a view from any provider if none is specified
	 */
	@Test
	public void getView_shouldGetAViewFromAnyProviderIfNoneIsSpecified() throws Exception {
		assertNotNull(factory.getView(new FragmentRequest("*", "somefragment"), null));
		assertNotNull(factory.getView(new FragmentRequest("*", "otherfragment"), null));
		assertNull(factory.getView(new FragmentRequest("*", "nothingwiththisname"), null));
	}
	
	/**
	 * @see FragmentFactory#getView(FragmentRequest,String)
	 * @verifies fail if an invalid provider name is specified
	 */
	@Test(expected = UiFrameworkException.class)
	public void getView_shouldFailIfAnInvalidProviderNameIsSpecified() throws Exception {
		factory.getView(new FragmentRequest("unknownmodule", "somefragment"), null);
	}
	
	@Test
	public void process_shouldSetCustomModelProperties() throws Exception {
		PageContext pageContext = buildPageContext();
		
		FragmentRequest fragmentRequest = new FragmentRequest("somemodule", "groovy");
		FragmentContext fragmentContext = new FragmentContext(fragmentRequest, pageContext);
		
		String result = factory.process(fragmentContext);
		assertThat(result, Matchers.containsString(("Testing Success!!!")));
	}
	
	private PageContext buildPageContext() {
		MockHttpSession httpSession = new MockHttpSession();
		Session session = new Session(httpSession);
		PageRequest pageRequest = new PageRequest("somemodule", "groovy", new MockHttpServletRequest(),
		        new MockHttpServletResponse(), session);
		PageContext pageContext = new PageContext(pageRequest);
		pageContext.setPageFactory(new PageFactory());
		return pageContext;
	}
	
	@Test
	public void shouldHandleCustomFragmentControllerArgumentsByType() throws Exception {
		PossibleFragmentControllerArgumentProvider argumentProvider = new PossibleFragmentControllerArgumentProvider() {
			
			@Override
			public void addPossibleFragmentControllerArguments(Map<Class<?>, Object> possibleArguments) {
				possibleArguments.put(Integer.class, new Integer(12345));
			}
		};
		factory.setPossibleFragmentControllerArgumentProviders(Collections.singletonList(argumentProvider));
		factory.addControllerProvider("test", new FragmentControllerProvider() {
			
			@Override
			public Object getController(String id) {
				return new ControllerAndActionThatTakeIntegerType();
			}
		});
		factory.addViewProvider("test", new FragmentViewProvider() {
			
			@Override
			public FragmentView getView(String name) {
				return new FragmentView() {
					
					@Override
					public String render(FragmentContext context) throws PageAction {
						return "View";
					}
				};
			}
		});
		
		PageContext pageContext = buildPageContext();
		FragmentRequest fragmentRequest = new FragmentRequest("test", "test");
		FragmentContext fragmentContext = new FragmentContext(fragmentRequest, pageContext);
		factory.process(fragmentContext);
	}
	
	@Test
	public void shouldHandleCustomFragmentActionArgumentsByType() throws Exception {
		PossibleFragmentActionArgumentProvider argumentProvider = new PossibleFragmentActionArgumentProvider() {
			
			@Override
			public void addPossibleFragmentActionArguments(Map<Class<?>, Object> possibleArguments) {
				possibleArguments.put(Integer.class, new Integer(12345));
			}
		};
		factory.setPossibleFragmentActionArgumentProviders(Collections.singletonList(argumentProvider));
		factory.addControllerProvider("test", new FragmentControllerProvider() {
			
			@Override
			public Object getController(String id) {
				return new ControllerAndActionThatTakeIntegerType();
			}
		});
		factory.addViewProvider("test", new FragmentViewProvider() {
			
			@Override
			public FragmentView getView(String name) {
				return new FragmentView() {
					
					@Override
					public String render(FragmentContext context) throws PageAction {
						return "View";
					}
				};
			}
		});
		
		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		httpRequest.setSession(new MockHttpSession());
		MockHttpServletResponse httpResponse = new MockHttpServletResponse();
		factory.invokeFragmentAction("test", "test", "action", httpRequest, httpResponse);
	}
	
	@Test
	public void shouldInjectRequestResponseInToFragmentActionMethod() {
		factory.addControllerProvider("test", new FragmentControllerProvider() {
			
			@Override
			public Object getController(String id) {
				return new ActionThatTakeServletRequestResponse();
			}
		});
		
		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		httpRequest.setSession(new MockHttpSession());
		MockHttpServletResponse httpResponse = new MockHttpServletResponse();
		factory.invokeFragmentAction("test", "test", "action", httpRequest, httpResponse);
	}
	
	@Test(expected = UiFrameworkException.class)
	public void shouldNotAllowYouToInvokeObjectClassMethods() throws Exception {
		factory.addControllerProvider("test", new FragmentControllerProvider() {
			
			@Override
			public Object getController(String id) {
				return new SimpleFragmentController();
			}
		});
		
		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		httpRequest.setSession(new MockHttpSession());
		MockHttpServletResponse httpResponse = new MockHttpServletResponse();
		factory.invokeFragmentAction("test", "test", "hashCode", httpRequest, httpResponse);
	}
	
	@Test(expected = UiFrameworkException.class)
	public void shouldNotAllowYouToInvokeControllerMethods() throws Exception {
		factory.addControllerProvider("test", new FragmentControllerProvider() {
			
			@Override
			public Object getController(String id) {
				return new SimpleFragmentController();
			}
		});
		
		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		httpRequest.setSession(new MockHttpSession());
		MockHttpServletResponse httpResponse = new MockHttpServletResponse();
		factory.invokeFragmentAction("test", "test", "controller", httpRequest, httpResponse);
	}
	
	class MockControllerProvider implements FragmentControllerProvider {
		
		private String fragmentName;
		
		public MockControllerProvider(String fragmentName) {
			this.fragmentName = fragmentName;
		}
		
		/**
		 * @see org.openmrs.ui.framework.fragment.FragmentControllerProvider#getController(java.lang.String)
		 */
		@Override
		public Object getController(String id) {
			if (fragmentName.equals(id)) {
				return new Object();
			} else {
				return null;
			}
		}
		
	}
	
	class MockViewProvider implements FragmentViewProvider {
		
		private String fragmentName;
		
		public MockViewProvider(String fragmentName) {
			this.fragmentName = fragmentName;
		}
		
		/**
		 * @see org.openmrs.ui.framework.fragment.FragmentViewProvider#getView(java.lang.String)
		 */
		@Override
		public FragmentView getView(String name) {
			if (fragmentName.equals(name)) {
				return new FragmentView() {
					
					@Override
					public String render(FragmentContext context) throws PageAction {
						return "Contents of Some Fragment";
					}
				};
			} else if ("groovy".equals(name)) {
				try {
					Template template = new SimpleTemplateEngine(getClass().getClassLoader())
					        .createTemplate("Testing ${ someCustomVariable }");
					return new GroovyFragmentView("somemodule:groovy", template);
				}
				catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			} else {
				return null;
			}
		}
		
	}
	
	public class ControllerAndActionThatTakeIntegerType {
		
		public void controller(Integer injected, Long notInjected) {
			assertNotNull("Integer argument was not injected", injected);
			assertNull("Long argument should not have been injected", notInjected);
		}
		
		public FragmentActionResult action(Integer injected, Long notInjected) {
			assertNotNull("Integer argument was not injected", injected);
			assertNull("Long argument should not have been injected", notInjected);
			return new SuccessResult();
		}
	}
	
	public class ActionThatTakeServletRequestResponse {
		
		public FragmentActionResult action(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
			assertNotNull("HttpServletRequest argument was not injected", httpRequest);
			assertNotNull("HttpServletResponse argument was not injected", httpResponse);
			return new SuccessResult();
		}
	}
	
	public class SimpleFragmentController {
		
		public void controller() {
		}
	}
	
}
