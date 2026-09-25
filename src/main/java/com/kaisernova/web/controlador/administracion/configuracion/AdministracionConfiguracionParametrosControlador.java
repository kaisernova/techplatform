package com.kaisernova.web.controlador.administracion.configuracion;

import java.util.List;
import java.util.logging.Level;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolationException;

import org.primefaces.event.RowEditEvent;

import com.kaisernova.logica.configuracion.ConfiguracionBean;
import com.kaisernova.logica.configuracion.ConfiguracionEnMemoriaBean;
import com.kaisernova.modelo.configuracion.Configuracion;
import com.kaisernova.web.controlador.BaseControlador;
@Named
@ViewScoped
public class AdministracionConfiguracionParametrosControlador extends BaseControlador {
	@Inject
	private ConfiguracionBean configuracionBean;
	@Inject
	private ConfiguracionEnMemoriaBean configuracionEnMemoriaBean;
	private List<Configuracion> configuraciones;
	private List<Configuracion> configuracionesFiltrados;
	private Configuracion configuracionSeleccionada;

	@PostConstruct
	public void inicializar() {
		reInicializar();
	}
	public void reInicializar() {
		configuraciones = configuracionBean.obtenerTodas();
		logger.info("[reInicializar]configuraciones=" + configuraciones);
	}
	
	public void onRowEdit(RowEditEvent<Configuracion> event) {
		Configuracion configuracion = event.getObject();
		try {
			logger.info("[onRowEdit]POR RECARGAR" );
			configuracion.setUsuarioActualiza(getUsuarioConectado());
			configuracionBean.actualizar(configuracion);
			recargarConfiguraciones();
			fijarMensaje("Configuración [" + configuracion.getNombreParametro() + "] actualizado exitosamente");
			logger.info("[onRowEdit]FIN RECARGA" );			
		} catch (ConstraintViolationException e) {
			logger.log(Level.SEVERE, "Exception: ");
			e.getConstraintViolations().forEach(err -> logger.log(Level.SEVERE, err.toString()));
		}

		catch (Exception e) {
			fijarMensajeError("Error al actualizar configuración", e.getMessage(), e);
		}
	}
	
	public void recargarConfiguraciones() {		
		configuracionEnMemoriaBean.recargar();
		fijarMensaje("Configuraciones en memoria recargadas exitosamente");
	}
	public void guardarConfiguracionSeleccionada() {
		configuracionSeleccionada.setUsuarioActualiza(getUsuarioConectado());
		configuracionBean.actualizar(configuracionSeleccionada);
		logger.info("[guardarConfiguracionSeleccionada]configuracionSeleccionada.valor="+configuracionSeleccionada.getValor() );
		fijarMensaje("Configuración [" + configuracionSeleccionada.getNombreParametro() + "] actualizado exitosamente");
		recargarConfiguraciones();
		
	}
	public List<Configuracion> getConfiguraciones() {
		return configuraciones;
	}

	public void setConfiguraciones(List<Configuracion> configuraciones) {
		this.configuraciones = configuraciones;
	}

	public List<Configuracion> getConfiguracionesFiltrados() {
		return configuracionesFiltrados;
	}

	public void setConfiguracionesFiltrados(List<Configuracion> configuracionesFiltrados) {
		this.configuracionesFiltrados = configuracionesFiltrados;
	}
	public Configuracion getConfiguracionSeleccionada() {
		return configuracionSeleccionada;
	}
	public void setConfiguracionSeleccionada(Configuracion configuracionSeleccionada) {
		this.configuracionSeleccionada = configuracionSeleccionada;
	}

	
}
