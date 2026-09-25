package com.kaisernova.validador.constraints;

import java.util.Calendar;
import java.util.Date;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MayorEdadConstraintValidator implements ConstraintValidator<MayorEdad, Date> {

	@Override
	public boolean isValid(Date value, ConstraintValidatorContext context) {
		return esMayorEdad18(value);
	}

	private boolean esMayorEdad18(Date fecha) {
		System.out.println("fecha=" + fecha);
		boolean esMayor = false;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -18);
		if (fecha.compareTo(calendar.getTime()) <= 0) {
			esMayor = true;
		}
		return esMayor;
	}
}
