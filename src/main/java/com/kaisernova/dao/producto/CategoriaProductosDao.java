package com.kaisernova.dao.producto;

import com.kaisernova.dao.GenericActivableDao;
import com.kaisernova.modelo.producto.CategoriaProductos;

public class CategoriaProductosDao extends GenericActivableDao<CategoriaProductos, Long> {

    public CategoriaProductosDao() {
        super(CategoriaProductos.class);
    }

}
