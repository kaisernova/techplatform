package com.kaisernova.dao.producto;

import com.kaisernova.dao.GenericActivableDao;
import com.kaisernova.modelo.producto.Producto;

public class ProductoDao extends GenericActivableDao<Producto, Long> {

    public ProductoDao() {
        super(Producto.class);
    }

}
