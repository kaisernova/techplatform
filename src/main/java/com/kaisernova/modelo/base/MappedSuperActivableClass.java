package com.kaisernova.modelo.base;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kaisernova.modelo.enums.SiNo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlTransient;

import lombok.Setter;
import lombok.ToString;

@XmlTransient
@MappedSuperclass
@Setter
@ToString
public class MappedSuperActivableClass extends MappedSuperClass {
	@JsonIgnore
	protected boolean activo=true;
	@Basic
	@Column(name = "activo")
	public boolean isActivo() {
		return activo;
	}
	@Transient
	public SiNo getActivoSiNo() {
		if(Objects.nonNull(activo) && activo) {
			return SiNo.SI;
		}
		return SiNo.NO;
	}

}
