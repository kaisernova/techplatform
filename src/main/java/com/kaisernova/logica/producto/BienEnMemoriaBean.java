package com.kaisernova.logica.producto;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.text.Normalizer;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Observes;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

import com.kaisernova.modelo.producto.Bien;

@Singleton
@Startup
public class BienEnMemoriaBean {
    private final static Map<Long, Bien> BIEN_MAP = new ConcurrentHashMap<>();
    // inverted indexes: token -> posting list of Bien ids (kept sorted by nombre)
    private final Map<String, List<Long>> NOMBRE_INDEX = new ConcurrentHashMap<>();
    private final Map<String, List<Long>> MARCA_INDEX = new ConcurrentHashMap<>();
    private final Map<String, List<Long>> DESCRIPCION_INDEX = new ConcurrentHashMap<>();
    private final static Pattern TOKEN_SPLIT = Pattern.compile("\\W+");

    @Inject
    private Logger logger;

    @Inject
    private com.kaisernova.dao.producto.BienDao bienDao;

    // configurable indexing/tokenization parameters (loaded at init)
    private boolean enableNgramIndex = false; // toggle for substring n-gram indexing
    private int ngramMin = 3;
    private int ngramMax = 5;
    // token document-frequency threshold (fraction). Tokens with DF above this will be skipped as they are non-selective.
    private double tokenDfThreshold = 0.6d;

    // cache normalized nombre for faster sorting and fewer allocations
    private final Map<Long, String> NORMALIZED_NOMBRE_CACHE = new ConcurrentHashMap<>();

    // mutable stopword set so it can be extended from configuration
    private final Set<String> STOPWORDS = ConcurrentHashMap.newKeySet();

    @PostConstruct
    public void init() {
        // load optional system properties to tune tokenization/indexing without code changes
        try {
            String ngramProp = System.getProperty("bien.index.ngram");
            if (ngramProp != null) enableNgramIndex = Boolean.parseBoolean(ngramProp);
            String ngramMinProp = System.getProperty("bien.index.ngram.min");
            if (ngramMinProp != null) ngramMin = Integer.parseInt(ngramMinProp);
            String ngramMaxProp = System.getProperty("bien.index.ngram.max");
            if (ngramMaxProp != null) ngramMax = Integer.parseInt(ngramMaxProp);
            String dfProp = System.getProperty("bien.index.df.threshold");
            if (dfProp != null) tokenDfThreshold = Double.parseDouble(dfProp);

            // default stopwords
            STOPWORDS.addAll(java.util.Set.of("de","la","el","y","en","con","para","por","un","una","los","las","del","al"));
            String sw = System.getProperty("bien.index.stopwords");
            if (sw != null && !sw.isBlank()) {
                for (String s : sw.split(",")) {
                    String t = s.trim().toLowerCase();
                    if (!t.isEmpty()) STOPWORDS.add(t);
                }
            }
        } catch (Exception ex) {
            // ignore and continue with defaults
            if (logger != null) logger.warning("[init] error loading index config: " + ex.getMessage());
        }

        // Only reload from DAO when available (avoids NPE when running outside container e.g. tests)
        if (bienDao != null) {
            recargar();
        } else {
            if (logger != null) logger.info("[init] bienDao not available, skipping recargar()");
        }
    }

    @Lock(LockType.WRITE)
    public void recargar() {
        logger.info("[recargar] POR RECARGAR BIENES");
        BIEN_MAP.clear();
        var bienes = bienDao.findAllActivos();
        if (bienes != null) {
            for (Bien bien : bienes) {
                BIEN_MAP.put(bien.getIdBien(), bien);
            }
        }
        // rebuild normalized cache
        NORMALIZED_NOMBRE_CACHE.clear();
        for (Bien bien : BIEN_MAP.values()) {
            if (bien != null && bien.getIdBien() != null) {
                NORMALIZED_NOMBRE_CACHE.put(bien.getIdBien(), normalize(bien.getNombre()));
            }
        }
        // rebuild inverted indexes
        NOMBRE_INDEX.clear();
        MARCA_INDEX.clear();
        DESCRIPCION_INDEX.clear();
        for (Bien bien : BIEN_MAP.values()) {
            if (bien == null || bien.getIdBien() == null) continue;
            long id = bien.getIdBien();
            indexText(NOMBRE_INDEX, bien.getNombre(), id);
            indexText(MARCA_INDEX, bien.getMarca(), id);
            indexText(DESCRIPCION_INDEX, bien.getDescripcion(), id);
        }
        logger.info("[recargar] BIEN_MAP size=" + BIEN_MAP.size());
    }

    /**
     * Bulk index a collection of Bien items. This builds posting lists in batch and sorts once,
     * which is much faster than repeatedly inserting into sorted lists.
     */
    @Lock(LockType.WRITE)
    public void bulkIndex(Collection<Bien> bienes) {
        if (bienes == null) return;
        BIEN_MAP.clear();
        NORMALIZED_NOMBRE_CACHE.clear();
        NOMBRE_INDEX.clear();
        MARCA_INDEX.clear();
        DESCRIPCION_INDEX.clear();

        // first pass: populate BIEN_MAP and normalized cache
        for (Bien b : bienes) {
            if (b == null || b.getIdBien() == null) continue;
            BIEN_MAP.put(b.getIdBien(), b);
            NORMALIZED_NOMBRE_CACHE.put(b.getIdBien(), normalize(b.getNombre()));
        }

        // temporary unsorted postings
        Map<String, List<Long>> tmpNombre = new java.util.HashMap<>();
        Map<String, List<Long>> tmpMarca = new java.util.HashMap<>();
        Map<String, List<Long>> tmpDesc = new java.util.HashMap<>();

        for (Bien b : BIEN_MAP.values()) {
            long id = b.getIdBien();
            String nn = normalize(b.getNombre());
            if (nn != null && !nn.isEmpty()) {
                for (String t : TOKEN_SPLIT.split(nn)) {
                    if (t == null || t.isEmpty() || isStopword(t)) continue;
                    tmpNombre.computeIfAbsent(t, k -> new ArrayList<>()).add(id);
                    if (enableNgramIndex && t.length() >= ngramMin) {
                        int max = Math.min(ngramMax, t.length());
                        for (int n = ngramMin; n <= max; n++) {
                            String gram = t.substring(0, n);
                            tmpNombre.computeIfAbsent(gram, k -> new ArrayList<>()).add(id);
                        }
                    }
                }
            }
            String mm = normalize(b.getMarca());
            if (mm != null && !mm.isEmpty()) {
                for (String t : TOKEN_SPLIT.split(mm)) {
                    if (t == null || t.isEmpty() || isStopword(t)) continue;
                    tmpMarca.computeIfAbsent(t, k -> new ArrayList<>()).add(id);
                }
            }
            String dd = normalize(b.getDescripcion());
            if (dd != null && !dd.isEmpty()) {
                for (String t : TOKEN_SPLIT.split(dd)) {
                    if (t == null || t.isEmpty() || isStopword(t)) continue;
                    tmpDesc.computeIfAbsent(t, k -> new ArrayList<>()).add(id);
                }
            }
        }

        // sort postings by normalized nombre and deduplicate
        for (Map.Entry<String, List<Long>> e : tmpNombre.entrySet()) {
            List<Long> list = e.getValue();
            list.sort((a, b) -> {
                String na = NORMALIZED_NOMBRE_CACHE.getOrDefault(a, normalize(BIEN_MAP.get(a) == null ? null : BIEN_MAP.get(a).getNombre()));
                String nb = NORMALIZED_NOMBRE_CACHE.getOrDefault(b, normalize(BIEN_MAP.get(b) == null ? null : BIEN_MAP.get(b).getNombre()));
                int cmp = na.compareTo(nb);
                if (cmp != 0) return cmp;
                return a.compareTo(b);
            });
            // dedupe
            List<Long> dedup = new ArrayList<>();
            Long prev = null;
            for (Long id : list) {
                if (prev == null || !prev.equals(id)) dedup.add(id);
                prev = id;
            }
            NOMBRE_INDEX.put(e.getKey(), dedup);
        }

        for (Map.Entry<String, List<Long>> e : tmpMarca.entrySet()) {
            List<Long> list = e.getValue();
            list.sort(Long::compareTo);
            List<Long> dedup = new ArrayList<>();
            Long prev = null;
            for (Long id : list) {
                if (prev == null || !prev.equals(id)) dedup.add(id);
                prev = id;
            }
            MARCA_INDEX.put(e.getKey(), dedup);
        }

        for (Map.Entry<String, List<Long>> e : tmpDesc.entrySet()) {
            List<Long> list = e.getValue();
            list.sort(Long::compareTo);
            List<Long> dedup = new ArrayList<>();
            Long prev = null;
            for (Long id : list) {
                if (prev == null || !prev.equals(id)) dedup.add(id);
                prev = id;
            }
            DESCRIPCION_INDEX.put(e.getKey(), dedup);
        }

        if (logger != null) {
            logger.info("[bulkIndex] built indexes, BIEN_MAP size=" + BIEN_MAP.size());
        }
    }

    @Lock(LockType.READ)
    public Bien obtenerPorId(Long id) {
        return BIEN_MAP.get(id);
    }

    @Lock(LockType.READ)
    public Collection<Bien> obtenerTodos() {
        return BIEN_MAP.values();
    }

    @Lock(LockType.READ)
    public Bien obtenerPorCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return null;
        }
        return BIEN_MAP.values().stream()
                .filter(b -> codigo.equals(b.getCodigo()))
                .findFirst()
                .orElse(null);
    }

    @Lock(LockType.READ)
    public int getTotalBienes() {
        return BIEN_MAP.size();
    }

    @Lock(LockType.READ)
    public boolean contieneBienConId(Long id) {
        return BIEN_MAP.containsKey(id);
    }

    private void indexText(Map<String, List<Long>> index, String text, long id) {
        if (text == null || text.isEmpty()) return;
        String normalized = normalize(text);
        String[] tokens = TOKEN_SPLIT.split(normalized);
        for (String t : tokens) {
            if (t == null || t.isEmpty() || isStopword(t)) continue;
            // add to posting list
            addToPosting((Map) index, t, id);
            if (enableNgramIndex && t.length() >= ngramMin) {
                addNGramsToIndex((Map) index, t, id);
            }
        }
    }

    private Set<String> tokenizeToSet(String termino) {
        if (termino == null) return java.util.Collections.emptySet();
        String[] parts = TOKEN_SPLIT.split(normalize(termino));
        if (parts == null || parts.length == 0) return java.util.Collections.emptySet();
        Set<String> out = new java.util.HashSet<>();
        for (String s : parts) {
            if (s == null || s.isEmpty()) continue;
            if (isStopword(s)) continue;
            out.add(s);
        }
        return out;
    }

    // simple accent folding and normalize
    private String normalize(String s) {
        if (s == null) return null;
        String lower = s.toLowerCase().trim();
        String normalized = Normalizer.normalize(lower, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return normalized;
    }

    // ngram limits are now configurable via fields (loaded during init)
    // Edge n-grams (prefixes) to reduce posting explosion
    private void addNGramsToIndex(Map<String, List<Long>> index, String token, long id) {
        int len = token.length();
        int max = Math.min(ngramMax, len);
        for (int n = ngramMin; n <= max; n++) {
            String gram = token.substring(0, n);
            addToPosting(index, gram, id);
        }
    }

    // add id into posting list keeping it sorted by normalized nombre using cached values
    private void addToPosting(Map<String, List<Long>> index, String token, Long id) {
        if (token == null || token.isEmpty() || id == null) return;
        List<Long> list = index.computeIfAbsent(token, k -> new ArrayList<>());
        // binary insert by comparing cached normalized nombre; do not use list.contains (O(n))
        String idName = NORMALIZED_NOMBRE_CACHE.getOrDefault(id, "");
        int lo = 0, hi = list.size();
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            Long midId = list.get(mid);
            String midName = NORMALIZED_NOMBRE_CACHE.getOrDefault(midId, normalize(BIEN_MAP.get(midId) == null ? null : BIEN_MAP.get(midId).getNombre()));
            int cmp = midName.compareTo(idName);
            if (cmp < 0) {
                lo = mid + 1;
            } else if (cmp > 0) {
                hi = mid;
            } else {
                // names equal, use id to decide order
                if (midId.equals(id)) return; // already present
                if (midId < id) {
                    lo = mid + 1;
                } else {
                    hi = mid;
                }
            }
        }
        // avoid duplicate if equal to element at insertion point
        if (lo < list.size() && list.get(lo).equals(id)) return;
        list.add(lo, id);
    }

    private void removeFromPosting(Map<String, List<Long>> index, String token, Long id) {
        if (token == null || token.isEmpty() || id == null) return;
        List<Long> list = index.get(token);
        if (list == null) return;
        list.remove(id);
        if (list.isEmpty()) index.remove(token);
    }

    private boolean isStopword(String s) {
        return s == null || s.isEmpty() ? false : STOPWORDS.contains(s);
    }

    private void removeIdFromIndex(Map<String, List<Long>> index, Long id) {
        if (id == null) return;
        for (Map.Entry<String, List<Long>> e : index.entrySet()) {
            List<Long> list = e.getValue();
            synchronized (list) {
                list.remove(id);
            }
        }
        // cleanup empty lists
        index.entrySet().removeIf(e -> e.getValue().isEmpty());
    }

    private void removeIdFromIndexForText(Map<String, List<Long>> index, String text, Long id) {
        if (text == null || text.isEmpty() || id == null) return;
        String normalized = normalize(text);
        String[] tokens = TOKEN_SPLIT.split(normalized);
        for (String t : tokens) {
            if (t == null || t.isEmpty()) continue;
            removeFromPosting((Map) index, t, id);
            if (enableNgramIndex && t.length() >= ngramMin) {
                int len = t.length();
                int max = Math.min(ngramMax, len);
                for (int n = ngramMin; n <= max; n++) {
                    String gram = t.substring(0, n);
                    removeFromPosting((Map) index, gram, id);
                }
            }
        }
        index.entrySet().removeIf(e -> e.getValue().isEmpty());
    }

    /**
     * Add or update a single Bien into the in-memory maps and indexes.
     */
    @Lock(LockType.WRITE)
    public void agregarOActualizarBien(Bien bien) {
        if (bien == null || bien.getIdBien() == null) return;
        Long id = bien.getIdBien();
        Bien prev = BIEN_MAP.put(id, bien);
        // update normalized cache
        NORMALIZED_NOMBRE_CACHE.put(id, normalize(bien.getNombre()));
        if (prev != null) {
            // remove previous tokens
            removeIdFromIndexForText(NOMBRE_INDEX, prev.getNombre(), id);
            removeIdFromIndexForText(MARCA_INDEX, prev.getMarca(), id);
            removeIdFromIndexForText(DESCRIPCION_INDEX, prev.getDescripcion(), id);
        }
        indexText(NOMBRE_INDEX, bien.getNombre(), id);
        indexText(MARCA_INDEX, bien.getMarca(), id);
        indexText(DESCRIPCION_INDEX, bien.getDescripcion(), id);
    }

    /**
     * CDI-friendly index event used to update indexes from other services without circular injection.
     * Usage from a service layer:
     *   @Inject Event<BienEnMemoriaBean.IndexEvent> indexEvent;
     *   indexEvent.fire(IndexEvent.addOrUpdate(bien));
     * The observer below will react to the event and update the in-memory index within the bean's EJB context.
     */
    public static class IndexEvent {
        public enum Type { ADD_OR_UPDATE, DELETE, RELOAD }
        private final Type type;
        private final Bien bien;
        private final Long id;

        private IndexEvent(Type type, Bien bien, Long id) {
            this.type = type; this.bien = bien; this.id = id;
        }

        public static IndexEvent addOrUpdate(Bien b) { return new IndexEvent(Type.ADD_OR_UPDATE, b, b == null ? null : b.getIdBien()); }
        public static IndexEvent delete(Long id) { return new IndexEvent(Type.DELETE, null, id); }
        public static IndexEvent reload() { return new IndexEvent(Type.RELOAD, null, null); }

        public Type getType() { return type; }
        public Bien getBien() { return bien; }
        public Long getId() { return id; }
    }

    // Observe index events fired by other components to maintain indexes incrementally without circular CDI/EJB injection.
    public void handleIndexEvent(@Observes IndexEvent ev) {
        if (ev == null || ev.getType() == null) return;
        switch (ev.getType()) {
            case ADD_OR_UPDATE:
                if (ev.getBien() != null) agregarOActualizarBien(ev.getBien());
                break;
            case DELETE:
                if (ev.getId() != null) eliminarBien(ev.getId());
                break;
            case RELOAD:
                recargar();
                break;
        }
    }

    /**
     * Remove a Bien from in-memory maps and indexes by id.
     */
    @Lock(LockType.WRITE)
    public void eliminarBien(Long id) {
        if (id == null) return;
        Bien prev = BIEN_MAP.remove(id);
        // remove from normalized cache
        NORMALIZED_NOMBRE_CACHE.remove(id);
        if (prev != null) {
            removeIdFromIndexForText(NOMBRE_INDEX, prev.getNombre(), id);
            removeIdFromIndexForText(MARCA_INDEX, prev.getMarca(), id);
            removeIdFromIndexForText(DESCRIPCION_INDEX, prev.getDescripcion(), id);
        } else {
            // also attempt generic removal
            removeIdFromIndex(NOMBRE_INDEX, id);
            removeIdFromIndex(MARCA_INDEX, id);
            removeIdFromIndex(DESCRIPCION_INDEX, id);
        }
    }

    private List<Bien> sortByNombre(Collection<Long> ids) {
        List<Bien> list = new ArrayList<>();
        for (Long id : ids) {
            Bien b = BIEN_MAP.get(id);
            if (b != null) list.add(b);
        }
        // use cached normalized names when available to avoid repeated normalization allocations
        list.sort((a, b) -> {
            String na = NORMALIZED_NOMBRE_CACHE.getOrDefault(a.getIdBien(), normalize(a.getNombre()));
            String nb = NORMALIZED_NOMBRE_CACHE.getOrDefault(b.getIdBien(), normalize(b.getNombre()));
            int cmp = na.compareTo(nb);
            if (cmp != 0) return cmp;
            return a.getIdBien().compareTo(b.getIdBien());
        });
        return list;
    }

    private Comparator<Bien> comparatorFor(OrderField field, OrderDirection dir) {
        Comparator<Bien> cmp;
        switch (field) {
            case MARCA:
                cmp = Comparator.comparing((Bien b) -> b.getMarca() == null ? "" : b.getMarca(), String.CASE_INSENSITIVE_ORDER);
                break;
            case PRECIO:
                cmp = (a, b) -> {
                    BigDecimal pa = a.getPrecio();
                    BigDecimal pb = b.getPrecio();
                    if (pa == null && pb == null) return 0;
                    if (pa == null) return 1;
                    if (pb == null) return -1;
                    int r = pa.compareTo(pb);
                    if (r != 0) return r;
                    return a.getIdBien().compareTo(b.getIdBien());
                };
                break;
            case FECHA_HORA_CREACION:
                cmp = (a, b) -> {
                    LocalDateTime ta = a.getFechaHoraCreacion();
                    LocalDateTime tb = b.getFechaHoraCreacion();
                    if (ta == null && tb == null) return 0;
                    if (ta == null) return 1;
                    if (tb == null) return -1;
                    int r = ta.compareTo(tb);
                    if (r != 0) return r;
                    return a.getIdBien().compareTo(b.getIdBien());
                };
                break;
            case NOMBRE:
            default:
                cmp = Comparator.comparing((Bien x) -> x.getNombre() == null ? "" : x.getNombre(), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Bien::getIdBien);
                break;
        }
        if (dir == OrderDirection.DESC) {
            return cmp.reversed();
        }
        return cmp;
    }

    private List<Bien> sortByField(Collection<Long> ids, OrderField field, OrderDirection dir) {
        List<Bien> list = new ArrayList<>();
        for (Long id : ids) {
            Bien b = BIEN_MAP.get(id);
            if (b != null) list.add(b);
        }
        Comparator<Bien> cmp = comparatorFor(field, dir);
        Collections.sort(list, cmp);
        return list;
    }

    /**
     * Inverted-indexed search. Faster for large datasets. Returns results in deterministic order:
     * first matches on nombre (sorted by nombre), then marca (sorted), then descripcion (sorted).
     */
    @Lock(LockType.READ)
    public LinkedHashMap<Long, Bien> buscarPorNombreMarcaDescripcionIndexed(String termino, int maxResults) {
        LinkedHashMap<Long, Bien> resultados = new LinkedHashMap<>();
        if (termino == null) return resultados;
        String q = termino.trim().toLowerCase();
        if (q.isEmpty()) return resultados;

        Set<String> tokens = tokenizeToSet(q);
        // gather id sets for each field (use non-concurrent local sets for faster single-threaded search)
        Set<Long> nombreIds = new java.util.HashSet<>();
        Set<Long> marcaIds = new java.util.HashSet<>();
        Set<Long> descIds = new java.util.HashSet<>();
        for (String t : tokens) {
            // skip tokens considered too common (non-selective)
            List<Long> s1 = NOMBRE_INDEX.get(t);
            if (s1 != null) {
                if (!shouldSkipToken(s1.size())) nombreIds.addAll(s1);
            }
            List<Long> s2 = MARCA_INDEX.get(t);
            if (s2 != null) {
                if (!shouldSkipToken(s2.size())) marcaIds.addAll(s2);
            }
            List<Long> s3 = DESCRIPCION_INDEX.get(t);
            if (s3 != null) {
                if (!shouldSkipToken(s3.size())) descIds.addAll(s3);
            }
        }

        // if no ids from index, fallback to single-pass contains search for correctness
        if (nombreIds.isEmpty() && marcaIds.isEmpty() && descIds.isEmpty()) {
            // fallback to previous behavior but preserve deterministic ordering: prefer nombre-sorted order
            Map<Long, Bien> fallback = buscarPorNombreMarcaDescripcion(q, maxResults);
            java.util.Set<Long> ids = fallback.keySet();
            List<Bien> nombreList = sortByNombre(ids);
            LinkedHashMap<Long, Bien> out = new LinkedHashMap<>();
            for (Bien b : nombreList) {
                out.put(b.getIdBien(), b);
                if (maxResults > 0 && out.size() >= maxResults) break;
            }
            return out;
        }

        // if the union of indexed ids is large compared to the total dataset, a single-pass contains
        // scan may be faster than building unions and sorting. Use a simple heuristic: if >50% of items
        // are matched by the index, fallback to the non-indexed scan.
        java.util.Set<Long> unionCheck = new java.util.HashSet<>();
        unionCheck.addAll(nombreIds);
        unionCheck.addAll(marcaIds);
        unionCheck.addAll(descIds);
        if (!BIEN_MAP.isEmpty() && unionCheck.size() > (BIEN_MAP.size() / 2)) {
            Map<Long, Bien> fallback = buscarPorNombreMarcaDescripcion(q, maxResults);
            java.util.Set<Long> ids = fallback.keySet();
            List<Bien> nombreList = sortByNombre(ids);
            LinkedHashMap<Long, Bien> out = new LinkedHashMap<>();
            for (Bien b : nombreList) {
                out.put(b.getIdBien(), b);
                if (maxResults > 0 && out.size() >= maxResults) break;
            }
            return out;
        }

        // add nombre matches first using k-way merge of postings (already sorted by nombre)
        List<List<Long>> nombrePostings = new ArrayList<>();
        for (String t : tokens) {
            List<Long> p = NOMBRE_INDEX.get(t);
            if (p != null && !p.isEmpty() && !shouldSkipToken(p.size())) nombrePostings.add(p);
        }
        List<Long> nombreOrdered = mergePostingLists(nombrePostings, maxResults);
        for (Long id : nombreOrdered) {
            Bien b = BIEN_MAP.get(id);
            if (b != null) {
                resultados.put(id, b);
                if (maxResults > 0 && resultados.size() >= maxResults) return resultados;
            }
        }

        // add marca matches (sorted by nombre, skip already added)
        java.util.Set<Long> marcaUnion = new java.util.HashSet<>();
        for (String t : tokens) {
            List<Long> p = MARCA_INDEX.get(t);
            if (p != null && !p.isEmpty() && !shouldSkipToken(p.size())) marcaUnion.addAll(p);
        }
        List<Bien> marcaList = sortByNombre(marcaUnion.stream().filter(id -> !resultados.containsKey(id)).collect(Collectors.toList()));
        for (Bien b : marcaList) {
            resultados.put(b.getIdBien(), b);
            if (maxResults > 0 && resultados.size() >= maxResults) return resultados;
        }

        // add descripcion matches
        java.util.Set<Long> descUnion = new java.util.HashSet<>();
        for (String t : tokens) {
            List<Long> p = DESCRIPCION_INDEX.get(t);
            if (p != null && !p.isEmpty() && !shouldSkipToken(p.size())) descUnion.addAll(p);
        }
        List<Bien> descList = sortByNombre(descUnion.stream().filter(id -> !resultados.containsKey(id)).collect(Collectors.toList()));
        for (Bien b : descList) {
            resultados.put(b.getIdBien(), b);
            if (maxResults > 0 && resultados.size() >= maxResults) return resultados;
        }

        return resultados;
    }

    private boolean shouldSkipToken(int postingSize) {
        if (postingSize <= 0) return true;
        int total = BIEN_MAP.size();
        if (total == 0) return true;
        double frac = ((double) postingSize) / (double) total;
        return frac >= tokenDfThreshold; // skip token when too common
    }

    // Merge multiple posting lists (each sorted by normalized nombre) into a single ordered list of ids
    // without duplicates. If maxResults>0 the merge stops after collecting that many ids.
    private List<Long> mergePostingLists(List<List<Long>> lists, int maxResults) {
        List<Long> out = new ArrayList<>();
        if (lists == null || lists.isEmpty()) return out;

        class Item { long id; int listIdx; int pos; }

        java.util.PriorityQueue<Item> pq = new java.util.PriorityQueue<>((a,b) -> {
            String na = NORMALIZED_NOMBRE_CACHE.getOrDefault(a.id, normalize(BIEN_MAP.get(a.id) == null ? null : BIEN_MAP.get(a.id).getNombre()));
            String nb = NORMALIZED_NOMBRE_CACHE.getOrDefault(b.id, normalize(BIEN_MAP.get(b.id) == null ? null : BIEN_MAP.get(b.id).getNombre()));
            int cmp = na.compareTo(nb);
            if (cmp != 0) return cmp;
            return Long.compare(a.id, b.id);
        });

        for (int i = 0; i < lists.size(); i++) {
            List<Long> lst = lists.get(i);
            if (lst != null && !lst.isEmpty()) {
                Item it = new Item(); it.id = lst.get(0); it.listIdx = i; it.pos = 0;
                pq.add(it);
            }
        }

        Long last = null;
        while (!pq.isEmpty()) {
            Item it = pq.poll();
            long id = it.id;
            if (last == null || last.longValue() != id) {
                out.add(id);
                last = id;
                if (maxResults > 0 && out.size() >= maxResults) break;
            }
            List<Long> src = lists.get(it.listIdx);
            int next = it.pos + 1;
            if (next < src.size()) {
                Item nit = new Item(); nit.id = src.get(next); nit.listIdx = it.listIdx; nit.pos = next;
                pq.add(nit);
            }
        }
        return out;
    }

    @Lock(LockType.READ)
    public LinkedHashMap<Long, Bien> buscarPorNombreMarcaDescripcionIndexed(String termino) {
        return buscarPorNombreMarcaDescripcionIndexed(termino, 0);
    }

    @Lock(LockType.READ)
    public LinkedHashMap<Long, Bien> buscarPorNombreMarcaDescripcionIndexed(String termino, OrderField orderField, OrderDirection orderDirection, int maxResults) {
        LinkedHashMap<Long, Bien> resultados = new LinkedHashMap<>();
        if (termino == null) return resultados;
        String q = termino.trim().toLowerCase();
        if (q.isEmpty()) return resultados;

        Set<String> tokens = tokenizeToSet(q);
        Set<Long> nombreIds = new java.util.HashSet<>();
        Set<Long> marcaIds = new java.util.HashSet<>();
        Set<Long> descIds = new java.util.HashSet<>();
        for (String t : tokens) {
            List<Long> s1 = NOMBRE_INDEX.get(t);
            if (s1 != null) {
                if (!shouldSkipToken(s1.size())) nombreIds.addAll(s1);
            }
            List<Long> s2 = MARCA_INDEX.get(t);
            if (s2 != null) {
                if (!shouldSkipToken(s2.size())) marcaIds.addAll(s2);
            }
            List<Long> s3 = DESCRIPCION_INDEX.get(t);
            if (s3 != null) {
                if (!shouldSkipToken(s3.size())) descIds.addAll(s3);
            }
        }

        if (nombreIds.isEmpty() && marcaIds.isEmpty() && descIds.isEmpty()) {
            // fallback to non-indexed search with ordering
            return buscarPorNombreMarcaDescripcion(termino, orderField, orderDirection, maxResults);
        }

        // union of ids
        java.util.Set<Long> union = new java.util.HashSet<>();
        union.addAll(nombreIds);
        union.addAll(marcaIds);
        union.addAll(descIds);

        // heuristic: if index returns most of the dataset, prefer single-pass scan which is often faster
        if (!BIEN_MAP.isEmpty() && union.size() > (BIEN_MAP.size() / 2)) {
            return buscarPorNombreMarcaDescripcion(termino, orderField, orderDirection, maxResults);
        }

        List<Bien> sorted = sortByField(union, orderField == null ? OrderField.NOMBRE : orderField, orderDirection == null ? OrderDirection.ASC : orderDirection);
        int count = 0;
        for (Bien b : sorted) {
            resultados.put(b.getIdBien(), b);
            count++;
            if (maxResults > 0 && count >= maxResults) break;
        }
        return resultados;
    }

    @Lock(LockType.READ)
    public LinkedHashMap<Long, Bien> buscarPorNombreMarcaDescripcionIndexed(String termino, OrderField orderField, OrderDirection orderDirection) {
        return buscarPorNombreMarcaDescripcionIndexed(termino, orderField, orderDirection, 0);
    }

    /**
     * Search in-memory bienes by nombre, then marca, then descripcion (in that priority order).
     * Matching is case-insensitive and uses String.contains semantics. The returned map is
     * keyed by id to guarantee uniqueness and preserve fast lookups.
     *
     * This implementation performs a single pass over the in-memory map and is memory-friendly
     * (no intermediate collections are created). If maxResults &gt; 0 the result will be capped.
     */
    @Lock(LockType.READ)
    public Map<Long, Bien> buscarPorNombreMarcaDescripcion(String termino, int maxResults) {
        Map<Long, Bien> resultados = new ConcurrentHashMap<>();
        if (termino == null) {
            return resultados;
        }
        String q = termino.trim().toLowerCase();
        if (q.isEmpty()) {
            return resultados;
        }

        int found = 0;
        for (Bien bien : BIEN_MAP.values()) {
            if (bien == null) {
                continue;
            }
            boolean matched = false;
            String nombre = bien.getNombre();
            if (nombre != null && !nombre.isEmpty() && nombre.toLowerCase().contains(q)) {
                matched = true;
            } else {
                String marca = bien.getMarca();
                if (marca != null && !marca.isEmpty() && marca.toLowerCase().contains(q)) {
                    matched = true;
                } else {
                    String descripcion = bien.getDescripcion();
                    if (descripcion != null && !descripcion.isEmpty() && descripcion.toLowerCase().contains(q)) {
                        matched = true;
                    }
                }
            }

            if (matched) {
                resultados.put(bien.getIdBien(), bien);
                found++;
                if (maxResults > 0 && found >= maxResults) {
                    break;
                }
            }
        }
        return resultados;
    }

    @Lock(LockType.READ)
    public Map<Long, Bien> buscarPorNombreMarcaDescripcion(String termino) {
        return buscarPorNombreMarcaDescripcion(termino, 0);
    }

    /**
     * Non-indexed search with ordering. Performs a single pass to collect matches, then sorts
     * results according to provided orderField and orderDirection.
     */
    @Lock(LockType.READ)
    public LinkedHashMap<Long, Bien> buscarPorNombreMarcaDescripcion(String termino, OrderField orderField, OrderDirection orderDirection, int maxResults) {
        LinkedHashMap<Long, Bien> resultados = new LinkedHashMap<>();
        if (termino == null) return resultados;
        String q = termino.trim().toLowerCase();
        if (q.isEmpty()) return resultados;

        java.util.Set<Long> ids = new java.util.HashSet<>();
        for (Bien bien : BIEN_MAP.values()) {
            if (bien == null) continue;
            boolean matched = false;
            String nombre = bien.getNombre();
            if (nombre != null && !nombre.isEmpty() && nombre.toLowerCase().contains(q)) {
                matched = true;
            } else {
                String marca = bien.getMarca();
                if (marca != null && !marca.isEmpty() && marca.toLowerCase().contains(q)) {
                    matched = true;
                } else {
                    String descripcion = bien.getDescripcion();
                    if (descripcion != null && !descripcion.isEmpty() && descripcion.toLowerCase().contains(q)) {
                        matched = true;
                    }
                }
            }
            if (matched) ids.add(bien.getIdBien());
        }

        List<Bien> sorted = sortByField(ids, orderField == null ? OrderField.NOMBRE : orderField, orderDirection == null ? OrderDirection.ASC : orderDirection);
        int count = 0;
        for (Bien b : sorted) {
            resultados.put(b.getIdBien(), b);
            count++;
            if (maxResults > 0 && count >= maxResults) break;
        }
        return resultados;
    }

    @Lock(LockType.READ)
    public LinkedHashMap<Long, Bien> buscarPorNombreMarcaDescripcion(String termino, OrderField orderField, OrderDirection orderDirection) {
        return buscarPorNombreMarcaDescripcion(termino, orderField, orderDirection, 0);
    }

}
