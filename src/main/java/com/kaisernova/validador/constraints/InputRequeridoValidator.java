package com.kaisernova.validador.constraints;

import java.util.Map;
import java.util.logging.Logger;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;

@FacesValidator("custom.inputRequerido")
public class InputRequeridoValidator  extends ValidatorBase  {
	@Inject
	private Logger logger;
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		//se asume que la validacion se hizo con el componente en EL JSF y no la requiere aqui
		//este validator se usa para la validacion del lado del cliente
		logger.info("[InputRequeridoValidator.validate] component=" + component);
		logger.info("[InputRequeridoValidator.validate] value=" + value);
		return;
	}

	@Override
	public Map<String, Object> getMetadata() {
		return null;
	}

	@Override
	public String getValidatorId() {
		// TODO Auto-generated method stub
		return "custom.inputRequerido";
	}

	
}
