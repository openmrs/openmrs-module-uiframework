/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.ui.framework.page;

import org.openmrs.module.uiframework.UiFrameworkActivator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class PageOverrideHandlerImpl implements PageOverrideHandler {

    @Autowired
    private PageOverrideDAO dao;

    /**
     * contains requested overrides. They are processed when {@link PageOverrideHandlerImpl#initialize()} is invoked
     * - key is PageIdentifier of overridden page
     * - value is map of registered overrides and their orders
     */
    private final Map<PageIdentifier, List<PageOverrideRequest>> requestedOverrides = new HashMap<PageIdentifier, List<PageOverrideRequest>>();

    /**
     * override candidates are all processed and persisted PageOverrides, active and inactive
     * - key is PageIdentifier of overridden page
     * - value is list of all registered and processed overrides
     */
    private final Map<PageIdentifier, List<PageOverride>> overrideCandidates = new HashMap<PageIdentifier, List<PageOverride>>();

    /**
     * active overrides is simple map of activated overrides
     */
    private final Map<PageIdentifier, PageOverride> activeOverrides = new HashMap<PageIdentifier, PageOverride>();

    public Map<PageIdentifier, List<PageOverrideRequest>> getRequestedOverrides() {
        return requestedOverrides;
    }

    public Map<PageIdentifier, List<PageOverride>> getOverrideCandidates() {
        return overrideCandidates;
    }

    public Map<PageIdentifier, PageOverride> getActiveOverrides() {
        return activeOverrides;
    }

    @Override
    public void requestPageOverride(PageOverrideRequest pageOverrideRequest) {
        if(pageOverrideRequest == null){
            throw new IllegalArgumentException("Cannot add null PageOverrideRequest");
        }

        PageIdentifier overriddenPageIdentifier = new PageIdentifier(pageOverrideRequest.getOverriddenProviderName(), pageOverrideRequest.getOverriddenPageName());
        List<PageOverrideRequest> overridesMap = requestedOverrides.get(overriddenPageIdentifier);
        if(overridesMap == null){
            overridesMap = new ArrayList<PageOverrideRequest>();
            requestedOverrides.put(overriddenPageIdentifier, overridesMap);
        }
        overridesMap.add(pageOverrideRequest);
    }

    @Override
    public void requestPageOverrides(List<PageOverrideRequest> pageOverrideRequests) {
        for (PageOverrideRequest pageOverrideRequest : pageOverrideRequests) {
            requestPageOverride(pageOverrideRequest);
        }
    }

    @Override
    public boolean overrideRequest(PageRequest pageRequest){
        PageOverride pageOverride = getActivePageOverride(pageRequest.getProviderName(), pageRequest.getPageName());

        if(pageOverride != null){
            pageRequest.setProviderNameOverride(pageOverride.getProviderName());
            pageRequest.setPageNameOverride(pageOverride.getPageName());
            return true;
        }
        return false;
    }

    public PageOverride getActivePageOverride(String providerName, String pageName){
        PageIdentifier pageIdentifier = new PageIdentifier(providerName, pageName);
        return activeOverrides.get(pageIdentifier);
    }

    @Override
    public void activateOverride(PageOverride pageOverride){
        PageIdentifier overriddenPage = new PageIdentifier(pageOverride.getOverriddenProviderName(), pageOverride.getOverriddenPageName());
        PageOverride previousOverride = activeOverrides.get(overriddenPage);

        if(previousOverride != null){
            previousOverride.setActive(false);
            dao.saveOrUpdate(previousOverride);
        }
        activeOverrides.put(overriddenPage, pageOverride);

        pageOverride.setActive(true);
        dao.saveOrUpdate(pageOverride);
    }

    @Override
    public void deactivateOverride(PageOverride pageOverride){
        PageIdentifier overriddenPage = new PageIdentifier(pageOverride.getOverriddenProviderName(), pageOverride.getOverriddenPageName());
        PageOverride actualOverride = activeOverrides.get(overriddenPage);

        if(actualOverride.equals(pageOverride)){
            activeOverrides.remove(overriddenPage);
        }

        pageOverride.setActive(false);
        dao.saveOrUpdate(pageOverride);
    }

    /**
     * process {@link PageOverrideHandlerImpl#requestedOverrides} to populate {@link PageOverrideHandlerImpl#activeOverrides}
     * and {@link PageOverrideHandlerImpl#overrideCandidates}. Persist PageOverrides, delete those PageOverrides, which are stored
     * in DB, but were not requested at current setup.
     *
     * invoked in {@link UiFrameworkActivator#contextRefreshed()}, so requests are properly overridden
     */
    @Override
    public synchronized void initialize(){
        List<PageOverride> persistedOverrides = dao.getAll();

        for (Map.Entry<PageIdentifier, List<PageOverrideRequest>> requestedOverrides : this.requestedOverrides.entrySet()) {
            //store key and value in variables for better readability
            PageIdentifier overriddenPage = requestedOverrides.getKey();
            List<PageOverrideRequest> pageOverrideRequests = requestedOverrides.getValue();

            PageOverride persistedActiveOverride = null;
            PageOverride highestOrderNewOverride = null;
            int highestOrder = Integer.MIN_VALUE;

            for (PageOverrideRequest overrideRequest : pageOverrideRequests) {

                PageOverride persistedOverride = findMatchingOverride(persistedOverrides, overrideRequest);

                if(persistedOverride != null){
                    persistedOverrides.remove(persistedOverride);
                    if(persistedOverride.isActive()){
                        persistedActiveOverride = persistedOverride;
                    }
                } else {
                    persistedOverride = new PageOverride(overrideRequest);
                    dao.saveOrUpdate(persistedOverride);

                    if(persistedActiveOverride == null && (highestOrderNewOverride == null || overrideRequest.getOrder() > highestOrder)){
                        highestOrderNewOverride = persistedOverride;
                        highestOrder = overrideRequest.getOrder();
                    }
                }

                addOverrideCandidate(overriddenPage, persistedOverride);
            }

            if(persistedActiveOverride!= null){
                activeOverrides.put(overriddenPage, persistedActiveOverride);
            } else if(highestOrderNewOverride != null){
                activateOverride(highestOrderNewOverride);
            }
        }

        for (PageOverride persistedOverride : persistedOverrides) {
            dao.delete(persistedOverride);
        }
    }

    /**
     * helper method for {@link PageOverrideHandlerImpl#initialize()}
     */
    private PageOverride findMatchingOverride(List<PageOverride> persistedOverrides, PageOverrideRequest overrideRequest) {
        for (PageOverride persistedOverride : persistedOverrides) {
            if(persistedOverride.matchesIdentifier(overrideRequest)) {
                return persistedOverride;
            }
        }
        return null;
    }

    private void addOverrideCandidate(PageIdentifier overriddenPage, PageOverride pageOverride){
        List<PageOverride> pageOverrides = overrideCandidates.get(overriddenPage);
        if(pageOverrides == null){
            pageOverrides = new ArrayList<PageOverride>();
            overrideCandidates.put(overriddenPage, pageOverrides);
        }
        pageOverrides.add(pageOverride);
    }

}
