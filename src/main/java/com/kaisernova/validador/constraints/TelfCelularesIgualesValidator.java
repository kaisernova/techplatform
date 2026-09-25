package com.kaisernova.validador.constraints;

import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.ValidatorException;

import com.kaisernova.constantes.ConstantesAplicacion;
@FacesValidator("custom.telfCelularesIgualesValidator")
public class TelfCelularesIgualesValidator extends ValoresIgualesBaseValidator {

	public TelfCelularesIgualesValidator() {
		super();
	}

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		validate(context, component, value, ConstantesAplicacion.TELF_CELULAR, ConstantesAplicacion.ERROR_CELULARES_IGUALES_TITULO, ConstantesAplicacion.ERROR_CELULARES_IGUALES);
	}

	@Override
	public Map<String, Object> getMetadata() {
		return null;
	}

	@Override
	public String getValidatorId() {
		return "custom.telfCelularesIgualesValidator";
	}

}
