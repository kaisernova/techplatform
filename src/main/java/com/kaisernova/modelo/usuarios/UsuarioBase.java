package com.kaisernova.modelo.usuarios;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kaisernova.modelo.base.MappedSuperAuditableActivableClass;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlTransient;
import lombok.Setter;
import lombok.ToString;
@XmlTransient
@MappedSuperclass
@Setter
//@ToString
public abstract class UsuarioBase  extends MappedSuperAuditableActivableClass{

	@Size(min = 6, max = 32, message = "Usuario debe tener una longitud de 6 a 32 caracteres")
	@Pattern(regexp = "^([a-zA-Z0-9@\\-_\\.]+\\s)*[a-zA-Z0-9@\\-_\\.]+$", message = "Usuario solo puede contener letras y un espacio entre palabras")
	protected String usuario;
	//esta es el hash de la clave que se guarda	
	protected String claveHash;

	@Size(min = 1, max=128, message = "Nombres debe tener una longitud de 1 a 128 caracteres")
	@Pattern(regexp = "^([a-zA-ZñÑáéíóúÁÉÍÓÚ]+\\s)*[a-zA-ZñÑáéíóúÁÉÍÓÚ]+$", message = "Nombres solo puede contener letras y un espacio entre palabras")
	protected String nombres;
	
	@Size(min = 1, max=128, message = "Apellidos debe tener una longitud de 1 a 128 caracteres")
	@Pattern(regexp = "^([a-zA-ZñÑáéíóúÁÉÍÓÚ]+\\s)*[a-zA-ZñÑáéíóúÁÉÍÓÚ]+$", message = "Apellidos solo puede contener letras y un espacio entre palabras")
	protected String apellidos;
	@Pattern(regexp = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$", message = "El Correo Electrónico debe ser tipo abcd@defg.com")
	@NotBlank(message = "El Correo Electrónico es requerido")	
	protected String email;
    @Pattern(regexp = "^\\+?[0-9]{8,17}$", message = "Teléfono debe contener solo números y puede incluir el código de país")
    @NotBlank(message = "El Teléfono es requerido")
    protected String telefono;

	@NotEmpty(message = "Es requerido seleccionar un rol")
	protected List<Rol> roles = new ArrayList<>();
	// esta es la clave original NO se guarda, se usa el campo para la confirmacion
	// y hasheo
	
	@Size(min = 8, max = 64, message = "Clave debe tener una longitud de 8 a 64 caracteres")
	protected String clave;
	


	@Column(name = "USUARIO",length = 32, nullable = false, unique = true)
	public String getUsuario() {
		return usuario;
	}

	@Column(name = "CLAVE_HASH", nullable = false)
	public String getClaveHash() {
		return claveHash;
	}

	@Column(name = "NOMBRES", nullable = false)
	public String getNombres() {
		return nombres;
	}

	@Column(name = "APELLIDOS", nullable = false)
	public String getApellidos() {
		return apellidos;
	}

	@Column(name = "EMAIL", nullable = false, unique = true, length = 256)
	public String getEmail() {
		return email;
	}
	
	@Column(name = "TELEFONO", nullable = true)
	public String getTelefono() {
		return telefono;
	}

	public abstract List<Rol> getRoles();

	@Transient
	public Set<String> getRolesStrings() {
		Set<String> rolesStrings = new HashSet();
		for (Rol rol : getRoles()) {
			rolesStrings.add(rol.getNombreRol().name());
		}
		return rolesStrings;
	}
	
	@Transient
	public String getRolesString() {
		Set<String> rolesStrings = new HashSet();
		for (Rol rol : getRoles()) {
			rolesStrings.add(rol.getNombreRol().name());
		}
		return rolesStrings.toString();
	}
	@Transient
	public String getClave() {
		return clave;
	}
	
	@Transient
	public String getApellidosNombres() {
		return this.getApellidos() + " " + this.getNombres();
	}
	@Transient
	public String getApellidosNombresUsuario() {
		return getApellidosNombres() + " [" + getUsuario() + "]";
	}

	@Override
	public String toString() {
		return "UsuarioBase [usuario=" + usuario + ", nombres=" + nombres + ", apellidos=" + apellidos + ", email="
				+ email + ", telefono=" + telefono + ", roles=" + roles + ", getRolesString()=" + getRolesString() + "]";
	}


}
