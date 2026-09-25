package com.kaisernova.logica.catalogo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;

import com.kaisernova.dao.catalogo.PlataformaVideoConferenciaDao;
import com.kaisernova.modelo.catalogo.PlataformaVideoConferencia;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

@Singleton
@Startup
public class PlataformaVideoConferenciaBean {

    private final static Map<String, PlataformaVideoConferencia> PLATAFORMAS_MAPA = new ConcurrentSkipListMap<>();
    private final static List<PlataformaVideoConferencia> PLATAFORMAS = new ArrayList<>();

    @Inject
    private PlataformaVideoConferenciaDao plataformaVideoConferenciaDao;

    @Lock(LockType.WRITE)
    public PlataformaVideoConferencia crear(PlataformaVideoConferencia plataforma) {
        PlataformaVideoConferencia registrada = plataformaVideoConferenciaDao.create(plataforma);
        reInicilizar();
        return registrada;
    }

    @Lock(LockType.WRITE)
    public PlataformaVideoConferencia actualizar(PlataformaVideoConferencia plataforma) {
        PlataformaVideoConferencia registrada = plataformaVideoConferenciaDao.update(plataforma);
        reInicilizar();
        return registrada;
    }

    @Lock(LockType.READ)
    public List<PlataformaVideoConferencia> obtenerTodas() {
        return plataformaVideoConferenciaDao.findAllOrderBy("nombre", false);
    }

    @Lock(LockType.READ)
    public List<PlataformaVideoConferencia> obtenerActivas() {
        return plataformaVideoConferenciaDao.findAllActivosOrderBy("nombre", false);
    }

    @Lock(LockType.READ)
    public PlataformaVideoConferencia obtenerPorNombre(String nombre) {
        List<PlataformaVideoConferencia> lista = plataformaVideoConferenciaDao.obtenerPorNombre(nombre);
        if (!lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    @Lock(LockType.READ)
    public PlataformaVideoConferencia obtenerPorCodigo(String codigo) {
        List<PlataformaVideoConferencia> lista = plataformaVideoConferenciaDao.obtenerPorCodigo(codigo);
        if (!lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    @Lock(LockType.READ)
    public boolean existePorNombre(String nombre) {
        return Objects.nonNull(this.obtenerPorNombre(nombre));
    }

    @Lock(LockType.READ)
    public boolean existePorCodigo(String codigo) {
        return Objects.nonNull(this.obtenerPorCodigo(codigo));
    }

    @Lock(LockType.READ)
    public boolean existeOtroPorNombre(String nombre, String idPlataforma) {
        PlataformaVideoConferencia p = this.obtenerPorNombre(nombre);
        return (Objects.nonNull(p) && !p.getCodigo().equals(idPlataforma));
    }

    @Lock(LockType.READ)
    public boolean existeOtroPorCodigo(String codigo, String idPlataforma) {
        PlataformaVideoConferencia p = this.obtenerPorCodigo(codigo);
        return (Objects.nonNull(p) && !p.getCodigo().equals(idPlataforma));
    }

    @PostConstruct
    public void inicializar() {
        reInicilizar();
    }

    @Lock(LockType.WRITE)
    public void reInicilizar() {
        PLATAFORMAS.clear();
        PLATAFORMAS_MAPA.clear();
        PLATAFORMAS.addAll(this.obtenerActivas());
        for (PlataformaVideoConferencia p : PLATAFORMAS) {
            PLATAFORMAS_MAPA.put(p.getCodigo(), p);
        }
    }

    @Lock(LockType.READ)
    public PlataformaVideoConferencia obtenerPlataforma(String codigo) {
        return PLATAFORMAS_MAPA.get(codigo);
    }

    @Lock(LockType.READ)
    public List<PlataformaVideoConferencia> obtenerListaActivas() {
        return PLATAFORMAS;
    }
}
