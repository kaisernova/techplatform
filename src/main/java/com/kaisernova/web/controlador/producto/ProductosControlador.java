package com.kaisernova.web.controlador.producto;

import com.kaisernova.logica.producto.CategoriaProductosBean;
import com.kaisernova.logica.producto.OrderDirection;
import com.kaisernova.logica.producto.OrderField;
import com.kaisernova.logica.producto.ProductoBean;
import com.kaisernova.logica.producto.ProductoEnMemoriaBean;
import com.kaisernova.modelo.producto.CategoriaProductos;
import com.kaisernova.modelo.producto.Producto;
import com.kaisernova.web.controlador.BaseControlador;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

@Named
@ViewScoped
public class ProductosControlador extends BaseControlador implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProductoBean productoBean;

    @Inject
    private CategoriaProductosBean categoriaProductosBean;

    @Inject
    private ProductoEnMemoriaBean productoEnMemoriaBean;

    private List<Producto> productos;
    private List<Producto> productosFiltrados = new ArrayList<>();
    private List<CategoriaProductos> categorias;
    private String filtroTexto = "";
    private String ordenSeleccionado = "NOMBRE_ASC";
    private String layout = "grid";

    @PostConstruct
    public void inicializar() {
        categorias = categoriaProductosBean.obtenerTodosActivos();
        productoEnMemoriaBean.recargar();
        productos = new ArrayList<>(productoEnMemoriaBean.obtenerTodos());
        aplicarFiltros();
    }

    public void aplicarFiltros() {
        if (productoEnMemoriaBean == null) {
            productosFiltrados = new ArrayList<>();
            return;
        }

        String texto = filtroTexto == null ? "" : filtroTexto.trim();
        if (texto == null || texto.isBlank()) {
            productosFiltrados = new ArrayList<>(productoEnMemoriaBean.obtenerTodos());
        } else {
            LinkedHashMap<Long, Producto> resultado = productoEnMemoriaBean.buscarPorNombreMarcaDescripcionIndexed(
                    texto,
                    mapOrderField(ordenSeleccionado),
                    mapOrderDirection(ordenSeleccionado),
                    0
            );
            productosFiltrados = new ArrayList<>(resultado.values());
        }

        if (productosFiltrados == null || productosFiltrados.isEmpty()) {
            productosFiltrados = new ArrayList<>();
        }
    }

    public void aplicarOrden() {
        aplicarFiltros();
    }

    private OrderField mapOrderField(String orden) {
        if (orden == null) {
            return OrderField.NOMBRE;
        }
        switch (orden) {
            case "PRECIO_ASC":
            case "PRECIO_DESC":
                return OrderField.PRECIO;
            case "MARCA_ASC":
                return OrderField.MARCA;
            case "NUEVOS":
                return OrderField.FECHA_HORA_CREACION;
            case "NOMBRE_ASC":
            default:
                return OrderField.NOMBRE;
        }
    }

    private OrderDirection mapOrderDirection(String orden) {
        if (orden == null) {
            return OrderDirection.ASC;
        }
        switch (orden) {
            case "PRECIO_DESC":
                return OrderDirection.DESC;
            case "NUEVOS":
                return OrderDirection.DESC;
            case "MARCA_ASC":
            case "PRECIO_ASC":
            case "NOMBRE_ASC":
            default:
                return OrderDirection.ASC;
        }
    }

    public void cambiarVistaGrid() {
        this.layout = "grid";
    }

    public void cambiarVistaLista() {
        this.layout = "list";
    }

    public String obtenerNombreCategoria(String codigoCategoria) {
        if (codigoCategoria == null || categorias == null) {
            return "Sin categoría";
        }
        return categorias.stream()
                .filter(categoria -> codigoCategoria.equals(categoria.getCodigo()))
                .map(CategoriaProductos::getNombre)
                .findFirst()
                .orElse("Sin categoría");
    }

    public String getPrecioFormateado(Producto producto) {
        if (producto == null || producto.getPrecio() == null) {
            return "Consultar";
        }
        return String.format(Locale.US, "$%.2f", producto.getPrecio());
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public List<Producto> getProductosFiltrados() {
        return productosFiltrados;
    }

    public void setProductosFiltrados(List<Producto> productosFiltrados) {
        this.productosFiltrados = productosFiltrados;
    }

    public List<CategoriaProductos> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<CategoriaProductos> categorias) {
        this.categorias = categorias;
    }

    public String getFiltroTexto() {
        return filtroTexto;
    }

    public void setFiltroTexto(String filtroTexto) {
        this.filtroTexto = filtroTexto;
    }

    public String getOrdenSeleccionado() {
        return ordenSeleccionado;
    }

    public void setOrdenSeleccionado(String ordenSeleccionado) {
        this.ordenSeleccionado = ordenSeleccionado;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }
}