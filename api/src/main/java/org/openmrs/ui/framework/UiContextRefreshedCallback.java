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
package org.openmrs.ui.framework;

import org.openmrs.ui.framework.fragment.FragmentFactory;
import org.openmrs.ui.framework.page.PageFactory;
import org.openmrs.ui.framework.resource.ResourceFactory;

/**
 * Any implementation of this interface that you instantiate a Spring-managed bean for will receive
 * a callback from the UI Framework module after the Spring context is refreshed.
 */
public interface UiContextRefreshedCallback {
	
	/**
	 * This will be called by the UI Framework module every time the spring context is refreshed.
	 * 
	 * @param pageFactory
	 * @param fragmentFactory
	 * @param resourceFactory TODO
	 */
	void afterContextRefreshed(PageFactory pageFactory, FragmentFactory fragmentFactory, ResourceFactory resourceFactory);
	
}
