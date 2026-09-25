package com.kaisernova.validador.constraints;

import org.primefaces.validate.bean.AbstractClientValidationConstraint;

public class MayorEdadClientConstraintValidator extends AbstractClientValidationConstraint {
	public static final String MESSAGE_METADATA = "data-p-blank-msg";
	public MayorEdadClientConstraintValidator() {
		super(null, MESSAGE_METADATA);
	}

	public MayorEdadClientConstraintValidator(String messageId, String messageMetadata) {
		super(messageId, messageMetadata);
	}

	@Override
	public String getValidatorId() {
		return MayorEdad.class.getSimpleName();
	}

}
