package com.kaisernova.constantes;

public final class ConstantesAplicacion {
	public final static String _INPUT="_input";
	public final static String USUARIO="usuario";
	public final static String EMAIL="email";
	public final static String NUMERO_DOCUMENTO = "numeroDocumento";
	public final static String TELF_CELULAR="telfCelular";
	public final static String TIPO_DOCUMENTO= "tipoDocumento_input";
	public final static String MESSAGES = "messages";
	public final static String ERROR_CLAVE_USUARIO_TITULO="error.clave.usuario.titulo";
	public final static String ERROR_CLAVE_USUARIO="error.clave.usuario";
	public final static String ERROR_IDENTIFICACION_TITULO="error.identificacion.titulo";
	public final static String ERROR_IDENTIFICACION="error.identificacion";
	public final static String ERROR_EMAILS_IGUALES_TITULO="error.emails.iguales.titulo";
	public final static String ERROR_EMAILS_IGUALES="error.emails.iguales";
	public final static String ERROR_CELULARES_IGUALES_TITULO="error.celulares.iguales.titulo";
	public final static String ERROR_CELULARES_IGUALES="error.celulares.iguales";
	public final static String REGEX_LETRAS_ESPACIOS = "/^[a-zA-ZñÑáéíóúÁÉÍÓÚ ]*$/";
	public final static String REGEX_LETRAS_NUMEROS_ESPACIOS = "/^[a-zA-Z0-9 ]*$/";
	public final static String REGEX_LETRAS_ESPACIOS_USUARIO = "/^[a-zA-Z0-9@\\-_\\. ]*$/";
	public final static String CODIGO_ORDEN_TRABAJO_PARAM="codigoOrdenTrabajo";
	public final static Integer HTTP_STATUS_OK = 200;
	public final static Integer HTTP_NOT_ACCEPTABLE = 406;
	public final static String FRONT = "FRONT";
	public final static String BACK = "BACK";
	public final static Integer LARGO_CEDULA = 10;
	public final static String NOMBRE_ARCHIVO_JSON_RESPUESTA = "respuesta.json";
	public final static String PREFIJO_COMPROBANTE_ELECTRONICO="E-";
	//para transacciones anonimas
	public final static String NO_USUARIO="NOUSU";
	//para login de pacientes
	public final static String PREFIJO_PACIENTE_USUARIO="____PACIENTE____";
	public final static String PACIENTE_MINUSCULAS="paciente";
	private ConstantesAplicacion() {
		// nada
	}
}
