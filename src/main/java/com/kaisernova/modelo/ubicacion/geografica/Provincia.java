package com.kaisernova.modelo.ubicacion.geografica;

import java.util.List;

import com.kaisernova.modelo.base.MappedSuperClass;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor

@Setter
@Entity
@Table(name = "PROVINCIA", schema = "ubicacion_geografica")
public class Provincia  extends MappedSuperClass {
	private Long idProvincia;
	private String codigo;
	private String nombre;

	private List<Ciudad> ciudades;
	@Id
	@Column(name = "ID_PROVINCIA")
	@GeneratedValue(generator = "ID_PROVINCIA_SEQ", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "ID_PROVINCIA_SEQ", sequenceName = "ID_PROVINCIA_SEQ", allocationSize = 1)
	public Long getIdProvincia() {
		return idProvincia;
	}
	
	@OneToMany
	@JoinColumn(name = "ID_PROVINCIA")
	public List<Ciudad> getCiudades() {
		return ciudades;
	}
	
	@Column(nullable = false, unique = true)
	public String getCodigo() {
		return codigo;
	}
	
	@Column(nullable = false, unique = true)
	public String getNombre() {
		return nombre;
	}
}
