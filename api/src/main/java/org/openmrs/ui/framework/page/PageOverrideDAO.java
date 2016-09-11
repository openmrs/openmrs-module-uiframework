package org.openmrs.ui.framework.page;

import org.openmrs.ui.framework.db.SingleClassDAO;

public interface PageOverrideDAO extends SingleClassDAO<PageOverride>{
    PageOverride getByUuid(String uuid);
}
