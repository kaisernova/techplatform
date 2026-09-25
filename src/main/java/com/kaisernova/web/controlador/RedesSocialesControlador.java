package com.kaisernova.web.controlador;

import com.kaisernova.logica.configuracion.ConfiguracionEnMemoriaBean;
import com.kaisernova.modelo.enums.NombreParametroConfiguracion;
import com.kaisernova.modelo.enums.NombreRol;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class RedesSocialesControlador extends BaseControlador {
	@Inject
	private ConfiguracionEnMemoriaBean configuracionEnMemoriaBean;

	public String getUrlWhatsappContacto() {
		if (!isWhatsappActivado()) {
			return "";
		}
		String urlBase = getWhatsappUrlBase();
		String numeroContacto = getWhatsappNumeroContacto();
		String mensajeDefecto = getWhatsappMensajeDefecto();

		StringBuilder urlContacto = new StringBuilder();
		urlContacto.append(urlBase);
		//urlContacto.append("?phone=");
		urlContacto.append(numeroContacto);
		if (mensajeDefecto != null && !mensajeDefecto.isEmpty()) {
			urlContacto.append("?text=");
			urlContacto.append(mensajeDefecto.replaceAll("\\s+", "%20").trim());
		}
		return urlContacto.toString();
	}
	/**
	 * Indica si la integración con WhatsApp está activada en la configuración.
	 */
	public boolean isWhatsappActivado() {
		return configuracionEnMemoriaBean.getValueAsBoolean(NombreParametroConfiguracion.WHATSAPP_ACTIVAR);
	}
	public boolean isMostrarWhatsapp() {
		return isWhatsappActivado() && !(verificarUsuarioTieneRol(NombreRol.ADMINISTRADOR) || verificarUsuarioTieneRol(NombreRol.ESPECIALISTA));
	}

	/**
	 * Retorna la URL base configurada para WhatsApp (por ejemplo https://api.whatsapp.com/send o https://wa.me/).
	 */
	public String getWhatsappUrlBase() {
		return configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.WHATSAPP_URL_BASE);
	}

	/**
	 * Retorna el número de contacto configurado para WhatsApp.
	 */
	public String getWhatsappNumeroContacto() {
		return configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.WHATSAPP_NUMERO_CONTACTO);
	}

	/**
	 * Retorna el mensaje por defecto configurado para WhatsApp.
	 */
	public String getWhatsappMensajeDefecto() {
		return configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.WHATSAPP_MENSAJE_DEFECTO);
	}



}