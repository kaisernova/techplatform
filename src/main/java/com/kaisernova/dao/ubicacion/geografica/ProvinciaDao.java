package com.kaisernova.dao.ubicacion.geografica;

import com.kaisernova.dao.GenericDao;
import com.kaisernova.modelo.ubicacion.geografica.Provincia;

import jakarta.ejb.Stateless;

@Stateless
public class ProvinciaDao extends GenericDao<Provincia, Long> {

	public ProvinciaDao() {
		super(Provincia.class);
	}
	
}
