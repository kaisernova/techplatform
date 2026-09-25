package com.kaisernova.dao.usuarios;

import java.io.Serializable;
import java.util.List;

import com.kaisernova.dao.GenericActivableDao;
import com.kaisernova.modelo.enums.NombreRol;
import com.kaisernova.modelo.usuarios.Usuario;
import com.kaisernova.modelo.usuarios.UsuarioBase;

public abstract class UsuarioBaseDao <T extends UsuarioBase, I extends Serializable> extends GenericActivableDao<T, I> {

	
	public UsuarioBaseDao(Class<T> clazz) {
		super(clazz);
		// TODO Auto-generated constructor stub
	}

	public List<T> obtenerUsuarioActivoPorUsuarioClaveHash(String usuario, String claveHash, String nombreClase, Class<T> clazz) { 
		var query = getEntityManager().createQuery("select u from "+nombreClase+" u where u.activo=:activo and u.usuario=:usuario and u.claveHash=:claveHash", clazz);
		query.setParameter("activo", true);
		query.setParameter("usuario", usuario);
		query.setParameter("claveHash", claveHash);
		return query.getResultList();
	}
	abstract public List<T> obtenerUsuarioActivoPorUsuarioClaveHash(String usuario, String claveHash);
	
	public List<T> obtenerTodosOrdenadosNuevos(String nombreClase, Class<T> clazz) {
		var query = getEntityManager().createQuery("select u from "+nombreClase+" u order by u.fechaHoraCreacion desc", clazz);
		return query.getResultList();
	}
	abstract public List<T> obtenerTodosOrdenadosNuevos();
	public List<T> obtenerTodosActivosPorRolOrdenadosApellido(NombreRol nombreRol, String nombreClase, Class<T> clazz) {
		var query = getEntityManager().createQuery("select u from "+nombreClase+" u JOIN u.roles r where r.nombreRol=:nombreRol and u.activo=:activo order by u.apellidos asc", clazz);
		query.setParameter("nombreRol", nombreRol);
		query.setParameter("activo", Boolean.TRUE);
		return query.getResultList();
	}
	abstract  public List<T> obtenerTodosActivosPorRolOrdenadosApellido(NombreRol nombreRol);
	
	public List<T> obtenerPorUsuario(String usuario, String nombreClase, Class<T> clazz) {
		var query = getEntityManager().createQuery("select u from "+nombreClase+" u where u.usuario=:usuario", clazz);
		query.setParameter("usuario", usuario);
		return query.getResultList();
	}
	abstract public List<T> obtenerPorUsuario(String usuario);
	public List<T> obtenerPorEmail(String email, String nombreClase, Class<T> clazz) {
		var query = getEntityManager().createQuery("select u from "+nombreClase+" u where u.email=:email", clazz);
		query.setParameter("email", email);
		return query.getResultList();
	}
	abstract public List<T> obtenerPorEmail(String email);
	
}