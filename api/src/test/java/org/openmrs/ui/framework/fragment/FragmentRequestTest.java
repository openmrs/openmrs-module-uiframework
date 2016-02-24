package org.openmrs.ui.framework.fragment;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class FragmentRequestTest {

    FragmentRequest fragmentRequest;

    /**
     * @see FragmentRequest#getMappedFragmentId()
     * @verifies Whether the mapped fragmentId returns the override value when available
     */
    @Test
    public void getMappedFragmentId_shouldReturnOverrideIfAvailable() {
        fragmentRequest = new FragmentRequest("providerName", "fragmentId");
        // before override
        assertEquals("fragmentId", fragmentRequest.getMappedFragmentId());

        fragmentRequest.setFragmentIdOverride("fragmentIdOverride");

        assertEquals("fragmentIdOverride", fragmentRequest.getMappedFragmentId());
    }

    /**
     * @see FragmentRequest#getMappedProviderName()
     * @verifies Whether the mapped providername returns the override value when available
     */
    @Test
    public void getMappedProviderName_shouldReturnOverrideIfAvailable() {
        fragmentRequest = new FragmentRequest("providerName", "fragmentId");
        // before override
        assertEquals("providerName", fragmentRequest.getMappedProviderName());

        fragmentRequest.setProviderNameOverride("providerNameOverride");

        assertEquals("providerNameOverride", fragmentRequest.getMappedProviderName());
    }


}
