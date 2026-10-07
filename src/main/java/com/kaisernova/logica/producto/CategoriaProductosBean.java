package com.kaisernova.logica.producto;

import java.util.List;

import com.kaisernova.dao.producto.CategoriaProductosDao;
import com.kaisernova.modelo.producto.CategoriaProductos;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class CategoriaProductosBean {

    @Inject
    private CategoriaProductosDao categoriaProductosDao;

    public CategoriaProductos crear(CategoriaProductos categoria) {
        return categoriaProductosDao.create(categoria);
    }

    public CategoriaProductos actualizar(CategoriaProductos categoria) {
        return categoriaProductosDao.update(categoria);
    }

    public CategoriaProductos obtenerPorId(Long id) {
        return categoriaProductosDao.findOne(id);
    }

    public List<CategoriaProductos> obtenerTodos() {
        return categoriaProductosDao.findAll();
    }

    public List<CategoriaProductos> obtenerTodosActivos() {
        return categoriaProductosDao.findAllActivos();
    }

}
