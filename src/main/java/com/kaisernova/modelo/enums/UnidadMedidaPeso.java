package com.kaisernova.modelo.enums;

public enum UnidadMedidaPeso {
	GR("Gramos"), KG("Kilogramos");
	private String nombre;
	private UnidadMedidaPeso(String nombre) {
		this.nombre=nombre;
	}
	public String getNombre() {
		return nombre;
	}
	
}
