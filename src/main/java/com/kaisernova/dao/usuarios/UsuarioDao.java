package com.kaisernova.dao.usuarios;

import java.util.List;


import com.kaisernova.modelo.enums.NombreRol;
import com.kaisernova.modelo.usuarios.Usuario;

public class UsuarioDao extends UsuarioBaseDao<Usuario, Long> {

	public UsuarioDao() {
		super(Usuario.class);
		// TODO Auto-generated constructor stub
	}
	public List<Usuario> obtenerUsuarioActivoPorUsuarioClaveHash(String usuario, String claveHash) {
		return super.obtenerUsuarioActivoPorUsuarioClaveHash(usuario, claveHash, Usuario.class.getSimpleName(),Usuario.class);
	}
	
	public List<Usuario> obtenerTodosOrdenadosNuevos() {
		return super.obtenerTodosOrdenadosNuevos(Usuario.class.getSimpleName(),Usuario.class);
	}
	
	public List<Usuario> obtenerTodosActivosPorRolOrdenadosApellido(NombreRol nombreRol) {
		return super.obtenerTodosActivosPorRolOrdenadosApellido(nombreRol, Usuario.class.getSimpleName(),Usuario.class);
	}
	
	public List<Usuario> obtenerPorUsuario(String usuario) {
		return super.obtenerPorUsuario(usuario, Usuario.class.getSimpleName(),Usuario.class);
	}
	
	public List<Usuario> obtenerPorNumeroIdentificacion(String numeroIdentificacion) {
		var query = getEntityManager().createQuery("select u from Usuario u where u.numeroIdentificacion=:numeroIdentificacion", Usuario.class);
		query.setParameter("numeroIdentificacion", numeroIdentificacion);
		return query.getResultList();
	}
	@Override
	public List<Usuario> obtenerPorEmail(String email) {
		return super.obtenerPorEmail(email, Usuario.class.getSimpleName(),Usuario.class);
	}
}