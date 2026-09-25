package com.kaisernova.web.converter;


import com.kaisernova.logica.catalogo.CuentaBancariaBean;
import com.kaisernova.modelo.catalogo.CuentaBancaria;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

@FacesConverter(value="cuentaBancariaConverter",managed=true)
public class CuentaBancariaConverter implements Converter<CuentaBancaria> {
	@Inject
	private CuentaBancariaBean cuentaBancariaBean;
	
	@Override
	public CuentaBancaria getAsObject(FacesContext context, UIComponent component, String value) {
		System.out.println("[CuentaBancariaConverter.getAsObject]value="+value);
		try {
			Long id=Long.parseLong(value);
			System.out.println("[getAsObject]id="+id);
			CuentaBancaria cuentaBancaria = cuentaBancariaBean.obtenerPorId(id);
			System.out.println("[getAsObject]cuentaBancaria="+cuentaBancaria);
			return cuentaBancaria;
		} catch (NumberFormatException e) {
			return null;
		}
		
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, CuentaBancaria value) {
		System.out.println("[CuentaBancariaConverter.getAsString]value="+value);
		return value.getIdCuentaBancaria().toString();
	}


}
