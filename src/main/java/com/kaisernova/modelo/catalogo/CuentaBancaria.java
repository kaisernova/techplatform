package com.kaisernova.modelo.catalogo;


import java.util.Objects;

import org.primefaces.model.file.UploadedFile;

import com.kaisernova.modelo.base.MappedSuperAuditableActivableClass;
import com.kaisernova.modelo.enums.MimeType;
import com.kaisernova.modelo.enums.TipoCuentaBancaria;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Entity
@Table(name = "CUENTA_BANCARIA", schema = "catalogo",  uniqueConstraints = {
		@UniqueConstraint(columnNames = { "NUMERO_CUENTA", "TIPO_CUENTA_BANCARIA", "CODIGO_INSTITUCION_FINANCIERA" }) })
public class CuentaBancaria extends MappedSuperAuditableActivableClass {
	private Long idCuentaBancaria;
	@NotBlank(message = "El número de cuenta es requerido")
	private String numeroCuenta;
	@NotBlank(message = "El titular de cuenta es requerido")
	private String titular;
	private TipoCuentaBancaria tipoCuentaBancaria=TipoCuentaBancaria.AHORROS;
	private InstitucionFinanciera institucionFinanciera;
	private String imagenBase64;
	private String extensionImagenBase64;
	@Transient
	private UploadedFile imagenUpload;
	
	@Transient
	public UploadedFile getImagenUpload() {
		return imagenUpload;
	}
	
	@Id
	@Column(name = "ID_CUENTA_BANCARIA")
	@GeneratedValue(generator = "ID_CUENTA_BANCARIA_SEQ", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(schema = "public",name = "ID_CUENTA_BANCARIA_SEQ", sequenceName = "ID_CUENTA_BANCARIA_SEQ", allocationSize = 1)
	public Long getIdCuentaBancaria() {
		return idCuentaBancaria;
	}
	@Column(name = "NUMERO_CUENTA", length = 128, nullable = false)
	public String getNumeroCuenta() {
		return numeroCuenta;
	}
	@Enumerated(EnumType.STRING)
	@Column(name = "TIPO_CUENTA_BANCARIA", length = 128, nullable = false)
	public TipoCuentaBancaria getTipoCuentaBancaria() {
		return tipoCuentaBancaria;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="CODIGO_INSTITUCION_FINANCIERA")
	public InstitucionFinanciera getInstitucionFinanciera() {
		return institucionFinanciera;
	}
	@Column(name = "titular", length = 255, nullable = false)
	public String getTitular() {
		return titular;
	}
	@Lob
	@Column(name = "IMAGEN_BASE64")
	public String getImagenBase64() {
		return imagenBase64;
	}
	@Column(name = "EXTENSION_IMAGEN_BASE64", length = 8)
	public String getExtensionImagenBase64() {
		return extensionImagenBase64;
	}
	
	@Override
	public String toString() {
		return "["+institucionFinanciera +"] " + numeroCuenta + "/" + tipoCuentaBancaria+"/"+titular;
	}
	@Transient
	public String getSrcImagenBase64() {
		if(Objects.nonNull(imagenBase64) && !imagenBase64.isEmpty()) {
			return "data:"+getContentType()+";base64,"+this.imagenBase64;
		}
		return "";
	}
	@Transient
	public String getContentType() {
		return MimeType.obtenerContentTypePorExtension(extensionImagenBase64);
	}

}
