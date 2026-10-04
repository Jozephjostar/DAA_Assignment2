package com.assignment.ds;

import com.assignment.metrics.OperationCounter;

/**
 * Common interface for list data structures storing primitive integers.
 * Enables uniform benchmarking of DynamicArray and MyLinkedList.
 */
public interface IntList {
    /**
     * Appends an integer to the end of the list.
     */
    void add(int x);

    /**
     * Inserts an integer at the specified index.
     * Throws IndexOutOfBoundsException if index is invalid.
     */
    void add(int index, int x);

    /**
     * Removes and returns the integer at the specified index.
     * Throws IndexOutOfBoundsException if index is invalid.
     */
    int remove(int index);

    /**
     * Returns the integer at the specified index.
     * Throws IndexOutOfBoundsException if index is invalid.
     */
    int get(int index);

    /**
     * Checks if the list contains the specified integer.
     */
    boolean contains(int x);

    /**
     * Returns the number of elements in the list.
     */
    int size();

    /**
     * Checks if the list is empty.
     */
    boolean isEmpty();

    /**
     * Returns the operation counter attached to this structure.
     */
    OperationCounter getCounter();

    /**
     * Sets the operation counter attached to this structure.
     */
    void setCounter(OperationCounter counter);

    /**
     * Resets the operation counter metrics.
     */
    void resetMetrics();
}
