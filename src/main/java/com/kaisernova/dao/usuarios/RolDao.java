package com.kaisernova.dao.usuarios;

import com.kaisernova.dao.GenericDao;
import com.kaisernova.modelo.enums.NombreRol;
import com.kaisernova.modelo.usuarios.Rol;

public class RolDao extends GenericDao<Rol, NombreRol>{

	public RolDao() {
		super(Rol.class);
	}

}
