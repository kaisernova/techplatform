package com.kaisernova.dao;

import java.io.Serializable;
import java.util.List;

import com.kaisernova.modelo.base.MappedSuperActivableClass;

import jakarta.persistence.Query;

public abstract class GenericActivableDao<T extends MappedSuperActivableClass, I extends Serializable>
		extends GenericDao<T, I> {
	public GenericActivableDao(Class<T> clazz) {
		super(clazz);
	}

	@SuppressWarnings("unchecked")
	public List<T> findAllActivos() {
		Query query = getEntityManager().createQuery("select e from " + clazz.getName() + " e where e.activo=:activo");
		query.setParameter("activo", true);
		return query.getResultList();
	}

	public List<T> findAllActivosOrderBy(String nombreCampoOrdenar, boolean descendiente) {
		String cadena = "select e from " + clazz.getName() + " e where e.activo=:activo order by e." + nombreCampoOrdenar;
		if (descendiente) {
			cadena = cadena + " desc";
		}
		return getEntityManager().createQuery(cadena).setParameter("activo", true).getResultList();
	}

	public List<T> findAllActivosOrderBy(String nombreCampoOrdenar, boolean descendiente, Integer maxResults) {
		String cadena = "select e from " + clazz.getName() + " e where e.activo=:activo order by e." + nombreCampoOrdenar;
		if (descendiente) {
			cadena = cadena + " desc";
		}
		return getEntityManager().createQuery(cadena).setParameter("activo", true).setMaxResults(maxResults)
				.getResultList();
	}

}