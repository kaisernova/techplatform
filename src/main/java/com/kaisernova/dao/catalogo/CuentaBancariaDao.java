package com.kaisernova.dao.catalogo;

import com.kaisernova.dao.GenericActivableDao;
import com.kaisernova.modelo.catalogo.CuentaBancaria;

public class CuentaBancariaDao extends GenericActivableDao<CuentaBancaria, Long>{

	public CuentaBancariaDao() {
		super(CuentaBancaria.class);
	}

}
