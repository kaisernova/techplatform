package com.kaisernova.modelo.base;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.xml.bind.annotation.XmlTransient;

import lombok.Setter;
import lombok.ToString;

@XmlTransient
@MappedSuperclass
@Setter
@ToString
public class MappedSuperAuditableClass extends MappedSuperClass {
	@XmlTransient private String usuarioCrea;
	@XmlTransient private String usuarioActualiza;
	@JsonIgnore
	@XmlTransient private String tipoUsuarioCrea;
	@JsonIgnore
	@XmlTransient private String tipoUsuarioActualiza;
	
	@Basic
	@Column(name = "usuario_crea", nullable = false)
	public String getUsuarioCrea() {
		return usuarioCrea;
	}
	@Basic
	@Column(name = "usuario_actualiza", nullable = false)
	public String getUsuarioActualiza() {
		return usuarioActualiza;
	}
	@Column(name = "TIPO_USUARIO_CREA", length = 128)
	public String getTipoUsuarioCrea() {
		return tipoUsuarioCrea;
	}
	@Column(name = "TIPO_USUARIO_ACTUALIZA", length = 128)
	public String getTipoUsuarioActualiza() {
		return tipoUsuarioActualiza;
	}
}
