package com.kaisernova.logica.usuarios;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.kaisernova.modelo.enums.NombreRol;
import com.kaisernova.modelo.usuarios.Usuario;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

@Singleton
@Startup
public class UsuarioEnMemoriaBean {

	private final static List<Usuario> USUARIOS = new ArrayList<>();
	
	@Inject
	private Logger logger;
	@Inject
	private UsuarioBean usuarioBean;
	

	@PostConstruct
	public void inicializar() {
		refrescar();
	}

	/**
	 * Rebuild the in-memory lists and maps from the database via injected beans.
	 */
	@Lock(LockType.WRITE)
	public void refrescar() {
		USUARIOS.clear();
		
		List<Usuario> usuariosDb = usuarioBean.obtenerTodosActivos();
		if (usuariosDb != null) {
			USUARIOS.addAll(usuariosDb);
			
		}
		logger.info("[refrescar] Especialistas en memoria cargados: " + USUARIOS);

		
	}

	@Lock(LockType.READ)
	public List<Usuario> getEspecialistas() {
		return USUARIOS;
	}

	
}