package com.kaisernova.modelo.catalogo;

import java.util.Objects;

import com.kaisernova.modelo.base.MappedSuperAuditableActivableClass;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Entity
@Table(name = "INSTITUCION_FINANCIERA", schema = "catalogo")
public class InstitucionFinanciera extends MappedSuperAuditableActivableClass {
	
	@NotBlank(message = "El código es requerido")
	private String codigo;
	@NotBlank(message = "El Valor es requerido")
	private String nombre;
	
	@Id
	@Column(name = "codigo", length = 64, unique = true, nullable = false)
	public String getCodigo() {
		return codigo;
	}
	@Column(name = "nombre", length = 255, unique = true, nullable = false)
	public String getNombre() {
		return nombre;
	}
	@Override
	public int hashCode() {
		return Objects.hash(codigo);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstitucionFinanciera other = (InstitucionFinanciera) obj;
		return Objects.equals(codigo, other.codigo);
	}
	@Override
	public String toString() {
		return codigo;
	}
	
}
