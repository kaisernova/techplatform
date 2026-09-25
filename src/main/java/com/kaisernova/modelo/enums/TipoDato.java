package com.kaisernova.modelo.enums;

import java.time.format.DateTimeFormatter;

public enum TipoDato {
	STRING, LONGTEXT, BASE64, BYTEA, INTEGER, LONG, BIGDECIMAL, BOOLEAN, ENUM, HTML,
	//iso 8601
	DATE("yyyy-MM-dd"), DATETIME("yyyy-MM-dd'T'HH:mm:ss.SSS");
	private String formatoDefecto;

	private TipoDato(String formatoDefecto) {
		this.formatoDefecto = formatoDefecto;
	}
	private TipoDato() {
		this.formatoDefecto = "";
	}

	public String getFormatoDefecto() {
		return formatoDefecto;
	}

	public String getName() {
		return this.name();
	}

	public static TipoDato fromName(String name) {
		for (TipoDato tipoDato : values()) {
			if (tipoDato.name().equalsIgnoreCase(name)) {
				return tipoDato;
			}
		}
		return TipoDato.STRING;
		
	}
}
