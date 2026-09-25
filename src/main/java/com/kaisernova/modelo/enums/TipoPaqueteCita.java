package com.kaisernova.modelo.enums;

public enum TipoPaqueteCita {
	PROMOCIONAL_PRIMERA_CITA("Promocional Primera Cita"), NORMAL("Normal");
	private String descripcion;
	
	private TipoPaqueteCita(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return descripcion;
	}
	
	public String getName() {
		return this.name();
	}
	
	public static TipoPaqueteCita fromString(String text) {
	    for (TipoPaqueteCita b : TipoPaqueteCita.values()) {
	        if (b.descripcion.equalsIgnoreCase(text)) {
	            return b;
	        }
	    }
	    return NORMAL;
	}
	
}
