package com.kaisernova.web.controlador.administracion.catalogo;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.file.UploadedFile;

import com.kaisernova.logica.catalogo.CuentaBancariaBean;
import com.kaisernova.logica.catalogo.InstitucionFinancieraBean;
import com.kaisernova.modelo.catalogo.CuentaBancaria;
import com.kaisernova.modelo.catalogo.InstitucionFinanciera;
import com.kaisernova.modelo.enums.TipoCuentaBancaria;
import com.kaisernova.web.controlador.BaseControlador;
import com.kaisernova.web.controlador.ControladorUtil;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolationException;


@Named
@ViewScoped
public class AdministracionCuentaBancariaControlador extends BaseControlador {
	@Inject
	private transient Logger logger;
	@Inject
	private InstitucionFinancieraBean institucionFinancieraBean;
	@Inject
	private CuentaBancariaBean cuentaBancariaBean;
	private CuentaBancaria cuentaBancaria;

	private List<InstitucionFinanciera> institucionesFinancieras;
	private List<CuentaBancaria> cuentasBancarias;
	private List<CuentaBancaria> cuentasBancariasFiltrados;
	private CuentaBancaria cuentaBancariaSeleccionada;
	 private UploadedFile fileCuentaBancariaSeleccionada;
	@PostConstruct
	public void inicializar() {
		reInicializar();
	}
	
	public void reInicializar() {
		institucionesFinancieras = institucionFinancieraBean.obtenerTodas();
		cuentasBancarias = cuentaBancariaBean.obtenerTodas();
		cuentaBancaria = new CuentaBancaria();
		cuentaBancaria.setUsuarioCrea(this.getUsuarioConectado());
		cuentaBancaria.setUsuarioActualiza(this.getUsuarioConectado());
		cuentaBancaria.setActivo(true);
		
	}

	public List<InstitucionFinanciera> getInstitucionesFinancieras() {
		return institucionesFinancieras;
	}

	public void crear() {
		try {
			convertirImagenABase64(cuentaBancaria);
			cuentaBancariaBean.crear(cuentaBancaria);
			this.fijarMensaje("Registro creado exitosamente");
			cuentaBancariaBean.reInicilizar();
			reInicializar();
		} catch (Exception e) {
			String errorImagen = (Objects.nonNull(cuentaBancaria.getImagenUpload()) 
					&& Objects.nonNull(cuentaBancaria.getImagenUpload().getContent()) 
					&& cuentaBancaria.getImagenUpload().getContent().length>0)?"Si quiere subir una imágen debe cargarla de nuevo":"";
			this.fijarMensajeError("Error al crear el registro:" + e.getMessage() + ". "+errorImagen, e);
		}
	}
	
	public void onRowEdit(RowEditEvent<CuentaBancaria> event) {
		CuentaBancaria cuentaBancariaSeleecionada = event.getObject();
		
		try {
			cuentaBancariaSeleecionada.setUsuarioActualiza(getUsuarioConectado());
			cuentaBancariaBean.actualizar(cuentaBancariaSeleecionada);
			cuentaBancariaBean.reInicilizar();
			fijarMensaje("Cuenta [" + cuentaBancariaSeleecionada + "] actualizada exitosamente");
			//reInicializar();
		} catch (ConstraintViolationException e) {
			logger.log(Level.SEVERE, "Exception: ");
			e.getConstraintViolations().forEach(err -> logger.log(Level.SEVERE, err.toString()));
		}

		catch (Exception e) {
			fijarMensajeError("Error al actualizar. ", e.getMessage(), e);
		}
	}
	public void cuentaBancariaSeleccionadaHandleFileUpload(FileUploadEvent event) {
		try {
		cuentaBancariaSeleccionada.setImagenUpload(event.getFile());
		convertirImagenABase64(cuentaBancariaSeleccionada);
		cuentaBancariaBean.actualizar(cuentaBancariaSeleccionada);
		cuentaBancariaBean.reInicilizar();
		fijarMensaje("Cuenta [" + cuentaBancariaSeleccionada + "] actualizada exitosamente");
		cuentaBancariaSeleccionada=null;
		reInicializar();
		} catch (Exception e) {
			fijarMensajeError("Error al actualizar. ", e.getMessage(), e);
		}
	}
	public void eliminarImagenCuentaBancariaSeleccionada() {
		try {
			cuentaBancariaSeleccionada.setImagenBase64(null);
			cuentaBancariaSeleccionada.setExtensionImagenBase64(null);
			cuentaBancariaSeleccionada.setImagenUpload(null);
			cuentaBancariaBean.actualizar(cuentaBancariaSeleccionada);
			cuentaBancariaBean.reInicilizar();
			fijarMensaje("Imagen removida exitosamente de la cuenta [" + cuentaBancariaSeleccionada + "]");
			cuentaBancariaSeleccionada=null;
			reInicializar();
		} catch (Exception e) {
			fijarMensajeError("Error al remover la imagen. ", e.getMessage(), e);
		}
	}
	
	/**
	 * Convierte el archivo de imagen subido a base64 y lo asigna a la propiedad fotoBase64.
	 * Si no hay archivo subido, la propiedad fotoBase64 se mantiene sin cambios.
	 * 
	 * @param datosEspecialista - El objeto DatosEspecialista que contiene el archivo de imagen a convertir
	 */
	private void convertirImagenABase64(CuentaBancaria cuentaBancaria) {
		if (cuentaBancaria == null) {
			return;
		}
		UploadedFile fotoUpload = cuentaBancaria.getImagenUpload();
		String base64String = ControladorUtil.obtenerBase64DeUploadesFile(fotoUpload);
		if (Objects.nonNull(base64String)) {
			try {
				cuentaBancaria.setImagenBase64(base64String);
				cuentaBancaria.setExtensionImagenBase64(obtenerExtensionArchivo(fotoUpload));
				logger.info("Imagen convertida a base64 para cuenta. Tamaño archivo: " + fotoUpload.getSize() + " bytes");
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error al convertir imagen a base64", e);
				fijarMensajeError("Error al procesar imagen", "Error al convertir imagen a base64: " + e.getMessage(), e);
			}
		}
	}

	public CuentaBancaria getCuentaBancaria() {
		return cuentaBancaria;
	}

	public void setCuentaBancaria(CuentaBancaria cuentaBancaria) {
		this.cuentaBancaria = cuentaBancaria;
	}

	public List<CuentaBancaria> getCuentasBancarias() {
		return cuentasBancarias;
	}

	public void setCuentasBancarias(List<CuentaBancaria> cuentasBancarias) {
		this.cuentasBancarias = cuentasBancarias;
	}

	public List<CuentaBancaria> getCuentasBancariasFiltrados() {
		return cuentasBancariasFiltrados;
	}

	public void setCuentasBancariasFiltrados(List<CuentaBancaria> cuentasBancariasFiltrados) {
		this.cuentasBancariasFiltrados = cuentasBancariasFiltrados;
	}	
	public List<TipoCuentaBancaria> getTiposCuentasBancarias() {
		
		return List.of(TipoCuentaBancaria.values());
	}

	public CuentaBancaria getCuentaBancariaSeleccionada() {
		return cuentaBancariaSeleccionada;
	}

	public void setCuentaBancariaSeleccionada(CuentaBancaria cuentaBancariaSeleccionada) {
		this.cuentaBancariaSeleccionada = cuentaBancariaSeleccionada;
	}

	public UploadedFile getFileCuentaBancariaSeleccionada() {
		return fileCuentaBancariaSeleccionada;
	}

	public void setFileCuentaBancariaSeleccionada(UploadedFile fileCuentaBancariaSeleccionada) {
		this.fileCuentaBancariaSeleccionada = fileCuentaBancariaSeleccionada;
	}
	
}