package com.kaisernova.logica.usuarios;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.kaisernova.dao.usuarios.UsuarioDao;
import com.kaisernova.excepcion.UsuarioExcepcion;
import com.kaisernova.logica.notificaciones.NotificacionesBean;
import com.kaisernova.modelo.enums.CodigoError;
import com.kaisernova.modelo.enums.NombreRol;
import com.kaisernova.modelo.usuarios.Usuario;
import com.kaisernova.util.aplicacion.UtilAplicacion;
import com.kaisernova.util.crypto.CryptoUtil;

import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;

@Stateless
public class UsuarioBean {
	@Inject
	private Logger logger;
	@Inject
	private UsuarioDao usuarioDao;

	@Inject
	private RolBean rolBean;
	@Inject
	private UsuarioEnMemoriaBean usuarioEnMemoriaBean;

	@Inject
	private NotificacionesBean notificacionesBean;
	
	
	@Asynchronous
	public void refrescarUsuariosEnMemoria() {
		usuarioEnMemoriaBean.refrescar();
	}
	
	
	public Usuario crearGenerandoClaveHash(Usuario usuario) throws UsuarioExcepcion {
		if(verificarExistePorUsuario(usuario.getUsuario())) {
			throw new UsuarioExcepcion(CodigoError.USUARIO_EXISTENTE,"El nombre de usuario [" + usuario.getUsuario()+ "] ya existe. Debe especificar otro.");
		}
		if(verificarExistePorNumeroIdentificacion(usuario.getNumeroIdentificacion())) {
			throw new UsuarioExcepcion(CodigoError.IDENTIFICACION_USUARIO_EXISTENTE,"Un Usuario con Número de Identificación [" + usuario.getNumeroIdentificacion()+ "] ya existe. Debe especificar otro.");
		}
		// NEW: validate email uniqueness for Usuario (not paciente)
		if(verificarExistePorEmail(usuario.getEmail())) {
			throw new UsuarioExcepcion(CodigoError.EMAIL_USUARIO_EXISTENTE, "El mail [" + usuario.getEmail()+ "] ya existe. Debe especificar otro.");
		}
		usuario.setClaveHash(obtenerClaveHash(usuario.getUsuario(), usuario.getClave()));
		
		var usuarioCreado = usuarioDao.create(usuario);
		//refrescarUsuariosEnMemoria();
		return usuarioCreado;
	}

	
	
	/**
	 * Utilizar este metodo para actualizar la clave de usuario
	 * @param usuario
	 * @param clave
	 * @return
	 */
	public Usuario actualizarGenerandoClaveHash(Usuario usuario) {
		logger.info("[actualizarGenerandoClaveHash]usuario="+usuario);
		usuario.setClaveHash(obtenerClaveHash(usuario.getUsuario(), usuario.getClave()));
		var usuarioActualizado = usuarioDao.update(usuario);
		return usuarioActualizado;
	}
	
	
	
	/**
	 * Utilizar este metodo para actualizar el usuario sin tocar la clave
	 * @param usuario
	 * @param clave
	 * @return
	 */
	public Usuario actualizar(Usuario usuario) throws UsuarioExcepcion {
		// NEW: validate email uniqueness when updating a Usuario (not paciente)
		if(verificarExistePorActualizarPorEmail(usuario)) {
			throw new UsuarioExcepcion(CodigoError.EMAIL_USUARIO_EXISTENTE, "El mail [" + usuario.getEmail()+ "] ya existe para otro usuario. Debe especificar otro.");
		}
		var usuarioActualizado = usuarioDao.update(usuario);
		refrescarUsuariosEnMemoria();
		return usuarioActualizado;
	}
	

	
	public String obtenerClaveHash(String usuario, String clave) {
		String salt = CryptoUtil.crearSalt16Bytes(usuario);
		return CryptoUtil.generarHashBcrypt(clave, salt);
	}
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Usuario obtenerUsuarioActivoPorUsuarioClaveHash(String usuario, String clave) {
		var claveHash = obtenerClaveHash(usuario, clave);
		var usuarios = usuarioDao.obtenerUsuarioActivoPorUsuarioClaveHash(usuario, claveHash);
		if(Objects.nonNull(usuarios) && usuarios.size()==1) {
			return usuarios.get(0);
		}
		return null;
	}
	
	
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Usuario> obtenerTodos() {
		return usuarioDao.findAll();
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Usuario> obtenerTodosActivos() {
		return usuarioDao.findAllActivos();
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Usuario> obtenerTodosOrdenadosNuevos() {
		return usuarioDao.obtenerTodosOrdenadosNuevos();
	}
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Usuario obtenerPorUsuario(String usuario) {
		var usuarios = usuarioDao.obtenerPorUsuario(usuario);
		if(Objects.nonNull(usuarios) && !usuarios.isEmpty()) {
			return usuarios.get(0);
		}
		return null;
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Usuario obtenerPorId(Long idUsuario) {
		return usuarioDao.findOne(idUsuario);		
	}

	

	
	// NEW: Usuario (no paciente) email helpers
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Usuario obtenerUsuarioPorEmail(String email) {
		var usuarios = usuarioDao.obtenerPorEmail(email);
		if(Objects.nonNull(usuarios) && !usuarios.isEmpty()) {
			return usuarios.get(0);
		}
		return null;
	}
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public boolean verificarExistePorEmail(String email) {
		return Objects.nonNull(obtenerUsuarioPorEmail(email));
	}
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public boolean verificarExistePorActualizarPorEmail(Usuario usuario) {
		var usuarioRegistrado = obtenerUsuarioPorEmail(usuario.getEmail());
		return Objects.nonNull(usuarioRegistrado) && !usuarioRegistrado.getIdUsuario().equals(usuario.getIdUsuario());
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public boolean verificarEsAdministrador(String usuario) {
		if(Objects.nonNull(usuario) && !usuario.isEmpty()) {
			Usuario usuarioRegistrado = obtenerPorUsuario(usuario);
			return usuarioRegistrado.isEsAdministrador();
		}
		return false;
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public boolean verificarExistePorUsuario(String usuario) {
		return  Objects.nonNull(obtenerPorUsuario(usuario));
	}
	
	

	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Usuario obtenerPorNumeroIdentificacion(String numeroIdentificacion) {
		var usuarios = usuarioDao.obtenerPorNumeroIdentificacion(numeroIdentificacion);
		if(Objects.nonNull(usuarios) && !usuarios.isEmpty()) {
			return usuarios.get(0);
		}
		return null;
	}
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public boolean verificarExistePorNumeroIdentificacion(String numeroIdentificacion) {
		return  Objects.nonNull(obtenerPorNumeroIdentificacion(numeroIdentificacion));
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Usuario> obtenerTodosActivosPorRolOrdenadosApellido(NombreRol nombreRol) {
		return usuarioDao.obtenerTodosActivosPorRolOrdenadosApellido(nombreRol);
	}
	

	

	
	public Map<String, Usuario> obtenerTodosUsuarioMapa() {
		List<Usuario> usuarios = usuarioDao.findAll();
		Map<String, Usuario> usuariosMapa = new ConcurrentSkipListMap<String, Usuario>();
		for (Usuario usuario : usuarios) {
			usuariosMapa.put(usuario.getUsuario(), usuario);
		}
		return usuariosMapa;
	}
	
	public void recuperarClaveUsuarioEmail(Usuario usuario) {
		String clave = UtilAplicacion.generateRandomString(8);
		usuario.setClave(clave);
		actualizarGenerandoClaveHash(usuario);
		enviarNotificacionRecuperacionClave(usuario.getEmail(), Usuario.class.getSimpleName(), usuario.getUsuario(), usuario.getApellidosNombres(), clave);
	}
	

	
	@Asynchronous
	public void enviarNotificacionRecuperacionClave(String destinatario, String tipoUsuario, String usuario, String nombresUsuario, String clave) {
		try {
			logger.info("[enviarNotificacionRecuperacionClave] Enviando notificación de recuperación de clave para destinatario=" + destinatario + ", usuario=" + usuario);
			notificacionesBean.enviarNotificacionGeneracionClaveRecuperacion(destinatario, tipoUsuario, usuario, nombresUsuario, clave);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "[enviarNotificacionRecuperacionClave]Error al enviar notificación de recuperación de clave para destinatario:" + destinatario + ", usuario:" + usuario, e);			
		}
	}
	
	
}