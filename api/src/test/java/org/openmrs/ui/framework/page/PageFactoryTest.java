package org.openmrs.ui.framework.page;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.internal.matchers.Contains;
import org.openmrs.ui.framework.ProviderAndName;
import org.openmrs.ui.framework.UiFrameworkException;
import org.openmrs.ui.framework.fragment.FragmentRequest;
import org.openmrs.ui.framework.session.Session;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import static org.mockito.Matchers.contains;

public class PageFactoryTest {
	
	PageFactory factory;
	
	@Before
	public void beforeEachTest() throws Exception {
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
                pageContext.getModel().put("someCustomVariable", "Success!!!");
            }
        };
        factory.setModelConfigurators(Collections.singletonList(configurator));
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
        Assert.assertThat(result, new Contains("Testing Success!!!"));
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
                    Template template = new SimpleTemplateEngine(getClass().getClassLoader()).createTemplate("Testing ${ someCustomVariable }");
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
	
}