package com.kaisernova.logica.producto;

import java.util.List;

import com.kaisernova.dao.producto.ProductoDao;
import com.kaisernova.modelo.producto.Producto;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class ProductoBean {

    @Inject
    private ProductoDao productoDao;

    // No injection of ProductoEnMemoriaBean to avoid circular dependency

    public Producto crear(Producto producto) {
        return productoDao.create(producto);
    }

    public Producto actualizar(Producto producto) {
        return productoDao.update(producto);
    }

    public Producto obtenerPorId(Long idProducto) {
        return productoDao.findOne(idProducto);
    }

    public List<Producto> obtenerTodos() {
        return productoDao.findAll();
    }

    public List<Producto> obtenerTodosActivos() {
        return productoDao.findAllActivos();
    }

}
