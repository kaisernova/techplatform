package com.kaisernova.web.controlador.producto;

import java.io.Serializable;
import java.util.Locale;

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

	@Inject
	private ProductoEnMemoriaBean productoEnMemoriaBean;

	@Inject
	private CategoriaProductosBean categoriaProductosBean;

	private Long idProducto;
	private Producto producto;

	@PostConstruct
	public void inicializar() {
		cargarProducto();
	}

	private void cargarProducto() {
		if (idProducto == null) {
			String valorId = FacesContext.getCurrentInstance() != null
					? FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id")
					: null;
			if (valorId != null && !valorId.isBlank()) {
				try {
					idProducto = Long.valueOf(valorId);
				} catch (NumberFormatException e) {
					idProducto = null;
				}
			}
		}
		producto = idProducto == null ? null : productoEnMemoriaBean.obtenerPorId(idProducto);
		if (producto == null) {
			fijarMensajeError("Producto no encontrado", "No se pudo cargar el producto solicitado.");
		}
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