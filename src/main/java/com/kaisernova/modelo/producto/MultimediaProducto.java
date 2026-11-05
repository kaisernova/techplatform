package com.kaisernova.modelo.producto;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
	private Map<TipoMultimedia, Set<String>> referenciasMultimedia= new ConcurrentHashMap<TipoMultimedia, Set<String>>();
}
