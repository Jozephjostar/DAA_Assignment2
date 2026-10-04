package com.assignment.ds;

import com.assignment.metrics.OperationCounter;

import java.util.Arrays;

/**
 * MinHeap is an array-based binary min-heap storing primitive integers.
 * The minimum element is always maintained at index 0.
 * Includes insert, peekMin, extractMin, and Floyd's bottom-up buildHeap.
 */
public class MinHeap {
    private static final int DEFAULT_CAPACITY = 10;

    private int[] heap;
    private int size;
    private OperationCounter counter;

    public MinHeap() {
        this(DEFAULT_CAPACITY);
    }

    public MinHeap(int initialCapacity) {
        if (initialCapacity < 1) {
            initialCapacity = DEFAULT_CAPACITY;
        }
        this.heap = new int[initialCapacity];
        this.size = 0;
        this.counter = new OperationCounter();
    }

    private void ensureCapacity() {
        if (size >= heap.length) {
            int newCapacity = heap.length * 2;
            int[] newHeap = new int[newCapacity];
            for (int i = 0; i < size; i++) {
                counter.countStep(); // Read old array
                counter.countMove(); // Write to new array
                newHeap[i] = heap[i];
            }
            heap = newHeap;
        }
    }

    /**
     * Inserts an element into the min-heap and restores the heap property
     * via bubble-up.
     */
    public void insert(int x) {
        ensureCapacity();
        heap[size] = x;
        counter.countMove(); // Place at the end
        bubbleUp(size);
        size++;
    }

    /**
     * Returns the minimum element without removing it in O(1).
     * Throws IllegalStateException if the heap is empty.
     */
    public int peekMin() {
        if (isEmpty()) {
            throw new IllegalStateException("Heap is empty");
        }
        counter.countStep(); // Read root element
        return heap[0];
    }

    /**
     * Extracts and returns the minimum element from the min-heap.
     * Restores the heap property via bubble-down.
     * Throws IllegalStateException if the heap is empty.
     */
    public int extractMin() {
        if (isEmpty()) {
            throw new IllegalStateException("Heap is empty");
        }
        counter.countStep(); // Read min at root
        int min = heap[0];

        // Move last element to root
        heap[0] = heap[size - 1];
        counter.countMove();
        size--;

        if (size > 0) {
            bubbleDown(0);
        }

        return min;
    }

    /**
     * Floyd's bottom-up heap construction algorithm running in O(n) time.
     */
    public void buildHeap(int[] array) {
        if (array == null) {
            throw excitingNullPointerException();
        }
        this.size = array.length;
        this.heap = new int[Math.max(DEFAULT_CAPACITY, size)];
        for (int i = 0; i < size; i++) {
            counter.countStep(); // Read array
            counter.countMove(); // Write to heap
            this.heap[i] = array[i];
        }

        // Sift down all non-leaf nodes starting from (size / 2) - 1 down to 0
        for (int i = (size / 2) - 1; i >= 0; i--) {
            bubbleDown(i);
        }
    }

    private NullPointerException excitingNullPointerException() {
        return new NullPointerException("Input array cannot be null");
    }

    /**
     * Restores min-heap property upwards from index i.
     */
    private void bubbleUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            counter.countStep(); // Read parent and current elements
            counter.countComparison(); // Compare values
            if (heap[i] < heap[parent]) {
                swap(i, parent);
                i = parent;
            } else {
                break;
            }
        }
    }

    /**
     * Restores min-heap property downwards from index i.
     */
    public void bubbleDown(int i) {
        while (true) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int smallest = i;

            if (left < size) {
                counter.countStep(); // Read left child
                counter.countComparison(); // Compare with current smallest
                if (heap[left] < heap[smallest]) {
                    smallest = left;
                }
            }

            if (right < size) {
                counter.countStep(); // Read right child
                counter.countComparison(); // Compare with current smallest
                if (heap[right] < heap[smallest]) {
                    smallest = right;
                }
            }

            if (smallest != i) {
                swap(i, smallest);
                i = smallest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        int temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
        counter.countMoves(2); // Two cell writes
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getCapacity() {
        return heap.length;
    }

    public int[] getElements() {
        return Arrays.copyOf(heap, size);
    }

    /**
     * Checks if the binary heap condition heap[parent] <= heap[child] holds
     * for every child in the current heap.
     */
    public boolean checkHeapProperty() {
        for (int i = 0; i < size; i++) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            if (left < size && heap[i] > heap[left]) {
                return false;
            }
            if (right < size && heap[i] > heap[right]) {
                return false;
            }
        }
        return true;
    }

    public OperationCounter getCounter() {
        return counter;
    }

    public void setCounter(OperationCounter counter) {
        this.counter = (counter != null) ? counter : new OperationCounter();
    }

    public void resetMetrics() {
        counter.reset();
    }
}
