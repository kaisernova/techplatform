package com.kaisernova.modelo.enums;

public enum AmbienteProductivo {
	PRUEBAS("1"), PRODUCCION("2");
	private String codigo;

	private AmbienteProductivo(String codigo) {
		this.codigo = codigo;
	}

	public String getCodigo() {
		return codigo;
	}
	public static AmbienteProductivo obtenerPorCodigo(String codigo) {
		
		for (AmbienteProductivo ambienteProductivo : AmbienteProductivo.values()) {
			if(ambienteProductivo.getCodigo().equals(codigo)) {
				return ambienteProductivo;
			}
		}
		return null;
		
	}
	public String getName() {
		return this.name();
	}
}
