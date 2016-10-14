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

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.ui.framework.annotation.RequestPageOverride;
import org.openmrs.ui.framework.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

public class PageOverrideHandlerImplTest extends BaseModuleContextSensitiveTest {

    @Autowired
    PageOverrideHandlerImpl handler;

    @Autowired
    PageOverrideDAO pageOverrideDAO;

    @Autowired
    protected DbSessionFactory sessionFactory;

    private List<PageOverrideRequest> testPageOverrides;

    @Before
    public void initializeHandler() throws Exception {
        executeDataSet("ModuleTestData-pageOverrides.xml");
        testPageOverrides = new ArrayList<PageOverrideRequest>();
        testPageOverrides.add(new PageOverrideRequest("awesomemodule", "dashboard", createMockOverride("referenceapplication", "dashboard")));
        testPageOverrides.add(new PageOverrideRequest("awesomemodule", "patients", createMockOverride("referenceapplication", "patients")));
        testPageOverrides.add(new PageOverrideRequest("awesomemodule", "home", createMockOverride("referenceapplication", "home")));
        testPageOverrides.add(new PageOverrideRequest("patientmoduleEXT", "coolpatient", createMockOverride("patientmodule", "patient"), 0));
        testPageOverrides.add(new PageOverrideRequest("referenceapplication", "patient", createMockOverride("patientmodule", "patient"), 33));
        handler.requestPageOverrides(testPageOverrides);
        handler.initialize();
    }

    @After
    public void clearHandler(){
        handler.getActiveOverrides().clear();
        handler.getOverrideCandidates().clear();
        handler.getRequestedOverrides().clear();
    }

    @Test
    public void initialize_shouldProcessPageOverrides(){
        //no requested override matching persisted override with id=3, have to be deleted
        assertThat(pageOverrideDAO.getById(3), is(nullValue()));
        assertThat(pageOverrideDAO.getById(2), isActiveOverride());
        assertThat(pageOverrideDAO.getAll(), hasSize(testPageOverrides.size()));
    }

    @Test
    public void overrideRequest_shouldOverrideRequestIfPageOverrideIsActive(){
        PageRequest mockRequest = createMockPageRequest("referenceapplication", "patients");
        handler.overrideRequest(mockRequest);
        assertThat(mockRequest, allOf(hasOverriddenProvider("awesomemodule"), hasOverriddenPage("patients")));
    }

    @Test
    public void overrideRequest_shouldNotOverrideRequestIfPageOverrideIsInactive(){
        PageRequest mockRequest = createMockPageRequest("referenceapplication", "home");
        handler.overrideRequest(mockRequest);
        assertThat(mockRequest, allOf(hasOverriddenProvider(null), hasOverriddenPage(null)));
    }

    @Test
    public void initialize_shouldActivateNewPageOverrideWithHighestOrder(){
        PageRequest mockRequest = createMockPageRequest("patientmodule", "patient");
        handler.overrideRequest(mockRequest);
        assertThat(mockRequest, allOf(hasOverriddenProvider("referenceapplication"), hasOverriddenPage("patient")));
    }

    @Test
    public void activateOverride_shouldMakeHandlerOverridePage(){
        //sanity check before activation
        PageRequest mockRequest = createMockPageRequest("referenceapplication", "home");
        handler.overrideRequest(mockRequest);
        assertThat(mockRequest.getPageNameOverride(), is(nullValue()));

        handler.activateOverride(pageOverrideDAO.getByUuid("e5237ef2-03be-47e8-9bbd-7a33d9c9fe7c"));

        handler.overrideRequest(mockRequest);
        assertThat(mockRequest, hasOverriddenProvider("awesomemodule"));

        handler.deactivateOverride(pageOverrideDAO.getByUuid("e5237ef2-03be-47e8-9bbd-7a33d9c9fe7c"));
    }

    private Matcher<PageRequest> hasOverriddenProvider(String providerName){
        return new FeatureMatcher<PageRequest, String>(equalTo(providerName), "provider override", ""){
            @Override
            protected String featureValueOf(PageRequest actual) {
                return actual.getProviderNameOverride();
            }
        };
    }

    private Matcher<PageRequest> hasOverriddenPage(String pageName){
        return new FeatureMatcher<PageRequest, String>(equalTo(pageName), "page override", "") {
            @Override
            protected String featureValueOf(PageRequest actual) {
                return actual.getPageNameOverride();
            }
        };
    }

    private Matcher<PageOverride> isActiveOverride(){
        return new FeatureMatcher<PageOverride, Boolean>(is(Boolean.TRUE), "active", "active") {
            @Override
            protected Boolean featureValueOf(PageOverride actual) {
                return actual.isActive();
            }
        };
    }

    public static PageRequest createMockPageRequest(String provider, String page) {
        MockHttpSession httpSession = new MockHttpSession();
        Session uiSession = new Session(httpSession);
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setSession(httpSession);
        return new PageRequest(provider, page, req, new MockHttpServletResponse(), uiSession);
    }

    private RequestPageOverride createMockOverride(final String providerName, final String pageName){
        return new RequestPageOverride() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return RequestPageOverride.class;
            }

            @Override
            public String providerName() {
                return providerName;
            }

            @Override
            public String pageName() {
                return pageName;
            }

            @Override
            public int order() {
                return 0;
            }
        };
    }
}
