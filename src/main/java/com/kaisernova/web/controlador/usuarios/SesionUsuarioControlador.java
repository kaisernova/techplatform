package com.kaisernova.web.controlador.usuarios;

import java.util.logging.Level;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;

import com.kaisernova.logica.usuarios.UsuarioBean;
import com.kaisernova.modelo.enums.NombreRol;

import com.kaisernova.modelo.usuarios.Usuario;
import com.kaisernova.modelo.usuarios.UsuarioBase;
import com.kaisernova.web.controlador.BaseControlador;
import com.kaisernova.web.controlador.administracion.usuarios.ActualizarClaveControladorBase;

import jakarta.inject.Named;

@Named
@SessionScoped
public class SesionUsuarioControlador extends ActualizarClaveControladorBase {
	@Inject
	private UsuarioBean usuarioBean;
	private Usuario usuarioSesion;
	private boolean usuarioEsPaciente;

	@PostConstruct
	public void inicializar() {

		usuarioSesion = usuarioBean.obtenerPorUsuario(getUsuarioConectado());
	}

	public void actualizarClaveUsuarioSesion() {
		try {
			System.out.println("SesionUsuarioControlador.actualizarClaveUsuarioSesion");
			usuarioSesion.setUsuarioActualiza(getUsuarioConectado());
			if (!validarClaveUsuarioIguales(usuarioSesion)) {
				return;
			}

//				usuarioBean.actualizarGenerandoClaveHash((Usuario)usuarioSesion);

			usuarioSesion
					.setClaveHash(usuarioBean.obtenerClaveHash(usuarioSesion.getUsuario(), usuarioSesion.getClave()));
			usuarioSesion.setUsuarioActualiza(getUsuarioConectado());
			usuarioBean.actualizar((Usuario) usuarioSesion);

			fijarMensaje("Usuario [" + usuarioSesion.getUsuario() + "] actualizado exitosamente");
			//
		} catch (ConstraintViolationException e) {
			fijarLogMensajeConstraints(e);
		} catch (Exception e) {
			fijarMensajeError("Error al actualizar usuario", e.getMessage(), e);
		}
	}

	public UsuarioBase getUsuarioSesion() {
		return usuarioSesion;
	}

}
