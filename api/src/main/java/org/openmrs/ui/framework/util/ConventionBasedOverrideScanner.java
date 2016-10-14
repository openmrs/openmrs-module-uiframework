/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.ui.framework.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ui.framework.annotation.RequestPageOverride;
import org.openmrs.ui.framework.page.PageControllerNamingConvention;
import org.openmrs.ui.framework.page.PageOverrideRequest;
import org.openmrs.util.OpenmrsClassLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Reflection utility to search classpath for page/fragment controllers which are overriding other pages
 */
public class ConventionBasedOverrideScanner {

    protected final Log log = LogFactory.getLog(getClass());

    private final MetadataReaderFactory metadataReaderFactory;

    private final ResourcePatternResolver resourceResolver;

    private ConventionBasedOverrideScanner() {
        this.metadataReaderFactory = new SimpleMetadataReaderFactory(OpenmrsClassLoader.getInstance());
        this.resourceResolver = new PathMatchingResourcePatternResolver(OpenmrsClassLoader.getInstance());
    }

    /**
     * @return the instance
     */
    public static synchronized ConventionBasedOverrideScanner getInstance() {
        if (InstanceHolder.INSTANCE == null) {
            InstanceHolder.INSTANCE = new ConventionBasedOverrideScanner();
        }

        return InstanceHolder.INSTANCE;
    }

    public static void destroyInstance() {
        InstanceHolder.INSTANCE = null;
    }

    public List<PageOverrideRequest> getPageOverrideIdentifiersToOrderMap(String moduleId){
        Set<Class<?>> classesWithAnnotation = getClassesWithAnnotation(RequestPageOverride.class, "classpath*:org/openmrs/module/" + moduleId + "/page/controller/**/*.class");
        List<PageOverrideRequest> overrides = new ArrayList<PageOverrideRequest>();

        for(Class<?> classWithAnnotation : classesWithAnnotation){
            RequestPageOverride overrideConfig = classWithAnnotation.getAnnotation(RequestPageOverride.class);
            String pageName = PageControllerNamingConvention.toPageName(classWithAnnotation.getName());
            // by convention moduleId is provider name
            overrides.add(new PageOverrideRequest(moduleId, pageName, overrideConfig, overrideConfig.order()));
        }

        return overrides;
    }

    /**
     * Searches for classes matching given pattern and with given annotation
     *
     * @param annotationClass the annotation class
     * @return the list of found classes
     */
    private Set<Class<?>> getClassesWithAnnotation(Class annotationClass, String pattern) {

        Set<Class<?>> types = new HashSet<Class<?>>();

        try {
            Resource[] resources = resourceResolver.getResources(pattern);
            TypeFilter typeFilter = new AnnotationTypeFilter(annotationClass);
            for (Resource resource : resources) {
                try {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    if (typeFilter.match(metadataReader, metadataReaderFactory)) {
                        String classname = metadataReader.getClassMetadata().getClassName();
                        try {
                            @SuppressWarnings("unchecked")
                            Class<?> metadata = (Class<?>) OpenmrsClassLoader.getInstance().loadClass(classname);
                            types.add(metadata);
                        }
                        catch (ClassNotFoundException e) {
                            throw new IOException("Class cannot be loaded: " + classname, e);
                        }
                    }
                }
                catch (IOException e) {
                    log.debug("Resource cannot be loaded: " + resource);
                }
            }
        }
        catch (IOException ex) {
            log.error("Failed to look for classes with annocation" + annotationClass, ex);
        }

        return types;
    }

    /**
     * Private class to hold the one class scanner used throughout openmrs.
     */
    private static class InstanceHolder {

        private static ConventionBasedOverrideScanner INSTANCE = null;
    }
}
