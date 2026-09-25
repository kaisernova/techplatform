package com.kaisernova.modelo.usuarios;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.kaisernova.modelo.base.MappedSuperClass;
import com.kaisernova.modelo.enums.NombreRol;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Entity
@Table(name = "ROL", schema = "usuarios")
public class Rol extends MappedSuperClass {

	private NombreRol nombreRol;
	@Id
	@Enumerated(EnumType.STRING)
	@Column(name = "NOMBRE_ROL", nullable = false, unique = true)
	public NombreRol getNombreRol() {
		return nombreRol;
	}
	@Override
	public String toString() {
		return nombreRol.name();
	}
	@Override
	public int hashCode() {
		return Objects.hash(nombreRol);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Rol other = (Rol) obj;
		return nombreRol == other.nombreRol;
	}

}
