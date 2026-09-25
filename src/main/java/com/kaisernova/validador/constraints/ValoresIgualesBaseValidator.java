package com.kaisernova.validador.constraints;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;

public abstract class ValoresIgualesBaseValidator extends ValidatorBase  {
	public void validate(FacesContext context, UIComponent component, Object value, String nombreVariableBase, String msgKeyTitulo, String msgKeyDetalle) throws ValidatorException {
		String nombreVariable= nombreVariableBase;
		for (String key : getParameterMap(context).keySet()) {
			
			String [] keySplit = key.split("\\:");
			
			if(keySplit.length>1) {
				if(nombreVariableBase.equalsIgnoreCase(keySplit[keySplit.length-1])) {
					nombreVariable = key;
					break;
				}
			}
		}
		String valorComparar = obtenerParametroPeticion(nombreVariable, context);
		if(value.equals(valorComparar)) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, msg(msgKeyTitulo, context),msg(msgKeyDetalle, context)));
		}
	}
}
