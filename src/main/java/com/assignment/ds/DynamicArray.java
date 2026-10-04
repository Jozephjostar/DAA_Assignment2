package com.assignment.ds;

import com.assignment.metrics.OperationCounter;

/**
 * DynamicArray is a resizable array storing primitive 32-bit integers.
 * It provides O(1) amortized append, O(1) random access, and doubles
 * capacity when full.
 */
public class DynamicArray implements IntList {
    private static final int DEFAULT_CAPACITY = 10;

    private int[] data;
    private int size;
    private int capacity;
    private OperationCounter counter;

    public DynamicArray() {
        this(DEFAULT_CAPACITY);
    }

    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 1) {
            initialCapacity = DEFAULT_CAPACITY;
        }
        this.capacity = initialCapacity;
        this.data = new int[capacity];
        this.size = 0;
        this.counter = new OperationCounter();
    }

    private void ensureCapacity() {
        if (size >= capacity) {
            int newCapacity = capacity * 2;
            int[] newData = new int[newCapacity];
            for (int i = 0; i < size; i++) {
                counter.countStep(); // Read old cell
                counter.countMove(); // Move to new cell
                newData[i] = data[i];
            }
            data = newData;
            capacity = newCapacity;
        }
    }

    @Override
    public void add(int x) {
        ensureCapacity();
        data[size] = x;
        counter.countMove();
        size++;
    }

    @Override
    public void add(int index, int x) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        ensureCapacity();
        for (int i = size - 1; i >= index; i--) {
            counter.countStep(); // Read cell
            counter.countMove(); // Shift element to the right
            data[i + 1] = data[i];
        }
        data[index] = x;
        counter.countMove(); // Place new element
        size++;
    }

    @Override
    public int remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        counter.countStep(); // Read target element
        int removedValue = data[index];
        for (int i = index; i < size - 1; i++) {
            counter.countStep(); // Read next cell
            counter.countMove(); // Shift element to the left
            data[i] = data[i + 1];
        }
        size--;
        return removedValue;
    }

    @Override
    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        counter.countStep(); // Direct array read
        return data[index];
    }

    @Override
    public boolean contains(int x) {
        for (int i = 0; i < size; i++) {
            counter.countStep(); // Read array cell
            counter.countComparison(); // Compare values
            if (data[i] == x) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public OperationCounter getCounter() {
        return counter;
    }

    @Override
    public void setCounter(OperationCounter counter) {
        this.counter = (counter != null) ? counter : new OperationCounter();
    }

    @Override
    public void resetMetrics() {
        counter.reset();
    }
}
