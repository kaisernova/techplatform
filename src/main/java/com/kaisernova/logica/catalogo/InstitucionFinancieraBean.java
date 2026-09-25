package com.kaisernova.logica.catalogo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Logger;

import com.kaisernova.dao.catalogo.InstitucionFinancieraDao;
import com.kaisernova.modelo.catalogo.InstitucionFinanciera;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Singleton
@Startup
public class InstitucionFinancieraBean {
	private final static Map<String, InstitucionFinanciera> IFIS_MAPA = new ConcurrentSkipListMap<>();
	private final static List<InstitucionFinanciera> IFIS = new ArrayList<>();

	@Inject
	private InstitucionFinancieraDao institucionFinancieraDao;

	@Lock(LockType.WRITE)
	public InstitucionFinanciera crear(InstitucionFinanciera institucionFinanciera) {
		InstitucionFinanciera institucionFinancieraRegistrada = institucionFinanciera;

		institucionFinancieraRegistrada = institucionFinancieraDao.create(institucionFinanciera);

		reInicilizar();
		return institucionFinancieraRegistrada;
	}

	@Lock(LockType.WRITE)
	public InstitucionFinanciera actualizar(InstitucionFinanciera institucionFinanciera) {
		InstitucionFinanciera institucionFinancieraRegistrada = institucionFinanciera;

		institucionFinancieraRegistrada = institucionFinancieraDao.update(institucionFinanciera);

		reInicilizar();
		return institucionFinancieraRegistrada;
	}

	@Lock(LockType.READ)
	public List<InstitucionFinanciera> obtenerTodas() {
		return institucionFinancieraDao.findAll();
	}

	@Lock(LockType.READ)
	public List<InstitucionFinanciera> obtenerActivas() {
		return institucionFinancieraDao.findAllActivosOrderBy("nombre", false);
	}

	@Lock(LockType.READ)
	public InstitucionFinanciera obtenerPorNombre(String nombre) {
		List<InstitucionFinanciera> institucionesFinancieras = institucionFinancieraDao.obtenerPorNombre(nombre);
		if (!institucionesFinancieras.isEmpty()) {
			return institucionesFinancieras.get(0);
		}
		return null;
	}

	@Lock(LockType.READ)
	public InstitucionFinanciera obtenerPorCodigo(String codigo) {
		List<InstitucionFinanciera> institucionesFinancieras = institucionFinancieraDao.obtenerPorCodigo(codigo);
		if (!institucionesFinancieras.isEmpty()) {
			return institucionesFinancieras.get(0);
		}
		return null;
	}

	@Lock(LockType.READ)
	public boolean existePorNombre(String nombre) {
		return Objects.nonNull(this.obtenerPorNombre(nombre));
	}

	@Lock(LockType.READ)
	public boolean existePorCodigo(String codigo) {
		return Objects.nonNull(this.obtenerPorCodigo(codigo));
	}

	@Lock(LockType.READ)
	public boolean existeOtroPorNombre(String nombre, String idInstitucionFinanciera) {
		InstitucionFinanciera institucionFinanciera = this.obtenerPorNombre(nombre);
		return (Objects.nonNull(institucionFinanciera)
				&& !institucionFinanciera.getCodigo().equals(idInstitucionFinanciera));
	}

	@Lock(LockType.READ)
	public boolean existeOtroPorCodigo(String codigo, String idInstitucionFinanciera) {
		InstitucionFinanciera institucionFinanciera = this.obtenerPorCodigo(codigo);
		return (Objects.nonNull(institucionFinanciera)
				&& !institucionFinanciera.getCodigo().equals(idInstitucionFinanciera));
	}

	@PostConstruct
	public void inicializar() {
		reInicilizar();
	}

	@Lock(LockType.WRITE)
	public void reInicilizar() {
		IFIS.clear();
		IFIS_MAPA.clear();
		IFIS.addAll(this.obtenerActivas());
		for (InstitucionFinanciera institucionFinanciera : IFIS) {
			IFIS_MAPA.put(institucionFinanciera.getCodigo(), institucionFinanciera);
		}
	}

	@Lock(LockType.READ)
	public InstitucionFinanciera obtenerInstitucionFinanciera(String codigo) {
		return IFIS_MAPA.get(codigo);
	}

	@Lock(LockType.READ)
	public List<InstitucionFinanciera> obtenerListaActivas() {
		return IFIS;
	}
}
