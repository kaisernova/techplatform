package com.kaisernova.logica.configuracion;

import java.util.List;

import com.kaisernova.modelo.enums.MetodoPago;
import com.kaisernova.modelo.enums.NombreParametroConfiguracion;

import jakarta.ejb.Singleton;
import jakarta.inject.Inject;

@Singleton
public class MetodoPagoBean {
	@Inject
	private ConfiguracionEnMemoriaBean configuracionEnMemoriaBean;
	
	public List<MetodoPago> obtenerMetodosPagoDisponibles() {
		String metodosPagoStr = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.METODOS_PAGO_HABILITADOS);
		if (metodosPagoStr != null && !metodosPagoStr.isEmpty()) {
			String[] metodosPagoArray = metodosPagoStr.split(",");
			
			return MetodoPago.fromStringArray(metodosPagoArray);
		}
		return List.of(); // Retorna una lista vacía si no hay métodos de pago configurados
	}
}
