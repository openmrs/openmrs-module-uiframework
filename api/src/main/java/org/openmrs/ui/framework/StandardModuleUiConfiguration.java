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

import org.openmrs.module.Module;
import org.openmrs.module.ModuleClassLoader;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.ModuleUtil;
import org.openmrs.ui.framework.fragment.ConventionBasedClasspathFragmentControllerProvider;
import org.openmrs.ui.framework.fragment.FragmentFactory;
import org.openmrs.ui.framework.fragment.GroovyFragmentViewProvider;
import org.openmrs.ui.framework.page.ConventionBasedClasspathPageControllerProvider;
import org.openmrs.ui.framework.page.GroovyPageViewProvider;
import org.openmrs.ui.framework.page.PageFactory;

/**
 *
 */
public class StandardModuleUiConfiguration implements UiContextRefreshedCallback {
	
	private String moduleId;
	
	/**
	 * @return the moduleId
	 */
	public String getModuleId() {
		return moduleId;
	}
	
	/**
	 * @param moduleId the moduleId to set
	 */
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	
	/**
	 * @see org.openmrs.ui.framework.UiContextRefreshedCallback#afterContextRefreshed(org.openmrs.ui.framework.page.PageFactory,
	 *      org.openmrs.ui.framework.fragment.FragmentFactory)
	 */
	@Override
	public void afterContextRefreshed(PageFactory pageFactory, FragmentFactory fragmentFactory) {

		ModuleClassLoader moduleClassLoader = ModuleFactory.getModuleClassLoader(moduleId);
		if (moduleClassLoader == null)
			throw new RuntimeException("Failed to get ModuleClassLoader for " + moduleId);

		// standard controller provider for pages
		{
			ConventionBasedClasspathPageControllerProvider pcp = new ConventionBasedClasspathPageControllerProvider();
			pcp.setBasePackage("org.openmrs.module." + moduleId + ".page.controller");
			pageFactory.addControllerProvider(moduleId, pcp);
		}
		
		// standard view provider for pages
		{
			GroovyPageViewProvider pvp = new GroovyPageViewProvider();
			pvp.setViewClassLoader(moduleClassLoader);
			pageFactory.addViewProvider(moduleId, pvp);
		}
		
		// standard controller provider for fragments
		{
			ConventionBasedClasspathFragmentControllerProvider fcp = new ConventionBasedClasspathFragmentControllerProvider();
			fcp.setBasePackage("org.openmrs.module." + moduleId + ".fragment.controller");
			fragmentFactory.addControllerProvider(moduleId, fcp);
		}
	
		// standard view provider for fragments
		{
			GroovyFragmentViewProvider fvp = new GroovyFragmentViewProvider();
			fvp.setViewClassLoader(moduleClassLoader);
			fragmentFactory.addViewProvider(moduleId, fvp);
		}
	}
	
}
