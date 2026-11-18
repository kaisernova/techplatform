package com.kaisernova.dao.producto;

import com.kaisernova.dao.GenericActivableDao;
import com.kaisernova.modelo.producto.CategoriaProductos;
import jakarta.persistence.NoResultException;

public class CategoriaProductosDao extends GenericActivableDao<CategoriaProductos, Long> {

    public CategoriaProductosDao() {
        super(CategoriaProductos.class);
    }

    public CategoriaProductos obtenerPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }
        try {
            return getEntityManager()
                    .createQuery("select c from CategoriaProductos c where c.codigo = :codigo", CategoriaProductos.class)
                    .setParameter("codigo", codigo.trim())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}