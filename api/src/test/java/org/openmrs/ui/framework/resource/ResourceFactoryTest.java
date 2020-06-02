package org.openmrs.ui.framework.resource;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class ResourceFactoryTest {

	private ResourceFactory resourceFactory;

	@Before
	public void setup() {
		this.resourceFactory = new ResourceFactory();
	}

	@Test
	public void getResource_shouldReturnNullWhenUsingPathTraversal() {
		assertThat(resourceFactory.getResource("./config/../../../../myfile.properites"), nullValue());
		assertThat(resourceFactory.getResource("config/../../../../myfile.properties"), nullValue());
	}

}
