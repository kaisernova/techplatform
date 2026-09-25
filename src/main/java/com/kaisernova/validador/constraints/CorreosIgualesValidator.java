package com.kaisernova.validador.constraints;

import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.ValidatorException;

import com.kaisernova.constantes.ConstantesAplicacion;

@FacesValidator("custom.correosIgualesValidator")
public class CorreosIgualesValidator  extends ValoresIgualesBaseValidator {

	public CorreosIgualesValidator() {
		super();
	}

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		validate(context, component, value, ConstantesAplicacion.EMAIL, ConstantesAplicacion.ERROR_EMAILS_IGUALES_TITULO,ConstantesAplicacion.ERROR_EMAILS_IGUALES);
		
	}

	@Override
	public Map<String, Object> getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getValidatorId() {
		// TODO Auto-generated method stub
		return "custom.correosIgualesValidator";
	}

}
