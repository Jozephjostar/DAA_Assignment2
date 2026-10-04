package com.assignment.ds;

import com.assignment.metrics.OperationCounter;

/**
 * MyLinkedList is a singly linked list storing primitive integers.
 * It maintains references to both head and tail nodes for efficient
 * O(1) additions at both ends.
 */
public class MyLinkedList implements IntList {

    /**
     * Internal node holding an integer data value and a next pointer.
     */
    private static class Node {
        int val;
        Node next;

        Node(int val) {
            this.val = val;
            this.next = null;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    private OperationCounter counter;

    public MyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
        this.counter = new OperationCounter();
    }

    @Override
    public void add(int x) {
        Node newNode = new Node(x);
        if (head == null) {
            head = newNode;
            tail = newNode;
            counter.countMoves(2); // Initializing head and tail
        } else {
            tail.next = newNode;
            tail = newNode;
            counter.countMoves(2); // Updating tail.next and tail
        }
        size++;
    }

    @Override
    public void add(int index, int x) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        if (index == size) {
            add(x);
            return;
        }

        Node newNode = new Node(x);
        if (index == 0) {
            newNode.next = head;
            head = newNode;
            counter.countMoves(2); // Link new node to head and update head
            if (tail == null) {
                tail = head;
                counter.countMove();
            }
        } else {
            Node curr = head;
            for (int i = 0; i < index - 1; i++) {
                curr = curr.next;
                counter.countStep(); // Traversal step
            }
            newNode.next = curr.next;
            curr.next = newNode;
            counter.countMoves(2); // Update newNode.next and curr.next
        }
        size++;
    }

    @Override
    public int remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        int removedVal;
        if (index == 0) {
            removedVal = head.val;
            head = head.next;
            counter.countMove(); // Update head pointer
            if (head == null) {
                tail = null;
                counter.countMove();
            }
        } else {
            Node curr = head;
            for (int i = 0; i < index - 1; i++) {
                curr = curr.next;
                counter.countStep(); // Traversal step
            }
            Node target = curr.next;
            removedVal = target.val;
            curr.next = target.next;
            counter.countMove(); // Unlink target node
            if (curr.next == null) {
                tail = curr;
                counter.countMove();
            }
        }
        size--;
        return removedVal;
    }

    @Override
    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Node curr = head;
        for (int i = 0; i < index; i++) {
            curr = curr.next;
            counter.countStep(); // Step: move to next node
        }
        return curr.val;
    }

    @Override
    public boolean contains(int x) {
        Node curr = head;
        while (curr != null) {
            counter.countComparison(); // Compare values
            if (curr.val == x) {
                return true;
            }
            curr = curr.next;
            counter.countStep(); // Step: move to next node
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
