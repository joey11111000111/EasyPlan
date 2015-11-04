package com.github.joey11111000111.EasyPlan.core;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by joey on 2015.11.02..
 */
public class BusStopTest {

    @Test
    public void testAll() {
        try {
            BusStop.getStop(16);
            assertTrue(false);
        } catch(IndexOutOfBoundsException ioobe) {
        }
        // test the bus station
        BusStop station = BusStop.getStop(0);
        assertEquals(0, station.id);
        assertEquals(9, station.x);
        assertEquals(5, station.y);
        assertTrue(station.isReachable(1));
        assertTrue(station.isReachable(4));
        assertTrue(station.isReachable(6));
        assertTrue(station.isReachable(2));
        assertFalse(station.isReachable(12));
        assertFalse(station.isReachable(3));
        assertFalse(station.isStationReachable());
        assertEquals(8, station.travelTimeTo(1));
        assertEquals(6, station.travelTimeTo(4));
        assertEquals(12, station.travelTimeTo(6));
        assertEquals(5, station.travelTimeTo(2));
        // must throw runtime exception
        try {
            station.travelTimeTo(15);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {
        }

        int[] reachIds = BusStop.getReachableIdsOfStation();
        for (int id : reachIds)
                assertTrue(station.isReachable(id));

        // test a bus stop
        BusStop bs = BusStop.getStop(15);
        assertEquals(15, bs.id);
        assertEquals(1, bs.x);
        assertEquals(10, bs.y);
        assertTrue(bs.isReachable(10));
        assertTrue(bs.isReachable(12));
        assertTrue(bs.isReachable(13));
        assertFalse(bs.isReachable(1));
        assertFalse(bs.isStationReachable());
        assertEquals(14, bs.travelTimeTo(10));
        assertEquals(13, bs.travelTimeTo(12));
        assertEquals(16, bs.travelTimeTo(13));

        reachIds = bs.getReachableIds();
        for (int id : reachIds)
            assertTrue(bs.isReachable(id));

        // test equals and hashCode
        BusStop stop1 = BusStop.getStop(1);     // there is no other way to acquire a BusStop object
        BusStop stop2 = BusStop.getStop(1);
        BusStop stop3 = BusStop.getStop(3);
        assertTrue(stop1.equals(stop2));
        assertFalse(stop1.equals(stop3));
        assertFalse(stop3.equals(stop2));
        assertEquals(stop1.hashCode(), stop2.hashCode());
        assertFalse(stop1.hashCode() == stop3.hashCode());
        assertFalse(stop3.hashCode() == stop2.hashCode());

    }


}
