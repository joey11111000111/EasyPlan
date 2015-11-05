package com.github.joey11111000111.EasyPlan.core;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by joey on 2015.11.05..
 */
public class TouchedStopsTest {

    TouchedStops ts;

    @Before
    public void init() {
        ts = new TouchedStops();
    }

    @Test
    public void testEmptyState() {
        // test boolean
        assertTrue(ts.isEmpty());
        assertFalse(ts.isClosed());
        assertFalse(ts.isModified());
        assertFalse(ts.isStationReachable());
        assertFalse(ts.canUndo());

        try {
            ts.closeService();
            assertTrue(false);
        } catch (IllegalStateException ise) {}
        try {
            ts.removeChainFrom(1);
            assertTrue(false);
        } catch (IllegalStateException ise) {}
        try {
            ts.undo();
            assertTrue(false);
        } catch (IllegalStateException ise) {}

        int[] reachableIds = ts.getReachableStopIds();
        Arrays.sort(reachableIds);
        assertEquals(4, reachableIds.length);
        assertEquals(1, reachableIds[0]);
        assertEquals(2, reachableIds[1]);
        assertEquals(4, reachableIds[2]);
        assertEquals(6, reachableIds[3]);

        try {
            ts.getStops();
            assertTrue(false);
        } catch (IllegalStateException ise) {}
    }

    @Test
    public void testAppendRemoveCloseClear() {
        try {
            ts.appendStop(0);
            assertTrue(false);
        } catch (IllegalArgumentException ise) {}
        try {
            ts.appendStop(-6);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
        try {
            ts.appendStop(7);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}

        ts.appendStop(1);
        try {
            ts.appendStop(12);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}

        ts.appendStop(4);
        ts.appendStop(1);
        ts.appendStop(4);
        try {
            ts.appendStop(1);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}

        ts.closeService();
        assertTrue(ts.isClosed());
        try {
            ts.appendStop(6);
            assertTrue(false);
        } catch (IllegalStateException ise) {}
        try {
            ts.appendStop(2);
            assertTrue(false);
        } catch (IllegalStateException ise) {}

        ts.removeChainFrom(4);
        assertFalse(ts.isClosed());
        int[] stops = ts.getStops();
        assertEquals(3, stops.length);
        assertEquals(1, stops[0]);
        assertEquals(4, stops[1]);
        assertEquals(1, stops[2]);

        ts.appendStop(4);
        ts.closeService();

        ts.clear();
        assertTrue(ts.isEmpty());
        assertFalse(ts.isClosed());

        ts.appendStop(2);
        ts.appendStop(3);
        ts.appendStop(5);
        ts.appendStop(6);
        ts.appendStop(9);
        ts.appendStop(8);
        ts.removeChainFrom(5);
        stops = ts.getStops();
        assertEquals(2, stops.length);
        assertEquals(2, stops[0]);
        assertEquals(3, stops[1]);

        ts.clear();
    }

    @Test
    public void testUndo() {
        ts.appendStop(2);
        ts.appendStop(3);
        ts.appendStop(5);
        System.out.println("1");

        assertTrue(ts.canUndo());
        ts.undo();
        System.out.println("2");
        int[] stops = ts.getStops();
        System.out.println("3");
        assertEquals(3, stops.length);
        System.out.println("4");
        assertEquals(2, stops[0]);
        System.out.println("5");
        assertEquals(3, stops[1]);
        System.out.println("6");
        assertEquals(5, stops[2]);
        System.out.println("7");
    }


}//class
