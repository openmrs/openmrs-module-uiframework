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

/**
 * Wrapper for page name and provider, makes storing overrides more structured and easier
 */
class PageIdentifier {

    private final String provider;

    private final String page;

    public PageIdentifier(String provider, String page) {
        this.provider = provider;
        this.page = page;
    }

    public String getProvider() {
        return provider;
    }

    public String getPage() {
        return page;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PageIdentifier that = (PageIdentifier) o;

        if (provider != null ? !provider.equals(that.provider) : that.provider != null) return false;
        return page != null ? page.equals(that.page) : that.page == null;

    }

    @Override
    public int hashCode() {
        int result = provider != null ? provider.hashCode() : 0;
        result = 31 * result + (page != null ? page.hashCode() : 0);
        return result;
    }
}
