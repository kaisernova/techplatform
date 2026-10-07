package com.kaisernova.logica.producto;

import java.util.List;

import com.kaisernova.dao.producto.BienDao;
import com.kaisernova.modelo.producto.Bien;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class BienBean {

    @Inject
    private BienDao bienDao;

    public Bien crear(Bien bien) {
        return bienDao.create(bien);
    }

    public Bien actualizar(Bien bien) {
        return bienDao.update(bien);
    }

    public Bien obtenerPorId(Long idBien) {
        return bienDao.findOne(idBien);
    }

    public List<Bien> obtenerTodos() {
        return bienDao.findAll();
    }

    public List<Bien> obtenerTodosActivos() {
        return bienDao.findAllActivos();
    }

}
