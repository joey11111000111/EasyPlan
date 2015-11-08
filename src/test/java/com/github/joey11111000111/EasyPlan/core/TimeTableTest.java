package com.github.joey11111000111.EasyPlan.core;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by joey on 2015.11.07..
 */
public class TimeTableTest {

    String name;
    int[] stopIds;
    int[] travelTimes;
    int timeGap;
    SimpleTime firstLeaveTime;
    SimpleTime boundaryTime;

    @Before
    public void init() {
        name = "22Y";
        stopIds = new int[] {1, 4, 6, 8 ,9};
        travelTimes = new int[stopIds.length];
        travelTimes[0] = BusStop.travelTimeToFromStation(stopIds[0]);
        for (int i = 1; i < stopIds.length; i++)
            travelTimes[i] = BusStop.travelTimeToFrom(stopIds[i], stopIds[i-1]);

        for (int i = 1; i < travelTimes.length; i++)
            travelTimes[i] += travelTimes[i-1];

        firstLeaveTime = new SimpleTime(22, 0);
        boundaryTime = new SimpleTime(0, 49);
        timeGap = 20;
    }

    private TimeTable.TimeTableArguments createArgs() {
        TimeTable.TimeTableArguments args = new TimeTable.TimeTableArguments();
        args.setTimeGap(timeGap);
        args.setName(name);
        args.setFirstLeaveTime(firstLeaveTime);
        args.setBoundaryTime(boundaryTime);
        args.setStopIds(stopIds);
        args.setTravelTimes(travelTimes);
        assertTrue(args.isValid());
        return args;
    }

    @Test
    public void testTimeTableArguments() {
        TimeTable.TimeTableArguments args = createArgs();

        int[] travelTimes = {-2, 1, 4};
        try {
            args.setTravelTimes(travelTimes);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}

        int[] stopIds = {23, 1, 4, 6};
        try {
            args.setStopIds(stopIds);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}

        // test null pointer exceptions
        try {
            args.setStopIds(null);
            assertTrue(false);
        } catch (NullPointerException npe) {}
        try {
            args.setName(null);
            assertTrue(false);
        } catch (NullPointerException npe) {}
        try {
            args.setTravelTimes(null);
            assertTrue(false);
        } catch (NullPointerException npe) {}
        try {
            args.setBoundaryTime(null);
            assertTrue(false);
        } catch (NullPointerException npe) {}
        try {
            args.setFirstLeaveTime(null);
            assertTrue(false);
        } catch (NullPointerException npe) {}

        try {
            args.setTimeGap(-1);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}

        stopIds = new int[] {1, 4, 6, 8};
        travelTimes = new int[] {12, 5, 9, 23, 11, 6, 7};
        args.setTravelTimes(travelTimes);
        args.setStopIds(stopIds);
        assertFalse(args.isValid());
    }

    @Test
    public void testStopTimes() {
        int id = 1;
        List<SimpleTime> list = new ArrayList<SimpleTime>();
        TimeTable.StopTimes st;
        try {
            st = new TimeTable.StopTimes(id, list);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}

        list.add(new SimpleTime(0, 0));
        try {
            st = new TimeTable.StopTimes(id, list);
        } catch (IllegalArgumentException iae) {}

        list = Collections.unmodifiableList(list);
        st = new TimeTable.StopTimes(id, list);
        assertEquals(id, st.id);
        assertEquals(list, st.times);
    }

    @Test
    public void testOpenTimeTable() {
        TimeTable.TimeTableArguments args = createArgs();
        TimeTable tt = TimeTable.newInstance(args);

        assertEquals("22Y", tt.name);
        List<TimeTable.StopTimes> list = tt.stopTimes;
        assertNotNull(list);
        assertEquals(6, list.size()); // 5 stops plus the station once

        assertEquals(BusStop.getIdOfStation(), list.get(0).id);
        assertEquals(1, list.get(1).id);
        assertEquals(4, list.get(2).id);
        assertEquals(6, list.get(3).id);
        assertEquals(8, list.get(4).id);
        assertEquals(9, list.get(5).id);

        // test at the station
        TimeTable.StopTimes st = list.get(0);
        assertEquals(firstLeaveTime.getTimeAsMinutes(), st.times.get(0).getTimeAsMinutes());
        int lastTimeInMinutes = st.times.get(st.times.size()-1).getTimeAsMinutes();
        int boundaryTimeInMinutes = boundaryTime.getTimeAsMinutes();
        assertTrue(boundaryTimeInMinutes >= lastTimeInMinutes);
        assertTrue(boundaryTimeInMinutes - timeGap < lastTimeInMinutes);

        System.out.println("---------------------------------");
        for (TimeTable.StopTimes st2 : list)
            System.out.println(st2);
    }

    @Test
    public void testClosedTimeTable() {
        int[] temp = travelTimes;
        travelTimes = new int[temp.length + 1];
        for (int i = 0; i < temp.length; i++)
            travelTimes[i] = temp[i];
        travelTimes[travelTimes.length - 1] = 19 + travelTimes[travelTimes.length - 2];

        TimeTable tb = TimeTable.newInstance(createArgs());
        int stationId = BusStop.getIdOfStation();
        List<TimeTable.StopTimes> st = tb.stopTimes;
        int firstId = st.get(0).id;
        int lastId = st.get(st.size()-1).id;
        assertEquals(firstId, lastId);

        System.out.println("------------------------");
        for (TimeTable.StopTimes st2 : st)
            System.out.println(st2);
    }

    @Test
    public void testEmptyTimeTable() {
        int[] travelTimesSave = travelTimes;
        int[] stopIdsSave = stopIds;
        travelTimes = new int[0];
        stopIds = new int[0];
        TimeTable tt = TimeTable.newInstance(createArgs());
        travelTimes = travelTimesSave;
        stopIds = stopIdsSave;

        List<TimeTable.StopTimes> st = tt.stopTimes;
        assertEquals(1, st.size());

        System.out.println("------------------------");
        for (TimeTable.StopTimes st2 : st)
            System.out.println(st2);

    }

}//class
