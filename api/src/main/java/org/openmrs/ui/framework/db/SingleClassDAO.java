package org.openmrs.ui.framework.db;

import java.util.List;

public interface SingleClassDAO<T> {
	
	T getById(Integer id);
	
	List<T> getAll();
	
	T saveOrUpdate(T object);
	
	T update(T object);
	
	void delete(T object);
	
}
