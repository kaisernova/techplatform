/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kaisernova.web.controlador.usuarios;

import static jakarta.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;

import java.util.Objects;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;

import com.kaisernova.constantes.ConstantesAplicacion;
import com.kaisernova.logica.usuarios.UsuarioBean;
import com.kaisernova.modelo.usuarios.UsuarioBase;

/**
 *
 * @author hantsy
 */
@ApplicationScoped
public class UsuariosIdentityStore implements IdentityStore {
	@Inject
	private UsuarioBean usuarioBean;
	@Inject
	private Logger logger;

	public CredentialValidationResult validate(UsernamePasswordCredential usernamePasswordCredential) {
		String caller = usernamePasswordCredential.getCaller();
		logger.info("[validate] validando usuario (caller)=" + caller);
		// boolean esPaciente =
		// caller.startsWith(ConstantesAplicacion.PREFIJO_PACIENTE_USUARIO);
		UsuarioBase usuario = null;

		usuario = usuarioBean.obtenerUsuarioActivoPorUsuarioClaveHash(caller,
				usernamePasswordCredential.getPasswordAsString());

		logger.info("[validate] usuario obtenido (usuario)=" + usuario);
		if (Objects.nonNull(usuario)) {
			return new CredentialValidationResult(usuario.getUsuario(), usuario.getRolesStrings());
		}

		return INVALID_RESULT;
	}

}