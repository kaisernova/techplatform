package com.kaisernova.web.controlador;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class ApplicationControlador extends BaseControlador {
	public String getUrlCaptchaCliente() {
		return super.getUrlCaptchaCliente();
	}
	public String getCaptchaSiteKey() {
		return super.getCaptchaSiteKey();
	}
	public boolean getActivarCaptcha() {
		return super.getActivarCaptcha();
	
	}
	
	public String getActivarCaptchaString() {
		return String.valueOf(getActivarCaptcha());
	
	}
}
