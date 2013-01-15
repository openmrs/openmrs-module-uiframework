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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.Role;
import org.springframework.test.AssertThrows;

import java.util.Locale;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class FormatterImplTest {

    MockMessageSource messageSource;
    FormatterImpl formatter;

    @Before
    public void setUp() {
        messageSource = new MockMessageSource();
        formatter = new FormatterImpl(messageSource);
    }

    @Test
    public void testFormattingAMetadata() throws Exception {
        Locale locale = Locale.ENGLISH;

        EncounterType encounterType = new EncounterType();
        encounterType.setName("Emergency Check-In");

        String output = formatter.format(encounterType, locale);

        assertThat(output, is(encounterType.getName()));
    }

    @Test
    public void testFormattingWithOverriddenMetadataName() throws Exception {
        Locale locale = Locale.ENGLISH;
        String displayName = "Emergency Check-In";
        String uuid = "a-fake-uuid";

        messageSource.addMessage("ui.i18n.EncounterType.name." + uuid, displayName);

        EncounterType encounterType = new EncounterType();
        encounterType.setUuid(uuid);

        String output = formatter.format(encounterType, locale);

        assertThat(output, is(displayName));
    }

    @Test
    public void testFormattingRole()  throws Exception {
        Locale locale = Locale.ENGLISH;

        Role role = new Role();
        role.setRole("Admin");

        String output = formatter.format(role, locale);
        assertThat(output, is(role.getRole()));
    }

    @Test
    public void testFormattingRoleWithOverriddenMetadataName()  throws Exception {
        Locale locale = Locale.ENGLISH;

        String displayName = "Administrator";
        String uuid = "a-fake-uuid";

        messageSource.addMessage("ui.i18n.Role.name." + uuid, displayName);

        Role role = new Role();
        role.setRole("Admin");
        role.setUuid(uuid);

        String output = formatter.format(role, locale);
        assertThat(output, is(displayName));
    }

}
