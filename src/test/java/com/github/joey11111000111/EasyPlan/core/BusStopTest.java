package com.github.joey11111000111.EasyPlan.core;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by joey on 2015.11.02..
 */
public class BusStopTest {

    @Test
    public void testReturnedValues() {
        // test the bus station
        assertEquals(5, BusStop.getXCoordOf(0));
        assertEquals(9, BusStop.getYCoordOf(0));
        assertTrue(BusStop.isReachableToFrom(1, 0));
        assertTrue(BusStop.isReachableToFrom(4, 0));
        assertTrue(BusStop.isReachableToFrom(6, 0));
        assertTrue(BusStop.isReachableToFrom(2, 0));
        assertFalse(BusStop.isReachableToFrom(12, 0));
        assertFalse(BusStop.isReachableToFrom(3, 0));
        assertEquals(8, BusStop.travelTimeToFrom(1, 0));
        assertEquals(6, BusStop.travelTimeToFrom(4, 0));
        assertEquals(12, BusStop.travelTimeToFrom(6, 0));
        assertEquals(5, BusStop.travelTimeToFrom(2, 0));

        assertEquals(9, BusStop.getXCoordOf(15));
        assertEquals(0, BusStop.getYCoordOf(15));

        assertFalse(BusStop.isReachableToFrom(0, 12));
        assertTrue(BusStop.isReachableToFrom(0, 1));

        assertEquals(13, BusStop.travelTimeToFrom(4, 1));

        assertTrue(BusStop.isReachableToFrom(3, 2));
        assertFalse(BusStop.isReachableToFrom(8, 15));


        int[] reachIds = BusStop.getReachableIdsOf(0);
        for (int id : reachIds)
                assertTrue(BusStop.isReachableToFrom(id, 0));

    }

    @Test
    public void testExceptions() {
        try {
            BusStop.travelTimeToFrom(15, 0);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
        try {
            BusStop.travelTimeToFrom(19, 0);
            assertTrue(false);
        } catch (IndexOutOfBoundsException ioobe) {}
        try {
            BusStop.isReachableToFrom(0, -1);
            assertTrue(false);
        } catch (IndexOutOfBoundsException ioobe) {}
        try {
            BusStop.isReachableToFrom(-1, 0);
            assertTrue(false);
        } catch (IndexOutOfBoundsException ioobe) {}
        try {
            BusStop.travelTimeToFrom(2, 1);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
        try {
            BusStop.getXCoordOf(-1);
            assertTrue(false);
        } catch(IndexOutOfBoundsException ioobe) {}
        try {
            BusStop.getYCoordOf(19);
            assertTrue(false);
        } catch (IndexOutOfBoundsException ioobe) {}
        try {
            BusStop.isReachableToFrom(1, 19);
            assertTrue(false);
        } catch (IndexOutOfBoundsException ioobe) {}
        try {
            BusStop.isReachableToFrom(-1, 3);
            assertTrue(false);
        } catch (IndexOutOfBoundsException ioobe) {}
        try {
            BusStop.travelTimeToFrom(19, 0);
            assertTrue(false);
        } catch (IndexOutOfBoundsException ioobe) {}
        try {
            BusStop.travelTimeToFrom(0, 9);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
        try {
            BusStop.travelTimeToFrom(-1, 12);
            assertTrue(false);
        } catch (IndexOutOfBoundsException ioobe) {}
        try {
            BusStop.travelTimeToFrom(5, 16);
            assertTrue(false);
        } catch (IndexOutOfBoundsException ioobe) {}
    }

}
