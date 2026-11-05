package com.kaisernova.modelo.enums;

public enum NombreParametroConfiguracion {
	TITULO,
	EMAIL_SIN_EMAIL_REGISTRADO,	
	MAXIMO_REGISTROS_MOSTRAR_DEFECTO,
	AMBIENTE_PRODUCTIVO,
	
	MAIL_SERVER,
	MAIL_PORT,
	MAIL_USER,
	MAIL_PASSWORD,
	MAIL_AUTH,
	MAIL_ADMIN,
	MAIL_SSL,
	MAIL_REMITENTE,
	MAIL_NOMBRE_REMITENTE,
	MAIL_SMTP_STARTTLS_ENABLE,
	
	MAIL_ASUNTO_GENERAL,

	MAIL_MENSAJE_RECUPERACION_CLAVE,

	ASUNTO_GENERAL,
	MENSAJE_GENERAL,
	ASUNTO_ERROR,
	MENSAJE_ERROR,	
	TELEFONO_CONTACTO,
	EMAIL_CONTACTO,
	URL_CAPTCHA_CLIENTE,
	URL_CAPTCHA_SERVER,
	ACTIVAR_CAPTCHA,
	CAPTCHA_SITE_KEY,
	CAPTCHA_SECRET_KEY,
	CAPTCHA_MIN_SCORE,
	WHATSAPP_URL_BASE,
	WHATSAPP_ACTIVAR,
	WHATSAPP_NUMERO_CONTACTO,
	WHATSAPP_MENSAJE_DEFECTO,

	TERMINOS_Y_CONDICIONES,
	METODOS_PAGO_HABILITADOS,
	SEPARADOR_CODIGO_CATEGORIA_PRODUCTO,
	// ---- S3 (OVH) ----
	S3_ACCESS_KEY,
	S3_SECRET_KEY,
	S3_ENDPOINT_URL,
	S3_REGION,
	S3_BUCKET,
	/** NEW. SI/NO: path-style access (needed by some S3-compatible providers). Default NO */
	S3_PATH_STYLE_ACCESS,
	/** NEW. Key prefix inside the bucket for product images. Default: productos/ */
	S3_PREFIJO_IMAGENES_PRODUCTO,
	/** NEW. Connection timeout in ms. Default: 2000 */
	S3_TIMEOUT_CONEXION_MS,
	/** NEW. Socket (read) timeout in ms. Default: 3000 */
	S3_TIMEOUT_SOCKET_MS,
	/** NEW. Whole-request timeout in ms. Default: 8000 */
	S3_TIMEOUT_PETICION_MS,
	/** NEW. After an S3 failure, skip S3 for this many ms (circuit breaker). Default: 30000 */
	S3_PAUSA_CAIDO_MS,
 
	// ---- Product images ----
	/** NEW. Local fallback directory (outside the war). Default: /opt/app/imagenes/productos */
	IMAGENES_DIR_LOCAL,
	/** NEW. Placeholder URL relative to the context path. Default: /resources/img/sin-imagen.png */
	IMAGENES_PLACEHOLDER,
	/** NEW. Regex that image file names must match. Default: see ConfiguracionImagenesBean */
	IMAGENES_PATRON_NOMBRE_VALIDO,
	/** NEW. Cache-Control max-age (seconds) for images served from S3. Default: 86400 */
	IMAGENES_CACHE_S3_SEGUNDOS,
	/** NEW. Cache-Control max-age (seconds) for images served from the local fallback. Default: 300 */
	IMAGENES_CACHE_LOCAL_SEGUNDOS,
	
	;
	;
	
}