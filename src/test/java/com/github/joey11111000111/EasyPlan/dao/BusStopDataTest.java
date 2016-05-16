package com.github.joey11111000111.EasyPlan.dao;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;
import java.util.TreeMap;


public class BusStopDataTest {

    private int id;
    private int x;
    private int y;
    private Map<Integer, Integer> reachables;
    private Map<Integer, Integer> extraReachables;

    @Before
    public void initTestData() {
        id = 2;
        x = 5;
        y = 7;
        reachables = new TreeMap<>();
        reachables.put(3, 9);
        reachables.put(5, 12);
        reachables.put(0, 7);

        extraReachables = new TreeMap<>();
        extraReachables.put(1, 8);
        extraReachables.put(4, 14);
    }


    @Test
    public void testGettersSettersValidation() {
        iBusStopData busStopData = new BusStopData();
        assertFalse(busStopData.isValid());
        busStopData.setId(id);
        assertFalse(busStopData.isValid());
        busStopData.setX(x);
        assertFalse(busStopData.isValid());
        busStopData.setY(y);
        assertFalse(busStopData.isValid());
        busStopData.setReachableStops(reachables);
        assertTrue(busStopData.isValid());

        assertEquals(id, busStopData.getId());
        assertEquals(x, busStopData.getX());
        assertEquals(y, busStopData.getY());
        assertEquals(reachables, busStopData.getReachableStops());

        for (Map.Entry<Integer, Integer> entry : extraReachables.entrySet())
            busStopData.addReachableStop(entry.getKey(), entry.getValue());
        assertTrue(busStopData.isValid());

        Map<Integer, Integer> allReachables = new TreeMap<>();
        allReachables.putAll(reachables);
        allReachables.putAll(extraReachables);

        assertEquals(allReachables, busStopData.getReachableStops());
    }

    @Test
    public void testDifferentOrder() {
        iBusStopData busStopData = new BusStopData();
        for (Map.Entry<Integer, Integer> entry : extraReachables.entrySet())
            busStopData.addReachableStop(entry.getKey(), entry.getValue());
        assertEquals(extraReachables, busStopData.getReachableStops());

        busStopData.setReachableStops(reachables);
        assertEquals(reachables, busStopData.getReachableStops());
    }

    @Test(expected = NullPointerException.class)
    public void testException() {
        iBusStopData busStopData = new BusStopData();
        busStopData.setReachableStops(null);
    }

}
