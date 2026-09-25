package com.kaisernova.web.controlador.administracion.catalogo;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolationException;

import org.primefaces.event.RowEditEvent;

import com.kaisernova.logica.catalogo.InstitucionFinancieraBean;
import com.kaisernova.modelo.catalogo.InstitucionFinanciera;
import com.kaisernova.web.controlador.BaseControlador;


@Named
@ViewScoped
public class AdministracionInstitucionFinancieraControlador extends BaseControlador {
	@Inject
	private transient Logger logger;
	@Inject
	private InstitucionFinancieraBean institucionFinancieraBean;
	

	private InstitucionFinanciera institucionFinanciera;

	private List<InstitucionFinanciera> institucionesFinancieras;
	private List<InstitucionFinanciera> institucionesFinancierasFiltrados;

	@PostConstruct
	public void inicializar() {
		reInicializar();
	}
	
	public void reInicializar() {
		institucionesFinancieras = institucionFinancieraBean.obtenerTodas();
		institucionFinanciera = new InstitucionFinanciera();
		institucionFinanciera.setUsuarioCrea(this.getUsuarioConectado());
		institucionFinanciera.setUsuarioActualiza(this.getUsuarioConectado());
		institucionFinanciera.setActivo(true);
	}

	public List<InstitucionFinanciera> getInstitucionesFinancieras() {
		return institucionesFinancieras;
	}

	public void crear() {
		try {
			if(institucionFinancieraBean.existePorNombre(institucionFinanciera.getNombre()) || institucionFinancieraBean.existePorCodigo(institucionFinanciera.getCodigo())) {
				this.fijarMensajeError("Error: Ya existe un registro previo con el nombre o código que se quiere registrar");
				return;
			}
			institucionFinancieraBean.crear(institucionFinanciera);
			this.fijarMensaje("Registro creado exitosamente");
			reInicializar();
		} catch (Exception e) {
			this.fijarMensajeError("Error al crear el registro:" + e.getMessage(), e);
		}
	}
	
	public void onRowEdit(RowEditEvent<InstitucionFinanciera> event) {
		InstitucionFinanciera institucionFinancieraEdit = event.getObject();
		logger.info("[onRowEdit]institucionFinancieraEdit="+institucionFinancieraEdit);
		try {
			institucionFinancieraEdit.setUsuarioActualiza(getUsuarioConectado());
			if(institucionFinancieraBean.existeOtroPorNombre(institucionFinancieraEdit.getNombre(), institucionFinancieraEdit.getCodigo()) 
					|| institucionFinancieraBean.existeOtroPorCodigo(institucionFinancieraEdit.getCodigo(), institucionFinancieraEdit.getCodigo())) {
				this.fijarMensajeError("Error: Ya existe un registro previo con el nombre o código que se quiere registrar");
				return;
			}
			logger.info("[onRowEdit](2)institucionFinancieraEdit="+institucionFinancieraEdit);
			institucionFinancieraBean.actualizar(institucionFinancieraEdit);
			fijarMensaje("Institución Financiera [" + institucionFinancieraEdit.getCodigo() + "] actualizada exitosamente");
			//reInicializar();
		} catch (ConstraintViolationException e) {
			logger.log(Level.SEVERE, "Exception: ");
			e.getConstraintViolations().forEach(err -> logger.log(Level.SEVERE, err.toString()));
		}

		catch (Exception e) {
			fijarMensajeError("Error al actualizar. ", e.getMessage(), e);
		}
	}

	public InstitucionFinanciera getInstitucionFinanciera() {
		return institucionFinanciera;
	}

	public List<InstitucionFinanciera> getInstitucionesFinancierasFiltrados() {
		return institucionesFinancierasFiltrados;
	}

	public void setInstitucionesFinancierasFiltrados(List<InstitucionFinanciera> institucionesFinancierasFiltrados) {
		this.institucionesFinancierasFiltrados = institucionesFinancierasFiltrados;
	}
	
	
}
