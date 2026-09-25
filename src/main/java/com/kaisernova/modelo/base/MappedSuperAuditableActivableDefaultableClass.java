package com.kaisernova.modelo.base;

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
public class MappedSuperAuditableActivableDefaultableClass extends MappedSuperAuditableActivableClass {
	protected boolean elementoPorDefecto;
	@Basic
	@Column(name = "elemento_por_defecto")
	public boolean isElementoPorDefecto() {
		return elementoPorDefecto;
	}	
}