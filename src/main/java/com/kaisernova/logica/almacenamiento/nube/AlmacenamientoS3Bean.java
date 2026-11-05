package com.kaisernova.logica.almacenamiento.nube;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.kaisernova.logica.configuracion.ConfiguracionEnMemoriaBean;
import com.kaisernova.modelo.enums.NombreParametroConfiguracion;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.DependsOn;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

/**
 * S3 (OVH) access for product images. All settings come from the DB through
 * ConfiguracionEnMemoriaBean. Call recargar() after reloading the configuration.
 */
@Singleton
@Startup
@DependsOn("ConfiguracionEnMemoriaBean") // config must be loaded before we build the client
public class AlmacenamientoS3Bean {

	private static final int DEF_CONNECTION_TIMEOUT_MS = 2_000;
	private static final int DEF_SOCKET_TIMEOUT_MS = 3_000;
	private static final int DEF_REQUEST_TIMEOUT_MS = 8_000;
	private static final long DEF_PAUSA_CAIDO_MS = 30_000;

	@Inject
	private Logger logger;
	@Inject
	private ConfiguracionEnMemoriaBean config;

	private volatile String s3Bucket;
	private volatile AmazonS3 s3client;

	/** Circuit breaker: epoch millis until which S3 is considered down. 0 = healthy. */
	private volatile long s3CaidoHasta = 0;
	private volatile long pausaCaidoMs = DEF_PAUSA_CAIDO_MS;

	@PostConstruct
	public void inicializar() {
		recargar();
	}

	@Lock(LockType.WRITE)
	public void recargar() {
		try {
			String accessKey = config.getValue(NombreParametroConfiguracion.S3_ACCESS_KEY);
			String secretKey = config.getValue(NombreParametroConfiguracion.S3_SECRET_KEY);
			String endpoint = config.getValue(NombreParametroConfiguracion.S3_ENDPOINT_URL);
			String region = config.getValue(NombreParametroConfiguracion.S3_REGION);
			s3Bucket = config.getValue(NombreParametroConfiguracion.S3_BUCKET);

			if (esVacio(accessKey) || esVacio(secretKey) || esVacio(endpoint) || esVacio(s3Bucket)) {
				s3client = null; // no S3 configured -> everything falls back to local
				logger.warning("[recargar] S3 no configurado completamente; se usara solo almacenamiento local");
				return;
			}

			ClientConfiguration clientConfig = new ClientConfiguration()
					.withConnectionTimeout(entero(NombreParametroConfiguracion.S3_TIMEOUT_CONEXION_MS, DEF_CONNECTION_TIMEOUT_MS))
					.withSocketTimeout(entero(NombreParametroConfiguracion.S3_TIMEOUT_SOCKET_MS, DEF_SOCKET_TIMEOUT_MS))
					.withRequestTimeout(entero(NombreParametroConfiguracion.S3_TIMEOUT_PETICION_MS, DEF_REQUEST_TIMEOUT_MS))
					.withMaxErrorRetry(0); // no retries: fall back to local immediately

			s3client = AmazonS3ClientBuilder.standard()
					.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
					.withEndpointConfiguration(new EndpointConfiguration(endpoint, region))
					.withPathStyleAccessEnabled(config.getValueAsBoolean(NombreParametroConfiguracion.S3_PATH_STYLE_ACCESS))
					.withClientConfiguration(clientConfig)
					.build();

			pausaCaidoMs = entero(NombreParametroConfiguracion.S3_PAUSA_CAIDO_MS, (int) DEF_PAUSA_CAIDO_MS);
			s3CaidoHasta = 0;
		} catch (Exception e) {
			s3client = null;
			logger.log(Level.SEVERE, "[recargar]ERROR:" + e.getMessage(), e);
		}
	}

	/**
	 * Reads an image from S3 with a single request. Never throws.
	 *
	 * @return the bytes, or empty if the object does not exist OR S3 is unavailable.
	 */
	@Lock(LockType.READ)
	public Optional<byte[]> obtenerImagen(String nombreObjeto) {
		AmazonS3 client = s3client;
		if (client == null || System.currentTimeMillis() < s3CaidoHasta) {
			return Optional.empty();
		}
		try (S3Object obj = client.getObject(s3Bucket, nombreObjeto)) {
			return Optional.of(obj.getObjectContent().readAllBytes());
		} catch (AmazonS3Exception e) { // must be caught before SdkClientException (subclass)
			if (e.getStatusCode() == 404) {
				return Optional.empty(); // object doesn't exist; S3 is healthy
			}
			if (e.getStatusCode() >= 500) {
				marcarCaido(e);
			} else {
				logger.log(Level.WARNING, "[obtenerImagen] S3 status " + e.getStatusCode() + " para ["
						+ nombreObjeto + "]: " + e.getErrorMessage());
			}
			return Optional.empty();
		} catch (SdkClientException | IOException e) { // timeout, connection refused, DNS...
			marcarCaido(e);
			return Optional.empty();
		}
	}

	/**
	 * Uploads an image to S3.
	 *
	 * @return md5 of the saved object
	 * @throws IOException if S3 is not configured or the upload fails
	 */
	@Lock(LockType.READ)
	public String guardarImagen(String nombreObjeto, byte[] datos, String contentType) throws IOException {
		AmazonS3 client = s3client;
		if (client == null) {
			throw new IOException("S3 no configurado");
		}
		try {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(datos.length);
			metadata.setContentType(contentType);
			return client.putObject(s3Bucket, nombreObjeto, new ByteArrayInputStream(datos), metadata)
					.getContentMd5();
		} catch (SdkClientException e) {
			logger.log(Level.SEVERE, "[guardarImagen] " + e.getMessage(), e);
			throw new IOException(e.getMessage(), e);
		}
	}

	/** Deletes an image from S3 (no error if it doesn't exist). */
	@Lock(LockType.READ)
	public void eliminarImagen(String nombreObjeto) throws IOException {
		AmazonS3 client = s3client;
		if (client == null) {
			throw new IOException("S3 no configurado");
		}
		try {
			client.deleteObject(s3Bucket, nombreObjeto);
		} catch (SdkClientException e) {
			logger.log(Level.SEVERE, "[eliminarImagen] " + e.getMessage(), e);
			throw new IOException(e.getMessage(), e);
		}
	}

	private void marcarCaido(Exception e) {
		s3CaidoHasta = System.currentTimeMillis() + pausaCaidoMs;
		logger.log(Level.WARNING, "[S3] no disponible, se usa fallback local por " + pausaCaidoMs / 1000
				+ "s. Causa: " + e.getMessage());
	}

	private boolean esVacio(String valor) {
		return valor == null || valor.isBlank();
	}

	/** Reads a positive int from config; falls back to the default if missing/invalid/zero. */
	private int entero(NombreParametroConfiguracion parametro, int defecto) {
		int valor = config.getValueAsInteger(parametro); // returns 0 when missing/invalid
		return valor > 0 ? valor : defecto;
	}
}