package com.github.joey11111000111.EasyPlan.core;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class TouchedStopsTest {

    TouchedStops ts;

    @Before
    public void init() {
        ts = new TouchedStops();
    }

    @Test
    public void testInitialState() {
        assertEquals(0, ts.getLastStop());
        // test boolean
        assertFalse(ts.isClosed());
        assertFalse(ts.isModified());
        assertFalse(ts.canUndo());

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

        assertEquals(1, ts.getStops().length);
    }

    @Test
    public void testAppendRemoveCloseClear() {
        ts.removeChainFrom(0);
        assertEquals(0, ts.getLastStop());
        ts.appendStop(1);
        ts.removeChainFrom(0);
        assertEquals(0, ts.getLastStop());

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

        ts.appendStop(0);
        assertTrue(ts.isClosed());
        try {
            ts.appendStop(6);
            assertTrue(false);
        } catch (IllegalStateException ise) {}
        try {
            ts.appendStop(2);
            assertTrue(false);
        } catch (IllegalStateException ise) {}

        try {
            ts.removeChainFrom(19);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
        ts.removeChainFrom(4);
        assertFalse(ts.isClosed());
        int[] stops = ts.getStops();
        assertEquals(4, stops.length);
        assertEquals(0, stops[0]);
        assertEquals(1, stops[1]);
        assertEquals(4, stops[2]);
        assertEquals(1, stops[3]);

        ts.appendStop(4);
        ts.appendStop(0);

        ts.clear();
        assertEquals(1, ts.getStops().length);
        assertFalse(ts.isClosed());

        ts.appendStop(2);
        ts.appendStop(3);
        ts.appendStop(5);
        ts.appendStop(6);
        ts.appendStop(9);
        ts.appendStop(8);
        ts.removeChainFrom(5);
        stops = ts.getStops();
        assertEquals(3, stops.length);
        assertEquals(0, stops[0]);
        assertEquals(2, stops[1]);
        assertEquals(3, stops[2]);

        ts.clear();
    }

    @Test
    public void testUndo() {
        ts.appendStop(2);
        ts.appendStop(3);
        ts.appendStop(5);
        // delete -undo test
        assertTrue(ts.canUndo());
        ts.undo();
        assertFalse(ts.isClosed());
        int[] stops = ts.getStops();
        assertEquals(3, stops.length);
        assertEquals(0, stops[0]);
        assertEquals(2, stops[1]);
        assertEquals(3, stops[2]);
        // delete -undo in a closed state test
        ts.appendStop(5);
        ts.appendStop(6);
        ts.appendStop(0);
        // open -undo test
        assertTrue(ts.isClosed());
        ts.undo();
        assertFalse(ts.isClosed());
        stops = ts.getStops();
        assertEquals(5, stops.length);
        assertEquals(0, stops[0]);
        assertEquals(2, stops[1]);
        assertEquals(3, stops[2]);
        assertEquals(5, stops[3]);
        assertEquals(6, stops[4]);

        ts.appendStop(9);
        ts.appendStop(10);
        ts.removeChainFrom(5);
        stops = ts.getStops();
        assertEquals(3, stops.length);
        // append -undo test
        ts.undo();
        assertFalse(ts.isClosed());
        stops = ts.getStops();
        assertEquals(7, stops.length);
        assertEquals(10, stops[6]);
        assertEquals(9, stops[5]);
        assertEquals(6, stops[4]);

        ts.appendStop(5);
        ts.appendStop(2);
        ts.appendStop(0);
        ts.removeChainFrom(5);
        assertFalse(ts.isClosed());
        // append_close -undo test
        ts.undo();
        assertTrue(ts.isClosed());
        stops = ts.getStops();
        assertEquals(10, stops.length);

        ts.clear();
        ts.markAsSaved();
        assertFalse(ts.isClosed());
        assertFalse(ts.canUndo());
        ts.markAsSaved();   // should have no effect


        ts.appendStop(1);
        ts.appendStop(4);
        ts.appendStop(6);
        ts.appendStop(9);
        ts.appendStop(8);
        ts.appendStop(11);
        ts.appendStop(7);
        ts.removeChainFrom(9);
        ts.appendStop(5);
        ts.removeChainFrom(5);
        ts.appendStop(0);

        // test "undo until there is nothing to undo"
        while (ts.canUndo())
            ts.undo();

        assertEquals(1, ts.getStopCount());
        assertFalse(ts.isClosed());
        ts.clear();
    }

    @Test
    public void testTravelTimes() {
        // test a closed service
        ts.appendStop(1);
        ts.appendStop(4);
        ts.appendStop(6);
        ts.appendStop(0);
        int[] times = ts.getTravelTimes();
        assertEquals(4, times.length);
        assertEquals(8, times[0]);
        assertEquals(21, times[1]);
        assertEquals(31, times[2]);
        assertEquals(43, times[3]);
        // test an open service
        ts.undo();
        times = ts.getTravelTimes();
        assertEquals(3, times.length);
        assertEquals(8, times[0]);
        assertEquals(21, times[1]);
        assertEquals(31, times[2]);

        // test a 1-stop-sized service
        ts.clear();
        ts.appendStop(2);
        times = ts.getTravelTimes();
        assertEquals(1, times.length);
        assertEquals(5, times[0]);

        // test an empty service
        ts.clear();
        times = ts.getTravelTimes();
        assertEquals(0, times.length);
    }

    @Test
    public void testOther() {
        ts.clear();
        ts.appendStop(1);
        assertTrue(ts.isModified());
        ts.markAsSaved();
        assertFalse(ts.isModified());
        ts.clear();
        assertTrue(ts.isModified());
        ts.appendStop(1);
        ts.appendStop(4);
        ts.appendStop(7);
        ts.undo();
        ts.appendStop(0);
        assertEquals(0, ts.getReachableStopIds().length);

        ts.undo();
        int[] reachStops = ts.getReachableStopIds();
        Arrays.sort(reachStops);
        assertEquals(4, reachStops.length);
        assertEquals(0, reachStops[0]);
        assertEquals(1, reachStops[1]);
        assertEquals(6, reachStops[2]);
        assertEquals(7, reachStops[3]);

        ts.appendStop(7);
        ts.appendStop(8);
        ts.appendStop(6);
        ts.appendStop(9);
        reachStops = ts.getReachableStopIds();
        Arrays.sort(reachStops);
        assertEquals(4, reachStops.length);
        assertEquals(6, reachStops[0]);
        assertEquals(8, reachStops[1]);
        assertEquals(10, reachStops[2]);
        assertEquals(12, reachStops[3]);

        ts.appendStop(8);
        ts.appendStop(9);
        reachStops = ts.getReachableStopIds();
        Arrays.sort(reachStops);
        assertEquals(3, reachStops.length);
        assertEquals(6, reachStops[0]);
        assertEquals(10, reachStops[1]);
        assertEquals(12, reachStops[2]);

        ts.clear();
    }
}//class
