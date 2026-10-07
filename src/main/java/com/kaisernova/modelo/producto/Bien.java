package com.kaisernova.modelo.producto;

import java.math.BigDecimal;
import java.util.Set;

import com.kaisernova.modelo.base.MappedSuperAuditableActivableClass;
import com.kaisernova.modelo.enums.NombreParametroConfiguracion;
import com.kaisernova.modelo.enums.TipoDato;
import com.kaisernova.modelo.enums.UnidadMedidaLongitud;
import com.kaisernova.modelo.enums.UnidadMedidaPeso;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	private String codigoCategoria;
	private String codigo;
	private String descripcion;
	private String marca;
	private BigDecimal precio;
	private BigDecimal precioReducido;	
	private BigDecimal peso;
	private UnidadMedidaPeso unidadMedidaPeso;
	private BigDecimal largo;
	private UnidadMedidaLongitud unidadMedidaLargo;
	private BigDecimal ancho;
	private UnidadMedidaLongitud unidadMedidaAncho;
	private BigDecimal altura;
	private UnidadMedidaLongitud unidadMedidaAltura;
	private String tags;
	@Id
	@Column(name = "ID_BIEN")
	@GeneratedValue(generator = "ID_BIEN_SEQ", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(schema = "public",name = "ID_BIEN_SEQ", sequenceName = "ID_BIEN_SEQ", allocationSize = 1)
	public Long getIdBien() {
		return idBien;
	}
    
			@Basic
			@Column(name = "codigo_categoria", length = 64)
			public String getCodigoCategoria() {
				return codigoCategoria;
			}

			@Basic
			@Column(name = "codigo", length = 64, nullable = false, unique = true)
			public String getCodigo() {
				return codigo;
			}

			@Basic
			@Column(name = "descripcion", length = 1024)
			public String getDescripcion() {
				return descripcion;
			}

			@Basic
			@Column(name = "marca", length = 128)
			public String getMarca() {
				return marca;
			}

			@Basic
			@Column(name = "precio", precision = 16, scale = 2)
			public BigDecimal getPrecio() {
				return precio;
			}

			@Basic
			@Column(name = "precio_reducido", precision = 16, scale = 2)
			public BigDecimal getPrecioReducido() {
				return precioReducido;
			}

			@Basic
			@Column(name = "peso", precision = 16, scale = 3)
			public BigDecimal getPeso() {
				return peso;
			}

			@Enumerated(EnumType.STRING)
			@Column(name = "unidad_medida_peso", length = 32)
			public UnidadMedidaPeso getUnidadMedidaPeso() {
				return unidadMedidaPeso;
			}

			@Basic
			@Column(name = "largo", precision = 16, scale = 3)
			public BigDecimal getLargo() {
				return largo;
			}

			@Enumerated(EnumType.STRING)
			@Column(name = "unidad_medida_largo", length = 32)
			public UnidadMedidaLongitud getUnidadMedidaLargo() {
				return unidadMedidaLargo;
			}

			@Basic
			@Column(name = "ancho", precision = 16, scale = 3)
			public BigDecimal getAncho() {
				return ancho;
			}

			@Enumerated(EnumType.STRING)
			@Column(name = "unidad_medida_ancho", length = 32)
			public UnidadMedidaLongitud getUnidadMedidaAncho() {
				return unidadMedidaAncho;
			}

			@Basic
			@Column(name = "altura", precision = 16, scale = 3)
			public BigDecimal getAltura() {
				return altura;
			}

			@Enumerated(EnumType.STRING)
			@Column(name = "unidad_medida_altura", length = 32)
			public UnidadMedidaLongitud getUnidadMedidaAltura() {
				return unidadMedidaAltura;
			}

			@Basic
			@Column(name = "tags", length = 1024)
			public String getTags() {
				return tags;
			}
}
