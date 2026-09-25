package com.kaisernova.dao.ubicacion.geografica;

import java.util.List;

import jakarta.ejb.Stateless;
import jakarta.persistence.Query;

import com.kaisernova.dao.GenericDao;
import com.kaisernova.modelo.ubicacion.geografica.Ciudad;



@Stateless
public class CiudadDao extends GenericDao<Ciudad, Long>{

	public CiudadDao() {
		super(Ciudad.class);
	}
	public List<Ciudad> obtenerPorCodigo(String codigo) {
		Query query = getEntityManager().createQuery("Select c from Ciudad c where c.codigo=:codigo");
		query.setParameter("codigo", codigo);
		return query.getResultList();
	}
	
	public List<Ciudad> obtenerTodasOrdenadasPorProvincia() {
		Query query = getEntityManager().createQuery("Select c from Ciudad c order by c.provincia.nombre, c.nombre");		
		return query.getResultList();
	}
}