package com.kaisernova.modelo.ubicacion.geografica;

import com.kaisernova.modelo.base.MappedSuperActivableClass;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor

@Setter
@Entity
@Table(name = "PAIS_NACIONALIDAD", schema = "ubicacion_geografica")
public class PaisNacionalidad extends MappedSuperActivableClass {
	
	private String codigo;
	private String pais;
	private String nacionalidad;
	
	@Id
	@Column(name = "CODIGO")	
	public String getCodigo() {
		return codigo;
	}
	@Column(nullable = false, unique = true)
	public String getPais() {
		return pais;
	}
	@Column(nullable = false, unique = true)
	public String getNacionalidad() {
		return nacionalidad;
	}
	@Transient
	public String getPaisNacionalidad() {
		return pais + " - " + nacionalidad;
	}
}
