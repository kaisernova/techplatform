package com.kaisernova.dao.catalogo;

import java.util.List;

import com.kaisernova.dao.GenericActivableDao;
import com.kaisernova.modelo.catalogo.InstitucionFinanciera;



public class InstitucionFinancieraDao extends GenericActivableDao<InstitucionFinanciera, String>{

	public InstitucionFinancieraDao() {
		super(InstitucionFinanciera.class);
	}

	public List<InstitucionFinanciera> obtenerPorNombre(String nombre) {
		return getEntityManager().createQuery(" from InstitucionFinanciera i  where i.nombre = :nombre").setParameter("nombre", nombre).getResultList();
	}
	public List<InstitucionFinanciera> obtenerPorCodigo(String codigo) {
		return getEntityManager().createQuery(" from InstitucionFinanciera i  where i.codigo = :codigo").setParameter("codigo", codigo).getResultList();
	}
}
