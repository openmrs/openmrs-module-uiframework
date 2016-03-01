package org.openmrs.ui.framework.fragment;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;

public class FragmentRequestTest {

	FragmentRequest fragmentRequest;

	/**
	 * @verifies Whether the mapped fragmentId returns the override value when available
	 * @see FragmentRequest#getMappedFragmentId()
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
	 * @verifies Whether the mapped providername returns the override value when available
	 * @see FragmentRequest#getMappedProviderName()
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