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

package org.openmrs.ui.framework;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class MissingRequiredCookieException extends RequestValidationException {

    private static final long serialVersionUID = 1L;

    private String requiredCookieName;

    public MissingRequiredCookieException(String requiredCookieName) {
        this.requiredCookieName = requiredCookieName;
    }

    @Override
    public List<String> getGlobalErrorCodes() {
        return Collections.singletonList("Required cookie: " + requiredCookieName);
    }
}
