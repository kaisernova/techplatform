package com.kaisernova.web.converter;


import com.kaisernova.modelo.catalogo.InstitucionFinanciera;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

@FacesConverter(value="institucionFinancieraConverter",managed=true)
public class InstitucionFinancieraConverter implements Converter<InstitucionFinanciera> {
	@Inject
	private com.kaisernova.logica.catalogo.InstitucionFinancieraBean InstitucionFinancieraBean;
	
	@Override
	public InstitucionFinanciera getAsObject(FacesContext context, UIComponent component, String value) {
		System.out.println("[getAsObject] value: " + value);
		return InstitucionFinancieraBean.obtenerPorCodigo(value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, InstitucionFinanciera value) {
		System.out.println("[getAsString] value: " + value);
		return value.getCodigo();
	}


}
