package com.kaisernova.logica.producto;


import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
 
import com.kaisernova.logica.configuracion.ConfiguracionEnMemoriaBean;
import com.kaisernova.modelo.enums.NombreParametroConfiguracion;
 
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
 
/**
 * Single place that reads the product-image settings from the DB (through
 * ConfiguracionEnMemoriaBean) and applies a default when the parameter is
 * missing or blank. Values are read on every call, so a config reload
 * (ConfiguracionEnMemoriaBean.recargar()) takes effect immediately.
 */
@ApplicationScoped
public class ConfiguracionImagenesBean {
 
	public static final String DEF_DIR_LOCAL = "/opt/app/imagenes/productos";
	public static final String DEF_PLACEHOLDER = "/resources/img/sin-imagen.png";
	public static final String DEF_PREFIJO_S3 = "productos/";
	public static final String DEF_PATRON_NOMBRE = "^[A-Za-z0-9_-][A-Za-z0-9_.-]*\\.(jpg|jpeg|png|webp|gif)$";
	public static final int DEF_CACHE_S3_SEG = 86_400;
	public static final int DEF_CACHE_LOCAL_SEG = 300;
 
	/** Pattern + the text it was compiled from, swapped atomically. */
	private record PatronCache(String texto, Pattern patron) {
	}
 
	@Inject
	private Logger logger;
	@Inject
	private ConfiguracionEnMemoriaBean config;
 
	private volatile PatronCache patronCache;
 
	public String dirLocal() {
		return texto(NombreParametroConfiguracion.IMAGENES_DIR_LOCAL, DEF_DIR_LOCAL);
	}
 
	/** Always starts with "/". */
	public String placeholder() {
		String valor = texto(NombreParametroConfiguracion.IMAGENES_PLACEHOLDER, DEF_PLACEHOLDER);
		return valor.startsWith("/") ? valor : "/" + valor;
	}
 
	/** Always ends with "/". */
	public String prefijoS3() {
		String valor = texto(NombreParametroConfiguracion.S3_PREFIJO_IMAGENES_PRODUCTO, DEF_PREFIJO_S3);
		return valor.endsWith("/") ? valor : valor + "/";
	}
 
	public int cacheS3Segundos() {
		return entero(NombreParametroConfiguracion.IMAGENES_CACHE_S3_SEGUNDOS, DEF_CACHE_S3_SEG);
	}
 
	public int cacheLocalSegundos() {
		return entero(NombreParametroConfiguracion.IMAGENES_CACHE_LOCAL_SEGUNDOS, DEF_CACHE_LOCAL_SEG);
	}
 
	/** Compiled regex for valid image names; recompiled only when the DB value changes. */
	public Pattern patronNombreValido() {
		String texto = texto(NombreParametroConfiguracion.IMAGENES_PATRON_NOMBRE_VALIDO, DEF_PATRON_NOMBRE);
		PatronCache actual = patronCache;
		if (actual != null && actual.texto().equals(texto)) {
			return actual.patron();
		}
		Pattern nuevo;
		try {
			nuevo = Pattern.compile(texto);
		} catch (PatternSyntaxException e) {
			logger.log(Level.SEVERE, "[patronNombreValido] regex invalida en configuracion [" + texto
					+ "], se usa el patron por defecto: " + e.getMessage());
			texto = DEF_PATRON_NOMBRE;
			nuevo = Pattern.compile(DEF_PATRON_NOMBRE);
		}
		patronCache = new PatronCache(texto, nuevo);
		return nuevo;
	}
 
	private String texto(NombreParametroConfiguracion parametro, String defecto) {
		String valor = config.getValue(parametro);
		return (valor == null || valor.isBlank()) ? defecto : valor.trim();
	}
 
	private int entero(NombreParametroConfiguracion parametro, int defecto) {
		try {
			int n = Integer.parseInt(texto(parametro, String.valueOf(defecto)));
			return n >= 0 ? n : defecto;
		} catch (NumberFormatException e) {
			return defecto;
		}
	}
}
 