package com.assignment;

import com.assignment.ds.MinHeap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MinHeapTest {

    private MinHeap heap;

    @BeforeEach
    public void setUp() {
        heap = new MinHeap(4);
    }

    @Test
    public void testEmptyStructure() {
        assertTrue(heap.isEmpty());
        assertEquals(0, heap.size());
        assertThrows(IllegalStateException.class, () -> heap.peekMin());
        assertThrows(IllegalStateException.class, () -> heap.extractMin());
    }

    @Test
    public void testOneElement() {
        heap.insert(42);
        assertFalse(heap.isEmpty());
        assertEquals(1, heap.size());
        assertEquals(42, heap.peekMin());
        assertTrue(heap.checkHeapProperty());

        int extracted = heap.extractMin();
        assertEquals(42, extracted);
        assertTrue(heap.isEmpty());
        assertEquals(0, heap.size());
    }

    @Test
    public void testDuplicateValues() {
        heap.insert(10);
        heap.insert(10);
        heap.insert(10);
        heap.insert(5);
        heap.insert(5);

        assertEquals(5, heap.size());
        assertTrue(heap.checkHeapProperty());

        assertEquals(5, heap.extractMin());
        assertEquals(5, heap.extractMin());
        assertEquals(10, heap.extractMin());
        assertEquals(10, heap.extractMin());
        assertEquals(10, heap.extractMin());
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testHeapPropertyAfterEveryOperation() {
        Random random = new Random(777);
        // Insert random elements and assert heap property after each insert
        for (int i = 0; i < 200; i++) {
            int val = random.nextInt(1000) - 500;
            heap.insert(val);
            assertTrue(heap.checkHeapProperty(), "Heap property violated after inserting " + val);
        }

        // Extract elements and assert heap property after each extraction
        int previous = Integer.MIN_VALUE;
        while (!heap.isEmpty()) {
            int current = heap.extractMin();
            assertTrue(current >= previous, "Values not in non-decreasing order: " + current + " < " + previous);
            previous = current;
            assertTrue(heap.checkHeapProperty(), "Heap property violated after extractMin");
        }
    }

    @Test
    public void testSortedOutputAgainstJavaPriorityQueue() {
        PriorityQueue<Integer> expected = new PriorityQueue<>();
        MinHeap actual = new MinHeap();
        Random random = new Random(888);

        int n = 500;
        for (int i = 0; i < n; i++) {
            int val = random.nextInt(2000);
            expected.add(val);
            actual.insert(val);
        }

        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < n; i++) {
            int expMin = expected.poll();
            int actMin = actual.extractMin();
            assertEquals(expMin, actMin, "Mismatch at index " + i);
        }

        assertTrue(actual.isEmpty());
    }

    @Test
    public void testFloydBuildHeap() {
        Random random = new Random(999);
        int[] data = new int[300];
        for (int i = 0; i < data.length; i++) {
            data[i] = random.nextInt(5000);
        }

        MinHeap floydHeap = new MinHeap();
        floydHeap.buildHeap(data);

        assertEquals(data.length, floydHeap.size());
        assertTrue(floydHeap.checkHeapProperty(), "Floyd's buildHeap did not maintain heap property");

        // Verify sorted output
        int[] sortedData = Arrays.copyOf(data, data.length);
        Arrays.sort(sortedData);

        for (int expectedVal : sortedData) {
            assertEquals(expectedVal, floydHeap.extractMin());
        }
    }

    @Test
    public void testMetricsCounter() {
        heap.insert(20);
        heap.insert(10); // Causes bubbleUp swap
        heap.insert(5);  // Causes bubbleUp swaps

        assertTrue(heap.getCounter().getMoves() > 0);
        assertTrue(heap.getCounter().getComparisons() > 0);
        assertTrue(heap.getCounter().getSteps() > 0);
    }
}
