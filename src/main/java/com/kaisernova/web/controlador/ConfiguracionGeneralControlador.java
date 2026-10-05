package com.kaisernova.web.controlador;

import java.util.Objects;

import com.kaisernova.logica.configuracion.ConfiguracionEnMemoriaBean;
import com.kaisernova.modelo.enums.NombreParametroConfiguracion;
import com.kaisernova.util.aplicacion.UtilAplicacion;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.Transient;

@Named
@ApplicationScoped
public class ConfiguracionGeneralControlador extends BaseControlador {
	
	@Inject
	private ConfiguracionEnMemoriaBean configuracionEnMemoriaBean;
	
	public String getTitulo() {
		return configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.TITULO);
	}

	public String getTerminosCondiciones() {
		return configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.TERMINOS_Y_CONDICIONES);
	}
}
