package com.kaisernova.validador.constraints;

import java.util.Map;
import java.util.ResourceBundle;

import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.Validator;
import jakarta.servlet.http.HttpServletRequest;

import org.primefaces.validate.ClientValidator;



public abstract class ValidatorBase implements Validator, ClientValidator {

	public Map<String, String[]> getParameterMap(FacesContext context) {
		return getHttpServletRequest(context).getParameterMap();
	}

	public HttpServletRequest getHttpServletRequest(FacesContext context) {
		return ((HttpServletRequest) context.getExternalContext().getRequest());
	}

	public String obtenerParametroPeticion(String nombreParametro, FacesContext context) {
		return getHttpServletRequest(context).getParameter(nombreParametro);
	}
	public String msg(String msgKey, FacesContext context) {
		ResourceBundle bundle = ResourceBundle.getBundle("messages");
		return bundle.getString(msgKey);
	}
}
