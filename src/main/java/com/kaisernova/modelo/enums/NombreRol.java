package com.kaisernova.modelo.enums;

public enum NombreRol {
	ADMINISTRADOR, ESPECIALISTA, PACIENTE;
	public String getName() {
		return this.name();
	}
	public static NombreRol obtenerPorNombreRolString(String nombreRolString) {
		for (var nombreRol : NombreRol.values()) {
			if (nombreRol.name().equalsIgnoreCase(nombreRolString)) {
				return nombreRol;
			}
		}
		return null;
	}
}
