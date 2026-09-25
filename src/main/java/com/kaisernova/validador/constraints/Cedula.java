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
@Constraint(validatedBy = CedulaConstraintValidator.class)
@ClientConstraint(resolvedBy = CedulaClientConstraintValidator.class)
@Documented
public @interface Cedula {
	String message() default "{org.primefaces.examples.primefaces}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
