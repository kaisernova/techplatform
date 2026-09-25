package com.kaisernova.logica.ubicacion.geografica;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import com.kaisernova.dao.ubicacion.geografica.CiudadDao;
import com.kaisernova.modelo.ubicacion.geografica.Ciudad;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

@Singleton
@Startup
public class CiudadBean {
	private final static Map<String, Ciudad> CIUDADES_MAPA = new ConcurrentSkipListMap<>();
	private final static List<Ciudad> CIUDADES = new ArrayList<>();
	@Inject
	private CiudadDao ciudadDao;
	@PostConstruct
	public void inicializar() {
		reInicilizar();
	}
	@Lock(LockType.WRITE)
	public void reInicilizar() {
		CIUDADES.clear();
		CIUDADES_MAPA.clear();
		CIUDADES.addAll(ciudadDao.obtenerTodasOrdenadasPorProvincia());
		for (Ciudad ciudad : CIUDADES) {
			CIUDADES_MAPA.put(ciudad.getCodigo(), ciudad);
		}
	}
	@Lock(LockType.READ)
	public Ciudad obtenerCiudad(String codigo) {
		return CIUDADES_MAPA.get(codigo);
	}
	@Lock(LockType.READ)
	public List<Ciudad> obtenerCiudadesOrdenadasProvincia() {
		return CIUDADES;
	}
	
}
