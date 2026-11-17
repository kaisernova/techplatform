package com.kaisernova.web.controlador.producto;

import java.io.Serializable;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.kaisernova.logica.producto.CategoriaProductosBean;
import com.kaisernova.logica.producto.ProductoEnMemoriaBean;
import com.kaisernova.modelo.producto.CategoriaProductos;
import com.kaisernova.modelo.producto.Producto;
import com.kaisernova.web.controlador.BaseControlador;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@ViewScoped
public class ProductoControlador extends BaseControlador implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(ProductoControlador.class.getName());

	@Inject
	private ProductoEnMemoriaBean productoEnMemoriaBean;

	@Inject
	private CategoriaProductosBean categoriaProductosBean;

	private Long idProducto;
	private Producto producto;

	@PostConstruct
	public void inicializar() {
		LOG.info("[ProductoControlador] inicializar() -> requestParam id=" + obtenerRequestId());
		cargarProducto();
	}

	private void cargarProducto() {
		if (idProducto == null) {
			String valorId = obtenerRequestId();
			if (valorId != null && !valorId.isBlank()) {
				try {
					idProducto = Long.valueOf(valorId);
					LOG.info("[ProductoControlador] idProducto seteado desde request: " + idProducto);
				} catch (NumberFormatException e) {
					idProducto = null;
					LOG.log(Level.WARNING, "[ProductoControlador] id no numerico: " + valorId, e);
				}
			}
		}
		producto = idProducto == null ? null : productoEnMemoriaBean.obtenerPorId(idProducto);
		if (producto == null) {
			LOG.warning("[ProductoControlador] producto no encontrado para idProducto=" + idProducto);
			fijarMensajeError("Producto no encontrado", "No se pudo cargar el producto solicitado.");
		} else {
			LOG.info("[ProductoControlador] producto cargado: id=" + producto.getIdProducto() + ", codigo=" + producto.getCodigo() + ", nombre=" + producto.getNombre());
		}
	}

	private String obtenerRequestId() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext == null || facesContext.getExternalContext() == null) {
			return null;
		}
		return facesContext.getExternalContext().getRequestParameterMap().get("id");
	}

	public String getPrecioFormateado() {
		if (producto == null || producto.getPrecio() == null) {
			return "Consultar";
		}
		return String.format(Locale.US, "$%.2f", producto.getPrecio());
	}

	public String getNombreCategoria() {
		if (producto == null || producto.getCodigoCategoria() == null) {
			return "Sin categoría";
		}
		return categoriaProductosBean.obtenerTodosActivos().stream()
				.filter(c -> producto.getCodigoCategoria().equals(c.getCodigo()))
				.map(CategoriaProductos::getNombre)
				.findFirst()
				.orElse("Sin categoría");
	}

	public Long getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(Long idProducto) {
		LOG.info("[ProductoControlador] setIdProducto(" + idProducto + ")");
		this.idProducto = idProducto;
		cargarProducto();
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}
}