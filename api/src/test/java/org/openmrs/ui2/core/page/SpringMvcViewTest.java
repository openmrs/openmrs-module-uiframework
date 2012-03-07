/**
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
package org.openmrs.ui2.core.page;

import org.junit.Assert;
import org.junit.Test;

public class SpringMvcViewTest {
	
	/**
	 * @see SpringMvcView#trimContent(String)
	 * @verifies not trim content if there are no comments
	 */
	@Test
	public void trimContent_shouldNotTrimContentIfThereAreNoComments() throws Exception {
		SpringMvcView view = new SpringMvcView("mapping", "<!-- START -->", "<!-- END -->", null);
		Assert.assertEquals("expected", view.trimContent("expected"));
	}
	
	/**
	 * @see SpringMvcView#trimContent(String)
	 * @verifies trim content after ending comment
	 */
	@Test
	public void trimContent_shouldTrimContentAfterEndingComment() throws Exception {
		SpringMvcView view = new SpringMvcView("mapping", "<!-- START -->", "<!-- END -->", null);
		Assert.assertEquals("expected", view.trimContent("expected<!-- END --> after"));
	}
	
	/**
	 * @see SpringMvcView#trimContent(String)
	 * @verifies trim content before starting comment
	 */
	@Test
	public void trimContent_shouldTrimContentBeforeStartingComment() throws Exception {
		SpringMvcView view = new SpringMvcView("mapping", "<!-- START -->", "<!-- END -->", null);
		Assert.assertEquals("expected", view.trimContent("before <!-- START -->expected"));
	}
	
	/**
	 * @see SpringMvcView#trimContent(String)
	 * @verifies trim content between comments
	 */
	@Test
	public void trimContent_shouldTrimContentBetweenComments() throws Exception {
		SpringMvcView view = new SpringMvcView("mapping", "<!-- START -->", "<!-- END -->", null);
		Assert.assertEquals("expected", view.trimContent("before <!-- START -->expected<!-- END --> after"));
	}
}
