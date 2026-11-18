package com.kaisernova.web.rest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.kaisernova.logica.producto.ImportacionCatalogoProductosBean;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/productos")
public class ProductoImportacionRest {

	private static final Logger LOG = Logger.getLogger(ProductoImportacionRest.class.getName());

	@Inject
	private ImportacionCatalogoProductosBean importacionCatalogoProductosBean;

	/**
	 * Temporal endpoint to start the product catalog import.
	 * Example: GET /api/productos/importar
	 */
	@GET
	@Path("/importar")
	@Produces(MediaType.APPLICATION_JSON)
	public Response importar() {
		try {
			ImportacionCatalogoProductosBean.ResultadoImportacion resultado =
					importacionCatalogoProductosBean.importarUltimosArchivos();

			Map<String, Object> body = new LinkedHashMap<>();
			body.put("success", resultado.errores.isEmpty());
			body.put("processed", resultado.productosProcesados);
			body.put("inserted", resultado.insertados);
			body.put("updated", resultado.actualizados);
			body.put("summary", resultado.resumen());
			body.put("errors", resultado.errores);

			LOG.info("[ProductoImportacionRest] GET /api/productos/importar: " + resultado.resumen()
					+ "; errores=" + resultado.errores.size());

			return Response.ok(body)
					.status(resultado.errores.isEmpty() ? Response.Status.OK : Response.Status.BAD_REQUEST)
					.build();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "[ProductoImportacionRest] error ejecutando importacion", e);
			Map<String, Object> body = new LinkedHashMap<>();
			body.put("success", false);
			body.put("error", e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(body).build();
		}
	}
}
