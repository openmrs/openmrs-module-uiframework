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

import org.openmrs.ui.framework.resource.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * To use this class, instantiate a bean in your module's application context, for example:
 * <pre>
 * <bean class="org.openmrs.ui.framework.page.GlobalResourceIncluder>
 *     <property name="cssResources">
 *         <list>
 *             <bean class="org.openmrs.ui.framework.resource.Resource">
 *                 <property name="providerName" value="mirebalais"/>
 *                 <property name="resourcePath" value="mirebalais.css"/>
 *             </bean>
 *         </list>
 *     </property>
 * </bean>
 * </pre>
 * @since 2.1
 */
public class GlobalResourceIncluder implements PageModelConfigurator {

    private List<Resource> jsResources;

    private List<Resource> cssResources;

    public List<Resource> getJsResources() {
        return jsResources;
    }

    public void setJsResources(List<Resource> jsResources) {
        this.jsResources = jsResources;
    }

    public List<Resource> getCssResources() {
        return cssResources;
    }

    public void setCssResources(List<Resource> cssResources) {
        this.cssResources = cssResources;
    }

    @Override
    public void configureModel(PageContext pageContext) {
        if (jsResources != null) {
            for (Resource jsResource : jsResources) {
                pageContext.includeJavascript(jsResource);
            }
        }
        if (cssResources != null) {
            for (Resource cssResource : cssResources) {
                pageContext.includeCss(cssResource);
            }
        }
    }

    public void addCssResource(String providerName, String resourcePath) {
        if (cssResources == null) {
            cssResources = new ArrayList<Resource>();
        }
        cssResources.add(new Resource(providerName, resourcePath));
    }

    public void addJsResource(String providerName, String resourcePath) {
        if (jsResources == null) {
            jsResources = new ArrayList<Resource>();
        }
        jsResources.add(new Resource(providerName, resourcePath));
    }

}
