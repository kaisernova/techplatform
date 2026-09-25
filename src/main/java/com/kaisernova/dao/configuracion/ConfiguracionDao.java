package com.kaisernova.dao.configuracion;

import java.util.List;

import jakarta.ejb.Stateless;

import com.kaisernova.dao.GenericActivableDao;
import com.kaisernova.modelo.configuracion.Configuracion;
import com.kaisernova.modelo.enums.NombreParametroConfiguracion;
@Stateless
public class ConfiguracionDao extends GenericActivableDao<Configuracion, Long> {
	public ConfiguracionDao() {
		super(Configuracion.class);
	}
	
	public List<Configuracion> obtenerPorNombreParametro(NombreParametroConfiguracion nombreParametro) {
		var consulta = "select c from Configuracion c where c.nombreParametro=:nombreParametro";
		var query = getEntityManager().createQuery(consulta, Configuracion.class);
		query.setParameter("nombreParametro", nombreParametro);
		return query.getResultList();
	}
	
}
