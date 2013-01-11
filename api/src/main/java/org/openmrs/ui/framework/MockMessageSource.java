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

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Behaves like the OpenMRS message source, i.e. it returns the passed-in code if no message is defined
 */
public class MockMessageSource implements MessageSource {

    Map<String, String> messages = new HashMap<String, String>();

    public void addMessage(String code, String message) {
        messages.put(code, message);
    }

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return helper(code, defaultMessage);
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        return helper(code, null);
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        return helper(resolvable.getCodes()[0], resolvable.getDefaultMessage());
    }

    private String helper(String code, String defaultMessage) {
        String message = messages.get(code);
        if (message != null) {
            return message;
        }
        return defaultMessage != null ? defaultMessage : code;
    }

}
