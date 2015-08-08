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

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.ViewException;

/**
 * UiUtils subclass specifically for pages
 */
public class PageUiUtils extends UiUtils {
	
	protected final Log log = LogFactory.getLog(getClass());
	
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
	
	/**
	 * @see {@link #requirePrivileges(List, String, String, String)}
	 */
	public void requirePrivilege(String privilege, String redirectViewProvider, String redirectView) throws Redirect {
		requirePrivileges(Collections.singletonList(privilege), redirectViewProvider, redirectView, null);
	}
	
	/**
	 * Checks if the user has all the specified privileges, if they are don't have any of the
	 * privileges, they get redirected to the specified view, if a handler is specified, it gets
	 * invoked when the privilege check fails. The handler should be the spring bean id of a
	 * subclass of MissingPrivilegesHandler
	 *
	 * @param privileges The privileges to check for
	 * @param redirectViewProvider The view provider for the page to redirect the user to in case
	 *            they don't have the privilege
	 * @param redirectView The view to redirect the user to in case they don't have the privilege
	 * @param handler The spring bean id of the handler to invoke
	 */
	public void requirePrivileges(List<String> privileges, String redirectViewProvider, String redirectView, String handler)
	    throws Redirect {
		
		if (CollectionUtils.isEmpty(privileges)) {
			throw new ViewException("At least one privilege is required");
		}
		if (StringUtils.isBlank(redirectViewProvider)) {
			throw new ViewException("redirectViewProvider cannot be blank");
		}
		if (StringUtils.isBlank(redirectView)) {
			throw new ViewException("redirectView cannot be blank");
		}
		
		for (String privilege : privileges) {
			if (!Context.hasPrivilege(privilege)) {
				if (StringUtils.isNotBlank(handler)) {
					MissingPrivilegesHandler preferredHandler = Context.getRegisteredComponent(handler,
					    MissingPrivilegesHandler.class);
					
					try {
						preferredHandler.handle(pageContext, privileges);
					}
					catch (Exception e) {
						log.error("An error occurred while invoking the missing privilege handler", e);
						break;
					}
				}
				
				throw new Redirect(redirectViewProvider, redirectView, null);
			}
		}
	}
}
