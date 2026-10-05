-- Crear schemas y asignar propietario
CREATE SCHEMA IF NOT EXISTS auditoria AUTHORIZATION techplatform;
CREATE SCHEMA IF NOT EXISTS catalogo AUTHORIZATION techplatform;
CREATE SCHEMA IF NOT EXISTS configuracion AUTHORIZATION techplatform;
CREATE SCHEMA IF NOT EXISTS ubicacion_geografica AUTHORIZATION techplatform;
CREATE SCHEMA IF NOT EXISTS usuarios AUTHORIZATION techplatform;
CREATE SCHEMA IF NOT EXISTS productos AUTHORIZATION techplatform;
ALTER schema public  OWNER TO techplatform;