package org.openmrs.ui2.core.db;

import java.util.List;

public interface SingleClassDAO<T> {
	
	T getById(Integer id);
	
	List<T> getAll();
	
	T saveOrUpdate(T object);
	
	T create(T object);
	
	T update(T object);
	
	void delete(T object);
	
}
