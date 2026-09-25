package com.kaisernova.logica.configuracion;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

import com.kaisernova.excepcion.JsonExcepcion;
import com.kaisernova.modelo.configuracion.Configuracion;
import com.kaisernova.modelo.enums.NombreParametroConfiguracion;
import com.kaisernova.util.Base64Util;
import com.kaisernova.util.JsonUtil;

@Singleton
@Startup
public class ConfiguracionEnMemoriaBean {
	private final static Map<String, String> CONFIG_MAP = new ConcurrentHashMap<>();
	
	@Inject
	private Logger logger;
	
	@Inject
	private ConfiguracionBean configuracionBean;
	
		
	@PostConstruct
	public void init() {
		recargar();
	}
	@Lock(LockType.WRITE)
	public void recargar() {
		logger.info("[recargar]POR RECARGAR" );
		CONFIG_MAP.clear();
		var configuraciones = configuracionBean.obtenerTodas();
		for (Configuracion configuracion : configuraciones) {
			CONFIG_MAP.put(configuracion.getNombreParametro().name(), configuracion.getValor());
		}
		logger.info("[recargar]CONFIG_MAP=" + CONFIG_MAP);
	}
	

	@Lock(LockType.READ)
	public Set<String> getPropertyNames() {
		return CONFIG_MAP.keySet();
	}


	@Lock(LockType.READ)
	public String getValue(String propertyName) {	
		if(CONFIG_MAP.containsKey(propertyName)) {
			return CONFIG_MAP.get(propertyName);
		}
		return "";
	}
	@Lock(LockType.READ)
	public String getValue(NombreParametroConfiguracion nombreParametro) {			
		return getValue(nombreParametro.name());
	}
	
	@Lock(LockType.READ)
	public byte[] getBytesFromBase64Value(NombreParametroConfiguracion nombreParametro) {			
		return Base64Util.transformarStringBase64ABytesContenido(getValue(nombreParametro.name()));
	}

	@Lock(LockType.READ)
	public BigDecimal getValueAsBigDecimal(String propertyName) {	
		if(CONFIG_MAP.containsKey(propertyName)) {
			try {
				BigDecimal valor = new BigDecimal(CONFIG_MAP.get(propertyName));
				return valor;
			}
			catch (Exception e) {
				logger.severe("[getValueAsBigDecimal] propertyName="+ propertyName+ " no puede convertirse a BigDecimal se devuelve Zero");
			}
			
		}
		return BigDecimal.ZERO;
	}
	
	@Lock(LockType.READ)
	public Integer getValueAsInteger(String propertyName) {	
		if(CONFIG_MAP.containsKey(propertyName)) {
			try {
				Integer valor = Integer.parseInt(CONFIG_MAP.get(propertyName));
				return valor;
			}
			catch (Exception e) {
				logger.severe("[getValueAsInteger] propertyName="+ propertyName+ " no puede convertirse a Integer se devuelve Zero");
			}
			
		}
		return 0;
	}
	
	/**
	 * Si el valor es SI, si, TRUE, true o 1 retorna Boolean.True, de lo contrario falso
	 * @return
	 */
	@Lock(LockType.READ)
	public Boolean getValueAsBoolean(String propertyName) {	
		if(CONFIG_MAP.containsKey(propertyName)) {
			try {
				Boolean valor = Boolean.FALSE;
				var valorString = CONFIG_MAP.get(propertyName);
				valorString = (Objects.nonNull(valorString))?valorString.trim():"";
				if("TRUE".equalsIgnoreCase(valorString) || "SI".equalsIgnoreCase(valorString) || "1".equalsIgnoreCase(valorString)) {
					valor = Boolean.TRUE;
				}
				return valor;
			}
			catch (Exception e) {
				logger.severe("[getValueAsInteger] propertyName="+ propertyName+ " no puede convertirse a Boolean se devuelve FALSE");
			}
			
		}
		return Boolean.FALSE;
	}
	
	
	@Lock(LockType.READ)
	public BigDecimal getValueAsBigDecimal(NombreParametroConfiguracion nombreParametro) {			
		return getValueAsBigDecimal(nombreParametro.name());
	}
	@Lock(LockType.READ)
	public Integer getValueAsInteger(NombreParametroConfiguracion nombreParametro) {	
		return getValueAsInteger(nombreParametro.name());
	}
	
	/**
	 * Si el valor es SI, si, TRUE, true o 1 retorna Boolean.True, de lo contrario falso
	 * @return
	 */
	@Lock(LockType.READ)
	public Boolean getValueAsBoolean(NombreParametroConfiguracion nombreParametro) {
		return getValueAsBoolean(nombreParametro.name());
	}

	@Lock(LockType.READ)
	public <T> T getValueJsonAsObject(NombreParametroConfiguracion nombreParametro,  final Class<T> valueType) {
		String valor = getValue(nombreParametro);
		logger.info("[getValueJsonAsObject]valor="+valor);
		try {
			return JsonUtil.jsonToObject(valor, valueType);
		} catch (JsonExcepcion e) {
			logger.log(Level.SEVERE, "No se puede transformar valor JSON ["+valor+"] a objeto:" + e.getMessage(), e);
			return null;
		}
	}

}
