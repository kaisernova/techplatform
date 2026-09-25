package com.kaisernova.logica.ubicacion.geografica;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;


import com.kaisernova.dao.ubicacion.geografica.PaisNacionalidadDao;
import com.kaisernova.modelo.ubicacion.geografica.PaisNacionalidad;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

@Singleton
@Startup
public class PaisNacionalidadBean {
	private final static Map<String, PaisNacionalidad> PAISES_NACIONALIDAD_MAPA = new ConcurrentSkipListMap<>();
	private final static Map<String, PaisNacionalidad> NOMBRE_NACIONALIDAD_MAPA = new ConcurrentSkipListMap<>();
	private final static List<PaisNacionalidad> PAISES_NACIONALIDAD = new ArrayList<>();
	@Inject
	private PaisNacionalidadDao paisNacionalidadDao;
	
	@PostConstruct
	public void inicializar() {
		reInicializar();
	}
	@Lock(LockType.WRITE)
	public void reInicializar() {
		PAISES_NACIONALIDAD.clear();
		PAISES_NACIONALIDAD_MAPA.clear();
		NOMBRE_NACIONALIDAD_MAPA.clear();
		PAISES_NACIONALIDAD.addAll(paisNacionalidadDao.findAllActivosOrderBy("pais", false));
		for (PaisNacionalidad paisNacionalidad : PAISES_NACIONALIDAD) {
			PAISES_NACIONALIDAD_MAPA.put(paisNacionalidad.getCodigo(), paisNacionalidad);
			NOMBRE_NACIONALIDAD_MAPA.put(paisNacionalidad.getNacionalidad(), paisNacionalidad);
		}
	}
	@Lock(LockType.READ)
	public PaisNacionalidad obtenerPaisNacionalidadPorCodigoPais(String codigo) {
		return PAISES_NACIONALIDAD_MAPA.get(codigo);
	}
	
	@Lock(LockType.READ)
	public PaisNacionalidad obtenerPaisNacionalidadPorNombreNacionalidad(String nacionalidad) {
		return NOMBRE_NACIONALIDAD_MAPA.get(nacionalidad);
	}
	
	@Lock(LockType.READ)
	public List<PaisNacionalidad> obtenerPaisesNacionalidadActivos() {
		return PAISES_NACIONALIDAD;
	}
}
