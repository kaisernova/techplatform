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
public class MappedSuperAuditableActivableClass  extends MappedSuperActivableClass {
	@JsonIgnore
	@XmlTransient private String usuarioCrea;
	@JsonIgnore
	@XmlTransient private String usuarioActualiza;

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
	
}
