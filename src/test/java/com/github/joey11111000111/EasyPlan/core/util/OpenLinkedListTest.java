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
        assertNull(openList.getHead());
        assertNull(openList.getHead());
        for (int i = 0; i < 22; i++) {
            assertEquals(i, openList.size());
            openList.append(i);
        }
        assertEquals(22, openList.size());
        assertNotNull(openList.getHead());
        assertNotNull(openList.getTail());

        for (int i = 21; i >= 0; i--) {
            Integer value = openList.removeLast().getElement();
            assertEquals(new Integer(i), value);
            assertEquals(i, openList.size());
        }
        assertEquals(0, openList.size());
        assertNull(openList.getHead());
        assertNull(openList.getTail());
    }

    @Test
    public void testIteration() {
        for (int i = 0; i < 100; i++)
            openList.append(i);
        assertEquals(100, openList.size());

        Node<Integer> head = openList.getHead();
        Node<Integer> tail = openList.getTail();

        Integer lastI = tail.getElement();
        assertEquals(new Integer(99), lastI);
        Integer firstI = head.getElement();
        assertEquals(new Integer(0), firstI);

        assertFalse(tail.hasNext());
        assertTrue(tail.hasPrevious());
        assertTrue(head.hasNext());
        assertFalse(head.hasPrevious());

        try {
            head.previous();
            assertTrue(false);
        } catch (IllegalStateException ise) {
        }
        try {
            tail.next();
            assertTrue(false);
        } catch (IllegalStateException ise) {
        }

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
    }


}//class
