package com.kaisernova.logica.producto;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

import com.kaisernova.modelo.producto.CategoriaProductos;

@Singleton
@Startup
public class CategoriaProductosEnMemoriaBean {
	private final static Map<Long, CategoriaProductos> CATEGORIA_PRODUCTOS_MAP = new ConcurrentHashMap<>();
	
	@Inject
	private Logger logger;
	
	@Inject
	private CategoriaProductosBean categoriaProductosBean;
	
	
	@PostConstruct
	public void init() {
		recargar();
	}
	
	@Lock(LockType.WRITE)
	public void recargar() {
		logger.info("[recargar] POR RECARGAR CATEGORIA PRODUCTOS");
		CATEGORIA_PRODUCTOS_MAP.clear();
		var categorias = categoriaProductosBean.obtenerTodos();
		for (CategoriaProductos categoria : categorias) {
			CATEGORIA_PRODUCTOS_MAP.put(categoria.getIdCategoriaProductos(), categoria);
		}
		logger.info("[recargar] CATEGORIA_PRODUCTOS_MAP=" + CATEGORIA_PRODUCTOS_MAP);
	}
	
	
	@Lock(LockType.READ)
	public CategoriaProductos obtenerPorId(Long id) {
		return CATEGORIA_PRODUCTOS_MAP.get(id);
	}
	
	@Lock(LockType.READ)
	public Collection<CategoriaProductos> obtenerTodos() {
		return CATEGORIA_PRODUCTOS_MAP.values();
	}
	
	@Lock(LockType.READ)
	public CategoriaProductos obtenerPorCodigo(String codigo) {
		if (codigo == null || codigo.isEmpty()) {
			return null;
		}
		return CATEGORIA_PRODUCTOS_MAP.values().stream()
			.filter(cat -> codigo.equals(cat.getCodigo()))
			.findFirst()
			.orElse(null);
	}
	
	@Lock(LockType.READ)
	public CategoriaProductos obtenerPorNombre(String nombre) {
		if (nombre == null || nombre.isEmpty()) {
			return null;
		}
		return CATEGORIA_PRODUCTOS_MAP.values().stream()
			.filter(cat -> nombre.equals(cat.getNombre()))
			.findFirst()
			.orElse(null);
	}
	
	@Lock(LockType.READ)
	public int getTotalCategorias() {
		return CATEGORIA_PRODUCTOS_MAP.size();
	}
	
	@Lock(LockType.READ)
	public boolean contieneCategoriaConId(Long id) {
		return CATEGORIA_PRODUCTOS_MAP.containsKey(id);
	}

}
