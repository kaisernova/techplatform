package com.kaisernova.web.servlet;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	private static final Logger LOG = Logger.getLogger(ProductoImagenServlet.class.getName());
	private static final String DEFAULT_MEDIA_PATTERN = "^[A-Za-z0-9_-][A-Za-z0-9_.-]*\\.(jpg|jpeg|png|webp|gif|mp4|webm|m4v|mov|mp3|wav|ogg|m4a|aac)$";

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
		String uri = req.getRequestURI();
		String nombre = extraerNombreArchivo(req);
		LOG.info("[ProductoImagenServlet] requestURI=" + uri + ", pathInfo=" + req.getPathInfo() + ", servletPath=" + req.getServletPath() + ", nombreExtraido=" + nombre);

		if (!esNombreSeguridad(nombre)) {
			LOG.warning("[ProductoImagenServlet] nombre invalido o inseguro: uri=" + uri + ", nombre=" + nombre);
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if (!nombreValido(nombre)) {
			LOG.warning("[ProductoImagenServlet] nombre no valido para patron configurado: nombre=" + nombre + ", patron=" + obtenerPatronConfigurado());
			// No devolvemos 400 si el nombre es seguro pero no encaja con el patrón de configuración.
			// Esto evita falsos positivos cuando la config aún no ha cargado o el valor no es válido.
			// Se intenta servir el archivo directamente desde el directorio local y, si no existe, se responde 404.
		}

		String mime = URLConnection.guessContentTypeFromName(nombre);
		if (mime == null || mime.isBlank()) {
			mime = getServletContext().getMimeType(nombre);
		}
		if (mime == null || mime.isBlank()) {
			mime = "application/octet-stream";
		}

		try {
			if (cfg != null && cfg.habilitarS3()) {
				String claveS3 = cfg.prefijoS3() + nombre;
				LOG.info("[ProductoImagenServlet] buscando en S3: " + claveS3);
				Optional<byte[]> remota = s3.obtenerImagen(claveS3);
				if (!remota.isPresent() && nombre.contains("_thumb.")) {
					String original = nombre.replaceFirst("_thumb(?=\\.[^.]+$)", "");
					String claveOriginal = cfg.prefijoS3() + original;
					LOG.info("[ProductoImagenServlet] thumbnail no encontrado en S3; intentando original: " + claveOriginal);
					remota = s3.obtenerImagen(claveOriginal);
				}
				if (remota.isPresent()) {
					byte[] datos = remota.get();
					resp.setHeader("Cache-Control", "public, max-age=" + cfg.cacheS3Segundos());
					resp.setContentType(mime);
					resp.setContentLength(datos.length);
					resp.getOutputStream().write(datos);
					LOG.info("[ProductoImagenServlet] servido desde S3: " + nombre + ", bytes=" + datos.length);
					return;
				}
				LOG.warning("[ProductoImagenServlet] no encontrado en S3: " + claveS3);
			} else {
				LOG.info("[ProductoImagenServlet] S3 deshabilitado; usando local fallback");
			}

			Path dirLocal = Paths.get(cfg != null ? cfg.dirLocal() : "D:/temp/imagenes/productos").toAbsolutePath().normalize();
			Path local = dirLocal.resolve(nombre).normalize();
			if (!(local.startsWith(dirLocal) && Files.isRegularFile(local)) && nombre.contains("_thumb.")) {
				String original = nombre.replaceFirst("_thumb(?=\\.[^.]+$)", "");
				local = dirLocal.resolve(original).normalize();
				LOG.info("[ProductoImagenServlet] thumbnail no encontrado local; intentando original: " + local);
			}
			LOG.info("[ProductoImagenServlet] comprobando local: dir=" + dirLocal + ", archivo=" + local + ", existe=" + Files.exists(local));
			if (local.startsWith(dirLocal) && Files.isRegularFile(local)) {
				resp.setHeader("Cache-Control", "public, max-age=" + cfg.cacheLocalSegundos());
				resp.setContentType(mime);
				resp.setContentLengthLong(Files.size(local));
				Files.copy(local, resp.getOutputStream());
				LOG.info("[ProductoImagenServlet] servido desde disco local: " + local);
				return;
			}

			LOG.warning("[ProductoImagenServlet] archivo no encontrado: nombre=" + nombre + ", dirLocal=" + dirLocal + ", existeDir=" + Files.exists(dirLocal));
			resp.setHeader("Cache-Control", "no-cache");
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "[ProductoImagenServlet] error al servir archivo: " + nombre, e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private String obtenerPatronConfigurado() {
		try {
			if (cfg == null) {
				return DEFAULT_MEDIA_PATTERN;
			}
			return cfg.patronNombreValido().pattern();
		} catch (Exception e) {
			return DEFAULT_MEDIA_PATTERN;
		}
	}

	private boolean esNombreSeguridad(String nombre) {
		if (nombre == null || nombre.isBlank()) {
			return false;
		}
		if (nombre.contains("..") || nombre.contains("/") || nombre.contains("\\")) {
			return false;
		}
		return true;
	}

	private String extraerNombreArchivo(HttpServletRequest req) {
		String nombre = req.getPathInfo();
		if (nombre != null && !nombre.isBlank()) {
			nombre = nombre.trim();
			if (nombre.startsWith("/")) {
				nombre = nombre.substring(1);
			}
			if (!nombre.isBlank()) {
				return nombre;
			}
		}

		String uri = req.getRequestURI();
		if (uri != null && !uri.isBlank()) {
			String contextPath = req.getContextPath();
			if (contextPath != null && !contextPath.isBlank() && uri.startsWith(contextPath)) {
				uri = uri.substring(contextPath.length());
			}
			if (uri.startsWith(RUTA_URL)) {
				String restante = uri.substring(RUTA_URL.length());
				return restante == null ? "" : restante;
			}
			int ultimoSlash = uri.lastIndexOf('/');
			if (ultimoSlash >= 0 && ultimoSlash < uri.length() - 1) {
				return uri.substring(ultimoSlash + 1);
			}
		}

		String servletPath = req.getServletPath();
		if (servletPath != null && !servletPath.isBlank()) {
			String clean = servletPath.trim();
			return clean.startsWith("/") ? clean.substring(1) : clean;
		}
		return "";
	}

	private boolean nombreValido(String nombre) {
		if (nombre == null || nombre.isBlank()) {
			return false;
		}
		try {
			if (cfg != null && cfg.patronNombreValido() != null) {
				return cfg.patronNombreValido().matcher(nombre).matches();
			}
		} catch (Exception e) {
			LOG.log(Level.WARNING, "[ProductoImagenServlet] patron invalido; usando regex por defecto", e);
		}
		return nombre.matches(DEFAULT_MEDIA_PATTERN);
	}
}