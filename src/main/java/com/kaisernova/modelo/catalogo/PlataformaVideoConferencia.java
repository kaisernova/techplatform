package com.kaisernova.modelo.catalogo;

import java.time.LocalDateTime;

import com.kaisernova.modelo.base.MappedSuperAuditableActivableClass;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Entity
@Table(name = "PLATAFORMA_VIDEO_CONFERENCIA", schema = "catalogo")
public class PlataformaVideoConferencia extends MappedSuperAuditableActivableClass {
	private String codigo;
	private String nombre;
	private String descripcion;
	private String urlAcceso;
	private String apiKey;
	private String apiSecret;
	@Id
	@Column(name = "CODIGO", length = 32, unique = true, nullable = false)
	public String getCodigo() {
		return codigo;
	}
	
	@Column(name = "NOMBRE", length = 128, unique = true, nullable = false)
	public String getNombre() {
		return nombre;
	}
	@Column(name = "DESCRIPCION", length = 512, unique = false, nullable = true)
	public String getDescripcion() {
		return descripcion;
	}
	@Column(name = "URL_ACCESO", length = 256, unique = true, nullable = false)
	public String getUrlAcceso() {
		return urlAcceso;
	}
	@Column(name= "API_KEY", length = 1024, unique = false, nullable = true)
	public String getApiKey() {
		return apiKey;
	}
	@Column(name= "API_SECRET", length = 1024, unique = false, nullable = true)
	public String getApiSecret() {
		return apiSecret;
	}
	
}