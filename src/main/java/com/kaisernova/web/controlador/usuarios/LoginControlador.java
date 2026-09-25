package com.kaisernova.web.controlador.usuarios;

import static jakarta.security.enterprise.AuthenticationStatus.SEND_CONTINUE;
import static jakarta.security.enterprise.AuthenticationStatus.SEND_FAILURE;
import static jakarta.security.enterprise.AuthenticationStatus.SUCCESS;
import static jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters.withParams;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.Password;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import com.kaisernova.constantes.ConstantesAplicacion;
import com.kaisernova.logica.usuarios.UsuarioBean;

import com.kaisernova.modelo.usuarios.Usuario;
import com.kaisernova.web.controlador.BaseControlador;

@Named
@RequestScoped
public class LoginControlador extends BaseControlador {
	@Inject
	protected SecurityContext securityContext;

	@NotNull(message = "El usuario es requerido")
	@NotBlank(message = "El usuario es requerido")
	protected String username;

	@NotNull(message = "La clave es requerida")
	@NotBlank(message = "La clave es requerida")
	protected String password;

	@Inject
	protected Logger LOG;

	@Inject
	protected ExternalContext externalContext;
	@Inject
	protected UsuarioBean usuarioBean;
	@NotBlank(message = "El email es requerido para recuperar la clave")
	@Pattern(regexp = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$", message = "El Correo Electrónico debe ser tipo abcd@defg.com")
	private String mailUsuario;
	
	@PostConstruct
	public void inicializar() {
		LOG.info("[inicializar] LoginControlador inicializado");
		if(tieneParametroPeticion("usuario")) {
			username = obtenerParametroPeticion("usuario");
			LOG.info("[inicializar] Usuario prellenado desde parametro de peticion: " + username);			
		}
	}

	public void login() {

		FacesContext context = FacesContext.getCurrentInstance();
		boolean esPaciente = getRequest(context).getRequestURI().toLowerCase().contains(ConstantesAplicacion.PACIENTE_MINUSCULAS);
		String usernameConPrefijo = esPaciente ? ConstantesAplicacion.PREFIJO_PACIENTE_USUARIO + username : username;
		Credential credential = new UsernamePasswordCredential(usernameConPrefijo, new Password(password));

		AuthenticationStatus status = securityContext.authenticate(getRequest(context), getResponse(context),
				withParams().credential(credential));

		LOG.info("[login]authentication result:" + status);

		if (status.equals(SEND_CONTINUE)) {
			context.responseComplete();
		} else if (status.equals(SEND_FAILURE)) {
			fijarMensajeError("NO PERMITIDO", "Credenciales NO VALIDAS");

		} else if (status.equals(SUCCESS)) {
			redireccionar("/index.html");
		}
	}




	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void goToStart() throws IOException {
		externalContext.redirect(externalContext.getRequestContextPath() + "/index.html");
	}

	public String logoff() {
		System.out.println("LoginBean.logoff()");
		try {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		session.invalidate();
		} catch (Exception e) {
			LOG.warning("logoff warn");
		}
		return "/index.xhtml?faces-redirect=true";
	}

	public String getMailUsuario() {
		return mailUsuario;
	}

	public void setMailUsuario(String mailUsuario) {
		this.mailUsuario = mailUsuario;
	}
	public void recuperarClave() {
		if (mailUsuario == null || mailUsuario.trim().isEmpty()) {
			fijarMensajeError("Email requerido", "Debe ingresar su email para recuperar la clave");
			return;
		}
		FacesContext context = FacesContext.getCurrentInstance();
		
		Usuario usuario = usuarioBean.obtenerUsuarioPorEmail(mailUsuario);
		usuarioBean.recuperarClaveUsuarioEmail(usuario);
		
		
		fijarMensaje("Recuperación de clave", "Si el email ingresado corresponde a una cuenta registrada, se enviará un correo con una clave de recuperación.");
		redireccionar("/index.html");
	}
}