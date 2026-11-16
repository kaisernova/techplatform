package com.kaisernova.modelo.producto;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaisernova.modelo.enums.TipoMultimedia;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MultimediaProducto implements Serializable {
	private Map<TipoMultimedia, Set<String>> referenciasMultimedia = new ConcurrentHashMap<>();

	public static MultimediaProducto fromJson(String json) {
		if (json == null || json.isBlank()) {
			return new MultimediaProducto();
		}
		try {
			String trimmed = json.trim();
			ObjectMapper mapper = new ObjectMapper();
			if (trimmed.startsWith("[") || trimmed.startsWith("\"")) {
				Set<String> imagenes = new java.util.HashSet<>();
				if (trimmed.startsWith("[")) {
					java.util.List<String> lista = mapper.readValue(trimmed, new TypeReference<java.util.List<String>>() {});
					imagenes.addAll(lista);
				} else {
					imagenes.add(trimmed.replaceAll("^\"|\"$", ""));
				}
				MultimediaProducto multimedia = new MultimediaProducto();
				multimedia.getReferenciasMultimedia().put(TipoMultimedia.IMG, imagenes);
				return multimedia;
			}
			Map<String, Set<String>> map = mapper.readValue(trimmed, new TypeReference<Map<String, Set<String>>>() {});
			MultimediaProducto multimedia = new MultimediaProducto();
			if (map != null) {
				for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
					if (entry == null || entry.getKey() == null || entry.getValue() == null) {
						continue;
					}
					try {
						TipoMultimedia tipo = TipoMultimedia.valueOf(entry.getKey().trim().toUpperCase());
						multimedia.getReferenciasMultimedia().put(tipo, entry.getValue());
					} catch (IllegalArgumentException ignored) {
						// ignore unknown types
					}
				}
			}
			return multimedia;
		} catch (Exception e) {
			return new MultimediaProducto();
		}
	}

	public String toJson() {
		try {
			return new ObjectMapper().writeValueAsString(referenciasMultimedia);
		} catch (Exception e) {
			return null;
		}
	}
}