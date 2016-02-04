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

package org.openmrs.ui.framework.fragment;

import java.util.Map;

/**
 * Modules can implement this interface to provide possible arguments for fragment action methods, which will be
 * injected by type into the controller's action method.
 */
public interface PossibleFragmentActionArgumentProvider {

    /**
     * @param possibleArguments Implementations should add new mappings to this map
     */
    void addPossibleFragmentActionArguments(Map<Class<?>, Object> possibleArguments);

}
