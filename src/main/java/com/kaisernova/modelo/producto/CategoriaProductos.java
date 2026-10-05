package com.kaisernova.modelo.producto;

import com.kaisernova.modelo.base.MappedSuperAuditableActivableClass;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Entity
@Table(name = "CATEGORIA_PRODUCTOS", schema = "productos", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "codigo", "codigo_padre" }) })
public class CategoriaProductos  extends MappedSuperAuditableActivableClass {
	private Long idCategoriaProductos;
	
	private String codigo;
	private String nombre;
	private String descripcion;
	private String codigoPadre;
	

	@Id
	@Column(name = "ID_CATEGORIA_PRODUCTOS")
	@GeneratedValue(generator = "ID_CATEGORIA_PRODUCTOS_SEQ", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(schema = "public",name = "ID_CATEGORIA_PRODUCTOS_SEQ", sequenceName = "ID_CATEGORIA_PRODUCTOS_SEQ", allocationSize = 1)
	public Long getIdCategoriaProductos() {
		return idCategoriaProductos;
	}

	@Column(name = "codigo", nullable = false, length = 64)
	public String getCodigo() {
		return codigo;
	}

	@Column(name = "nombre", unique = true, nullable = false, length = 128)
	public String getNombre() {
		return nombre;
	}

	@Column(name = "descripcion", length = 1024)
	public String getDescripcion() {
		return descripcion;
	}

	@Column(name = "codigo", length = 64)
	public String getCodigoPadre() {
		return codigoPadre;
	}
	
	
	
}
