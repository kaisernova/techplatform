package com.kaisernova.modelo.catalogo;


import java.util.Objects;

import com.kaisernova.modelo.base.MappedSuperAuditableActivableClass;
import com.kaisernova.util.aplicacion.UtilAplicacion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor

@Setter
@Entity
@Table(name = "ESPECIALIDAD", schema = "catalogo")
public class Especialidad extends MappedSuperAuditableActivableClass{
	private String codigo;
	private String nombre;
	private String descripcion;
	private String urlVideo;
	@Id
	@Column(name = "CODIGO", length = 64, unique = true, nullable = false)
	public String getCodigo() {
		return codigo;
	}
	@Column(name = "NOMBRE", length = 255, unique = true, nullable = false)
	public String getNombre() {
		return nombre;
	}
	@Column(name = "DESCRIPCION", length = 1024, unique = false, nullable = true)
	public String getDescripcion() {
		return descripcion;
	}
	@Column(name = "URL_VIDEO", length = 512, unique = false, nullable = true)
	public String getUrlVideo() {
		return urlVideo;
	}
	
	/**
	 * check if urlVideo is a youtube link and convert it to embed format, remember
	 * there are many formats of youtube links, this method only checks for the most common one, you can add more checks if needed
	 * @return
	 */
	@Transient
	public String getUrlVideoYoutubeEmbed() {
		return UtilAplicacion.obtenerUrlYoutubeEmbed(urlVideo);
	}
	
	@Transient
	public String getVideoIdYoutube() {
		return UtilAplicacion.obtenerVideoIdDeUrlYoutube(urlVideo);
	}
	@Override
	public String toString() {
		return codigo;
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
		Especialidad other = (Especialidad) obj;
		return Objects.equals(codigo, other.codigo);
	}
	
	
}