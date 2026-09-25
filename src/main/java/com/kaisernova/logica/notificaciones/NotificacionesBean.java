package com.kaisernova.logica.notificaciones;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;

import com.kaisernova.constantes.ConstantesCorreo;
import com.kaisernova.excepcion.EnvioCorreoExcepcion;
import com.kaisernova.logica.configuracion.ConfiguracionEnMemoriaBean;
import com.kaisernova.modelo.enums.NombreParametroConfiguracion;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class NotificacionesBean {
	@Inject
	private EnvioCorreoBean envioCorreoBean;
	@Inject
	private ConfiguracionEnMemoriaBean configuracionEnMemoriaBean;

	public void enviarNotificacionRegistroPaciente(String destinatario, String usuarioPaciente, String nombresPaciente, LocalDateTime fechaRegistro) throws EnvioCorreoExcepcion {
		String asunto = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_ASUNTO_GENERAL) + " - Registro de Paciente";
		String remitente = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_NOMBRE_REMITENTE);
		String mailRemitente = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_REMITENTE);
		String mailAdmin = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_ADMIN);
		String mensaje = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_MENSAJE_NOTIFICACION_REGISTRO_PACIENTE);
		Map<String, String> paramsMensaje = new HashMap<String, String>();
		paramsMensaje.put(ConstantesCorreo.NOMBRES_PACIENTE, nombresPaciente);
		paramsMensaje.put(ConstantesCorreo.FECHA_REGISTRO, fechaRegistro.toString());
		paramsMensaje.put(ConstantesCorreo.USUARIO_PACIENTE, usuarioPaciente);
		paramsMensaje.put(ConstantesCorreo.REMITENTE, remitente);
		
		enviarNotificacion(destinatario, mailAdmin, mailRemitente, asunto, mensaje, paramsMensaje);
	}
	
	public void enviarNotificacionGeneracionClaveRecuperacion(String destinatario, String tipoUsuario, String usuario, String nombresUsuario, String clave) throws EnvioCorreoExcepcion {
		String asunto = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_ASUNTO_GENERAL) + " - Recuperación de clave de " + tipoUsuario;
		String remitente = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_NOMBRE_REMITENTE);
		String mailRemitente = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_REMITENTE);
		String mailAdmin = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_ADMIN);
		String mensaje = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_MENSAJE_RECUPERACION_CLAVE);
		Map<String, String> paramsMensaje = new HashMap<String, String>();
		paramsMensaje.put(ConstantesCorreo.NOMBRES_USUARIO, nombresUsuario);
		paramsMensaje.put(ConstantesCorreo.CLAVE, clave);
		paramsMensaje.put(ConstantesCorreo.USUARIO, usuario);
		paramsMensaje.put(ConstantesCorreo.REMITENTE, remitente);
		
		enviarNotificacion(destinatario, mailAdmin, mailRemitente, asunto, mensaje, paramsMensaje);
	}
	
	private void enviarNotificacion(String destinatario, String mailAdmin, String mailRemitente, String asunto, String mensaje, Map<String, String> paramsMensaje) throws EnvioCorreoExcepcion {
		mensaje = StringSubstitutor.replace(mensaje, paramsMensaje);
		List<String> destinatarios = new ArrayList<>();
		List<String> destinatariosBcc = new ArrayList<>();
		destinatarios.add(destinatario);
		destinatariosBcc.add(mailAdmin);
		envioCorreoBean.enviarCorreo(destinatarios, null, destinatariosBcc, mailRemitente, asunto, mensaje, null);
	}
	
	
	public void enviarNotificacionCitaPaciente(String destinatario, String usuarioPaciente, String nombresPaciente, String nombresEspecialista, LocalDate fechaCita, LocalTime horaInicio ,String urlCitaVirtual) throws EnvioCorreoExcepcion {
		String asunto = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_ASUNTO_GENERAL) + " - Cita Programada Paciente";		
		String mensaje = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_MENSAJE_NOTIFICACION_CITA_PACIENTE);
		enviarNotificacionCitaPacienteEspecialista(destinatario, usuarioPaciente, nombresPaciente, nombresEspecialista, fechaCita, horaInicio, urlCitaVirtual, asunto,mensaje);
	}
	
	public void enviarNotificacionCitaEspecialista(String destinatario, String usuarioEspecialista, String nombresPaciente, String nombresEspecialista, LocalDate fechaCita, LocalTime horaInicio ,String urlCitaVirtual) throws EnvioCorreoExcepcion {
		String asunto = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_ASUNTO_GENERAL) + " - Cita Programada Especialista";
		String mensaje = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_MENSAJE_NOTIFICACION_CITA_ESPECIALISTA);
		enviarNotificacionCitaPacienteEspecialista(destinatario, usuarioEspecialista, nombresPaciente, nombresEspecialista, fechaCita, horaInicio, urlCitaVirtual, asunto,mensaje);
	}
	
	private void enviarNotificacionCitaPacienteEspecialista(String destinatario, String usuario, String nombresPaciente, String nombresEspecialista, LocalDate fechaCita, LocalTime horaInicio ,String urlCitaVirtual, String asunto, String mensaje) throws EnvioCorreoExcepcion {
		String remitente = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_NOMBRE_REMITENTE);
		String mailRemitente = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_REMITENTE);
		String mailAdmin = configuracionEnMemoriaBean.getValue(NombreParametroConfiguracion.MAIL_ADMIN);
		
		Map<String, String> paramsMensaje = new HashMap<String, String>();
		paramsMensaje.put(ConstantesCorreo.NOMBRES_PACIENTE, nombresPaciente);
		paramsMensaje.put(ConstantesCorreo.NOMBRES_ESPECIALISTA, nombresEspecialista);
		paramsMensaje.put(ConstantesCorreo.FECHA_CITA, fechaCita.toString());
		paramsMensaje.put(ConstantesCorreo.HORA_INICIO, horaInicio.toString());
		paramsMensaje.put(ConstantesCorreo.USUARIO, usuario);
		paramsMensaje.put(ConstantesCorreo.URL_CITA_VIRTUAL, urlCitaVirtual);
		paramsMensaje.put(ConstantesCorreo.REMITENTE, remitente);
		
		enviarNotificacion(destinatario, mailAdmin, mailRemitente, asunto, mensaje, paramsMensaje);
	}
}
