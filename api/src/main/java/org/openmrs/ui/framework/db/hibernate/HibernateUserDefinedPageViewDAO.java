/**
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
package org.openmrs.ui.framework.db.hibernate;

import org.openmrs.ui.framework.UserDefinedPageView;
import org.openmrs.ui.framework.db.UserDefinedPageViewDAO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Allows pages to be mapped to internal user defined page views
 */
@Repository("userDefinedPageviewDAO")
public class HibernateUserDefinedPageViewDAO extends SingleClassHibernateDAO<UserDefinedPageView> implements UserDefinedPageViewDAO {
	
	public HibernateUserDefinedPageViewDAO() {
		super(UserDefinedPageView.class);
	}
	
	/**
	 * @see org.openmrs.ui2.reference.page.db.UserDefinedPageViewDAO#getPageViewByName(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public UserDefinedPageView getPageViewByName(String pageName) {
		return (UserDefinedPageView) sessionFactory.getCurrentSession()
		        .createQuery("from UserDefinedPageView pv where pv.name = :name").setString("name", pageName).uniqueResult();
	}
}
