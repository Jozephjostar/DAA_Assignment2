package com.assignment;

import com.assignment.ds.MyLinkedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MyLinkedListTest {

    private MyLinkedList list;

    @BeforeEach
    public void setUp() {
        list = new MyLinkedList();
    }

    @Test
    public void testEmptyStructure() {
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
        assertFalse(list.contains(99));
    }

    @Test
    public void testOneElement() {
        list.add(100);
        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
        assertEquals(100, list.get(0));
        assertTrue(list.contains(100));
        assertFalse(list.contains(200));

        int removed = list.remove(0);
        assertEquals(100, removed);
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    public void testFirstAndLastIndexOperations() {
        list.add(10);
        list.add(20);
        list.add(30);

        // Add at head (index 0)
        list.add(0, 5);
        assertEquals(5, list.get(0));
        assertEquals(4, list.size());

        // Add at tail (index size)
        list.add(list.size(), 40);
        assertEquals(40, list.get(list.size() - 1));
        assertEquals(5, list.size());

        // Remove from head
        assertEquals(5, list.remove(0));
        assertEquals(10, list.get(0));

        // Remove from tail
        assertEquals(40, list.remove(list.size() - 1));
        assertEquals(30, list.get(list.size() - 1));
    }

    @Test
    public void testDuplicateValues() {
        list.add(5);
        list.add(5);
        list.add(5);
        assertEquals(3, list.size());
        assertTrue(list.contains(5));

        list.remove(1);
        assertEquals(2, list.size());
        assertEquals(5, list.get(0));
        assertEquals(5, list.get(1));
    }

    @Test
    public void testInvalidIndexThrowsException() {
        list.add(1);
        list.add(2);

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 99));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(3, 99));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(2));
    }

    @Test
    public void testCorrectnessAgainstJavaLinkedList() {
        LinkedList<Integer> expected = new LinkedList<>();
        MyLinkedList actual = new MyLinkedList();
        Random random = new Random(54321);

        for (int i = 0; i < 1000; i++) {
            int op = random.nextInt(4);
            int val = random.nextInt(500);

            if (op == 0 || expected.isEmpty()) {
                // Append
                expected.add(val);
                actual.add(val);
            } else if (op == 1) {
                // Insert at random index
                int index = random.nextInt(expected.size() + 1);
                expected.add(index, val);
                actual.add(index, val);
            } else if (op == 2) {
                // Remove at random index
                int index = random.nextInt(expected.size());
                int expRemoved = expected.remove(index);
                int actRemoved = actual.remove(index);
                assertEquals(expRemoved, actRemoved);
            } else {
                // Get / Contains
                int index = random.nextInt(expected.size());
                assertEquals(expected.get(index), actual.get(index));
                assertEquals(expected.contains(val), actual.contains(val));
            }
        }

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    public void testMetricsCounter() {
        list.add(10);
        list.add(20);
        list.add(30);

        list.resetMetrics();
        // get(2) needs 2 traversal steps to reach index 2
        int val = list.get(2);
        assertEquals(30, val);
        assertEquals(2, list.getCounter().getSteps());

        list.resetMetrics();
        list.contains(30);
        // contains 30: traverses 3 nodes (first is compared, step to 2nd, step to 3rd) -> 2 steps, 3 comparisons
        assertEquals(3, list.getCounter().getComparisons());
        assertEquals(2, list.getCounter().getSteps());
    }
}
