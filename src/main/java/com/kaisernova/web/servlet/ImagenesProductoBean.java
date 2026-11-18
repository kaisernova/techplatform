package com.kaisernova.web.servlet;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	private static final Logger LOG = Logger.getLogger(ImagenesProductoBean.class.getName());

	@Inject
	private ConfiguracionImagenesBean cfg;

	/** Image names of the product, sorted alphabetically (PROD-001_01 < PROD-001_02 ...). */
	public List<String> nombres(Producto producto) {
		if (producto == null) {
			LOG.warning("[ImagenesProductoBean] producto null al calcular nombres");
			return List.of();
		}
		try {
			MultimediaProducto multimedia = producto.getMultimediaProducto();
			if (multimedia == null || multimedia.getReferenciasMultimedia() == null) {
				LOG.warning("[ImagenesProductoBean] sin multimedia para producto id=" + producto.getIdProducto() + ", codigo=" + producto.getCodigo());
				return List.of();
			}
			Set<String> imagenes = multimedia.getReferenciasMultimedia().get(TipoMultimedia.IMG);
			if (imagenes == null) {
				LOG.warning("[ImagenesProductoBean] sin referencias IMG para producto id=" + producto.getIdProducto());
				return List.of();
			}
			List<String> result = imagenes.stream()
					.filter(Objects::nonNull)
					.filter(n -> !n.isBlank())
					.sorted()
					.toList();
			LOG.info("[ImagenesProductoBean] nombres detectados para producto " + producto.getIdProducto() + ": " + result);
			return result;
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "[ImagenesProductoBean] error leyendo multimedia para producto id=" + producto.getIdProducto(), e);
			return List.of();
		}
	}

	public List<String> urls(Producto producto) {
		List<String> nombres = nombres(producto);
		List<String> urls = nombres.stream().map(this::url).toList();
		LOG.info("[ImagenesProductoBean] urls generadas para producto id=" + (producto == null ? null : producto.getIdProducto()) + ": " + urls);
		return urls;
	}

	public String principal(Producto producto) {
		List<String> nombres = nombres(producto);
		if (nombres.isEmpty()) {
			String valor = placeholder();
			LOG.info("[ImagenesProductoBean] principal para producto id=" + (producto == null ? null : producto.getIdProducto()) + ": " + valor);
			return valor;
		}
		String primera = nombres.get(0);
		String thumb = obtenerNombreThumbnail(primera);
		String valor = url(thumb);
		LOG.info("[ImagenesProductoBean] principal/thum para producto id=" + (producto == null ? null : producto.getIdProducto()) + ": " + valor);
		return valor;
	}

	public boolean tieneImagenes(Producto producto) {
		boolean tiene = !nombres(producto).isEmpty();
		LOG.info("[ImagenesProductoBean] tieneImagenes(productoId=" + (producto == null ? null : producto.getIdProducto()) + ") = " + tiene);
		return tiene;
	}

	public String placeholder() {
		String contextPath = contexto();
		String valor = contextPath == null ? cfg.placeholder() : contextPath + cfg.placeholder();
		LOG.info("[ImagenesProductoBean] placeholder=" + valor);
		return valor;
	}

	private String url(String nombreArchivo) {
		String contextPath = contexto();
		String base = contextPath == null ? "" : contextPath;
		String url = base + ProductoImagenServlet.RUTA_URL + URLEncoder.encode(nombreArchivo, StandardCharsets.UTF_8);
		LOG.info("[ImagenesProductoBean] url archivo " + nombreArchivo + " -> " + url);
		return url;
	}

	private String contexto() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext == null || facesContext.getExternalContext() == null) {
			LOG.warning("[ImagenesProductoBean] FacesContext nulo al construir contexto de URL");
			return null;
		}
		String contextPath = facesContext.getExternalContext().getRequestContextPath();
		LOG.info("[ImagenesProductoBean] contextPath=" + contextPath);
		return contextPath;
	}

	private String obtenerNombreThumbnail(String nombreOriginal) {
		if (nombreOriginal == null) return "";
		int dot = nombreOriginal.lastIndexOf('.');
		if (dot <= 0) return nombreOriginal;
		String base = nombreOriginal.substring(0, dot);
		String extension = nombreOriginal.substring(dot);
		return base + "_thumb" + extension;
	}
}