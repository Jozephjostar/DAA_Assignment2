package com.assignment.benchmark;

import com.assignment.ds.DynamicArray;
import com.assignment.ds.IntList;
import com.assignment.ds.MinHeap;
import com.assignment.ds.MyLinkedList;
import com.assignment.metrics.OperationCounter;
import org.openjdk.jol.info.GraphLayout;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * BenchmarkRunner executes Workloads 1-4 for n = 100, 1000, 10000, 100000
 * using a fixed seed (42). It records steps, moves, comparisons, and the
 * median execution time over 5 runs (discarding JVM warm-up runs).
 * Also measures Bonus Task A (JOL memory layout) and Bonus Task B (Floyd vs N-inserts).
 */
public class BenchmarkRunner {

    private static final int[] SIZES = {100, 1000, 10000, 100000};
    private static final int REPEAT_RUNS = 5;
    private static final int WARMUP_RUNS = 2;
    private static final long SEED = 42;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("Starting Benchmark Suite (DAA Assignment 2)");
        System.out.println("Sizes: " + Arrays.toString(SIZES));
        System.out.println("Warmup runs: " + WARMUP_RUNS + ", Measured runs: " + REPEAT_RUNS);
        System.out.println("=================================================");

        File resultsDir = new File("results");
        if (!resultsDir.exists()) {
            resultsDir.mkdirs();
        }

        List<BenchmarkResult> results = new ArrayList<>();

        // Warm up JVM JIT compiler before running measurements
        performWarmup();

        // 1. Workload 1: Random Access
        System.out.println("\n>>> Running Workload 1: Random Access (10,000 gets)...");
        runWorkload1(results);

        // 2. Workload 2: Search
        System.out.println("\n>>> Running Workload 2: Search (1,000 contains)...");
        runWorkload2(results);

        // 3. Workload 3: Insert & Remove (Head and Middle)
        System.out.println("\n>>> Running Workload 3: Insert & Remove (Head and Middle)...");
        runWorkload3(results);

        // 4. Workload 4: Priority Processing (MinHeap)
        System.out.println("\n>>> Running Workload 4: Priority Processing (MinHeap)...");
        runWorkload4(results);

        // Export main benchmark results to results.csv
        writeResultsCsv(new File(resultsDir, "results.csv"), results);

        // Bonus Task A: JOL Memory Footprint
        System.out.println("\n>>> Running Bonus Task A: JOL Memory Footprint...");
        runMemoryBenchmark(new File(resultsDir, "memory.csv"));

        // Bonus Task B: Floyd's buildHeap vs N-inserts
        System.out.println("\n>>> Running Bonus Task B: Floyd buildHeap vs N inserts...");
        runHeapBuildBenchmark(new File(resultsDir, "heap_build.csv"));

        System.out.println("\n=================================================");
        System.out.println("All benchmarks completed successfully!");
        System.out.println("Results saved to results/results.csv, memory.csv, heap_build.csv");
        System.out.println("=================================================");
    }

    private static void performWarmup() {
        System.out.println("Warming up JVM JIT compiler...");
        for (int i = 0; i < 3; i++) {
            DynamicArray da = new DynamicArray();
            MyLinkedList ll = new MyLinkedList();
            MinHeap mh = new MinHeap();
            for (int k = 0; k < 5000; k++) {
                da.add(k);
                ll.add(k);
                mh.insert(k);
            }
            for (int k = 0; k < 1000; k++) {
                da.get(k % 5000);
                ll.get(k % 500);
                da.contains(k);
                ll.contains(k);
            }
            while (!mh.isEmpty()) {
                mh.extractMin();
            }
        }
        System.out.println("Warmup completed.");
    }

    private static int[] generateData(int n) {
        Random rng = new Random(SEED);
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = rng.nextInt(1_000_000);
        }
        return data;
    }

    // =========================================================================
    // WORKLOAD 1: Random Access
    // =========================================================================
    private static void runWorkload1(List<BenchmarkResult> results) {
        final int GET_CALLS = 10000;

        for (int n : SIZES) {
            System.out.println("  W1: n = " + n);
            int[] data = generateData(n);

            // Generate query indices
            Random idxRng = new Random(SEED + 101);
            int[] queryIndices = new int[GET_CALLS];
            for (int i = 0; i < GET_CALLS; i++) {
                queryIndices[i] = idxRng.nextInt(n);
            }

            // Benchmark DynamicArray
            results.add(benchmarkW1ForStructure("DynamicArray", n, data, queryIndices, true));

            // Benchmark MyLinkedList
            results.add(benchmarkW1ForStructure("MyLinkedList", n, data, queryIndices, false));
        }
    }

    private static BenchmarkResult benchmarkW1ForStructure(String name, int n, int[] data, int[] queryIndices, boolean isArray) {
        List<Double> times = new ArrayList<>();
        long steps = 0, moves = 0, comps = 0;

        for (int run = 0; run < WARMUP_RUNS + REPEAT_RUNS; run++) {
            IntList list = isArray ? new DynamicArray(n) : new MyLinkedList();
            for (int val : data) {
                list.add(val);
            }

            OperationCounter counter = new OperationCounter();
            list.setCounter(counter);

            long start = System.nanoTime();
            for (int idx : queryIndices) {
                list.get(idx);
            }
            long end = System.nanoTime();

            if (run >= WARMUP_RUNS) {
                times.add((end - start) / 1_000_000.0);
                steps = counter.getSteps();
                moves = counter.getMoves();
                comps = counter.getComparisons();
            }
        }

        Collections.sort(times);
        double medianTime = times.get(times.size() / 2);
        return new BenchmarkResult("W1", "-", name, n, medianTime, steps, moves, comps);
    }

    // =========================================================================
    // WORKLOAD 2: Search (contains)
    // =========================================================================
    private static void runWorkload2(List<BenchmarkResult> results) {
        final int TOTAL_QUERIES = 1000;
        final int PRESENT_COUNT = 500;
        final int ABSENT_COUNT = 500;

        for (int n : SIZES) {
            System.out.println("  W2: n = " + n);
            int[] data = generateData(n);

            // Half present, half absent
            int[] queries = new int[TOTAL_QUERIES];
            Random qRng = new Random(SEED + 202);

            // 500 present
            for (int i = 0; i < PRESENT_COUNT; i++) {
                queries[i] = data[qRng.nextInt(n)];
            }
            // 500 absent (numbers >= 2_000_000)
            for (int i = 0; i < ABSENT_COUNT; i++) {
                queries[PRESENT_COUNT + i] = 2_000_000 + qRng.nextInt(1_000_000);
            }

            // Shuffle queries
            for (int i = queries.length - 1; i > 0; i--) {
                int j = qRng.nextInt(i + 1);
                int tmp = queries[i];
                queries[i] = queries[j];
                queries[j] = tmp;
            }

            // DynamicArray
            results.add(benchmarkW2ForStructure("DynamicArray", n, data, queries, true));

            // MyLinkedList
            results.add(benchmarkW2ForStructure("MyLinkedList", n, data, queries, false));
        }
    }

    private static BenchmarkResult benchmarkW2ForStructure(String name, int n, int[] data, int[] queries, boolean isArray) {
        List<Double> times = new ArrayList<>();
        long steps = 0, moves = 0, comps = 0;

        for (int run = 0; run < WARMUP_RUNS + REPEAT_RUNS; run++) {
            IntList list = isArray ? new DynamicArray(n) : new MyLinkedList();
            for (int val : data) {
                list.add(val);
            }

            OperationCounter counter = new OperationCounter();
            list.setCounter(counter);

            long start = System.nanoTime();
            for (int q : queries) {
                list.contains(q);
            }
            long end = System.nanoTime();

            if (run >= WARMUP_RUNS) {
                times.add((end - start) / 1_000_000.0);
                steps = counter.getSteps();
                moves = counter.getMoves();
                comps = counter.getComparisons();
            }
        }

        Collections.sort(times);
        double medianTime = times.get(times.size() / 2);
        return new BenchmarkResult("W2", "-", name, n, medianTime, steps, moves, comps);
    }

    // =========================================================================
    // WORKLOAD 3: Insert & Remove (Head and Middle)
    // =========================================================================
    private static void runWorkload3(List<BenchmarkResult> results) {
        for (int n : SIZES) {
            System.out.println("  W3: n = " + n);
            int[] data = generateData(n);

            // Head variant
            results.add(benchmarkW3Variant("DynamicArray", n, data, "head", true));
            results.add(benchmarkW3Variant("MyLinkedList", n, data, "head", false));

            // Middle variant
            results.add(benchmarkW3Variant("DynamicArray", n, data, "middle", true));
            results.add(benchmarkW3Variant("MyLinkedList", n, data, "middle", false));
        }
    }

    private static BenchmarkResult benchmarkW3Variant(String name, int n, int[] data, String variant, boolean isArray) {
        final int OP_COUNT = 1000;
        Random rng = new Random(SEED + 303);
        int[] insertValues = new int[OP_COUNT];
        for (int i = 0; i < OP_COUNT; i++) {
            insertValues[i] = rng.nextInt(1_000_000);
        }

        List<Double> times = new ArrayList<>();
        long steps = 0, moves = 0, comps = 0;

        for (int run = 0; run < WARMUP_RUNS + REPEAT_RUNS; run++) {
            IntList list = isArray ? new DynamicArray(n + OP_COUNT) : new MyLinkedList();
            for (int val : data) {
                list.add(val);
            }

            OperationCounter counter = new OperationCounter();
            list.setCounter(counter);

            int targetIndex = variant.equals("head") ? 0 : (n / 2);

            long start = System.nanoTime();
            // 1,000 insertions
            for (int i = 0; i < OP_COUNT; i++) {
                list.add(targetIndex, insertValues[i]);
            }
            // 1,000 removals
            for (int i = 0; i < OP_COUNT; i++) {
                list.remove(targetIndex);
            }
            long end = System.nanoTime();

            if (run >= WARMUP_RUNS) {
                times.add((end - start) / 1_000_000.0);
                steps = counter.getSteps();
                moves = counter.getMoves();
                comps = counter.getComparisons();
            }
        }

        Collections.sort(times);
        double medianTime = times.get(times.size() / 2);
        return new BenchmarkResult("W3", variant, name, n, medianTime, steps, moves, comps);
    }

    // =========================================================================
    // WORKLOAD 4: Priority Processing (MinHeap)
    // =========================================================================
    private static void runWorkload4(List<BenchmarkResult> results) {
        for (int n : SIZES) {
            System.out.println("  W4: n = " + n);
            int[] data = generateData(n);

            List<Double> times = new ArrayList<>();
            long steps = 0, moves = 0, comps = 0;

            for (int run = 0; run < WARMUP_RUNS + REPEAT_RUNS; run++) {
                MinHeap heap = new MinHeap(n);
                OperationCounter counter = new OperationCounter();
                heap.setCounter(counter);

                long start = System.nanoTime();
                // Insert n values
                for (int val : data) {
                    heap.insert(val);
                }

                // ExtractMin n times and verify non-decreasing order
                int prev = Integer.MIN_VALUE;
                for (int i = 0; i < n; i++) {
                    int current = heap.extractMin();
                    if (current < prev) {
                        throw new IllegalStateException("MinHeap invariant violated: " + current + " < " + prev);
                    }
                    prev = current;
                }
                long end = System.nanoTime();

                if (run >= WARMUP_RUNS) {
                    times.add((end - start) / 1_000_000.0);
                    steps = counter.getSteps();
                    moves = counter.getMoves();
                    comps = counter.getComparisons();
                }
            }

            Collections.sort(times);
            double medianTime = times.get(times.size() / 2);
            results.add(new BenchmarkResult("W4", "-", "MinHeap", n, medianTime, steps, moves, comps));
        }
    }

    // =========================================================================
    // BONUS TASK A: JOL Memory Footprint
    // =========================================================================
    private static void runMemoryBenchmark(File outputFile) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("structure,n,bytes,megabytes");

            for (int n : SIZES) {
                int[] data = generateData(n);

                // DynamicArray
                DynamicArray da = new DynamicArray(n);
                for (int v : data) da.add(v);
                long daBytes = GraphLayout.parseInstance(da).totalSize();
                double daMb = daBytes / (1024.0 * 1024.0);
                writer.printf("DynamicArray,%d,%d,%.5f%n", n, daBytes, daMb);

                // MyLinkedList
                MyLinkedList ll = new MyLinkedList();
                for (int v : data) ll.add(v);
                long llBytes = GraphLayout.parseInstance(ll).totalSize();
                double llMb = llBytes / (1024.0 * 1024.0);
                writer.printf("MyLinkedList,%d,%d,%.5f%n", n, llBytes, llMb);

                // MinHeap
                MinHeap mh = new MinHeap(n);
                for (int v : data) mh.insert(v);
                long mhBytes = GraphLayout.parseInstance(mh).totalSize();
                double mhMb = mhBytes / (1024.0 * 1024.0);
                writer.printf("MinHeap,%d,%d,%.5f%n", n, mhBytes, mhMb);

                System.out.printf("  JOL Memory (n=%d): DynamicArray=%.3f MB, MinHeap=%.3f MB, MyLinkedList=%.3f MB%n",
                        n, daMb, mhMb, llMb);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================================================================
    // BONUS TASK B: Floyd's buildHeap vs N-inserts
    // =========================================================================
    private static void runHeapBuildBenchmark(File outputFile) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("method,n,time_ms,comparisons,moves,steps");

            for (int n : SIZES) {
                int[] data = generateData(n);

                // Method 1: N separate insert(x) calls -> O(n log n)
                List<Double> nInsertTimes = new ArrayList<>();
                long nInsertSteps = 0, nInsertMoves = 0, nInsertComps = 0;
                for (int run = 0; run < WARMUP_RUNS + REPEAT_RUNS; run++) {
                    MinHeap heap = new MinHeap(n);
                    OperationCounter counter = new OperationCounter();
                    heap.setCounter(counter);

                    long start = System.nanoTime();
                    for (int val : data) {
                        heap.insert(val);
                    }
                    long end = System.nanoTime();

                    if (run >= WARMUP_RUNS) {
                        nInsertTimes.add((end - start) / 1_000_000.0);
                        nInsertSteps = counter.getSteps();
                        nInsertMoves = counter.getMoves();
                        nInsertComps = counter.getComparisons();
                    }
                }
                Collections.sort(nInsertTimes);
                double medianNInsertTime = nInsertTimes.get(nInsertTimes.size() / 2);
                writer.printf("N_Inserts,%d,%.4f,%d,%d,%d%n", n, medianNInsertTime, nInsertComps, nInsertMoves, nInsertSteps);

                // Method 2: Floyd's buildHeap(array) -> O(n)
                List<Double> floydTimes = new ArrayList<>();
                long floydSteps = 0, floydMoves = 0, floydComps = 0;
                for (int run = 0; run < WARMUP_RUNS + REPEAT_RUNS; run++) {
                    MinHeap heap = new MinHeap(n);
                    OperationCounter counter = new OperationCounter();
                    heap.setCounter(counter);

                    long start = System.nanoTime();
                    heap.buildHeap(data);
                    long end = System.nanoTime();

                    if (run >= WARMUP_RUNS) {
                        floydTimes.add((end - start) / 1_000_000.0);
                        floydSteps = counter.getSteps();
                        floydMoves = counter.getMoves();
                        floydComps = counter.getComparisons();
                    }
                }
                Collections.sort(floydTimes);
                double medianFloydTime = floydTimes.get(floydTimes.size() / 2);
                writer.printf("Floyd_O(n),%d,%.4f,%d,%d,%d%n", n, medianFloydTime, floydComps, floydMoves, floydSteps);

                System.out.printf("  BuildHeap (n=%d): N_Inserts=%.3f ms (%d comps) vs Floyd=%.3f ms (%d comps)%n",
                        n, medianNInsertTime, nInsertComps, medianFloydTime, floydComps);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeResultsCsv(File file, List<BenchmarkResult> results) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("workload,variant,structure,n,time_ms,steps,moves,comparisons");
            for (BenchmarkResult r : results) {
                writer.printf("%s,%s,%s,%d,%.4f,%d,%d,%d%n",
                        r.workload, r.variant, r.structure, r.n, r.timeMs, r.steps, r.moves, r.comparisons);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class BenchmarkResult {
        String workload;
        String variant;
        String structure;
        int n;
        double timeMs;
        long steps;
        long moves;
        long comparisons;

        BenchmarkResult(String workload, String variant, String structure, int n, double timeMs, long steps, long moves, long comparisons) {
            this.workload = workload;
            this.variant = variant;
            this.structure = structure;
            this.n = n;
            this.timeMs = timeMs;
            this.steps = steps;
            this.moves = moves;
            this.comparisons = comparisons;
        }
    }
}
