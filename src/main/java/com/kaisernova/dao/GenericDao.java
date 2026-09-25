package com.kaisernova.dao;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

public abstract class GenericDao<T extends Serializable, I extends Serializable> {
    protected final Class<T> clazz;

    @PersistenceContext(unitName = "kaiserPU")
    private EntityManager entityManager;
    public EntityManager getEntityManager() {
    	return this.entityManager;
    }
    public GenericDao(Class<T> clazz) {
    	this.clazz = clazz;
    }

    public T findOne(final I id) {
        return getEntityManager().find(clazz, id);
    }
    
    public T findOneForUpdate(final I id) {
        return getEntityManager().find(clazz, id, LockModeType.PESSIMISTIC_WRITE);
    }

    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        return getEntityManager().createQuery("select e from " + clazz.getName() + " e").getResultList();
    }
    
    public List<T> findAll(Integer maxResults) {
        return getEntityManager().createQuery("select e from  " + clazz.getName() + " e").setMaxResults(maxResults).getResultList();
    }

    public List<T> findAllOrderBy(String nombreCampoOrdenar, boolean descendiente) {
    	String cadena = "select e from  " + clazz.getName() + " e  order by e." + nombreCampoOrdenar;
		if(descendiente) {
			cadena = cadena + " desc";
		}
        return getEntityManager().createQuery(cadena).getResultList();
    }
    
    public List<T> findAllOrderBy(String nombreCampoOrdenar, boolean descendiente, Integer maxResults) {
    	String cadena = "select e from  " + clazz.getName() + " e  order by e." + nombreCampoOrdenar;
		if(descendiente) {
			cadena = cadena + " desc";
		}
        return getEntityManager().createQuery(cadena).setMaxResults(maxResults).getResultList();
    }

    public T create(final T entity) {
    	getEntityManager().persist(entity);
        return entity;
    }

    public T update(final T entity) {
        return getEntityManager().merge(entity);
    }

    public void delete(final T entity) {
    	T entityToDelete = entity;
    	if (!getEntityManager().contains(entity)) {
    		entityToDelete = getEntityManager().merge(entity);
    	}
    	getEntityManager().remove(entityToDelete);
    }

    public void deleteById(final I entityId) {
        final T entity = findOne(entityId);
        delete(entity);
    }
	public Class<T> getClazz() {
		return clazz;
	}
    public int deleteAll() {
        return getEntityManager().createQuery("delete from " + clazz.getName() + " e").executeUpdate();
    }
    public void flush() {
    	getEntityManager().flush();
    }
    
    public void refresh(T entity) {
		getEntityManager().refresh(entity);
	}
    
}