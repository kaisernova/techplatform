package com.kaisernova.validador.constraints;

import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.ValidatorException;

import com.kaisernova.constantes.ConstantesAplicacion;

@FacesValidator("custom.claveNoIgualUsuario")
public class ClaveUsuarioValidator extends ValoresIgualesBaseValidator {

	@Override
	public Map<String, Object> getMetadata() {
		return null;
	}

	@Override
	public String getValidatorId() {
		return "custom.claveNoIgualUsuario";
	}

	// el value es la clave, se trae el usuario desde el contexto
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		this.validate(context, component, value, ConstantesAplicacion.USUARIO,
				ConstantesAplicacion.ERROR_CLAVE_USUARIO_TITULO, ConstantesAplicacion.ERROR_CLAVE_USUARIO);
	}

}
