package com.kaisernova.web.servlet;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import com.kaisernova.logica.almacenamiento.nube.AlmacenamientoS3Bean;
import com.kaisernova.logica.producto.ConfiguracionImagenesBean;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Serves product images: /imagenes/producto/PROD-001_01.jpg
 * Order: 1) S3 (OVH)  2) local directory  3) 404
 * Everything configurable comes from the DB through ConfiguracionImagenesBean.
 */
@WebServlet(urlPatterns = { ProductoImagenServlet.RUTA_URL + "*", ProductoImagenServlet.RUTA_URL })
public class ProductoImagenServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * URL prefix. NOT configurable from the DB: @WebServlet is resolved at deploy time.
	 * ImagenesProductoBean uses this same constant to build the links.
	 */
	public static final String RUTA_URL = "/imagenes/producto/";

	@EJB
	private AlmacenamientoS3Bean s3;
	@Inject
	private ConfiguracionImagenesBean cfg;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String nombre = extraerNombreArchivo(req);
		if (nombre.isBlank() || nombre.contains("..") || nombre.contains("/") || nombre.contains("\\")) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if (!nombreValido(nombre)) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String mime = URLConnection.guessContentTypeFromName(nombre);
		if (mime == null || mime.isBlank()) {
			mime = getServletContext().getMimeType(nombre);
		}
		if (mime == null || mime.isBlank()) {
			mime = "application/octet-stream";
		}

		byte[] datos = null;
		if (cfg.habilitarS3()) {
			Optional<byte[]> remota = s3.obtenerImagen(cfg.prefijoS3() + nombre);
			if (remota.isPresent()) {
				datos = remota.get();
				resp.setHeader("Cache-Control", "public, max-age=" + cfg.cacheS3Segundos());
				resp.setContentType(mime);
				resp.setContentLength(datos.length);
				resp.getOutputStream().write(datos);
				return;
			}
		}

		Path dirLocal = Paths.get(cfg.dirLocal()).toAbsolutePath().normalize();
		Path local = dirLocal.resolve(nombre).normalize();
		if (local.startsWith(dirLocal) && Files.isRegularFile(local)) {
			resp.setHeader("Cache-Control", "public, max-age=" + cfg.cacheLocalSegundos());
			resp.setContentType(mime);
			resp.setContentLengthLong(Files.size(local));
			Files.copy(local, resp.getOutputStream());
			return;
		}

		resp.setHeader("Cache-Control", "no-cache");
		resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	private String extraerNombreArchivo(HttpServletRequest req) {
		String nombre = req.getPathInfo();
		if (nombre == null || nombre.isBlank()) {
			nombre = req.getServletPath();
		}
		if (nombre == null || nombre.isBlank()) {
			String uri = req.getRequestURI();
			if (uri != null && !uri.isBlank()) {
				int ultimoSlash = uri.lastIndexOf('/');
				if (ultimoSlash >= 0 && ultimoSlash < uri.length() - 1) {
					nombre = uri.substring(ultimoSlash + 1);
				}
			}
		}
		if (nombre != null) {
			nombre = nombre.trim();
			if (nombre.startsWith("/")) {
				nombre = nombre.substring(1);
			}
		}
		return nombre == null ? "" : nombre;
	}

	private boolean nombreValido(String nombre) {
		if (nombre == null || nombre.isBlank()) {
			return false;
		}
		try {
			return cfg.patronNombreValido().matcher(nombre).matches();
		} catch (Exception e) {
			return nombre.matches("^[A-Za-z0-9_-][A-Za-z0-9_.-]*\\.(jpg|jpeg|png|webp|gif|mp4|webm|m4v|mov|mp3|wav|ogg|m4a|aac)$");
		}
	}
}