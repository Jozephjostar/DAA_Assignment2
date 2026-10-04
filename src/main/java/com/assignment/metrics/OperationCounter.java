package com.assignment.metrics;

/**
 * OperationCounter tracks the fundamental physical operations executed
 * by the data structures during workloads:
 * - steps: array cell reads or moves to the next node in a linked list.
 * - moves: array element shifts or pointer/link updates in a linked list.
 * - comparisons: comparisons between two data elements.
 */
public class OperationCounter {
    private long steps;
    private long moves;
    private long comparisons;

    public OperationCounter() {
        this.steps = 0;
        this.moves = 0;
        this.comparisons = 0;
    }

    public void countStep() {
        steps++;
    }

    public void countSteps(long count) {
        steps += count;
    }

    public void countMove() {
        moves++;
    }

    public void countMoves(long count) {
        moves += count;
    }

    public void countComparison() {
        comparisons++;
    }

    public void countComparisons(long count) {
        comparisons += count;
    }

    public long getSteps() {
        return steps;
    }

    public long getMoves() {
        return moves;
    }

    public long getComparisons() {
        return comparisons;
    }

    public void reset() {
        this.steps = 0;
        this.moves = 0;
        this.comparisons = 0;
    }

    @Override
    public String toString() {
        return "OperationCounter{" +
                "steps=" + steps +
                ", moves=" + moves +
                ", comparisons=" + comparisons +
                '}';
    }
}
