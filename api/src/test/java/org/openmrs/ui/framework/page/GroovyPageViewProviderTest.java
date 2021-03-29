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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class GroovyPageViewProviderTest {
	
	private GroovyPageViewProvider provider;
	
	@Before
	public void setUp() throws Exception {
		provider = new GroovyPageViewProvider() {
			
			@Override
			public String getViewContents(String name) throws Exception {
				return "<% def squared = { it * it } %> <html>A view named " + name
				        + ". <% [1:3].each { %>${ it } squared is ${ squared(it) }. <% } %></html>";
			}
		};
	}
	
	@Test
	public void testThatViewsAreCachedInProductionMode() throws Exception {
		PageView first = provider.getView("home");
		assertNotNull(first);
		
		PageView second = provider.getView("home");
		assertTrue(first == second);
	}
}
