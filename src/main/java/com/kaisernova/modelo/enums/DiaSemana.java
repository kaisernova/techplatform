package com.kaisernova.modelo.enums;

import java.time.DayOfWeek;
import java.util.Calendar;

public enum DiaSemana {
	LUN(DayOfWeek.MONDAY),
	MAR(DayOfWeek.TUESDAY),
	MIE(DayOfWeek.WEDNESDAY),
	JUE(DayOfWeek.THURSDAY),
	VIE(DayOfWeek.FRIDAY),
	SAB(DayOfWeek.SATURDAY),
	DOM(DayOfWeek.SUNDAY);
	private DayOfWeek dayOfWeek;

	private DiaSemana(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}
	
	public static DiaSemana obtenerPorNombre(String nombre) {
		for (DiaSemana diaSemana : DiaSemana.values()) {
			if(nombre.toUpperCase().startsWith(diaSemana.name())) {
				return diaSemana;
			}
		}
		//por defecto
		return DOM;
	}

	public static DiaSemana obtenerPorDayOfWeek(DayOfWeek dayOfWeek) {
		for (DiaSemana diaSemana : DiaSemana.values()) {
			if (diaSemana.getDayOfWeek().equals(dayOfWeek)) {
				return diaSemana;
			}
		}
		// por defecto
		return LUN;
	}
}
