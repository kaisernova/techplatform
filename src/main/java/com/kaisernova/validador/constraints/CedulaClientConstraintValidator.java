package com.kaisernova.validador.constraints;

import org.primefaces.validate.bean.AbstractClientValidationConstraint;

public class CedulaClientConstraintValidator extends AbstractClientValidationConstraint {
	public static final String MESSAGE_METADATA = "data-p-blank-msg";
	public CedulaClientConstraintValidator() {
		super(null, MESSAGE_METADATA);
	}

	public CedulaClientConstraintValidator(String messageId, String messageMetadata) {
		super(messageId, messageMetadata);
	}

	@Override
	public String getValidatorId() {
		return Cedula.class.getSimpleName();
	}

}
