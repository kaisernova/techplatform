package com.kaisernova.dao.producto;

import com.kaisernova.dao.GenericActivableDao;
import com.kaisernova.modelo.producto.Producto;
import jakarta.persistence.NoResultException;

public class ProductoDao extends GenericActivableDao<Producto, Long> {

    public ProductoDao() {
        super(Producto.class);
    }

    public Producto obtenerPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }
        try {
            return getEntityManager()
                    .createQuery("select p from Producto p where p.codigo = :codigo", Producto.class)
                    .setParameter("codigo", codigo.trim())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}