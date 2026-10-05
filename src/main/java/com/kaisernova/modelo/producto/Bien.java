package com.kaisernova.modelo.producto;

import java.util.Set;

import com.kaisernova.modelo.base.MappedSuperAuditableActivableClass;
import com.kaisernova.modelo.enums.NombreParametroConfiguracion;
import com.kaisernova.modelo.enums.TipoDato;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Setter;
@Setter
@Entity
@Table(name = "BIEN", schema = "productos")
public class Bien extends MappedSuperAuditableActivableClass {
	private Long idBien;
	

	@Id
	@Column(name = "ID_BIEN")
	@GeneratedValue(generator = "ID_BIEN_SEQ", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(schema = "public",name = "ID_BIEN_SEQ", sequenceName = "ID_BIEN_SEQ", allocationSize = 1)
	public Long getIdBien() {
		return idBien;
	}
}
