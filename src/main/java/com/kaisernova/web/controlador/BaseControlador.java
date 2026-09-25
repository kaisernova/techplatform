package com.kaisernova.web.controlador;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.file.UploadedFile;

import com.kaisernova.constantes.ConstantesAplicacion;
import com.kaisernova.logica.configuracion.ConfiguracionEnMemoriaBean;
import com.kaisernova.modelo.enums.AmbienteProductivo;
import com.kaisernova.modelo.enums.NombreParametroConfiguracion;
import com.kaisernova.modelo.enums.NombreRol;
import com.kaisernova.modelo.enums.SiNo;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Named
@ViewScoped
public class BaseControlador implements Serializable {
	@Inject
	protected transient Logger logger;
	@Inject
	protected ConfiguracionEnMemoriaBean configuracionEnMemoriaBean;
	protected String recaptchaToken;

	private FacesContext obtenerFacesContextMessages() {
		var facesContext = FacesContext.getCurrentInstance();
		Flash flash = facesContext.getExternalContext().getFlash();
		flash.setKeepMessages(true);
		return facesContext;
	}

	public void fijarMensaje(String sumario, String detalle) {
		var msg = new FacesMessage(sumario, detalle);
		obtenerFacesContextMessages().addMessage(null, msg);
		logger.info("sumario=" + sumario + ", detalle=" + detalle);
	}

	public void fijarMensaje(String sumario) {
		var msg = new FacesMessage(sumario);
		obtenerFacesContextMessages().addMessage(null, msg);
		logger.info("sumario=" + sumario);
	}

	public void fijarMensajeError(String sumario) {
		fijarMensajeError(sumario, "");
	}

	public void fijarMensajeError(String sumario, String detalle) {
		var err = new FacesMessage(FacesMessage.SEVERITY_ERROR, sumario, detalle);
		obtenerFacesContextMessages().addMessage(null, err);
		logger.log(Level.SEVERE, "sumario=" + sumario + ", detalle=" + detalle);
	}

	public void fijarMensajeError(String sumario, Throwable excepcion) {
		var err = new FacesMessage(FacesMessage.SEVERITY_ERROR, sumario, "");
		obtenerFacesContextMessages().addMessage(null, err);
		logger.log(Level.SEVERE, "sumario=" + sumario, excepcion);
	}

	public void fijarMensajeError(String sumario, String detalle, Throwable excepcion) {
		var err = new FacesMessage(FacesMessage.SEVERITY_ERROR, sumario, detalle);
		obtenerFacesContextMessages().addMessage(null, err);
		logger.log(Level.SEVERE, "sumario=" + sumario + ", detalle=" + detalle, excepcion);
	}

	public void fijarMensajeWarning(String sumario, String detalle) {
		var warn = new FacesMessage(FacesMessage.SEVERITY_WARN, sumario, detalle);
		obtenerFacesContextMessages().addMessage(null, warn);
		logger.log(Level.WARNING, "sumario=" + sumario + ", detalle=" + detalle);
	}

	public void fijarMensajeWarning(String sumario) {
		fijarMensajeWarning(sumario, "");
	}

	// para fijar id de cliente
	public void fijarMensaje(String clientId, String sumario, String detalle) {
		var msg = new FacesMessage(sumario, detalle);
		obtenerFacesContextMessages().addMessage(clientId, msg);
		logger.info("sumario=" + sumario + ", detalle=" + detalle);
	}

	public void fijarMensajeError(String clientId, String sumario, String detalle) {
		var err = new FacesMessage(FacesMessage.SEVERITY_ERROR, sumario, detalle);
		obtenerFacesContextMessages().addMessage(clientId, err);
		logger.log(Level.SEVERE, "sumario=" + sumario + ", detalle=" + detalle);
	}

	public void fijarMensajeError(String clientId, String sumario, String detalle, Throwable excepcion) {
		var err = new FacesMessage(FacesMessage.SEVERITY_ERROR, sumario, detalle);
		obtenerFacesContextMessages().addMessage(clientId, err);
		logger.log(Level.SEVERE, "sumario=" + sumario + ", detalle=" + detalle, excepcion);
	}

	public void fijarMensajeWarning(String clientId, String sumario, String detalle) {
		var warn = new FacesMessage(FacesMessage.SEVERITY_WARN, sumario, detalle);
		obtenerFacesContextMessages().addMessage(null, warn);
		logger.log(Level.WARNING, "sumario=" + sumario + ", detalle=" + detalle);
	}

	public <T> void onRowCancel(RowEditEvent<T> event) {
		cancelar();
	}

	public void cancelar() {
		FacesMessage msg = new FacesMessage("Cancelado");
		obtenerFacesContextMessages().addMessage(null, msg);
	}

	public static List<String> getExceptionMessageChain(Throwable throwable) {
		List<String> result = new ArrayList<String>();
		while (throwable != null) {
			result.add(throwable.getMessage());
			throwable = throwable.getCause();
		}
		return result; // ["THIRD EXCEPTION", "SECOND EXCEPTION", "FIRST EXCEPTION"]
	}

	/**
	 * Metodo generico para descargar archivos poniendolos en el response
	 * 
	 * @param nombreArchivoDescarga
	 * @param inputStreamArchivo
	 * @throws IOException
	 */
	protected void descargarArchivoResponse(String nombreArchivoDescarga, InputStream inputStreamArchivo)
			throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
				.getResponse();
		response.reset();
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=" + nombreArchivoDescarga);
		OutputStream responseOutputStream = response.getOutputStream();

		InputStream fis = new BufferedInputStream(inputStreamArchivo, 2048);
		byte[] bytesBuffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = fis.read(bytesBuffer)) > 0) {
			responseOutputStream.write(bytesBuffer, 0, bytesRead);
		}
		responseOutputStream.flush();
		fis.close();
		responseOutputStream.close();
		facesContext.responseComplete();
	}

	public String obtenerParametroPeticion(String nombreParametro) {

		return obtenerHttpServletRequest().getParameter(nombreParametro);
	}

	public boolean tieneParametroPeticion(String nombreParametro) {

		return Objects.nonNull(obtenerHttpServletRequest().getParameter(nombreParametro));
	}

	public HttpServletRequest obtenerHttpServletRequest() {
		return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}

	public HttpServletResponse obtenerHttpServletResponse() {
		return (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
	}

	public String obtenerAmbienteProductivo(String codigoAmbiente) {
		return AmbienteProductivo.obtenerPorCodigo(codigoAmbiente).name();
	}

	public void redireccionar(String path) {
		var facesContext = FacesContext.getCurrentInstance();
		try {
			facesContext.getExternalContext()
					.redirect(facesContext.getExternalContext().getRequestContextPath() + path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * get username of connected user
	 * @return
	 */
	public String getUsuarioConectado() {
		var facesContext = FacesContext.getCurrentInstance();
		return facesContext.getExternalContext().getRemoteUser();
	}

	public boolean getEstaUsuarioConectado() {
		return (Objects.nonNull(getUsuarioConectado()));
	}

	public boolean verificarUsuarioTieneRol(NombreRol nombreRol) {
		logger.info("[verificarUsuarioTieneRol]nombreRol=" + nombreRol
				+ ", this.obtenerHttpServletRequest().isUserInRole(nombreRol.name())="
				+ this.obtenerHttpServletRequest().isUserInRole(nombreRol.name()));
		return this.obtenerHttpServletRequest().isUserInRole(nombreRol.name());
	}

	public String msg(String msgKey) {
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(ConstantesAplicacion.MESSAGES);
		return bundle.getString(msgKey);
	}

	public SiNo[] getSiNo() {
		return SiNo.values();
	}

	public void actualizarElementoVista(String nombreElemento) {
		PrimeFaces.current().ajax().update(nombreElemento);
	}

	protected boolean verifyRecaptcha() {
		
		if(!getActivarCaptcha()) {
			logger.info("[verifyRecaptcha]Captcha desactivado en configuracion, se omite verificacion.");
			return true;
		}
		String token=getRecaptchaToken();
		logger.info("[verifyRecaptcha]token="+token);
		try {
			double scoreMinimo = configuracionEnMemoriaBean.getValueAsBigDecimal(NombreParametroConfiguracion.CAPTCHA_MIN_SCORE)
					.doubleValue();
			String secret = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.CAPTCHA_SECRET_KEY);
			URI uri = new URI(configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.URL_CAPTCHA_SERVER));
			URL url = uri.toURL();
			logger.info("[verifyRecaptcha]url="+url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			String params = "secret=" + secret + "&response=" + token;
			try (OutputStream out = conn.getOutputStream()) {
				out.write(params.getBytes());
			}
			Scanner in = new Scanner(conn.getInputStream());
			String response = in.useDelimiter("\\A").next();
			in.close();
			logger.info("[verifyRecaptcha]response="+response);
			JSONObject json = new JSONObject(response);
			boolean success = json.getBoolean("success");
			double score = json.optDouble("score", 0.0);
			String action = json.optString("action");
			if (success && "submit".equals(action)) {
				if (score >= scoreMinimo) {
					return true;
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "[verifyRecaptcha]Error verifying reCAPTCHA:"+e.getMessage(), e);
		}
		return false;
	}

	public String getRecaptchaToken() {
		return recaptchaToken;
	}

	public void setRecaptchaToken(String recaptchaToken) {
		this.recaptchaToken = recaptchaToken;
	}
	
	public String getUrlCaptchaCliente() {
		return configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.URL_CAPTCHA_CLIENTE)+"?render="+getCaptchaSiteKey();
	}
	public String getCaptchaSiteKey() {
		return configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.CAPTCHA_SITE_KEY);
	}
	public boolean getActivarCaptcha() {
		return configuracionEnMemoriaBean.getValueAsBoolean(NombreParametroConfiguracion.ACTIVAR_CAPTCHA);
	
	}
	public String getIp() {
		HttpServletRequest request = obtenerHttpServletRequest();
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (Objects.isNull(ipAddress)) {
			ipAddress = request.getRemoteAddr();
		}
		return ipAddress;
	}
	
	protected String obtenerExtensionArchivo(UploadedFile uploadedFile) {
		return FilenameUtils.getExtension(uploadedFile.getFileName());
	}
	protected static HttpServletResponse getResponse(FacesContext context) {
		return (HttpServletResponse) context.getExternalContext().getResponse();
	}

	protected static HttpServletRequest getRequest(FacesContext context) {
		return (HttpServletRequest) context.getExternalContext().getRequest();
	}
}
