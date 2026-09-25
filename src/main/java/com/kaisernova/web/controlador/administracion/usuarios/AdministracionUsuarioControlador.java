package com.kaisernova.web.controlador.administracion.usuarios;

import java.util.List;
import java.util.logging.Level;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolationException;

import org.primefaces.event.RowEditEvent;

import com.kaisernova.constantes.ConstantesAplicacion;
import com.kaisernova.excepcion.UsuarioExcepcion;
import com.kaisernova.logica.usuarios.RolBean;
import com.kaisernova.logica.usuarios.UsuarioBean;
import com.kaisernova.modelo.enums.CodigoError;
import com.kaisernova.modelo.usuarios.Rol;
import com.kaisernova.modelo.usuarios.Usuario;
import com.kaisernova.web.controlador.BaseControlador;


@Named
@ViewScoped
public class AdministracionUsuarioControlador extends BaseControlador {
	@Inject
	private UsuarioBean usuarioBean;
	@Inject
	private RolBean rolBean;

	private Usuario usuario;
	private List<Rol> roles;
	

//	@NotNull(message = "Confirmación de Clave requerida")
//	@Size(min = 8, max = 64, message = "Confirmación de Clave debe tener una longitud de 8 a 64 caracteres")
//	private String claveConfirmacion;

	@PostConstruct
	public void inicializar() {
		reInicializar();
		roles = rolBean.getRoles();
	}

	public void reInicializar() {
		usuario = new Usuario();
		usuario.setUsuarioCrea(getUsuarioConectado());
		usuario.setUsuarioActualiza(getUsuarioConectado());
	}

	public String crear() {
		try {
			usuarioBean.crearGenerandoClaveHash(usuario);
			fijarMensaje("Usuario [" + usuario.getUsuario() + "] creado exitosamente");
			reInicializar();
			return "/administracion/usuarios/usuarios.xhtml?faces-redirect=true";
		} catch (ConstraintViolationException e) {
			logger.log(Level.SEVERE, "Exception: ");
			e.getConstraintViolations().forEach(err -> logger.log(Level.SEVERE, err.toString()));
		} catch (UsuarioExcepcion e) {
			//formUsuario:usuario
			if(CodigoError.USUARIO_EXISTENTE.equals(e.getCodigoError())) {
				fijarMensajeError("formUsuario:usuario", "Error al crear usuario", e.getMessage(), e);		
			} else if(CodigoError.IDENTIFICACION_USUARIO_EXISTENTE.equals(e.getCodigoError())) {
				fijarMensajeError("formUsuario:numeroIdentificacion", "Error al crear usuario", e.getMessage(), e);		
			} else {
				fijarMensajeError("Error al crear usuario", e.getMessage(), e);
			}			
		} catch (Exception e) {
			fijarMensajeError("Error al crear usuario", e.getMessage(), e);		
			if( e.getCause() instanceof ConstraintViolationException) {
				((ConstraintViolationException) e.getCause()).getConstraintViolations().forEach(err -> logger.log(Level.SEVERE, err.toString()));
			}
		}
		return null;
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

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Rol> getRoles() {
		return roles;
	}
	public String getRegexLetrasEspaciosUsuario() {
		return ConstantesAplicacion.REGEX_LETRAS_ESPACIOS_USUARIO;
	}

}
