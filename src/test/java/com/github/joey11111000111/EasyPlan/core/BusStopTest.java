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
        assertEquals(9, BusStop.getXCoordOfStation());
        assertEquals(5, BusStop.getYCoordOfStation());
        assertTrue(BusStop.isReachableFromStation(1));
        assertTrue(BusStop.isReachableFromStation(4));
        assertTrue(BusStop.isReachableFromStation(6));
        assertTrue(BusStop.isReachableFromStation(2));
        assertFalse(BusStop.isReachableFromStation(12));
        assertFalse(BusStop.isReachableFromStation(3));
        assertEquals(8, BusStop.travelTimeToFromStation(1));
        assertEquals(6, BusStop.travelTimeToFromStation(4));
        assertEquals(12, BusStop.travelTimeToFromStation(6));
        assertEquals(5, BusStop.travelTimeToFromStation(2));

        assertEquals(1, BusStop.getXCoordOf(15));
        assertEquals(10, BusStop.getYCoordOf(15));
        assertEquals(9, BusStop.getXCoordOfStation());
        assertEquals(5, BusStop.getYCoordOfStation());

        assertFalse(BusStop.isStationReachableFrom(12));
        assertTrue(BusStop.isStationReachableFrom(1));

        assertEquals(13, BusStop.travelTimeToFrom(4, 1));

        assertTrue(BusStop.isReachableToFrom(3, 2));
        assertFalse(BusStop.isReachableToFrom(8, 15));


        int[] reachIds = BusStop.getReachableIdsOfStation();
        for (int id : reachIds)
                assertTrue(BusStop.isReachableFromStation(id));

    }

    @Test
    public void testExceptions() {
        try {
            BusStop.travelTimeToFromStation(15);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
        try {
            BusStop.travelTimeToFromStation(19);
            assertTrue(false);
        } catch (IndexOutOfBoundsException ioobe) {}
        try {
            BusStop.isStationReachableFrom(-1);
            assertTrue(false);
        } catch (IndexOutOfBoundsException ioobe) {}
        try {
            BusStop.travelTimeToFrom(2, 1);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}

    }

}
