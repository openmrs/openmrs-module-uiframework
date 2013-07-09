package org.openmrs.ui.framework.page;


import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import org.apache.commons.collections.Transformer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.Contains;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.ProviderAndName;
import org.openmrs.ui.framework.UiFrameworkException;
import org.openmrs.ui.framework.interceptor.PageRequestInterceptor;
import org.openmrs.ui.framework.resource.Resource;
import org.openmrs.ui.framework.session.Session;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class PageFactoryTest {
	
	PageFactory factory;

	@Before
	public void beforeEachTest() throws Exception {

		mockStatic(Context.class);
		when(Context.getAdministrationService()).thenReturn(null);

		factory = new PageFactory();

		Map<String, PageControllerProvider> cps = new HashMap<String, PageControllerProvider>();
		cps.put("somemodule", new MockControllerProvider("somepage"));
		cps.put("othermodule", new MockControllerProvider("otherpage"));
		factory.setControllerProviders(cps);

		Map<String, PageViewProvider> vps = new HashMap<String, PageViewProvider>();
		vps.put("somemodule", new MockViewProvider("somepage"));
		vps.put("othermodule", new MockViewProvider("otherpage"));
		factory.setViewProviders(vps);

		PageModelConfigurator configurator = new PageModelConfigurator() {
			@Override
			public void configureModel(PageContext pageContext) {
				pageContext.getModel().put("varInjectedByConfigurator", "Success!!!");
			}
		};

		PageRequestInterceptor interceptor = new PageRequestInterceptor() {
			@Override
			public void beforeHandleRequest(PageContext pageContext) {
				pageContext.getModel().put("varInjectedByInterceptor", "Success!!!");
			}
		};

		List<PageModelConfigurator> configurators = new ArrayList<PageModelConfigurator>();
		configurators.add(configurator);
		factory.setModelConfigurators(configurators);

		List<PageRequestInterceptor> interceptors = new ArrayList<PageRequestInterceptor>();
		interceptors.add(interceptor);
		factory.setPageRequestInterceptors(interceptors);
	}
	
	/**
	 * @see PageFactory#getController(PageRequest)
	 * @verifies get a controller from the specified provider
	 */
	@Test
	public void getController_shouldGetAControllerFromTheSpecifiedProvider() throws Exception {
		Assert.assertNotNull(factory.getController(pageRequest("somemodule", "somepage")));
		Assert.assertNull(factory.getController(pageRequest("somemodule", "otherpage")));
		Assert.assertNull(factory.getController(pageRequest("othermodule", "somepage")));
	}
	
	/**
	 * @see PageFactory#getController(PageRequest)
	 * @verifies get a controller from any provider if none specified
	 */
	@Test
	public void getController_shouldGetAControllerFromAnyProviderIfNoneSpecified() throws Exception {
		Assert.assertNotNull(factory.getController(pageRequest("*", "somepage")));
		Assert.assertNotNull(factory.getController(pageRequest("*", "otherpage")));
		Assert.assertNull(factory.getController(pageRequest("*", "nothingwiththisname")));
	}
	
	/**
	 * @see PageFactory#getView(String,PageRequest)
	 * @verifies get a view from the requested provider
	 */
	@Test
	public void getView_shouldGetAViewFromTheRequestedProvider() throws Exception {
		Assert.assertNotNull(factory.getView(null, pageRequest("somemodule", "somepage")));
		try {
			factory.getView(null, pageRequest("somemodule", "otherpage"));
			Assert.fail("Expected exception");
		} catch (UiFrameworkException ex) {
			// expected
		}
		try {
			factory.getView(null, pageRequest("othermodule", "somepage"));
			Assert.fail("Expected exception");
		} catch (UiFrameworkException ex) {
			// expected
		}
	}
	
	/**
	 * @see PageFactory#getView(String,PageRequest)
	 * @verifies get a view from any provider if none is specified
	 */
	@Test
	public void getView_shouldGetAViewFromAnyProviderIfNoneIsSpecified() throws Exception {
		Assert.assertNotNull(factory.getView(null, pageRequest("*", "somepage")));
		Assert.assertNotNull(factory.getView(null, pageRequest("*", "otherpage")));
		try {
			factory.getView(null, pageRequest("*", "nothingwiththisname"));
			Assert.fail("Expected exception");
		} catch (UiFrameworkException ex) {
			// expected
		}
	}
	
	/**
	 * @see PageFactory#getView(String,PageRequest)
	 * @verifies fail if an invalid provider name is specified
	 */
	@Test(expected=UiFrameworkException.class)
	public void getView_shouldFailIfAnInvalidProviderNameIsSpecified() throws Exception {
		factory.getView(null, pageRequest("unknownmodule", "somepage"));
	}

    @Test
    public void process_shouldSetCustomModelProperties() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();
        Session session = new Session(httpSession);
        String result = factory.handle(new PageRequest("somemodule", "groovy", new MockHttpServletRequest(), new MockHttpServletResponse(), session));
        assertThat(result, new Contains("Testing Success!!!"));
    }

    @Test
    public void shouldHandleCustomPageControllerArgumentsByType() throws Exception {
        PossiblePageControllerArgumentProvider argumentProvider = new PossiblePageControllerArgumentProvider() {
            @Override
            public void addPossiblePageControllerArguments(Map<Class<?>, Object> possibleArguments) {
                possibleArguments.put(Integer.class, new Integer(12345));
            }
        };
        factory.setPossiblePageControllerArgumentProviders(Collections.singletonList(argumentProvider));
        factory.addControllerProvider("test", new PageControllerProvider() {
            @Override
            public Object getController(String id) {
                return new ControllerThatTakesIntegerType();
            }
        });
        factory.addViewProvider("test", new PageViewProvider() {
            @Override
            public PageView getView(String name) {
                return new PageView() {
                    @Override
                    public String render(PageContext context) throws PageAction {
                        return "Contents of Some Page";
                    }
                    @Override
                    public ProviderAndName getController() {
                        return null;
                    }
                };
            }
        });
        factory.handle(pageRequest("test", "takesInteger"));
    }

    @Test
    public void shouldHandleGlobalResourceIncluder() throws Exception {
        GlobalResourceIncluder globalResourceIncluder = new GlobalResourceIncluder();
        globalResourceIncluder.addResource(new Resource(Resource.CATEGORY_CSS, "mirebalais", "mirebalais.css", -100));
        globalResourceIncluder.addResource(new Resource(Resource.CATEGORY_CSS, "emr", "emr.css", null));
        globalResourceIncluder.addResource(new Resource(Resource.CATEGORY_JS, "mirebalais", "mirebalais-utils.js", null));

        factory.getModelConfigurators().add(globalResourceIncluder);

        String output = factory.handle(pageRequest("somemodule", "somepage"));

        Assert.assertTrue(Pattern.compile("<link rel=\"stylesheet\" href=\".*/mirebalais\\.css\" type=\"text/css\"/>").matcher(output).find());
        Assert.assertTrue(Pattern.compile("<link rel=\"stylesheet\" href=\".*/emr\\.css\" type=\"text/css\"/>").matcher(output).find());
        Assert.assertTrue(output.indexOf("emr.css") < output.indexOf("mirebalais.css"));

        Assert.assertTrue(Pattern.compile("<script type=\"text/javascript\" src=\".*/mirebalais/mirebalais-utils\\.js\"").matcher(output).find());
    }

    @Test
    public void shouldGetCorrectOrder() throws Exception {
        PageContext pageContext = new PageContext(null);
        pageContext.includeResource(new Resource(Resource.CATEGORY_JS, "normal", "normal.js", null));
        pageContext.includeResource(new Resource(Resource.CATEGORY_JS, "low", "low.js", -100));
        pageContext.includeResource(new Resource(Resource.CATEGORY_JS, "high", "high.js", 100));

        Collection<String> includes = factory.uniqueSortedIncludesByCategory(pageContext, Resource.CATEGORY_JS, new Transformer() {
            @Override
            public Object transform(Object input) {
                return ((Resource) input).getResourcePath();
            }
        });
        assertThat(includes, contains("high.js", "normal.js", "low.js"));
    }

    /**
     * @param provider
     * @param page
     * @return a page request, with appropriate MockHttp*
     */
    private PageRequest pageRequest(String provider, String page) {
    	MockHttpSession httpSession = new MockHttpSession();
    	Session uiSession = new Session(httpSession);
    	MockHttpServletRequest req = new MockHttpServletRequest();
    	req.setSession(httpSession);
	    return new PageRequest(provider, page, req, new MockHttpServletResponse(), uiSession);
    }
	
	class MockControllerProvider implements PageControllerProvider {

		private String pageName;
		
		public MockControllerProvider(String pageName) {
			this.pageName = pageName;
		}
		
		/**
		 * @see org.openmrs.ui.framework.page.PageControllerProvider#getController(java.lang.String)
		 */
		@Override
        public Object getController(String id) {
	        if (pageName.equals(id)) {
	        	return new MockPageController();
	        } else {
	        	return null;
	        }
        }
		
	}
	
	class MockViewProvider implements PageViewProvider {

		private String pageName;
		
		public MockViewProvider(String pageName) {
			this.pageName = pageName;
		}
		
        /**
         * @see org.openmrs.ui.framework.page.PageViewProvider#getView(java.lang.String)
         */
        @Override
        public PageView getView(String name) {
	        if (pageName.equals(name)) {
	        	return new PageView() {
					@Override
					public String render(PageContext context) throws PageAction {
						return "Contents of Some Page";
					}
					@Override
                    public ProviderAndName getController() {
	                    return null;
                    }
				};
            } else if ("groovy".equals(name)) {
                try {
                    Template template = new SimpleTemplateEngine(getClass().getClassLoader()).createTemplate("Testing ${ varInjectedByConfigurator } and ${ varInjectedByInterceptor }");
                    return new GroovyPageView(template, "somemodule:groovy");
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
	        } else {
	        	return null;
	        }
        }
		
	}

    public class MockPageController {
        public void controller() { }
    }

    public class ControllerThatTakesIntegerType {
        public void controller(Integer injected, Long notInjected) {
            Assert.assertNotNull("Integer argument was not injected", injected);
            Assert.assertNull("Long argument should not have been injected", notInjected);
        }
    }
}