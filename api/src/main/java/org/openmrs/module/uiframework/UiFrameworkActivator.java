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
package org.openmrs.module.uiframework;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.ModuleActivator;
import org.openmrs.ui.framework.StandardModuleUiConfiguration;
import org.openmrs.ui.framework.UiContextRefreshedCallback;
import org.openmrs.ui.framework.WebConstants;
import org.openmrs.ui.framework.fragment.FragmentFactory;
import org.openmrs.ui.framework.page.PageFactory;
import org.openmrs.ui.framework.resource.ResourceFactory;

/**
 * Activator for the UI Framework module
 */
public class UiFrameworkActivator extends BaseModuleActivator implements ModuleActivator {
	
	private Log log = LogFactory.getLog(getClass());
	private volatile static long contextLastRefreshedTime;

	public static long getContextLastRefreshedTime() {
		return contextLastRefreshedTime;
	}

	/**
	 * @see org.openmrs.module.BaseModuleActivator#willStart()
	 */
	@Override
	public void willStart() {
		log.info("Starting UI Framework module");
	}
	
	/**
	 * @see org.openmrs.module.BaseModuleActivator#willStop()
	 */
	@Override
	public void willStop() {
		log.info("Stopping UI Framework module");
	}
	
	/**
	 * Every time the spring context is refreshed, we make callbacks to any {@link UiContextRefreshedCallback}
	 * beans that are managed by Spring. The main purpose of this is to that modules can be configured to use
	 * the UI Framework through simple usage of {@link StandardModuleUiConfiguration}.
	 * 
	 * @see org.openmrs.module.BaseModuleActivator#contextRefreshed()
	 */
	@Override
	public void contextRefreshed() {
		contextLastRefreshedTime = System.currentTimeMillis();

		// START HACK
		// since we're not using a Listener anymore, these are not set at startup
		try {
			Class<?> webConstants1x = Context.loadClass("org.openmrs.web.WebConstants");
			String webappName = (String) webConstants1x.getField("WEBAPP_NAME").get(null);
			WebConstants.CONTEXT_PATH = webappName;
			WebConstants.WEBAPP_NAME = webappName;
		} catch (Exception ex) {
			log.error("Failed to get CONTEXT_PATH from WebConstants during UI Framework startup");
		}
		// END HACK
		
		PageFactory pageFactory = getComponent(PageFactory.class);
		FragmentFactory fragmentFactory = getComponent(FragmentFactory.class);
		ResourceFactory resourceFactory = getComponent(ResourceFactory.class);

		List<UiContextRefreshedCallback> callbacks = Context.getRegisteredComponents(UiContextRefreshedCallback.class);
		for (UiContextRefreshedCallback callback : callbacks) {
			try {
				callback.afterContextRefreshed(pageFactory, fragmentFactory, resourceFactory);
			} catch (Exception ex) {
				log.error("Error in UiContextRefreshedCallback: " + callback, ex);
			}
		}
	}
	
	
	private <T> T getComponent(Class<T> clazz) {
		List<T> list = Context.getRegisteredComponents(clazz);
		if (list == null || list.size() == 0)
			throw new RuntimeException("Cannot find component of " + clazz);
		return list.get(0);
	}
	
}
