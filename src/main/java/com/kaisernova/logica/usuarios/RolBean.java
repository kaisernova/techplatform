package com.kaisernova.logica.usuarios;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

import com.kaisernova.dao.usuarios.RolDao;
import com.kaisernova.modelo.enums.NombreRol;
import com.kaisernova.modelo.usuarios.Rol;

@Singleton
@Startup
public class RolBean {
	private final static Map<NombreRol, Rol> ROLES_MAP = new ConcurrentSkipListMap<>();
	//no pacientes, sistema
	private final static List<Rol> ROLES_USUARIO = new ArrayList<>();
	private final static List<Rol> ROLES = new ArrayList<>();
	
	@Inject
	private RolDao rolDao;
	@PostConstruct
	public void inicializar() {
		ROLES_MAP.clear();
		ROLES.clear();
		ROLES_USUARIO.clear();
		ROLES.addAll(rolDao.findAll());
		for (Rol rol : ROLES) {
			ROLES_MAP.put(rol.getNombreRol(), rol);
			if (!rol.getNombreRol().equals(NombreRol.PACIENTE)) {
				ROLES_USUARIO.add(rol);
			}
		}
	}
	@Lock(LockType.READ)
	public List<Rol> getRoles() {
		return ROLES;
	}
	
	@Lock(LockType.READ)
	public List<Rol> getRolesUsuario() {
		return ROLES_USUARIO;
	}
	
	@Lock(LockType.READ)
	public Rol getRol(NombreRol nombreRol) {
		return ROLES_MAP.get(nombreRol);
	}
	@Lock(LockType.READ)
	public Rol getRol(String nombreRolString) {
		NombreRol nombreRol = NombreRol.obtenerPorNombreRolString(nombreRolString);
		return ROLES_MAP.get(nombreRol);
	}
}
