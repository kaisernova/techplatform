package com.kaisernova.logica.notificaciones;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import com.kaisernova.excepcion.EnvioCorreoExcepcion;
import com.kaisernova.logica.configuracion.ConfiguracionEnMemoriaBean;
import com.kaisernova.modelo.enums.NombreParametroConfiguracion;
import com.kaisernova.util.Constantes;

import jakarta.activation.DataHandler;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;



@Stateless
public class EnvioCorreoBean {

	@Inject
	private Logger logger;

	@Inject
	private ConfiguracionEnMemoriaBean configuracionEnMemoriaBean;

	
	private String servidor;

	private String puerto;

	private String usuario;

	private String password;
	private String autenticacion;
	private String mailAdmin;
	private String mailSSL;
	private String mailStartTls;
	private String mailRemitente;
	private String mailNombreRemitente;
	private String asuntoComprobante;

	private String mensajeComprobante;
	private String asuntoError;
	private String mensajeError;

	private SimpleDateFormat formateadorFecha;

	private SMTPAuthenticator authenticator;

	// Crear objeto de propiedades
	private Properties propiedades;
	
	//private byte[] logoEmpresa;

	@PostConstruct
	public void postConstruccion() {
		servidor = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_SERVER);

		puerto = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_PORT);

		usuario = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_USER);

		password = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_PASSWORD);

		autenticacion = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_AUTH);

		mailAdmin = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_ADMIN);
		
		mailSSL = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_SSL);

		mailStartTls = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_SMTP_STARTTLS_ENABLE);

		mailRemitente = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_REMITENTE);

		mailNombreRemitente = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_NOMBRE_REMITENTE);

		fijarConfiguracionMail();
		formateadorFecha = Constantes.FORMATO_FECHA_DDMMYYYY;

	}

	public void fijarConfiguracionMail() {
		propiedades = new Properties();
		if ("true".equals(autenticacion)) {
			propiedades.setProperty("mail.transport.protocol", "smtp");
			propiedades.setProperty("mail.smtp.host", getServidor());
			propiedades.setProperty("mail.smtp.port", getPuerto());
			propiedades.setProperty("mail.smtp.auth", "true");
			authenticator = new SMTPAuthenticator(getUsuario(), getPassword());
		} else {
			propiedades.setProperty("mail.smtp.host", getServidor());
			propiedades.setProperty("mail.smtp.port", getPuerto());
			propiedades.setProperty("mail.user", getUsuario());
			propiedades.setProperty("mail.password", getPassword());

		}

		if ("true".equals(mailSSL)) {
			propiedades.setProperty("mail.smtp.socketFactory.port", getPuerto());
			propiedades.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		}
		if ("true".equals(mailStartTls)) {
			propiedades.setProperty("mail.smtp.starttls.enable", "true");
		}
	}


	public void enviarCorreo(List<String> destinatarios, List<String> destinatariosConCopia, String remitente, String asunto, String mensaje, Map<String, byte[]> adjuntos) throws EnvioCorreoExcepcion {
		enviarCorreo(destinatarios, destinatariosConCopia, null, remitente, asunto, mensaje, adjuntos);
	}
	
	public void enviarCorreo(List<String> destinatarios, List<String> destinatariosConCopia, List<String> destinatariosConBCC, String mailRemitente, String asunto, String mensaje, Map<String, byte[]> adjuntos) throws EnvioCorreoExcepcion {
		Session sesion;
		try {
			System.out.println("[enviarCorreo]propiedades="+propiedades);
			if (propiedades != null) {
				// Obtener sesion para envio de correo
				if ("true".equals(autenticacion)) {
					sesion = Session.getDefaultInstance(propiedades, authenticator);

				} else {
					sesion = Session.getDefaultInstance(propiedades);
				}
				// Crear mensaje
				MimeMessage mimeMessage = new MimeMessage(sesion);

				mimeMessage.setFrom(new InternetAddress(mailRemitente));
				for (String destinatario : destinatarios) {
					mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
				}
				if (destinatariosConCopia != null) {
					for (String destinatarioConCopia : destinatariosConCopia) {
						mimeMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(destinatarioConCopia));
					}
				}
				if(destinatariosConBCC != null) {
					for (String destinatarioConBCC : destinatariosConBCC) {
						mimeMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(destinatarioConBCC));
					}
				}

				mimeMessage.setSubject(asunto);

				Multipart multiPart = new MimeMultipart();

				MimeBodyPart messageText = new MimeBodyPart();
				messageText.setContent(mensaje, "text/html");
				multiPart.addBodyPart(messageText);
			
				if (adjuntos != null) {
					for (Map.Entry<String, byte[]> adjunto : adjuntos.entrySet()) {
						MimeBodyPart attachment = new MimeBodyPart();
						ByteArrayDataSource file = new ByteArrayDataSource(adjunto.getValue(), "application/octet-stream");
						// FileDataSource file = new FileDataSource("C:/my-file.rar");
						attachment.setDataHandler(new DataHandler(file));
						attachment.setFileName(adjunto.getKey());
						multiPart.addBodyPart(attachment);
					}
				}

				mimeMessage.setContent(multiPart);

				// Colocar cuerpo mensaje html
				// mimeMessage.setContent(mensaje, "text/html");
				System.out.println("[enviarCorreo] por enviar");
				// Enviar mensaje
				Transport.send(mimeMessage);
				logger.info("Mensaje enviado correctamente");

			} else {
				String errorPropiedades = "No existe objeto de propiedades, no se pudo enviar el correo";
				logger.severe(errorPropiedades);
				throw new EnvioCorreoExcepcion(errorPropiedades);
			}
		} catch (Exception e) {
			throw new EnvioCorreoExcepcion(e.getMessage(), e);
		}

	}

	private class SMTPAuthenticator extends Authenticator {

		String usuario;
		String password;

		public SMTPAuthenticator(String user, String password) {
			this.usuario = user;
			this.password = password;
		}

		public PasswordAuthentication getPasswordAuthentication() {

			if ((usuario == null || usuario.isEmpty()) || (password == null || password.isEmpty())) {
				return null;
			} else {
				return new PasswordAuthentication(usuario, password);
			}
		}
	}

	

	public String getServidor() {
		return servidor;
	}

	public void setServidor(String servidor) {
		this.servidor = servidor;
	}

	public String getPuerto() {
		return puerto;
	}

	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAutenticacion() {
		return autenticacion;
	}

	public void setAutenticacion(String autenticacion) {
		this.autenticacion = autenticacion;
	}

	public String getMailAdmin() {
		return mailAdmin;
	}

	public void setMailAdmin(String mailAdmin) {
		this.mailAdmin = mailAdmin;
	}

	public String getMailSSL() {
		return mailSSL;
	}

	public void setMailSSL(String mailSSL) {
		this.mailSSL = mailSSL;
	}

	public String getMailRemitente() {
		return mailRemitente;
	}

	public void setMailRemitente(String mailRemitente) {
		this.mailRemitente = mailRemitente;
	}

	public String getMailNombreRemitente() {
		return mailNombreRemitente;
	}

	public void setMailNombreRemitente(String mailNombreRemitente) {
		this.mailNombreRemitente = mailNombreRemitente;
	}

	public String getAsuntoComprobante() {
		return asuntoComprobante;
	}

	public void setAsuntoComprobante(String asuntoComprobante) {
		this.asuntoComprobante = asuntoComprobante;
	}

	public String getMensajeComprobante() {
		return mensajeComprobante;
	}


	public String getAsuntoError() {
		return asuntoError;
	}

	public void setAsuntoError(String asuntoError) {
		this.asuntoError = asuntoError;
	}

	public String getMensajeError() {
		return mensajeError;
	}

	public void setMensajeError(String mensajeError) {
		this.mensajeError = mensajeError;
	}

	public SimpleDateFormat getFormateadorFecha() {
		return formateadorFecha;
	}

	public void setFormateadorFecha(SimpleDateFormat formateadorFecha) {
		this.formateadorFecha = formateadorFecha;
	}

	public String getMailStartTls() {
		return mailStartTls;
	}

	public void setMailStartTls(String mailStartTls) {
		this.mailStartTls = mailStartTls;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

}
