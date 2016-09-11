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

import org.openmrs.BaseOpenmrsObject;

public class PageOverride extends BaseOpenmrsObject {

    private Integer pageOverrideId;

    private String providerName;

    private String pageName;

    private String overriddenPageName;

    private String overriddenProviderName;

    private boolean active;

    public PageOverride(){}

    public PageOverride(PageOverrideRequest identifier) {
        this.pageName = identifier.getPageName();
        this.providerName = identifier.getProviderName();
        this.overriddenPageName = identifier.getOverriddenPageName();
        this.overriddenProviderName = identifier.getOverriddenProviderName();
        this.active = false;
    }

    public Integer getPageOverrideId() {
        return pageOverrideId;
    }

    public void setPageOverrideId(Integer pageOverrideId) {
        this.pageOverrideId = pageOverrideId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public Integer getId() {
        return pageOverrideId;
    }

    @Override
    public void setId(Integer id) {
        this.pageOverrideId = id;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getOverriddenPageName() {
        return overriddenPageName;
    }

    public void setOverriddenPageName(String overriddenPageName) {
        this.overriddenPageName = overriddenPageName;
    }

    public String getOverriddenProviderName() {
        return overriddenProviderName;
    }

    public void setOverriddenProviderName(String overriddenProviderName) {
        this.overriddenProviderName = overriddenProviderName;
    }

    public boolean matchesIdentifier(PageOverrideRequest overrideIdentifier) {
        return getPageName().equals(overrideIdentifier.getPageName())
                && getProviderName().equals(overrideIdentifier.getProviderName())
                && getOverriddenPageName().equals(overrideIdentifier.getOverriddenPageName())
                && getOverriddenProviderName().equals(overrideIdentifier.getOverriddenProviderName());
    }

    @Override
    public String toString() {
        return "PageOverride{"
                +providerName+":"+pageName+" overrides"+overriddenProviderName+":"+overriddenPageName+","
                +"active: "+active
                +"}";
    }
}
