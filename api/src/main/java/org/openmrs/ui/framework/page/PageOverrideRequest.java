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

import org.openmrs.ui.framework.annotation.RequestPageOverride;

public class PageOverrideRequest {
    private String providerName;

    private String pageName;

    private String overriddenPageName;

    private String overriddenProviderName;

    int order;

    public PageOverrideRequest(String providerName, String pageName, RequestPageOverride requestPageOverride, int order) {
        this.providerName = providerName;
        this.pageName = pageName;
        this.overriddenPageName = requestPageOverride.pageName();
        this.overriddenProviderName = requestPageOverride.providerName();
        this.order = order;
    }

    public PageOverrideRequest(String providerName, String pageName, RequestPageOverride requestPageOverride) {
        this(providerName, pageName, requestPageOverride, requestPageOverride.order());
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
