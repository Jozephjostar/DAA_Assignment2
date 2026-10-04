package com.assignment;

import com.assignment.ds.DynamicArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicArrayTest {

    private DynamicArray array;

    @BeforeEach
    public void setUp() {
        array = new DynamicArray(4);
    }

    @Test
    public void testEmptyStructure() {
        assertTrue(array.isEmpty());
        assertEquals(0, array.size());
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));
        assertFalse(array.contains(10));
    }

    @Test
    public void testOneElement() {
        array.add(42);
        assertFalse(array.isEmpty());
        assertEquals(1, array.size());
        assertEquals(42, array.get(0));
        assertTrue(array.contains(42));
        assertFalse(array.contains(99));

        int removed = array.remove(0);
        assertEquals(42, removed);
        assertTrue(array.isEmpty());
        assertEquals(0, array.size());
    }

    @Test
    public void testDynamicResizing() {
        // Initial capacity is 4
        assertEquals(4, array.getCapacity());
        for (int i = 0; i < 5; i++) {
            array.add(i * 10);
        }
        assertEquals(5, array.size());
        assertEquals(8, array.getCapacity()); // Doubled from 4 to 8
        for (int i = 0; i < 5; i++) {
            assertEquals(i * 10, array.get(i));
        }
    }

    @Test
    public void testFirstAndLastIndexOperations() {
        array.add(10);
        array.add(20);
        array.add(30);

        // Add at head (index 0)
        array.add(0, 5);
        assertEquals(5, array.get(0));
        assertEquals(4, array.size());

        // Add at tail (index size)
        array.add(array.size(), 40);
        assertEquals(40, array.get(array.size() - 1));
        assertEquals(5, array.size());

        // Remove from head
        assertEquals(5, array.remove(0));
        assertEquals(10, array.get(0));

        // Remove from tail
        assertEquals(40, array.remove(array.size() - 1));
        assertEquals(30, array.get(array.size() - 1));
    }

    @Test
    public void testDuplicateValues() {
        array.add(7);
        array.add(7);
        array.add(7);
        assertEquals(3, array.size());
        assertTrue(array.contains(7));

        array.remove(1);
        assertEquals(2, array.size());
        assertEquals(7, array.get(0));
        assertEquals(7, array.get(1));
    }

    @Test
    public void testInvalidIndexThrowsException() {
        array.add(1);
        array.add(2);

        assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(-1, 99));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(3, 99));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(2));
    }

    @Test
    public void testCorrectnessAgainstJavaArrayList() {
        ArrayList<Integer> expected = new ArrayList<>();
        DynamicArray actual = new DynamicArray();
        Random random = new Random(12345);

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
        array.add(10);
        array.add(20);
        array.add(30);

        array.resetMetrics();
        assertEquals(0, array.getCounter().getSteps());
        assertEquals(0, array.getCounter().getMoves());
        assertEquals(0, array.getCounter().getComparisons());

        // get should take 1 step
        array.get(1);
        assertEquals(1, array.getCounter().getSteps());

        // contains should count comparisons and steps
        array.contains(20);
        assertTrue(array.getCounter().getComparisons() > 0);
    }
}
