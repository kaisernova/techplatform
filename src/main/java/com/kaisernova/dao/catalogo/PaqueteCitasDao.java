package com.kaisernova.dao.catalogo;

import java.util.List;

import com.kaisernova.dao.GenericActivableDao;
import com.kaisernova.modelo.catalogo.PaqueteCitas;
import com.kaisernova.modelo.enums.TipoPaqueteCita;

import jakarta.persistence.TypedQuery;

public class PaqueteCitasDao extends GenericActivableDao<PaqueteCitas, String> {

    public PaqueteCitasDao() {
        super(PaqueteCitas.class);
    }

    @Override
    public List<PaqueteCitas> findAll() {
        return super.findAll();
    }

    public PaqueteCitas findByCodigo(String codigo) {
        if (codigo == null) return null;
        return findOne(codigo);
    }

    public PaqueteCitas findByNombre(String nombre) {
        if (nombre == null) return null;
        TypedQuery<PaqueteCitas> q = getEntityManager().createQuery("SELECT p FROM PaqueteCitas p WHERE p.nombre = :nombre", PaqueteCitas.class);
        q.setParameter("nombre", nombre);
        List<PaqueteCitas> res = q.getResultList();
        return res.isEmpty() ? null : res.get(0);
    }

   

    public PaqueteCitas findByNumeroCitasAndTipoPaqueteCita(Integer numeroCitas, TipoPaqueteCita tipoPaqueteCita) {
        if (numeroCitas == null || tipoPaqueteCita == null) return null;
        TypedQuery<PaqueteCitas> q = getEntityManager().createQuery(
                "SELECT p FROM PaqueteCitas p WHERE p.numeroCitas = :numeroCitas AND p.tipoPaqueteCita = :tipoPaqueteCita order by p.numeroCitas ASC",
                PaqueteCitas.class);
        q.setParameter("numeroCitas", numeroCitas);
        q.setParameter("tipoPaqueteCita", tipoPaqueteCita);
        List<PaqueteCitas> res = q.getResultList();
        return res.isEmpty() ? null : res.get(0);
    }

    public boolean existsByCodigo(String codigo) {
        return findByCodigo(codigo) != null;
    }

    public boolean existsByNombre(String nombre, String excludeCodigo) {
        PaqueteCitas found = findByNombre(nombre);
        if (found == null) return false;
        if (excludeCodigo == null) return true;
        return !excludeCodigo.equals(found.getCodigo());
    }

    

    public boolean existsByNumeroCitasAndTipoPaqueteCita(Integer numeroCitas, TipoPaqueteCita tipoPaqueteCita, String excludeCodigo) {
        PaqueteCitas found = findByNumeroCitasAndTipoPaqueteCita(numeroCitas, tipoPaqueteCita);
        if (found == null) return false;
        if (excludeCodigo == null) return true;
        return !excludeCodigo.equals(found.getCodigo());
    }


}