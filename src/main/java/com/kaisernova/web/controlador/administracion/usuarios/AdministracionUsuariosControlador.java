package com.kaisernova.web.controlador.administracion.usuarios;

import java.util.ArrayList;
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

@Named
@ViewScoped
public class AdministracionUsuariosControlador extends ActualizarClaveControladorBase {
	@Inject
	private UsuarioBean usuarioBean;


	@Inject
	private RolBean rolBean;

	private Usuario usuarioActualiza;

	private List<Usuario> usuarios;
	private List<Usuario> usuariosFiltrados;
	private List<Rol> roles;
	private List<String> usuariosAsignables;
	private List<String> usuariosAsignados;

	@PostConstruct
	public void inicializar() {
		reInicializar();
		roles = rolBean.getRoles();
	}

	public void reInicializar() {
		usuarios = usuarioBean.obtenerTodosOrdenadosNuevos();
		usuarioActualiza = null;
		usuariosAsignables=new ArrayList<String>();
		usuariosAsignados=new ArrayList<String>();
	}


	
	public void actualizarClave() {
		try {
			if(!validarClaveUsuarioIguales(usuarioActualiza)) {
				return;
			}
			usuarioActualiza.setClaveHash(
					usuarioBean.obtenerClaveHash(usuarioActualiza.getUsuario(), usuarioActualiza.getClave()));			
			usuarioActualiza.setUsuarioActualiza(getUsuarioConectado());
			usuarioBean.actualizar(usuarioActualiza);
			fijarMensaje("Usuario [" + usuarioActualiza.getUsuario() + "] actualizado exitosamente");
			//reInicializar();
			//
		} catch (ConstraintViolationException e) {
			logger.log(Level.SEVERE, "Exception: ");
			e.getConstraintViolations().forEach(err -> logger.log(Level.SEVERE, err.toString()));
		}	
		catch (Exception e) {
			fijarMensajeError("Error al actualizar usuario", e.getMessage(), e);
		}
	}

	public void onRowEdit(RowEditEvent<Usuario> event) {
		Usuario usuario = event.getObject();
		try {
			usuario.setUsuarioActualiza(getUsuarioConectado());
			usuarioBean.actualizar(usuario);
			fijarMensaje("Usuario [" + usuario.getUsuario() + "] actualizado exitosamente");
			reInicializar();
		} catch (ConstraintViolationException e) {
			logger.log(Level.SEVERE, "Exception: ");
			e.getConstraintViolations().forEach(err -> logger.log(Level.SEVERE, err.toString()));
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

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public List<Usuario> getUsuariosFiltrados() {
		return usuariosFiltrados;
	}

	public void setUsuariosFiltrados(List<Usuario> usuariosFiltrados) {
		this.usuariosFiltrados = usuariosFiltrados;
	}

	public List<Rol> getRoles() {
		return roles;
	}

	public List<String> getUsuariosAsignables() {
		return usuariosAsignables;
	}

	public void setUsuariosAsignables(List<String> usuariosAsignables) {
		this.usuariosAsignables = usuariosAsignables;
	}

	public List<String> getUsuariosAsignados() {
		return usuariosAsignados;
	}

	public void setUsuariosAsignados(List<String> usuariosAsignados) {
		this.usuariosAsignados = usuariosAsignados;
	}

}
