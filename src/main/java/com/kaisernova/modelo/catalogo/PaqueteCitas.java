package com.kaisernova.modelo.catalogo;

import java.math.BigDecimal;
import java.util.Objects;

import com.kaisernova.modelo.base.MappedSuperAuditableActivableClass;
import com.kaisernova.modelo.enums.TipoPaqueteCita;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor

@Setter
@Entity
@Table(name = "PAQUETE_CITAS", schema = "catalogo", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "NUMERO_CITAS", "TIPO_CITA" }) })
public class PaqueteCitas extends MappedSuperAuditableActivableClass {
	private String codigo;
	private String nombre;
	private Integer numeroCitas;
	private String descripcion;
	private BigDecimal precio;
	private TipoPaqueteCita tipoPaqueteCita=TipoPaqueteCita.NORMAL;

	@Id
	@Column(name = "CODIGO", length = 64, unique = true, nullable = false)
	public String getCodigo() {
		return codigo;
	}

	@Column(name = "NOMBRE", length = 255, unique = true, nullable = false)
	public String getNombre() {
		return this.nombre;
	}

	@Column(name = "NUMERO_CITAS", nullable = false)
	public Integer getNumeroCitas() {
		return this.numeroCitas;
	}

	@Column(name = "DESCRIPCION", length = 1024, unique = false, nullable = true)
	public String getDescripcion() {
		return descripcion;
	}

	@Column(name = "PRECIO", precision = 10, scale = 2, unique = false, nullable = false)
	public BigDecimal getPrecio() {
		return this.precio;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "TIPO_PAQUETE_CITA", length = 64, nullable = false)
	public TipoPaqueteCita getTipoPaqueteCita() {
		return tipoPaqueteCita;
	}

	@Transient
	public String getPrecioFormateadoUSD() {
		return String.format("US$%,.2f", precio);
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
		PaqueteCitas other = (PaqueteCitas) obj;
		return Objects.equals(codigo, other.codigo);
	}

	@Override
	public String toString() {
		return "PaqueteCitas [codigo=" + codigo + ", nombre=" + nombre + ", numeroCitas=" + numeroCitas
				+ ", descripcion=" + descripcion + ", precio=" + precio + "]";
	}

}
