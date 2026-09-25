package com.kaisernova.excepcion;

import com.kaisernova.modelo.enums.CodigoError;

/**
 * Excepcion base que tiene un codigo de error referencial en el caso de
 * necesitar comportamientos
 * 
 * @author user
 *
 */
public abstract class CodigoErrorExcepcionBase extends Exception {

	private CodigoError codigoError;

	public CodigoErrorExcepcionBase() {
		super();
	}

	public CodigoErrorExcepcionBase(String message) {
		super(message);
	}

	public CodigoErrorExcepcionBase(Throwable cause) {
		super(cause);
	}

	public CodigoErrorExcepcionBase(String message, Throwable cause) {
		super(message, cause);
	}

	public CodigoErrorExcepcionBase(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CodigoErrorExcepcionBase(CodigoError codigoError) {
		super();
		this.codigoError = codigoError;
	}

	public CodigoErrorExcepcionBase(CodigoError codigoError, String message) {
		super(message);
		this.codigoError = codigoError;
	}

	public CodigoErrorExcepcionBase(CodigoError codigoError, Throwable cause) {
		super(cause);
		this.codigoError = codigoError;
	}

	public CodigoErrorExcepcionBase(CodigoError codigoError, String message, Throwable cause) {
		super(message, cause);
		this.codigoError = codigoError;
	}

	public CodigoErrorExcepcionBase(CodigoError codigoError, String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.codigoError = codigoError;
	}

	public CodigoError getCodigoError() {
		return codigoError;
	}

	public void setCodigoError(CodigoError codigoError) {
		this.codigoError = codigoError;
	}

}
