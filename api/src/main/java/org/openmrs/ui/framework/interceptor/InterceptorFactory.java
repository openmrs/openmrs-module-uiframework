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

package org.openmrs.ui.framework.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.UiContextRefreshedCallback;
import org.openmrs.ui.framework.fragment.FragmentFactory;
import org.openmrs.ui.framework.page.PageContext;
import org.openmrs.ui.framework.page.PageFactory;
import org.openmrs.ui.framework.resource.ResourceFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages registered interceptors
 */
@Component
public class InterceptorFactory implements UiContextRefreshedCallback {

	private static final Log log = LogFactory.getLog(InterceptorFactory.class);

	private List<PageRequestInterceptor> pageInterceptors = new ArrayList<PageRequestInterceptor>();

	/**
	 * Invoked when the application context has been refreshed
	 */
	@Override
	public void afterContextRefreshed(PageFactory pageFactory, FragmentFactory fragmentFactory, ResourceFactory resourceFactory) {
		// Load registered page interceptors
		pageInterceptors = Context.getRegisteredComponents(PageRequestInterceptor.class);

		if (log.isDebugEnabled()) {
			for (PageRequestInterceptor interceptor : pageInterceptors) {
				log.debug("Found interceptor " + interceptor.getClass().getName() + " for page requests");
			}
		}
	}

	/**
	 * Invokes the interceptors for this page request
	 * @param context the page context
	 */
	public void handlePageRequest(PageContext context) {
		// Invoke general page request interceptors
		for (PageRequestInterceptor interceptor : pageInterceptors) {
			interceptor.beforeHandleRequest(context);
		}
	}
}