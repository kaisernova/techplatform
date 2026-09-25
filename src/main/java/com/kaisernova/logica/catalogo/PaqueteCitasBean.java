package com.kaisernova.logica.catalogo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;

import com.kaisernova.dao.catalogo.PaqueteCitasDao;
import com.kaisernova.modelo.catalogo.PaqueteCitas;
import com.kaisernova.modelo.enums.TipoPaqueteCita;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

@Singleton
@Startup
public class PaqueteCitasBean {

    private final static Map<String, PaqueteCitas> PAQUETES_MAP = new ConcurrentSkipListMap<>();
    private final static Map<TipoPaqueteCita, List<PaqueteCitas>> PAQUETES_TIPO_PAQUETE_MAP = new ConcurrentSkipListMap<>();
    private final static List<PaqueteCitas> PAQUETES = new ArrayList<>();

    @Inject
    private PaqueteCitasDao paqueteCitasDao;

    @Lock(LockType.WRITE)
    public PaqueteCitas crear(PaqueteCitas paquete) {
        PaqueteCitas registrado = paqueteCitasDao.create(paquete);
        reInicializar();
        return registrado;
    }

    @Lock(LockType.WRITE)
    public PaqueteCitas actualizar(PaqueteCitas paquete) {
        PaqueteCitas actualizado = paqueteCitasDao.update(paquete);
        reInicializar();
        return actualizado;
    }

    @Lock(LockType.READ)
    public List<PaqueteCitas> obtenerTodas() {
        return paqueteCitasDao.findAll();
    }

    @Lock(LockType.READ)
    public List<PaqueteCitas> obtenerActivas() {
        return paqueteCitasDao.findAllActivosOrderBy("numeroCitas", false);
    }

    @Lock(LockType.READ)
    public PaqueteCitas obtenerPorNombre(String nombre) {
        return paqueteCitasDao.findByNombre(nombre);
    }

    @Lock(LockType.READ)
    public PaqueteCitas obtenerPorCodigo(String codigo) {
        return paqueteCitasDao.findByCodigo(codigo);
    }


    @Lock(LockType.READ)
    public PaqueteCitas obtenerPorNumeroCitasYTipo(Integer numeroCitas, TipoPaqueteCita tipoPaqueteCita) {
        return paqueteCitasDao.findByNumeroCitasAndTipoPaqueteCita(numeroCitas, tipoPaqueteCita);
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
    public boolean existePorNumeroCitasYTipo(Integer numeroCitas, TipoPaqueteCita tipoPaqueteCita) {
        return Objects.nonNull(this.obtenerPorNumeroCitasYTipo(numeroCitas, tipoPaqueteCita));
    }

    @Lock(LockType.READ)
    public boolean existeOtroPorNombre(String nombre, String idPaquete) {
        PaqueteCitas paquete = this.obtenerPorNombre(nombre);
        return (Objects.nonNull(paquete) && !paquete.getCodigo().equals(idPaquete));
    }



    @Lock(LockType.READ)
    public boolean existeOtroPorNumeroCitasYTipo(Integer numeroCitas, TipoPaqueteCita tipoPaqueteCita, String idPaquete) {
        PaqueteCitas paquete = this.obtenerPorNumeroCitasYTipo(numeroCitas, tipoPaqueteCita);
        return (Objects.nonNull(paquete) && !paquete.getCodigo().equals(idPaquete));
    }

    @PostConstruct
    public void inicializar() {
        reInicializar();
    }

    @Lock(LockType.WRITE)
    public void reInicializar() {
        PAQUETES.clear();
        PAQUETES_MAP.clear();
        PAQUETES_TIPO_PAQUETE_MAP.clear();
        List<PaqueteCitas> activos = this.obtenerActivas();
        PAQUETES.addAll(activos);
        for (PaqueteCitas p : activos) {
            PAQUETES_MAP.put(p.getCodigo(), p);
            PAQUETES_TIPO_PAQUETE_MAP.computeIfAbsent(p.getTipoPaqueteCita(), k -> new ArrayList<>()).add(p);
        }
    }

    @Lock(LockType.READ)
    public PaqueteCitas obtenerPaquete(String codigo) {
        return PAQUETES_MAP.get(codigo);
    }
    
    @Lock(LockType.READ)
    public List<PaqueteCitas> obtenerPaquetesPorTipoPaqueteCita(TipoPaqueteCita tipoPaqueteCita) {
        return PAQUETES_TIPO_PAQUETE_MAP.getOrDefault(tipoPaqueteCita, new ArrayList<>());
    }

    @Lock(LockType.READ)
    public List<PaqueteCitas> obtenerListaActivas() {
        return PAQUETES;
    }

    // Compatibility methods used by existing controller (English names)
    @Lock(LockType.READ)
    public List<PaqueteCitas> getAll() {
        return obtenerListaActivas();
    }

    @Lock(LockType.WRITE)
    public void refresh() {
        reInicializar();
    }

    @Lock(LockType.READ)
    public boolean existsCodigo(String codigo) {
        return existePorCodigo(codigo);
    }

    @Lock(LockType.READ)
    public boolean existsNombre(String nombre, String excludeCodigo) {
        if (excludeCodigo == null) {
            return existePorNombre(nombre);
        }
        return existeOtroPorNombre(nombre, excludeCodigo);
    }


    @Lock(LockType.READ)
    public boolean existsNumeroCitasAndTipo(Integer numeroCitas, TipoPaqueteCita tipoPaqueteCita, String excludeCodigo) {
        if (excludeCodigo == null) {
            return existePorNumeroCitasYTipo(numeroCitas, tipoPaqueteCita);
        }
        return existeOtroPorNumeroCitasYTipo(numeroCitas, tipoPaqueteCita, excludeCodigo);
    }
}