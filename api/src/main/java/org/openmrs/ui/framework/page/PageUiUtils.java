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

package org.openmrs.ui.framework.page;

import org.openmrs.ui.framework.UiUtils;

/**
 * UiUtils subclass specifically for pages
 */
public class PageUiUtils extends UiUtils {
	
	/**
	 * Constructs instance from page context
	 * 
	 * @param pageContext the page context
	 */
	public PageUiUtils(PageContext pageContext) {
		this.pageContext = pageContext;
		this.fragmentIncluder = pageContext;
		this.resourceIncluder = pageContext;
		this.formatter = pageContext.getFormatter();
		this.messager = pageContext;
		this.decoratable = pageContext;
		this.extensionManager = pageContext.getExtensionManager();
		this.conversionService = pageContext.getPageFactory().getConversionService();
	}
}
