package com.kaisernova.web.converter;

import com.kaisernova.logica.catalogo.EspecialidadBean;
import com.kaisernova.modelo.catalogo.Especialidad;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

@FacesConverter("especialidadConverter")
public class EspecialidadConverter implements Converter<Especialidad>{
	@Inject
	private EspecialidadBean especialidadEnMemoriaBean;
	
	@Override
	public Especialidad getAsObject(FacesContext context, UIComponent component, String value) {
		System.out.println("[getAsObject]Convertir Especialidad : " + value);
		return especialidadEnMemoriaBean.getEspecialidad(value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Especialidad value) {
		System.out.println("[getAsObject]getAsString : " + value);
		return value.getCodigo();
	}

}