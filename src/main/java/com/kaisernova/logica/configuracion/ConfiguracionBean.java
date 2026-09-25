package com.kaisernova.logica.configuracion;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Logger;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;

import com.kaisernova.dao.configuracion.ConfiguracionDao;
import com.kaisernova.modelo.configuracion.Configuracion;
import com.kaisernova.modelo.enums.AmbienteProductivo;
import com.kaisernova.modelo.enums.Mes;
import com.kaisernova.modelo.enums.NombreParametroConfiguracion;
import com.kaisernova.modelo.enums.SiNo;
import com.kaisernova.modelo.enums.TipoDato;

@Stateless
public class ConfiguracionBean {
	private static final Map<String, Set<Enum>> ENUMS_VALORES_MAP= new ConcurrentSkipListMap<>();
	@Inject
	private ConfiguracionDao configuracionDao;
	@Inject
	protected transient Logger logger;
	static {
		ENUMS_VALORES_MAP.put(AmbienteProductivo.class.getName(), new LinkedHashSet(Arrays.asList(AmbienteProductivo.values())));
		ENUMS_VALORES_MAP.put(Mes.class.getName(), new LinkedHashSet(Arrays.asList(Mes.values())));
		ENUMS_VALORES_MAP.put(SiNo.class.getName(), new LinkedHashSet(Arrays.asList(SiNo.values())));
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Configuracion obtenerPorNombreParametro(NombreParametroConfiguracion nombreParametro) {
		var configuraciones = configuracionDao.obtenerPorNombreParametro(nombreParametro);
		if (!configuraciones.isEmpty()) {
			Configuracion configuracion = configuraciones.get(0);
			fijarValoresEnum(configuracion);
			return configuracion;
		}
		return null;
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Configuracion> obtenerTodas() {
		logger.info("[obtenerTodas] obteniendo todas");
		List<Configuracion> configuraciones =configuracionDao.findAllActivos();
		for (Configuracion configuracion : configuraciones) {
			fijarValoresEnum(configuracion);
		}
		return configuraciones;
	}
	
	public Configuracion actualizar(Configuracion configuracion) {
		return configuracionDao.update(configuracion);
	}

	public void fijarValoresEnum(Configuracion configuracion) {
		logger.info("[fijarValoresEnum]configuracion=" + configuracion);
		logger.info("[fijarValoresEnum]ENUMS_VALORES_MAP=" + ENUMS_VALORES_MAP);
		
		if (TipoDato.ENUM.equals(configuracion.getTipoDato()) 
				&& ENUMS_VALORES_MAP.containsKey(configuracion.getNombreEnumValores())) {
			configuracion.setValoresEnum(ENUMS_VALORES_MAP.get(configuracion.getNombreEnumValores()));
		}		
	}


}
