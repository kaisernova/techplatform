package com.kaisernova.web.controlador.administracion.usuarios;

import java.util.logging.Level;

import jakarta.validation.ConstraintViolationException;

import com.kaisernova.constantes.ConstantesAplicacion;
import com.kaisernova.modelo.usuarios.Usuario;
import com.kaisernova.web.controlador.BaseControlador;

public class ActualizarClaveControladorBase extends BaseControlador {
	protected void fijarLogMensajeConstraints(ConstraintViolationException e) {
		logger.log(Level.SEVERE, "Exception: ");
		e.getConstraintViolations().forEach(err -> logger.log(Level.SEVERE, err.toString()));
		fijarMensajeError("Error al actualizar usuario", e.getMessage(), e);
	}

	protected boolean validarClaveUsuarioIguales(Usuario usuario) {
		System.out.println("[validarClaveUsuarioIguales]usuario.getUsuario()="+usuario.getUsuario()+", usuario.getClave()="+usuario.getClave());
		if(usuario.getUsuario().equalsIgnoreCase(usuario.getClave())) {
			fijarMensajeError(msg(ConstantesAplicacion.ERROR_CLAVE_USUARIO_TITULO), msg(ConstantesAplicacion.ERROR_CLAVE_USUARIO));
			return false;
		}
		return true;
	}
}
