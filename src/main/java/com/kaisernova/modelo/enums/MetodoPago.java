package com.kaisernova.modelo.enums;

import java.util.ArrayList;
import java.util.List;

public enum MetodoPago {
	//CREDITO_DEBITO,
	TRANSFERENCIA("Transferencia bancaria"),
	//EFEFCTIVO,
	PAYPHONE("PayPhone"),
	PAYPAL("PayPal");
	private String nombre;
	
	private MetodoPago(String nombre) {
		this.nombre = nombre;
	}

	public static List<MetodoPago> fromStringArray(String[] metodosPagoArray) {
		List<MetodoPago> result = new ArrayList<>();
		if (metodosPagoArray == null) {
			return result;
		}
		for (String s : metodosPagoArray) {
			if (s == null) {
				continue;
			}
			String normalized = s.trim().toUpperCase();
			if (normalized.isEmpty()) {
				continue;
			}
			MetodoPago mp = MetodoPago.fromName(normalized);
			if (mp != null) {
				result.add(mp);
			}
		}
		return result;
	}
	
	public static MetodoPago fromName(String name) {
		for (MetodoPago metodoPago : values()) {
			if (metodoPago.name().equalsIgnoreCase(name)) {
				return metodoPago;
			}
		}
		return null; // Retorna null si no se encuentra un método de pago coincidente
	}

	public String getNombre() {
		return nombre;
	}


	
}