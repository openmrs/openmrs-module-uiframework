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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ui.framework.resource.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * To use this class, instantiate a bean in your module's application context, for example:
 * <pre>
 * <bean class="org.openmrs.ui.framework.page.GlobalResourceIncluder>
 *     <property name="resources">
 *         <list>
 *             <bean class="org.openmrs.ui.framework.resource.Resource">
 *                 <property name="category" value="css"/>
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

    private final Log log = LogFactory.getLog(getClass());

    private List<Resource> resources;

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }
    
    public void addResource(Resource resource) {
        if (resources == null) {
            resources = new ArrayList<Resource>();
        }
        resources.add(resource);
    }
    
    @Override
    public void configureModel(PageContext pageContext) {
        for (Resource resource : resources) {
            if (resource.getCategory() != null) {
                pageContext.includeResource(resource);
            } else {
                log.warn(GlobalResourceIncluder.class.getName() + " is trying to include a resource with no category: " + resource.getProviderName() + ":" + resource.getResourcePath());
            }
        }
    }

}
