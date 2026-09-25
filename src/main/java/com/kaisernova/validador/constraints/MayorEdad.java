package com.kaisernova.validador.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import org.primefaces.validate.bean.ClientConstraint;

@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MayorEdadConstraintValidator.class)
@ClientConstraint(resolvedBy = MayorEdadClientConstraintValidator.class)
@Documented
public @interface MayorEdad {
	String message() default "Debe ser mayor de edad";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
