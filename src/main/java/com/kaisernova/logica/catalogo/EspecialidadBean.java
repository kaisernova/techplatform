package com.kaisernova.logica.catalogo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import com.kaisernova.dao.catalogo.EspecialidadDao;
import com.kaisernova.modelo.catalogo.Especialidad;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

@Singleton
@Startup
public class EspecialidadBean {
	private final static Map<String, Especialidad> ESPECIALIDADES_MAP = new ConcurrentSkipListMap<>();
	private final static List<Especialidad> ESPECIALIDADES = new ArrayList<>();
	
	@Inject
	private EspecialidadDao especialidadDao;
	

	@Lock(LockType.WRITE)
	public Especialidad crear(Especialidad especialidad) {
		Especialidad especialidadRegistrada = especialidadDao.create(especialidad);

		reInicilizar();
		return especialidadRegistrada;
	}
	
	@Lock(LockType.WRITE)
	public Especialidad actualizar(Especialidad especialidad) {
		Especialidad especialidadRegistrada = especialidadDao.update(especialidad);

		reInicilizar();
		return especialidadRegistrada;
	}

	@Lock(LockType.READ)
	public List<Especialidad> obtenerEspecialidadesActivasOrdenadasNombre() {
		return especialidadDao.findAllActivosOrderBy("nombre", false);
	}
	
	@Lock(LockType.READ)
	public List<Especialidad> obtenerTodas() {
		return especialidadDao.findAllOrderBy("nombre", false);
	}

	@PostConstruct
	public void inicializar() {
		reInicilizar();
	}

	@Lock(LockType.WRITE)
	public void reInicilizar() {
		ESPECIALIDADES.clear();
		ESPECIALIDADES_MAP.clear();
		ESPECIALIDADES.addAll(this.obtenerEspecialidadesActivasOrdenadasNombre());
		for (Especialidad especialidad : ESPECIALIDADES) {
			ESPECIALIDADES_MAP.put(especialidad.getCodigo(), especialidad);
		}
	}

	@Lock(LockType.READ)
	public Especialidad getEspecialidad(String codigo) {
		return ESPECIALIDADES_MAP.get(codigo);
	}

	@Lock(LockType.READ)
	public List<Especialidad> getEspecialidades() {
		return ESPECIALIDADES;
	}
}