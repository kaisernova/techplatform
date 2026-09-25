package com.kaisernova.excepcion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class RestExcepcion extends Exception  {
	private String jsonPeticion;
	private String jsonRespuesta;
	public RestExcepcion() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RestExcepcion(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public RestExcepcion(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
	public RestExcepcion(String message, Throwable cause, String jsonPeticion, String jsonRespuesta) {
		super(message, cause);
		this.jsonPeticion=jsonPeticion;
		this.jsonRespuesta=jsonRespuesta;
	}

	public RestExcepcion(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public RestExcepcion(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
