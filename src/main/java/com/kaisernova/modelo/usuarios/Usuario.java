 package com.kaisernova.modelo.usuarios;

import java.util.List;
import java.util.Objects;

import com.kaisernova.modelo.enums.NombreRol;
import com.kaisernova.validador.constraints.Cedula;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Entity
@Table(name = "USUARIO", schema = "usuarios")
public class Usuario extends UsuarioBase {


	private Long idUsuario;
	@Cedula(message = "El número de identificación no es válido")
	private String numeroIdentificacion;
	

	public Usuario() {
		super();
		this.activo = true;		
	}

	@Id
	@Column(name = "ID_USUARIO")
	@GeneratedValue(generator = "ID_USUARIO_SEQ", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(schema = "public",name = "ID_USUARIO_SEQ", sequenceName = "ID_USUARIO_SEQ", allocationSize = 1)
	public Long getIdUsuario() {
		return idUsuario;
	}
	@Column(name = "NUMERO_IDENTIFICACION",length = 13,  nullable = false, unique = true)
	public String getNumeroIdentificacion() {
		return numeroIdentificacion;
	}
	

	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(schema = "usuarios", name = "usuario_x_rol", joinColumns = @JoinColumn(name = "ID_USUARIO"), inverseJoinColumns = @JoinColumn(name = "NOMBRE_ROL"))
	public List<Rol> getRoles() {
		return roles;
	}


	
	@Override
	public int hashCode() {
		return Objects.hash(idUsuario);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return Objects.equals(idUsuario, other.idUsuario);
	}

	
	@Transient
	public boolean isEsAdministrador() {
		for (Rol rol : this.getRoles()) {
			if (NombreRol.ADMINISTRADOR.equals(rol.getNombreRol())) {
				return true;
			}
		}
		return false;
	}
	
	
	
	@Transient
	public boolean isTieneId() {
		return Objects.nonNull(this.idUsuario);
	}
}
