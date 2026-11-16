-- Crear schemas y asignar propietario
CREATE SCHEMA IF NOT EXISTS auditoria AUTHORIZATION techplatform;
CREATE SCHEMA IF NOT EXISTS catalogo AUTHORIZATION techplatform;
CREATE SCHEMA IF NOT EXISTS configuracion AUTHORIZATION techplatform;
CREATE SCHEMA IF NOT EXISTS ubicacion_geografica AUTHORIZATION techplatform;
CREATE SCHEMA IF NOT EXISTS usuarios AUTHORIZATION techplatform;
CREATE SCHEMA IF NOT EXISTS productos AUTHORIZATION techplatform;
ALTER schema public  OWNER TO techplatform;

-- configuracion inserts
CREATE TABLE IF NOT EXISTS configuracion.configuracion (
    nombre_parametro VARCHAR(128) PRIMARY KEY,
    valor TEXT,
    descripcion VARCHAR(255),
    tipo_dato VARCHAR(32) NOT NULL,
    nombre_enum_valores VARCHAR(256),
    usuario_crea VARCHAR(128) NOT NULL DEFAULT 'system',
    usuario_actualiza VARCHAR(128) NOT NULL DEFAULT 'system',
    fecha_hora_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_hora_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

TRUNCATE TABLE configuracion.configuracion;

INSERT INTO configuracion.configuracion (
    nombre_parametro,
    valor,
    descripcion,
    tipo_dato,
    nombre_enum_valores,
    usuario_crea,
    usuario_actualiza,
    fecha_hora_creacion,
    fecha_hora_actualizacion,
    activo
) VALUES
    ('TITULO', 'TechPlatform', 'Título general de la aplicación', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('EMAIL_SIN_EMAIL_REGISTRADO', 'no-reply@techplatform.local', 'Email a mostrar cuando el usuario no tiene email registrado', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('MAXIMO_REGISTROS_MOSTRAR_DEFECTO', '20', 'Cantidad máxima de filas a mostrar por defecto en listados', 'INTEGER', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('AMBIENTE_PRODUCTIVO', 'PRUEBAS', 'Ambiente de ejecución de la aplicación', 'ENUM', 'AmbienteProductivo', 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),

    ('MAIL_SERVER', 'smtp.mailtrap.io', 'Servidor SMTP para envío de correos', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('MAIL_PORT', '2525', 'Puerto SMTP', 'INTEGER', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('MAIL_USER', 'demo-user', 'Usuario SMTP', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('MAIL_PASSWORD', 'demo-password', 'Clave SMTP', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('MAIL_AUTH', 'true', 'Si el servidor SMTP requiere autenticación', 'BOOLEAN', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('MAIL_ADMIN', 'admin@techplatform.local', 'Correo administrador', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('MAIL_SSL', 'false', 'Usar SSL en SMTP', 'BOOLEAN', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('MAIL_REMITENTE', 'noreply@techplatform.local', 'Email remitente por defecto', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('MAIL_NOMBRE_REMITENTE', 'TechPlatform', 'Nombre remitente', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('MAIL_SMTP_STARTTLS_ENABLE', 'true', 'Habilita STARTTLS para SMTP', 'BOOLEAN', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),

    ('MAIL_ASUNTO_GENERAL', 'Información TechPlatform', 'Asunto general para correos del sistema', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('MAIL_MENSAJE_RECUPERACION_CLAVE', 'Para recuperar tu clave, haz click en el siguiente enlace.', 'Mensaje de recuperación de clave', 'LONGTEXT', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),

    ('ASUNTO_GENERAL', 'Información', 'Asunto general del portal', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('MENSAJE_GENERAL', 'Gracias por visitar TechPlatform.', 'Mensaje general para la aplicación', 'LONGTEXT', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('ASUNTO_ERROR', 'Error en la aplicación', 'Asunto para mensajes de error', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('MENSAJE_ERROR', 'Ha ocurrido un error inesperado. Intente nuevamente.', 'Texto de error general', 'LONGTEXT', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('TELEFONO_CONTACTO', '+56900000000', 'Teléfono de contacto del negocio', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('EMAIL_CONTACTO', 'contacto@techplatform.local', 'Correo de contacto del negocio', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('URL_CAPTCHA_CLIENTE', 'https://www.google.com/recaptcha/api.js', 'URL pública del captcha', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('URL_CAPTCHA_SERVER', 'https://www.google.com/recaptcha/api/siteverify', 'URL del backend del captcha', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('ACTIVAR_CAPTCHA', 'false', 'Habilita validación con captcha', 'BOOLEAN', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('CAPTCHA_SITE_KEY', 'demo-site-key', 'Clave pública del captcha', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('CAPTCHA_SECRET_KEY', 'demo-secret-key', 'Clave privada del captcha', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('CAPTCHA_MIN_SCORE', '0.5', 'Score mínimo requerido por CAPTCHA', 'BIGDECIMAL', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('WHATSAPP_URL_BASE', 'https://wa.me/', 'Base URL para WhatsApp', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('WHATSAPP_ACTIVAR', 'true', 'Habilita integración de WhatsApp', 'BOOLEAN', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('WHATSAPP_NUMERO_CONTACTO', '56900000000', 'Número de contacto para WhatsApp', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('WHATSAPP_MENSAJE_DEFECTO', 'Hola, quiero más información sobre este producto', 'Mensaje inicial de WhatsApp', 'LONGTEXT', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),

    ('TERMINOS_Y_CONDICIONES', '<h1>Términos y condiciones</h1><p>Texto de ejemplo para pruebas.</p>', 'Texto de términos y condiciones', 'HTML', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('METODOS_PAGO_HABILITADOS', 'TRANSFERENCIA,CREDITO,DEBITO', 'Lista de métodos de pago habilitados', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('SEPARADOR_CODIGO_CATEGORIA_PRODUCTO', '-', 'Separador para códigos de categorías de producto', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),

    ('S3_ACCESS_KEY', 'demo-access-key', 'Access key de S3/OVH', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('S3_SECRET_KEY', 'demo-secret-key', 'Secret key de S3/OVH', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('S3_ENDPOINT_URL', 'https://s3.example.com', 'Endpoint S3 compatible', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('S3_REGION', 'us-east-1', 'Región del bucket S3', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('S3_BUCKET', 'techplatform-media', 'Nombre del bucket de archivos multimedia', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('S3_PATH_STYLE_ACCESS', 'true', 'Usar path-style access para S3 compatible', 'BOOLEAN', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('S3_PREFIJO_IMAGENES_PRODUCTO', 'productos/', 'Prefijo dentro del bucket para multimedia de productos', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('S3_TIMEOUT_CONEXION_MS', '2000', 'Timeout de conexión S3 en milisegundos', 'INTEGER', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('S3_TIMEOUT_SOCKET_MS', '3000', 'Timeout socket S3 en milisegundos', 'INTEGER', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('S3_TIMEOUT_PETICION_MS', '8000', 'Timeout de petición S3 en milisegundos', 'INTEGER', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('S3_PAUSA_CAIDO_MS', '30000', 'Tiempo de pausa S3 cuando cae el servicio', 'INTEGER', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('HABILITAR_S3', 'NO', 'Activa o desactiva el uso de S3 para multimedia', 'BOOLEAN', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),

    ('IMAGENES_DIR_LOCAL', 'D:/temp/imagenes/productos', 'Directorio local de respaldo para archivos de producto', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('IMAGENES_PLACEHOLDER', '/resources/img/sin-imagen.png', 'URL de imagen placeholder', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('IMAGENES_PATRON_NOMBRE_VALIDO', '^[A-Za-z0-9_-][A-Za-z0-9_.-]*\\.(jpg|jpeg|png|webp|gif|mp4|webm|m4v|mov|mp3|wav|ogg|m4a|aac)$', 'Regex para validar nombres de archivos multimedia', 'STRING', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('IMAGENES_CACHE_S3_SEGUNDOS', '86400', 'Cache-Control max-age para media servida desde S3', 'INTEGER', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE),
    ('IMAGENES_CACHE_LOCAL_SEGUNDOS', '300', 'Cache-Control max-age para media servida desde disco local', 'INTEGER', NULL, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE);