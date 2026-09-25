package com.kaisernova.modelo.enums;

public enum TipoArchivoAdjunto {
	ADJUNTO_TRANSFERENCIA("adjunto_pago");
	
	private String nombre;

	private TipoArchivoAdjunto(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}

	public static TipoArchivoAdjunto obtenerPorNombre(String nombre) {
		for (var tipoArchivoOrden : TipoArchivoAdjunto.values()) {
			if (tipoArchivoOrden.getNombre().equalsIgnoreCase(nombre)) {
				return tipoArchivoOrden;
			}
		}
		return null;
	}
}
