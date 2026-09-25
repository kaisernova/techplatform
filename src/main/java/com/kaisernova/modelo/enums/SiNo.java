package com.kaisernova.modelo.enums;

public enum SiNo {
	SI(true), NO(false);
	private boolean valorBoolean;

	private SiNo(boolean valorBoolean) {
		this.valorBoolean = valorBoolean;
	}

	public boolean getValorBoolean() {
		return valorBoolean;
	}
	public String getCodigo() {
		return this.name();
	}
	
}
