package com.kaisernova.web.controlador.administracion.usuarios;

import java.util.List;
import java.util.logging.Level;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolationException;

import org.primefaces.event.RowEditEvent;

import com.kaisernova.logica.usuarios.RolBean;
import com.kaisernova.logica.usuarios.UsuarioBean;
import com.kaisernova.modelo.usuarios.Rol;
import com.kaisernova.modelo.usuarios.Usuario;
import com.kaisernova.web.controlador.BaseControlador;

@Named
@ViewScoped
public class ActualizarUsuarioControlador extends BaseControlador {
	@Inject
	private UsuarioBean usuarioBean;

	@Inject
	private RolBean rolBean;

	private Usuario usuarioActualiza;
	

	private List<Rol> roles;


	@PostConstruct
	public void inicializar() {
		reInicializar();
		roles = rolBean.getRoles();
	}

	public void reInicializar() {
		usuarioActualiza = null;
	}

	public void actualizarClave() {
		try {
			usuarioActualiza.setUsuarioActualiza(getUsuarioConectado());
			usuarioBean.actualizarGenerandoClaveHash(usuarioActualiza);
			fijarMensaje("Usuario [" + usuarioActualiza.getUsuario() + "] actualizado exitosamente");
			reInicializar();
			//
		} catch (ConstraintViolationException e) {
			logger.log(Level.SEVERE, "Exception: ");
			e.getConstraintViolations().forEach(err -> logger.log(Level.SEVERE, err.toString()));
			fijarMensajeError("Error al actualizar usuario", e.getMessage(), e);
		}	
		catch (Exception e) {
			fijarMensajeError("Error al actualizar usuario", e.getMessage(), e);
		}
	}
	

	public UsuarioBean getUsuarioBean() {
		return usuarioBean;
	}

	public void setUsuarioBean(UsuarioBean usuarioBean) {
		this.usuarioBean = usuarioBean;
	}



	public Usuario getUsuarioActualiza() {
		return usuarioActualiza;
	}

	public void setUsuarioActualiza(Usuario usuarioActualiza) {
		this.usuarioActualiza = usuarioActualiza;
	}

	public List<Rol> getRoles() {
		return roles;
	}

}
