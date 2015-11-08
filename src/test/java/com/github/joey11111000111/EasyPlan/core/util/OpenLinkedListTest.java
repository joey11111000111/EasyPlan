package com.github.joey11111000111.EasyPlan.core.util;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static com.github.joey11111000111.EasyPlan.core.util.OpenLinkedList.Node;

/**
 * Created by joey on 2015.11.03..
 */
public class OpenLinkedListTest {

    OpenLinkedList<Integer> openList;

    @Before
    public void init() {
        openList = new OpenLinkedList<Integer>();
    }

    @Test
    public void testAppendAndRemoveLast() {
        // test append
        assertNull(openList.getHead());
        assertNull(openList.getHead());
        for (int i = 0; i < 22; i++) {
            assertEquals(i, openList.size());
            openList.append(i);
        }
        assertEquals(22, openList.size());
        assertNotNull(openList.getHead());
        assertNotNull(openList.getTail());

        // test removeLast
        for (int i = 21; i >= 0; i--) {
            Integer value = openList.removeLast().getElement();
            assertEquals(new Integer(i), value);
            assertEquals(i, openList.size());
        }

        // test that the list really is empty
        assertEquals(0, openList.size());
        assertNull(openList.getHead());
        assertNull(openList.getTail());

        // test removal exceptions
        try {
            openList.removeLast();
            assertTrue(false);
        } catch (IllegalStateException ise) {}
        try {
            openList.removeChainFrom(0);
            assertTrue(false);
        } catch (IllegalStateException ise) {}
        try {
            openList.removeChainFrom(new Integer(-1));
            assertTrue(false);
        } catch (IllegalStateException ise) {}

        openList.append(23);
        openList.append(12);
        try {
            openList.removeChainFrom(new Integer(-1));
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
        try {
            openList.removeChainFrom(2);
            assertTrue(false);
        } catch (IndexOutOfBoundsException ioobe) {}
    }//test

    @Test
    public void testIteration() {
        for (int i = 0; i < 100; i++)
            openList.append(i);
        assertEquals(100, openList.size());

        Node<Integer> head = openList.getHead();
        Node<Integer> tail = openList.getTail();

        // test the first (head) and last (tail) elements in value and next/previous references
        Integer lastI = tail.getElement();
        assertEquals(new Integer(99), lastI);
        Integer firstI = head.getElement();
        assertEquals(new Integer(0), firstI);
        assertFalse(tail.hasNext());
        assertTrue(tail.hasPrevious());
        assertTrue(head.hasNext());
        assertFalse(head.hasPrevious());

        // trying to get next/previous should throw and exception
        try {
            head.previous();
            assertTrue(false);
        } catch (IllegalStateException ise) {}
        try {
            tail.next();
            assertTrue(false);
        } catch (IllegalStateException ise) {}

        // iterate forward
        Node<Integer> node = head;
        for (int i = 0; i < openList.size(); i++) {
            Integer value = node.getElement();
            assertEquals(new Integer(i), value);
            if (!node.hasNext())
                break;
            node = node.next();
        }

        // iterate backwards
        node = tail;
        for (int i = 0; i < openList.size(); i++) {
            Integer value = node.getElement();
            assertEquals(new Integer(99 - i), value);
            if (!node.hasPrevious())
                break;
            node = node.previous();
        }

        // clear test
        openList.clear();
        assertNull(openList.getHead());
        assertNull(openList.getTail());
        assertEquals(0, openList.size());
        openList.clear(); // should have no effect
    }

    @Test
    public void testChainOperations() {
        for (int i = 10; i < 110; i++)
            openList.append(i);

        // test the removed and the remaining chains
        Node<Integer> node = openList.removeChainFrom(new Integer(106));
        assertFalse(node.hasPrevious());
        for (int i = 0; i < 4; i++) {
            assertEquals(new Integer(106 + i), node.getElement());
            if (!node.hasNext())
                break;
            node = node.next();
        }
        assertEquals(new Integer(105), openList.getTail().getElement());

        // refill the list with some repeating 0 values among the incrementing values
        openList.clear();
        for (int i = 0; i < 18; i++) {
            if (i % 3 == 0)
                openList.append(0);
            else
                openList.append(i);
        }

        // test that the chain was cut at the last appearance of the value 0
        node = openList.removeChainFrom(new Integer(0));
        assertEquals(15, openList.size());

        // test the appendChain method in a non-empty state of the list
        openList.appendChain(node);
        assertEquals(18, openList.size());
        Integer value = openList.removeLast().getElement();
        assertEquals(new Integer(17), value);
        value = openList.removeLast().getElement();
        assertEquals(new Integer(16), value);
        value = openList.removeLast().getElement();
        assertEquals(new Integer(0), value);

        // test index-based chain removal in both iteration directions
        openList.clear();
        for (int i = 0; i < 10; i++)
            openList.append(i);
        node = openList.removeChainFrom(8);
        assertEquals(new Integer(8), node.getElement());
        node = openList.removeChainFrom(1);
        assertEquals(new Integer(1), node.getElement());
        assertEquals(1, openList.size());

        // test appendChain method in an empty state of the list
        openList.clear();
        openList.appendChain(node);
        assertEquals(7, openList.size());
        for (int i = 1; i < 8; i++) {
            assertEquals(new Integer(i), node.getElement());
            if (node.hasNext())
                node = node.next();
        }

    }


}//class
