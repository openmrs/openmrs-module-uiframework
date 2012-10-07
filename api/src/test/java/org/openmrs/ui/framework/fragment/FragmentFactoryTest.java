package org.openmrs.ui.framework.fragment;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Contains;
import org.openmrs.ui.framework.UiFrameworkException;
import org.openmrs.ui.framework.page.GroovyPageView;
import org.openmrs.ui.framework.page.PageAction;
import org.openmrs.ui.framework.page.PageContext;
import org.openmrs.ui.framework.page.PageFactory;
import org.openmrs.ui.framework.page.PageModelConfigurator;
import org.openmrs.ui.framework.page.PageRequest;
import org.openmrs.ui.framework.session.Session;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

public class FragmentFactoryTest {
	
	FragmentFactory factory;
	
	@Before
	public void beforeEachTest() throws Exception {
		factory = new FragmentFactory();

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
		Assert.assertNotNull(factory.getController(new FragmentRequest("somemodule", "somefragment")));
		Assert.assertNull(factory.getController(new FragmentRequest("somemodule", "otherfragment")));
		Assert.assertNull(factory.getController(new FragmentRequest("othermodule", "somefragment")));
	}
	
	/**
	 * @see FragmentFactory#getController(FragmentRequest)
	 * @verifies get a controller from any provider if none specified
	 */
	@Test
	public void getController_shouldGetAControllerFromAnyProviderIfNoneSpecified() throws Exception {
		Assert.assertNotNull(factory.getController(new FragmentRequest("*", "somefragment")));
		Assert.assertNotNull(factory.getController(new FragmentRequest("*", "otherfragment")));
		Assert.assertNull(factory.getController(new FragmentRequest("*", "nothingwiththisname")));
	}
	
	/**
     * @see FragmentFactory#getController(FragmentRequest)
     * @verifies fail if an invalid provider is specified
     */
    @Test(expected=UiFrameworkException.class)
    public void getController_shouldFailIfAnInvalidProviderIsSpecified() throws Exception {
		factory.getController(new FragmentRequest("unknownmodule", "somefragment"));
    }
	
	/**
	 * @see FragmentFactory#getView(FragmentRequest,String)
	 * @verifies get a view from the requested provider
	 */
	@Test
	public void getView_shouldGetAViewFromTheRequestedProvider() throws Exception {
		Assert.assertNotNull(factory.getView(new FragmentRequest("somemodule", "somefragment"), null));
		Assert.assertNull(factory.getView(new FragmentRequest("somemodule", "otherfragment"), null));
		Assert.assertNull(factory.getView(new FragmentRequest("othermodule", "somefragment"), null));
	}
	
	/**
	 * @see FragmentFactory#getView(FragmentRequest,String)
	 * @verifies get a view from any provider if none is specified
	 */
	@Test
	public void getView_shouldGetAViewFromAnyProviderIfNoneIsSpecified() throws Exception {
		Assert.assertNotNull(factory.getView(new FragmentRequest("*", "somefragment"), null));
		Assert.assertNotNull(factory.getView(new FragmentRequest("*", "otherfragment"), null));
		Assert.assertNull(factory.getView(new FragmentRequest("*", "nothingwiththisname"), null));
	}
	
	/**
     * @see FragmentFactory#getView(FragmentRequest,String)
     * @verifies fail if an invalid provider name is specified
     */
	@Test(expected=UiFrameworkException.class)
    public void getView_shouldFailIfAnInvalidProviderNameIsSpecified() throws Exception {
		factory.getView(new FragmentRequest("unknownmodule", "somefragment"), null);
    }

    @Test
    public void process_shouldSetCustomModelProperties() throws Exception {
        MockHttpSession httpSession = new MockHttpSession();
        Session session = new Session(httpSession);
        PageRequest pageRequest = new PageRequest("somemodule", "groovy", new MockHttpServletRequest(), new MockHttpServletResponse(), session);
        PageContext pageContext = new PageContext(pageRequest);
        pageContext.setPageFactory(new PageFactory());

        FragmentRequest fragmentRequest = new FragmentRequest("somemodule", "groovy");
        FragmentContext fragmentContext = new FragmentContext(fragmentRequest, pageContext);

        String result = factory.process(fragmentContext);
        Assert.assertThat(result, new Contains("Testing Success!!!"));
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
                    Template template = new SimpleTemplateEngine(getClass().getClassLoader()).createTemplate("Testing ${ someCustomVariable }");
                    return new GroovyFragmentView("somemodule:groovy", template);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
	        } else {
	        	return null;
	        }
        }
		
	}

}