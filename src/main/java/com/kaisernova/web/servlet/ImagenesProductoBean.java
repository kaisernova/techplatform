package com.kaisernova.web.servlet;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.kaisernova.logica.producto.ConfiguracionImagenesBean;
import com.kaisernova.modelo.enums.TipoMultimedia;
import com.kaisernova.modelo.producto.MultimediaProducto;
import com.kaisernova.modelo.producto.Producto;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Builds image URLs from the names stored in Producto.multimediaProductoJson
 * (MultimediaProducto.referenciasMultimedia[IMG]).
 *
 * EL usage:
 *   #{imagenesBean.principal(prod)}   -> first image URL (or placeholder)
 *   #{imagenesBean.urls(prod)}        -> all image URLs (for p:galleria)
 *   #{imagenesBean.placeholder()}     -> placeholder URL (for onerror)
 */
@Named("imagenesBean")
@ApplicationScoped
public class ImagenesProductoBean {

	@Inject
	private ConfiguracionImagenesBean cfg;

	/** Image names of the product, sorted alphabetically (PROD-001_01 < PROD-001_02 ...). */
	public List<String> nombres(Producto producto) {
		if (producto == null) {
			return List.of();
		}
		MultimediaProducto multimedia = producto.getMultimediaProducto(); // parses the JSON
		if (multimedia == null || multimedia.getReferenciasMultimedia() == null) {
			return List.of();
		}
		Set<String> imagenes = multimedia.getReferenciasMultimedia().get(TipoMultimedia.IMG);
		if (imagenes == null) {
			return List.of();
		}
		// The JSON is deserialized into a Set (no order guaranteed), so sort here.
		return imagenes.stream()
				.filter(Objects::nonNull)
				.filter(n -> !n.isBlank())
				.sorted()
				.toList();
	}

	public List<String> urls(Producto producto) {
		return nombres(producto).stream().map(this::url).toList();
	}

	public String principal(Producto producto) {
		List<String> nombres = nombres(producto);
		return nombres.isEmpty() ? placeholder() : url(nombres.get(0));
	}

	public boolean tieneImagenes(Producto producto) {
		return !nombres(producto).isEmpty();
	}

	public String placeholder() {
		return contexto() + cfg.placeholder();
	}

	private String url(String nombreArchivo) {
		return contexto() + ProductoImagenServlet.RUTA_URL + URLEncoder.encode(nombreArchivo, StandardCharsets.UTF_8);
	}

	private String contexto() {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
	}
}