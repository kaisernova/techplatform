package com.kaisernova.modelo.base;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.xml.bind.annotation.XmlTransient;

import lombok.Setter;
import lombok.ToString;

@XmlTransient
@MappedSuperclass
//@Getter
@Setter
@ToString
public class MappedSuperClass implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 101L;
	@JsonIgnore
	@XmlTransient
	private LocalDateTime fechaHoraCreacion;
	@JsonIgnore
	@XmlTransient
	private LocalDateTime fechaHoraActualizacion;

	@PrePersist
	public void prePersist() {
		fechaHoraCreacion = LocalDateTime.now();
		fechaHoraActualizacion = LocalDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		fechaHoraActualizacion = LocalDateTime.now();
	}
	
	@Basic
	@Column(name = "FECHA_HORA_CREACION")
	public LocalDateTime getFechaHoraCreacion() {
		return fechaHoraCreacion;
	}
	@Basic
	@Column(name = "FECHA_HORA_ACTUALIZACION")
	public LocalDateTime getFechaHoraActualizacion() {
		return fechaHoraActualizacion;
	}

}
