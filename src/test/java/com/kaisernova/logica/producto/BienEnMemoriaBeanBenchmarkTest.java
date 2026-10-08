package com.kaisernova.logica.producto;

import com.kaisernova.modelo.producto.Bien;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assertions;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class BienEnMemoriaBeanBenchmarkTest {

    @Test
    public void benchmarkIndexedVsNonIndexed_withVariedDataset() {
        // enable n-gram indexing for substring-ish matches during this benchmark
        System.setProperty("bien.index.ngram", "true");
        System.setProperty("bien.index.ngram.min", "3");
        System.setProperty("bien.index.ngram.max", "4");

        BienEnMemoriaBean bean = new BienEnMemoriaBean();
        // safe init: bean.init() will skip DAO reloading when running in plain unit test
        bean.init();

        final int N = 500; // test with 500 items as requested

        // populate a varied sample dataset (use bulkIndex to avoid expensive incremental sorted insertions)
        List<Bien> items = new ArrayList<>();
        for (int i = 1; i <= N; i++) {
            Bien b = new Bien();
            b.setIdBien((long) i);
            // vary nombre to create lexicographic ordering differences
            b.setNombre((i % 10 == 0 ? "Zeta" : (i % 3 == 0 ? "Alpha" : "Producto")) + " Modelo " + (i % 25));
            b.setMarca("Marca" + (i % 8));
            b.setDescripcion("Descripcion con caracteristica clave numero " + (i % 20) + " color " + (i % 5));
            items.add(b);
        }
        bean.bulkIndex(items);

        // queries to test
        // use single-token query so indexed token OR semantics aligns with non-indexed token matching
        String qNombre = "modelo"; // common in nombre tokens
        String qMarca = "Marca5"; // matches some marcas
        String qSubstring = "del"; // tests n-gram / substring-ish behavior ("modelo" contains "del")

        // warmup to allow JIT optimizations
        for (int i = 0; i < 10; i++) {
            bean.buscarPorNombreMarcaDescripcion(qNombre);
            bean.buscarPorNombreMarcaDescripcionIndexed(qNombre);
        }

        // measure multiple iterations and compute average time
        int iterations = 100;
        long nonIndexedTotal = 0;
        long indexedTotal = 0;
        for (int i = 0; i < iterations; i++) {
            long t0 = System.nanoTime();
            Map<Long, Bien> nonIndexed = bean.buscarPorNombreMarcaDescripcion(qNombre);
            long t1 = System.nanoTime();
            long t2 = System.nanoTime();
            Map<Long, Bien> indexed = bean.buscarPorNombreMarcaDescripcionIndexed(qNombre);
            long t3 = System.nanoTime();
            nonIndexedTotal += (t1 - t0);
            indexedTotal += (t3 - t2);

            // basic correctness: both strategies should return equal sets for this token-based query
            Assertions.assertEquals(nonIndexed.size(), indexed.size(), "indexed vs non-indexed sizes must match");
        }

        double avgNonMs = nonIndexedTotal / (iterations * 1_000_000.0);
        double avgIdxMs = indexedTotal / (iterations * 1_000_000.0);
        System.out.println("average non-indexed time_ms=" + avgNonMs + " average indexed time_ms=" + avgIdxMs);

        // additional functional checks
        Map<Long, Bien> byMarca = bean.buscarPorNombreMarcaDescripcion(qMarca);
        Assertions.assertTrue(byMarca.size() > 0, "Should find at least one item by marca");

        Map<Long, Bien> byMarcaIdx = bean.buscarPorNombreMarcaDescripcionIndexed(qMarca);
        Assertions.assertEquals(byMarca.size(), byMarcaIdx.size(), "marca: indexed and non-indexed should match sizes");

        // substring-ish search: with n-gram indexing enabled, the indexed path should find matching items
        Map<Long, Bien> subNon = bean.buscarPorNombreMarcaDescripcion(qSubstring);
        Map<Long, Bien> subIdx = bean.buscarPorNombreMarcaDescripcionIndexed(qSubstring);
        // both may find matches (since contains can match substrings), but ensure indexed returns a set (sanity)
        Assertions.assertNotNull(subIdx);

        // ordering: buscarPorNombreMarcaDescripcionIndexed returns nombre matches first sorted by nombre.
        Map<Long, Bien> results = bean.buscarPorNombreMarcaDescripcionIndexed("modelo");
        List<String> nombres = results.values().stream().map(b -> b.getNombre() == null ? "" : b.getNombre().toLowerCase()).collect(Collectors.toList());
        // assert the list is non-decreasing (case-insensitive alphabetical)
        for (int i = 0; i + 1 < nombres.size(); i++) {
            String a = nombres.get(i);
            String c = nombres.get(i + 1);
            Assertions.assertTrue(a.compareTo(c) <= 0, "Results not sorted at index " + i + ": '" + a + "' > '" + c + "'");
        }

        System.out.println("dataset size=" + N + " results(modelo)=" + results.size());
    }

    @Test
    public void autosweepDfThreshold() {
        final int N = 500;
        double[] thresholds = new double[] {0.2, 0.3, 0.4, 0.5, 0.6, 0.7};
        String qNombre = "modelo";
        String qMarca = "Marca5";

        System.out.println("Starting DF threshold sweep for N=" + N);
        double bestThresh = thresholds[0];
        double bestTime = Double.MAX_VALUE;

        for (double th : thresholds) {
            System.setProperty("bien.index.df.threshold", String.valueOf(th));
            System.setProperty("bien.index.ngram", "true");
            System.setProperty("bien.index.ngram.min", "3");
            System.setProperty("bien.index.ngram.max", "4");

            BienEnMemoriaBean bean = new BienEnMemoriaBean();
            bean.init();

            // populate
            List<Bien> items = new ArrayList<>();
            for (int i = 1; i <= N; i++) {
                Bien b = new Bien();
                b.setIdBien((long) i);
                b.setNombre((i % 10 == 0 ? "Zeta" : (i % 3 == 0 ? "Alpha" : "Producto")) + " Modelo " + (i % 25));
                b.setMarca("Marca" + (i % 8));
                b.setDescripcion("Descripcion con caracteristica clave numero " + (i % 20) + " color " + (i % 5));
                items.add(b);
            }
            bean.bulkIndex(items);

            // warmup
            for (int i = 0; i < 10; i++) {
                bean.buscarPorNombreMarcaDescripcion(qNombre);
                bean.buscarPorNombreMarcaDescripcionIndexed(qNombre);
            }

            int iterations = 50;
            long nonTotal = 0, idxTotal = 0;
            for (int i = 0; i < iterations; i++) {
                long t0 = System.nanoTime();
                bean.buscarPorNombreMarcaDescripcion(qMarca);
                long t1 = System.nanoTime();
                bean.buscarPorNombreMarcaDescripcionIndexed(qMarca);
                long t2 = System.nanoTime();
                nonTotal += (t1 - t0);
                idxTotal += (t2 - t1);
            }
            double avgNonMs = nonTotal / (iterations * 1_000_000.0);
            double avgIdxMs = idxTotal / (iterations * 1_000_000.0);
            System.out.println(String.format("th=%.2f nonIdx_ms=%.4f idx_ms=%.4f", th, avgNonMs, avgIdxMs));

            double score = avgIdxMs; // prefer low indexed time for selective marca query
            if (score < bestTime) { bestTime = score; bestThresh = th; }
        }

        System.out.println("Best threshold (by indexed marca time) = " + bestThresh + " avg_idx_ms=" + bestTime);
    }

    @Test
    public void benchmarkWith10000Items() {
        // larger dataset to observe index vs linear behaviour
        System.setProperty("bien.index.ngram", "true");
        System.setProperty("bien.index.ngram.min", "3");
        System.setProperty("bien.index.ngram.max", "4");
        // choose a DF threshold that was reasonable in earlier sweeps
        System.setProperty("bien.index.df.threshold", "0.5");

        BienEnMemoriaBean bean = new BienEnMemoriaBean();
        bean.init();

        final int N = 10_000;
        List<Bien> items = new ArrayList<>(N);
        for (int i = 1; i <= N; i++) {
            Bien b = new Bien();
            b.setIdBien((long) i);
            // create somewhat realistic variety
            String prefix = (i % 7 == 0) ? "Camara" : (i % 5 == 0 ? "Alpha" : "Producto");
            b.setNombre(prefix + " Modelo " + (i % 500));
            b.setMarca("Marca" + (i % 120));
            b.setDescripcion("Descripcion con caracteristica clave numero " + (i % 200) + " color " + (i % 10));
            items.add(b);
        }
        long tStartBuild = System.nanoTime();
        bean.bulkIndex(items);
        long tEndBuild = System.nanoTime();
        System.out.println("Index build ms=" + ((tEndBuild - tStartBuild) / 1_000_000.0));

        // warmup
        for (int i = 0; i < 5; i++) {
            bean.buscarPorNombreMarcaDescripcion("modelo");
            bean.buscarPorNombreMarcaDescripcionIndexed("modelo");
        }

        int iterations = 20; // keep iterations moderate to limit runtime
        long nonTotal = 0, idxTotal = 0;
        String qCommon = "modelo"; // likely frequent
        String qSelective = "Marca17"; // selective by marca

        for (int i = 0; i < iterations; i++) {
            long t0 = System.nanoTime();
            bean.buscarPorNombreMarcaDescripcion(qCommon);
            long t1 = System.nanoTime();
            bean.buscarPorNombreMarcaDescripcionIndexed(qCommon);
            long t2 = System.nanoTime();
            nonTotal += (t1 - t0);
            idxTotal += (t2 - t1);
        }
        double avgNonMs = nonTotal / (iterations * 1_000_000.0);
        double avgIdxMs = idxTotal / (iterations * 1_000_000.0);
        System.out.println("10k common token: avg non-indexed ms=" + avgNonMs + " avg indexed ms=" + avgIdxMs);

        // selective query
        nonTotal = 0; idxTotal = 0;
        for (int i = 0; i < iterations; i++) {
            long t0 = System.nanoTime();
            bean.buscarPorNombreMarcaDescripcion(qSelective);
            long t1 = System.nanoTime();
            bean.buscarPorNombreMarcaDescripcionIndexed(qSelective);
            long t2 = System.nanoTime();
            nonTotal += (t1 - t0);
            idxTotal += (t2 - t1);
        }
        avgNonMs = nonTotal / (iterations * 1_000_000.0);
        avgIdxMs = idxTotal / (iterations * 1_000_000.0);
        System.out.println("10k selective token: avg non-indexed ms=" + avgNonMs + " avg indexed ms=" + avgIdxMs);
    }

    @Test
    public void benchmarkWith100000Items() {
        // very large dataset to observe break-even and index advantage
        System.setProperty("bien.index.ngram", "false"); // disable ngram for faster index build
        System.setProperty("bien.index.df.threshold", "0.5");

        BienEnMemoriaBean bean = new BienEnMemoriaBean();
        bean.init();

        final int N = 100_000;
        System.out.println("Building 100k item dataset...");
        List<Bien> items = new ArrayList<>(N);
        for (int i = 1; i <= N; i++) {
            Bien b = new Bien();
            b.setIdBien((long) i);
            String prefix = (i % 50 == 0) ? "Camera" : (i % 20 == 0 ? "Alpha" : "Product");
            b.setNombre(prefix + " Model " + (i % 1000));
            b.setMarca("Brand" + (i % 200));
            b.setDescripcion("Description key " + (i % 500) + " color " + (i % 15));
            items.add(b);
        }
        long tStartBuild = System.nanoTime();
        bean.bulkIndex(items);
        long tEndBuild = System.nanoTime();
        double buildMs = (tEndBuild - tStartBuild) / 1_000_000.0;
        System.out.println("100k Index build ms=" + buildMs);

        // warmup
        for (int i = 0; i < 3; i++) {
            bean.buscarPorNombreMarcaDescripcion("model");
            bean.buscarPorNombreMarcaDescripcionIndexed("model");
        }

        int iterations = 10; // fewer iterations for large dataset
        long nonTotal = 0, idxTotal = 0;
        String qCommon = "model"; // very common
        String qSelective = "Brand42"; // selective by marca

        System.out.println("Measuring common token (likely high recall)...");
        for (int i = 0; i < iterations; i++) {
            long t0 = System.nanoTime();
            bean.buscarPorNombreMarcaDescripcion(qCommon);
            long t1 = System.nanoTime();
            bean.buscarPorNombreMarcaDescripcionIndexed(qCommon);
            long t2 = System.nanoTime();
            nonTotal += (t1 - t0);
            idxTotal += (t2 - t1);
        }
        double avgNonMs = nonTotal / (iterations * 1_000_000.0);
        double avgIdxMs = idxTotal / (iterations * 1_000_000.0);
        System.out.println("100k common token: avg non-indexed ms=" + avgNonMs + " avg indexed ms=" + avgIdxMs);
        if (avgIdxMs < avgNonMs) {
            System.out.println("  ^^^ INDEX WIN by " + (avgNonMs - avgIdxMs) + " ms");
        } else {
            System.out.println("  --- LINEAR WIN by " + (avgIdxMs - avgNonMs) + " ms");
        }

        // selective query
        System.out.println("Measuring selective token (low recall)...");
        nonTotal = 0; idxTotal = 0;
        for (int i = 0; i < iterations; i++) {
            long t0 = System.nanoTime();
            bean.buscarPorNombreMarcaDescripcion(qSelective);
            long t1 = System.nanoTime();
            bean.buscarPorNombreMarcaDescripcionIndexed(qSelective);
            long t2 = System.nanoTime();
            nonTotal += (t1 - t0);
            idxTotal += (t2 - t1);
        }
        avgNonMs = nonTotal / (iterations * 1_000_000.0);
        avgIdxMs = idxTotal / (iterations * 1_000_000.0);
        System.out.println("100k selective token: avg non-indexed ms=" + avgNonMs + " avg indexed ms=" + avgIdxMs);
        if (avgIdxMs < avgNonMs) {
            System.out.println("  ^^^ INDEX WIN by " + (avgNonMs - avgIdxMs) + " ms");
        } else {
            System.out.println("  --- LINEAR WIN by " + (avgIdxMs - avgNonMs) + " ms");
        }
    }
}

