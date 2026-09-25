package com.kaisernova.modelo.ubicacion.geografica;

import com.kaisernova.modelo.base.MappedSuperClass;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "CIUDAD", schema = "ubicacion_geografica")
public class Ciudad extends MappedSuperClass {
	private Long idCiudad;
	private Provincia provincia;
	private String codigo;
	private String nombre;


	@Id
	@Column(name = "ID_CIUDAD")
	@GeneratedValue(generator = "ID_CIUDAD_SEQ", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "ID_CIUDAD_SEQ", sequenceName = "ID_CIUDAD_SEQ", allocationSize = 1)
	public Long getIdCiudad() {
		return idCiudad;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="ID_PROVINCIA")
	public Provincia getProvincia() {
		return provincia;
	}

	@Column(length = 8, nullable = false, unique = true)
	public String getCodigo() {
		return codigo;
	}
	
	@Column(nullable = false)
	public String getNombre() {
		return nombre;
	}
	@Transient
	public String getNombreProvinciaNombreCiudad() {
		return this.getProvincia().getNombre() + " - " + this.getNombre();
	}
	

	@Override
	public String toString() {
		return "Ciudad [idCiudad=" + idCiudad + ", provincia.codigo=" + provincia.getCodigo() + ", codigo=" + codigo + ", nombre=" + nombre
				+  "]";
	}
	
}
