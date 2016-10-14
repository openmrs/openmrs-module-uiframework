package org.openmrs.ui.framework.page;

import java.util.List;

public interface PageOverrideHandler {

    /**
     * request page override, won't be activated until {@link PageOverrideHandlerImpl#initialize()} is invoked and requests are processed
     */
    void requestPageOverride(PageOverrideRequest pageOverrideRequest);

    /**
     * request multiple page overrides, won't be active until {@link PageOverrideHandlerImpl#initialize()} is invoked and requests are processed
     */
    void requestPageOverrides(List<PageOverrideRequest> pageOverrideRequests);

    /**
     * Does the actual PageRequest overriding
     */
    boolean overrideRequest(PageRequest pageRequest);

    /**
     * Activate selected
     */
    void activateOverride(PageOverride pageOverride);

    void deactivateOverride(PageOverride pageOverride);

    void initialize();
}
