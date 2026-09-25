package com.kaisernova.web.converter;

import com.kaisernova.logica.usuarios.RolBean;
import com.kaisernova.modelo.usuarios.Rol;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

@FacesConverter("rolConverter")
public class RolConverter implements Converter<Rol>{
	@Inject
	private RolBean rolBean;
	
	@Override
	public Rol getAsObject(FacesContext context, UIComponent component, String value) {
		
		return rolBean.getRol(value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Rol value) {
		
		return value.getNombreRol().getName();
	}

}