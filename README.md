# DAA Assignment 2 - Data Structures Benchmark & Analysis

This project implements three fundamental data structures from scratch in Java: `DynamicArray`, `MyLinkedList`, and `MinHeap` storing primitive `int` values without boxed objects or `java.util` collections.

It provides a full benchmarking suite measuring execution time, physical steps, element moves, and comparisons across four workloads, exports metrics to CSV, and generates performance charts.

---

## Project Structure

```text
├── pom.xml                               # Maven project configuration
├── README.md                             # Build and execution guide
├── REPORT.md                             # Complete assignment analysis report
├── generate_plots.py                     # Script generating PNG charts
├── results/
│   ├── results.csv                       # Workloads 1-4 benchmark metrics
│   ├── memory.csv                        # JOL memory footprint results (Bonus A)
│   ├── heap_build.csv                    # Floyd vs N-inserts results (Bonus B)
│   └── plots/                            # High-resolution PNG charts
│       ├── w1_random_access.png
│       ├── w2_search.png
│       ├── w3_insert_remove.png
│       ├── w4_priority_processing.png
│       ├── bonus_a_memory.png
│       └── bonus_b_build_heap.png
└── src/
    ├── main/java/com/assignment/
    │   ├── ds/
    │   │   ├── IntList.java              # Common interface for list structures
    │   │   ├── DynamicArray.java         # Resizable array with 2x growth
    │   │   ├── MyLinkedList.java         # Singly linked list with head & tail
    │   │   └── MinHeap.java              # Array-based binary min-heap
    │   ├── metrics/
    │   │   └── OperationCounter.java     # Tracks steps, moves, and comparisons
    │   └── benchmark/
    │       └── BenchmarkRunner.java      # Automated benchmark engine
    └── test/java/com/assignment/
        ├── DynamicArrayTest.java         # JUnit 5 tests for DynamicArray
        ├── MyLinkedListTest.java         # JUnit 5 tests for MyLinkedList
        └── MinHeapTest.java              # JUnit 5 tests for MinHeap
```

---

## Prerequisites

- **Java JDK 17+** (e.g., Eclipse Temurin 17 or OpenJDK 17)
- **Apache Maven 3.8+**
- **Python 3.8+** with `matplotlib` and `pandas` (for regenerating plots)

---

## Quick Start / Build Commands

### 1. Compile the Project
```bash
mvn clean compile
```

### 2. Run All JUnit 5 Tests
Runs 22 unit tests checking correctness against `java.util`, edge cases (empty, single element, duplicates, boundaries), and min-heap properties:
```bash
mvn test
```

### 3. Run the Benchmarks
Executes Workloads 1–4 ($n \in \{100, 1\,000, 10\,000, 100\,000\}$), takes median time of 5 runs after warmup, and records metrics to `results/results.csv`, `results/memory.csv`, and `results/heap_build.csv`:
```bash
mvn exec:java
```

### 4. Regenerate Performance Plots
```bash
python3 generate_plots.py
```

---

## Git Workflow & Branches

The repository maintains standard Git feature branches merged into `main`:
- `feature/metrics`: Operation counter implementation.
- `feature/array`: DynamicArray implementation and tests.
- `feature/list`: MyLinkedList implementation and tests.
- `feature/heap`: MinHeap implementation, Floyd's buildHeap, and tests.
- `main`: Release branch with complete code, tagged at `v1.0`.
