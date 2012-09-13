package org.openmrs.ui.framework.db.hibernate;

import java.util.List;

import org.hibernate.SessionFactory;
import org.openmrs.ui.framework.db.SingleClassDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class SingleClassHibernateDAO<T> implements SingleClassDAO<T> {
	
	@Autowired
	protected SessionFactory sessionFactory;
	
	protected Class<T> mappedClass;
	
	/**
	 * Marked private because you *must* provide the class at runtime when instantiating one of
	 * these, using the next constructor
	 */
	@SuppressWarnings("unused")
	private SingleClassHibernateDAO() {
	}
	
	/**
	 * You must call this before using any of the data access methods, since it's not actually
	 * possible to write them all with compile-time class information.
	 * 
	 * @param mappedClass
	 */
	protected SingleClassHibernateDAO(Class<T> mappedClass) {
		this.mappedClass = mappedClass;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@SuppressWarnings("unchecked")
    @Override
	@Transactional(readOnly = true)
	public T getById(Integer id) {
		return (T) sessionFactory.getCurrentSession().get(mappedClass, id);
	}
	
	@SuppressWarnings("unchecked")
    @Override
	@Transactional(readOnly = true)
	public List<T> getAll() {
		return (List<T>) sessionFactory.getCurrentSession().createCriteria(mappedClass).list();
	}
	
	@Override
	@Transactional
	public T saveOrUpdate(T object) {
		sessionFactory.getCurrentSession().saveOrUpdate(object);
		return object;
	}
	
	@Override
	@Transactional
	public T update(T object) {
		sessionFactory.getCurrentSession().update(object);
		return object;
	}
	
	@Override
	@Transactional
	public void delete(T object) {
		sessionFactory.getCurrentSession().delete(object);
	}
	
}
