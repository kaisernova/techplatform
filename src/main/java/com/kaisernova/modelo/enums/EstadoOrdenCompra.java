package com.kaisernova.modelo.enums;

public enum EstadoOrdenCompra {
	PENDIENTE,
	PAGO_CONFIRMADO,
	ORDEN_RECHAZADA;
	public String getName() {
		return this.name();
	}
}
