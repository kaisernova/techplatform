package com.kaisernova.modelo.enums;

public enum UnidadMedidaLongitud {
	CM("Centimetros"), MT("Metros");
	private String nombre;
	private UnidadMedidaLongitud(String nombre) {
		this.nombre=nombre;
	}
	public String getNombre() {
		return nombre;
	}
}
