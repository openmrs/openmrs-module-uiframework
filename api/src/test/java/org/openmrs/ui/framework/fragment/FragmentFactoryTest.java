package org.openmrs.ui.framework.fragment;


import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ui.framework.UiFrameworkException;
import org.openmrs.ui.framework.page.PageAction;

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
	        } else {
	        	return null;
	        }
        }
		
	}

}