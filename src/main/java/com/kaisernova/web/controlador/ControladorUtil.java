package com.kaisernova.web.controlador;

import java.util.Objects;

import org.primefaces.model.file.UploadedFile;

import com.kaisernova.util.Base64Util;

public final class ControladorUtil {
	private ControladorUtil() {
		super();
	}
	public static String obtenerBase64DeUploadesFile(UploadedFile archivo) {
		if (Objects.isNull(archivo) || Objects.isNull(archivo.getContent()) || archivo.getSize()<=0) {
			return null;
		}
		byte[] contenidoArchivo = archivo.getContent();
		return Base64Util.transformarContenidoAStringBase64(contenidoArchivo);
		
	}
}
