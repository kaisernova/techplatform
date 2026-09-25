package com.kaisernova.logica.catalogo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;

import com.kaisernova.dao.catalogo.CuentaBancariaDao;
import com.kaisernova.modelo.catalogo.CuentaBancaria;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

@Singleton
@Startup
public class CuentaBancariaBean {
	private final static Map<Long, CuentaBancaria> CUENTAS_MAPA = new ConcurrentSkipListMap<>();
	private final static List<CuentaBancaria> CUENTAS = new ArrayList<>();

	@Inject
	private CuentaBancariaDao cuentaBancariaDao;

	@Lock(LockType.WRITE)
	public CuentaBancaria crear(CuentaBancaria cuentaBancaria) {
		CuentaBancaria cuentaBancariaRegistrada = cuentaBancariaDao.create(cuentaBancaria);

		reInicilizar();
		return cuentaBancariaRegistrada;
	}

	@Lock(LockType.WRITE)
	public CuentaBancaria actualizar(CuentaBancaria cuentaBancaria) {
		CuentaBancaria cuentaBancariaRegistrada = cuentaBancariaDao.update(cuentaBancaria);

		reInicilizar();
		return cuentaBancariaRegistrada;
	}

	@Lock(LockType.READ)
	public List<CuentaBancaria> obtenerTodasActivas() {
		return cuentaBancariaDao.findAllActivosOrderBy("numeroCuenta", false);
	}
	@Lock(LockType.READ)
	public List<CuentaBancaria> obtenerTodas() {
		return cuentaBancariaDao.findAllOrderBy("numeroCuenta", false);
	}

	@Lock(LockType.READ)
	public CuentaBancaria obtenerPorId(Long idCuentaBancaria) {
		return CUENTAS_MAPA.get(idCuentaBancaria);
	}

	@PostConstruct
	public void inicializar() {
		reInicilizar();
	}

	@Lock(LockType.WRITE)
	public void reInicilizar() {
		CUENTAS.clear();
		CUENTAS_MAPA.clear();
		CUENTAS.addAll(this.obtenerTodasActivas());
		for (CuentaBancaria cuenta : CUENTAS) {
			if (cuenta.getIdCuentaBancaria() != null) {
				CUENTAS_MAPA.put(cuenta.getIdCuentaBancaria(), cuenta);
			}
		}
	}

	@Lock(LockType.READ)
	public List<CuentaBancaria> obtenerListaActivas() {
		return CUENTAS;
	}
	 
}