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

import org.openmrs.ui.framework.page.PageAction;
import org.openmrs.ui.framework.page.PageContext;

/**
 * Interface for general page request interceptors. When
 * {@link org.openmrs.ui.framework.page.PageFactory} is handling a request it will invoke the
 * <code>beforeHandleRequest</code> method of any component which implements this interface, before
 * handling the request using it's associated controller.
 */
public interface PageRequestInterceptor {
	
	/**
	 * Invoked before the page request is handled by the controller
	 * 
	 * @param pageContext the page context
	 */
	void beforeHandleRequest(PageContext pageContext) throws PageAction;
}
