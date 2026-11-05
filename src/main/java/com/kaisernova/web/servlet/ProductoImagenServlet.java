package com.kaisernova.web.servlet;

import java.io.IOException;
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
@WebServlet(urlPatterns = ProductoImagenServlet.RUTA_URL + "*")
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
		String pathInfo = req.getPathInfo();
		String nombre = (pathInfo == null || pathInfo.length() < 2) ? "" : pathInfo.substring(1);

		// Hard-coded safety checks: they apply even if the DB regex is too permissive.
		if (nombre.isEmpty() || nombre.contains("..") || nombre.contains("/") || nombre.contains("\\")
				|| !cfg.patronNombreValido().matcher(nombre).matches()) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String mime = getServletContext().getMimeType(nombre);
		if (mime == null) {
			mime = "application/octet-stream";
		}

		// 1) S3 first (never throws; empty = not found or S3 down)
		Optional<byte[]> remota = s3.obtenerImagen(cfg.prefijoS3() + nombre);
		if (remota.isPresent()) {
			byte[] datos = remota.get();
			resp.setHeader("Cache-Control", "public, max-age=" + cfg.cacheS3Segundos());
			resp.setContentType(mime);
			resp.setContentLength(datos.length);
			resp.getOutputStream().write(datos);
			return;
		}

		// 2) local fallback
		Path dirLocal = Paths.get(cfg.dirLocal()).toAbsolutePath().normalize();
		Path local = dirLocal.resolve(nombre).normalize();
		if (local.startsWith(dirLocal) && Files.isRegularFile(local)) {
			// shorter cache: when S3 comes back, clients should pick it up soon
			resp.setHeader("Cache-Control", "public, max-age=" + cfg.cacheLocalSegundos());
			resp.setContentType(mime);
			resp.setContentLengthLong(Files.size(local));
			Files.copy(local, resp.getOutputStream());
			return;
		}

		// 3) not found anywhere
		resp.setHeader("Cache-Control", "no-cache");
		resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}
}