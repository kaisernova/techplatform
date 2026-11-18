package com.kaisernova.logica.producto;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.kaisernova.modelo.enums.TipoMultimedia;
import com.kaisernova.modelo.producto.CategoriaProductos;
import com.kaisernova.modelo.producto.MultimediaProducto;
import com.kaisernova.modelo.producto.Producto;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;

@Stateless
public class ImportacionCatalogoProductosBean {

	private static final Logger LOG = Logger.getLogger(ImportacionCatalogoProductosBean.class.getName());
	private static final String CATEGORIA_TEC = "TEC";
	private static final int THUMB_WIDTH = 300;
	private static final int THUMB_HEIGHT = 300;

	@Inject
	private ConfiguracionImagenesBean configuracionImagenes;
	@Inject
	private ProductoBean productoBean;
	@Inject
	private CategoriaProductosBean categoriaProductosBean;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ResultadoImportacion importarUltimosArchivos() {
		ResultadoImportacion resultado = new ResultadoImportacion();
		Path dirUploads;
		try {
			dirUploads = Paths.get(configuracionImagenes.dirUploadsProductos()).toAbsolutePath().normalize();
			Files.createDirectories(dirUploads);
			LOG.info("[ImportacionCatalogo] directorio uploads=" + dirUploads);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "[ImportacionCatalogo] no se pudo preparar directorio uploads", e);
			resultado.errores.add("No se pudo acceder al directorio de uploads: " + e.getMessage());
			return resultado;
		}

		Path xlsx = ultimoArchivo(dirUploads, ".xlsx");
		Path zip = ultimoArchivo(dirUploads, ".zip");
		LOG.info("[ImportacionCatalogo] ultimo xlsx=" + xlsx + ", ultimo zip=" + zip);
		if (xlsx == null) {
			resultado.errores.add("No se encontró un archivo XLSX en " + dirUploads);
			return resultado;
		}

		CategoriaProductos categoria = obtenerCrearTecnologia();
		Map<String, List<String>> imagenesPorCodigo = new LinkedHashMap<>();
		Path tempDir = null;
		try {
			if (zip != null) {
				tempDir = Files.createTempDirectory("techplatform-catalogo-");
				imagenesPorCodigo = extraerAsociarZip(zip, tempDir);
				LOG.info("[ImportacionCatalogo] imagenes asociadas=" + imagenesPorCodigo.keySet());
			}

			try (InputStream in = Files.newInputStream(xlsx); Workbook workbook = WorkbookFactory.create(in)) {
				Sheet sheet = workbook.getSheetAt(0);
				if (sheet.getPhysicalNumberOfRows() < 2) {
					resultado.errores.add("El XLSX no contiene filas de productos.");
					return resultado;
				}

				Row header = sheet.getRow(sheet.getFirstRowNum());
				Map<String, Integer> columnas = mapearEncabezados(header);
				Integer colCodigo = columnas.get("CODIGO");
				Integer colNombre = columnas.get("PRODUCTO");
				if (colCodigo == null || colNombre == null) {
					resultado.errores.add("El XLSX debe contener las columnas CODIGO y PRODUCTO.");
					return resultado;
				}

				for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
					Row row = sheet.getRow(i);
					if (row == null) continue;
					String codigoOriginal = texto(row, colCodigo);
					String nombre = texto(row, colNombre);
					if (codigoOriginal.isBlank() || nombre.isBlank()) continue;

					String codigoNormalizado = normalizarCodigo(codigoOriginal);
					Producto producto = productoBean.obtenerPorCodigo(codigoOriginal.trim());
					boolean nuevo = producto == null;
					if (nuevo) producto = new Producto();

					producto.setCodigo(codigoOriginal.trim());
					producto.setNombre(nombre);
					producto.setDescripcion(nombre);
					producto.setMarca(inferirMarca(nombre));
					producto.setCodigoCategoria(CATEGORIA_TEC);
					producto.setPrecio(obtenerPrecio(row, columnas));
					producto.setPrecioReducido(producto.getPrecio());
					producto.setTags("tecnologia,importado");
					producto.setActivo(true);
					producto.setUsuarioCrea(nuevo ? "import-catalogo" : producto.getUsuarioCrea());
					producto.setUsuarioActualiza("import-catalogo");
					producto.setFechaHoraActualizacion(LocalDateTime.now());

					List<String> archivosCodigo = imagenesPorCodigo.get(codigoNormalizado);
					MultimediaProducto multimedia = new MultimediaProducto();
					Set<String> imagenes = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
					if (archivosCodigo != null) {
						for (String nombreArchivo : archivosCodigo) imagenes.add(nombreArchivo);
					}
					multimedia.getReferenciasMultimedia().put(TipoMultimedia.IMG, imagenes);
					producto.setMultimediaProductoJson(multimedia.toJson());
					producto.setMultimediaProducto(multimedia);

					if (nuevo) productoBean.crear(producto);
					else productoBean.actualizar(producto);

					resultado.insertados += nuevo ? 1 : 0;
					resultado.actualizados += nuevo ? 0 : 1;
					resultado.productosProcesados++;
					LOG.info("[ImportacionCatalogo] " + (nuevo ? "insertado" : "actualizado") + " codigo=" + codigoOriginal);
				}
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "[ImportacionCatalogo] error importando catálogo", e);
			resultado.errores.add("Error importando catálogo: " + e.getMessage());
		} finally {
			if (tempDir != null) {
				try { borrarRecursivo(tempDir); } catch (IOException e) {
					LOG.log(Level.WARNING, "[ImportacionCatalogo] no se pudo borrar temporal " + tempDir, e);
				}
			}
		}
		return resultado;
	}

	private CategoriaProductos obtenerCrearTecnologia() {
		CategoriaProductos categoria = categoriaProductosBean.obtenerPorCodigo(CATEGORIA_TEC);
		if (categoria != null) return categoria;
		categoria = new CategoriaProductos();
		categoria.setCodigo(CATEGORIA_TEC);
		categoria.setNombre("Tecnología");
		categoria.setDescripcion("Categoría predeterminada para productos importados del catálogo masivo");
		categoria.setCodigoPadre(null);
		categoria.setActivo(true);
		categoria.setUsuarioCrea("import-catalogo");
		categoria.setUsuarioActualiza("import-catalogo");
		categoria.setFechaHoraActualizacion(LocalDateTime.now());
		return categoriaProductosBean.crear(categoria);
	}

	private Path ultimoArchivo(Path dir, String extension) {
		try (var stream = Files.list(dir)) {
			return stream
					.filter(Files::isRegularFile)
					.filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(extension))
					.max(Comparator.comparingLong(p -> {
						try { return Files.getLastModifiedTime(p).toMillis(); } catch (IOException e) { return 0L; }
					}))
					.orElse(null);
		} catch (IOException e) {
			LOG.log(Level.WARNING, "[ImportacionCatalogo] no se pudo listar " + dir, e);
			return null;
		}
	}

	private Map<String, Integer> mapearEncabezados(Row header) {
		Map<String, Integer> result = new LinkedHashMap<>();
		if (header == null) return result;
		for (int i = header.getFirstCellNum(); i <= header.getLastCellNum(); i++) {
			String h = texto(header, i).toUpperCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
			if (h.equals("CODIGO") || h.startsWith("CODIGO ")) result.put("CODIGO", i);
			if (h.equals("PRODUCTO") || h.startsWith("PRODUCTO ")) result.put("PRODUCTO", i);
			if (h.contains("PROPUEST") || h.contains("PROPUESTA")) result.put("PRECIO_PROPUESTO", i);
		}
		if (!result.containsKey("PRECIO_PROPUESTO")) {
			for (int i = header.getFirstCellNum(); i <= header.getLastCellNum(); i++) {
				String h = texto(header, i).toUpperCase(Locale.ROOT).trim();
				if (h.equals("P1") || h.startsWith("P1 ")) {
					result.put("PRECIO_PROPUESTO", i);
					break;
				}
			}
		}
		return result;
	}

	private BigDecimal obtenerPrecio(Row row, Map<String, Integer> columnas) {
		Integer col = columnas.get("PRECIO_PROPUESTO");
		if (col == null) {
			col = columnas.get("P1");
		}
		if (col == null) col = 8;
		BigDecimal valor = decimal(row, col);
		if (valor == null) valor = decimal(row, 2);
		if (valor == null) valor = BigDecimal.ZERO;
		return valor;
	}

	private Map<String, List<String>> extraerAsociarZip(Path zip, Path tempDir) throws IOException {
		Map<String, List<String>> result = new TreeMap<>();
		Path dirLocal = Paths.get(configuracionImagenes.dirLocal()).toAbsolutePath().normalize();
		Files.createDirectories(dirLocal);

		// Extract safely in a temporary directory. The caller removes this directory in finally.
		try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zip))) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (entry.isDirectory()) continue;
				String relative = entry.getName().replace('\\', '/');
				while (relative.startsWith("/")) relative = relative.substring(1);
				Path extracted = tempDir.resolve(relative).normalize();
				if (!extracted.startsWith(tempDir)) {
					LOG.warning("[ImportacionCatalogo] entrada ZIP insegura ignorada: " + entry.getName());
					continue;
				}
				Files.createDirectories(extracted.getParent());
				Files.copy(zis, extracted, StandardCopyOption.REPLACE_EXISTING);
				zis.closeEntry();
			}
		}

		try (var paths = Files.walk(tempDir)) {
			List<Path> imagenes = paths
					.filter(Files::isRegularFile)
					.filter(p -> esImagen(p.getFileName().toString()))
					.sorted(Comparator.comparing(p -> p.getFileName().toString(), String.CASE_INSENSITIVE_ORDER))
					.toList();

			for (Path image : imagenes) {
				String fileName = image.getFileName().toString();
				String base = fileName.replaceFirst("(?i)\\.[a-z0-9]+$", "");
				Matcher m = Pattern.compile("^(.*?)[_-](\\d+)$").matcher(base);
				String prefijoZip = m.matches() ? m.group(1) : base;
				String codigo = normalizarCodigo(prefijoZip);
				if (codigo.isBlank()) continue;

				int secuencia;
				if (m.matches()) {
					try { secuencia = Integer.parseInt(m.group(2)); }
					catch (NumberFormatException e) { secuencia = 1; }
				} else {
					secuencia = result.computeIfAbsent(codigo, k -> new ArrayList<>()).size() + 1;
				}

				String extension = extension(fileName);
				String nombreSeguro = codigo + "_" + String.format(Locale.ROOT, "%02d", secuencia) + extension;
				Path destino = dirLocal.resolve(nombreSeguro).normalize();
				if (!destino.startsWith(dirLocal)) continue;

				Files.copy(image, destino, StandardCopyOption.REPLACE_EXISTING);
				crearThumbnail(destino, codigo, secuencia, extension);
				result.computeIfAbsent(codigo, k -> new ArrayList<>()).add(nombreSeguro);
				LOG.info("[ImportacionCatalogo] imagen asociada codigo=" + codigo + ", archivo=" + nombreSeguro);
			}
		}
		return result;
	}

	private void crearThumbnail(Path original, String codigo, int secuencia, String extension) {
		try {
			BufferedImage source = ImageIO.read(original.toFile());
			if (source == null) return;
			int type = BufferedImage.TYPE_INT_RGB;
			BufferedImage thumb = new BufferedImage(THUMB_WIDTH, THUMB_HEIGHT, type);
			Graphics2D g = thumb.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.fillRect(0, 0, THUMB_WIDTH, THUMB_HEIGHT);

			double scale = Math.min((double) THUMB_WIDTH / source.getWidth(), (double) THUMB_HEIGHT / source.getHeight());
			int w = (int) Math.round(source.getWidth() * scale);
			int h = (int) Math.round(source.getHeight() * scale);
			int x = (THUMB_WIDTH - w) / 2;
			int y = (THUMB_HEIGHT - h) / 2;
			g.drawImage(source, x, y, w, h, null);
			g.dispose();

			String format = extension.equalsIgnoreCase(".png") ? "png" : "jpg";
			String thumbName = codigo + "_" + String.format(Locale.ROOT, "%02d", secuencia) + "_thumb." + format;
			Path thumbPath = original.getParent().resolve(thumbName);
			ImageIO.write(thumb, format, thumbPath.toFile());
			LOG.info("[ImportacionCatalogo] thumbnail creado=" + thumbPath);
		} catch (Exception e) {
			LOG.log(Level.WARNING, "[ImportacionCatalogo] no se pudo crear thumbnail para " + original, e);
		}
	}

	private boolean esImagen(String nombre) {
		String n = nombre.toLowerCase(Locale.ROOT);
		return n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png") || n.endsWith(".webp");
	}

	private String extension(String nombre) {
		int dot = nombre.lastIndexOf('.');
		return dot >= 0 ? nombre.substring(dot).toLowerCase(Locale.ROOT) : ".jpg";
	}

	private String normalizarCodigo(String valor) {
		if (valor == null) return "";
		return valor.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
	}

	private String inferirMarca(String nombre) {
		String upper = nombre.toUpperCase(Locale.ROOT);
		for (String marca : List.of("EZVIZ", "TP-LINK", "TPLINK", "IMOU", "DAHUA", "TP LINK")) {
			if (upper.contains(marca)) return marca;
		}
		String[] partes = nombre.trim().split("\\s+");
		return partes.length > 1 ? partes[1] : "No especificada";
	}

	private String texto(Row row, int column) {
		if (row == null || column < 0) return "";
		Cell cell = row.getCell(column);
		if (cell == null) return "";
		return switch (cell.getCellType()) {
			case STRING -> cell.getStringCellValue();
			case NUMERIC -> DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue().toString() : BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
			case BOOLEAN -> Boolean.toString(cell.getBooleanCellValue());
			case FORMULA -> cell.getCachedFormulaResultType() == org.apache.poi.ss.usermodel.CellType.NUMERIC
					? BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString()
					: cell.getStringCellValue();
			default -> "";
		};
	}

	private BigDecimal decimal(Row row, int column) {
		if (row == null || column < 0) return null;
		Cell cell = row.getCell(column);
		if (cell == null) return null;
		try {
			if (cell.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
				return BigDecimal.valueOf(cell.getNumericCellValue()).setScale(2, java.math.RoundingMode.HALF_UP);
			}
			String s = texto(row, column).replace("$", "").replace(",", "").trim();
			return s.isBlank() ? null : new BigDecimal(s).setScale(2, java.math.RoundingMode.HALF_UP);
		} catch (Exception e) {
			return null;
		}
	}

	private void borrarRecursivo(Path dir) throws IOException {
		try (var paths = Files.walk(dir)) {
			paths.sorted(Comparator.reverseOrder()).forEach(p -> {
				try { Files.deleteIfExists(p); } catch (IOException e) { LOG.log(Level.WARNING, "[ImportacionCatalogo] no se pudo borrar temporal=" + p, e); }
			});
		}
	}

	public static class ResultadoImportacion {
		public int productosProcesados;
		public int insertados;
		public int actualizados;
		public final List<String> errores = new ArrayList<>();

		public String resumen() {
			return "Procesados=" + productosProcesados + ", insertados=" + insertados + ", actualizados=" + actualizados;
		}
	}
}
