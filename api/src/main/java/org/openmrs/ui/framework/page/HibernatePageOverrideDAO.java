package org.openmrs.ui.framework.page;

import org.hibernate.criterion.Restrictions;
import org.openmrs.ui.framework.db.hibernate.SingleClassHibernateDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("pageOverrideDAO")
public class HibernatePageOverrideDAO extends SingleClassHibernateDAO<PageOverride> implements PageOverrideDAO {

    protected HibernatePageOverrideDAO() {
        super(PageOverride.class);
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public PageOverride getByUuid(String uuid) {
        return (PageOverride) sessionFactory.getCurrentSession().createCriteria(PageOverride.class)
                .add(Restrictions.eq("uuid", uuid)).uniqueResult();
    }
}
