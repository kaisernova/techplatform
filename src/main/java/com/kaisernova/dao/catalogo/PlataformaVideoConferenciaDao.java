package com.kaisernova.dao.catalogo;

import java.util.List;

import com.kaisernova.dao.GenericActivableDao;
import com.kaisernova.modelo.catalogo.PlataformaVideoConferencia;

public class PlataformaVideoConferenciaDao extends GenericActivableDao<PlataformaVideoConferencia, String> {

    public PlataformaVideoConferenciaDao() {
        super(PlataformaVideoConferencia.class);
    }

    public List<PlataformaVideoConferencia> obtenerPorNombre(String nombre) {
        return getEntityManager().createQuery(" from PlataformaVideoConferencia p where p.nombre = :nombre")
                .setParameter("nombre", nombre).getResultList();
    }

    public List<PlataformaVideoConferencia> obtenerPorCodigo(String codigo) {
        return getEntityManager().createQuery(" from PlataformaVideoConferencia p where p.codigo = :codigo")
                .setParameter("codigo", codigo).getResultList();
    }
}
