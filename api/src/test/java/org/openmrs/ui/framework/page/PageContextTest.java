/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.ui.framework.page;

import org.junit.Test;
import org.openmrs.ui.framework.resource.Resource;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class PageContextTest {

	@Test
	public void getResourcesToInclude_shouldMaintainInsertionOrder() throws Exception {
		Resource first = new Resource(Resource.CATEGORY_CSS, "source", "z_first", null);
		Resource second = new Resource(Resource.CATEGORY_CSS, "source", "a_second", null);
		Resource third = new Resource(Resource.CATEGORY_CSS, "source", "c_third", null);

		PageContext pageContext = new PageContext(null);
		pageContext.includeResource(first);
		pageContext.includeResource(second);
		pageContext.includeResource(third);

		List<Resource> resources = pageContext.getResourcesToInclude(Resource.CATEGORY_CSS);

		assertThat(resources.get(0), is(first));
		assertThat(resources.get(1), is(second));
		assertThat(resources.get(2), is(third));
	}

	@Test
	public void getUniqueResourcesByCategory_shouldGetCorrectOrder() throws Exception {
		Resource first = new Resource(Resource.CATEGORY_JS, "normal", "normal.js", null);
		Resource second = new Resource(Resource.CATEGORY_JS, "low", "low.js", -100);
		Resource third = new Resource(Resource.CATEGORY_JS, "high", "high.js", 100);

		PageContext pageContext = new PageContext(null);
		pageContext.includeResource(first);
		pageContext.includeResource(second);
		pageContext.includeResource(third);

		Collection<Resource> resources = pageContext.getUniqueResourcesByCategory(Resource.CATEGORY_JS);
		assertThat(resources, contains(third, first, second));
	}
}