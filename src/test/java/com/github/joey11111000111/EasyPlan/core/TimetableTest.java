package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.util.DayTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by joey on 2015.11.07..
 */
public class TimetableTest {

    String name;
    int[] stopIds;
    int[] travelTimes;
    int timeGap;
    DayTime firstLeaveTime;
    DayTime boundaryTime;

    @Before
    public void init() {
        name = "22Y";
        stopIds = new int[] {0, 1, 4, 6, 8 ,9};
        travelTimes = new int[stopIds.length - 1];
        for (int i = 1; i < stopIds.length; i++)
            travelTimes[i] = BusStop.travelTimeToFrom(stopIds[i], stopIds[i-1]);

        for (int i = 1; i < travelTimes.length; i++)
            travelTimes[i] += travelTimes[i-1];

        firstLeaveTime = new DayTime(22, 0);
        boundaryTime = new DayTime(0, 49);
        timeGap = 20;
    }

    private Timetable.TimeTableArguments createArgs() {
        Timetable.TimeTableArguments args = new Timetable.TimeTableArguments();
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
        Timetable.TimeTableArguments args = createArgs();

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

        travelTimes = new int[] {12, 5, 9, 23, 11, 6, 7};
        try {
            args.setTravelTimes(travelTimes);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
    }

    @Test
    public void testStopTimes() {
        int id = 1;
        List<DayTime> list = new ArrayList<DayTime>();
        Timetable.StopTimes st;
        try {
            st = new Timetable.StopTimes(id, list);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}

        list.add(new DayTime(0, 0));
        try {
            st = new Timetable.StopTimes(id, list);
        } catch (IllegalArgumentException iae) {
            assertTrue(false);
        }

        list = Collections.unmodifiableList(list);
        st = new Timetable.StopTimes(id, list);
        assertEquals(id, st.id);
        assertEquals(list, st.times);

        // toString must not throw exception
        st.toString();
        assertTrue(true);
    }

    @Test
    public void testOpenTimeTable() {
        int backupHours = boundaryTime.getHours();
        int backupMinutes = boundaryTime.getMinutes();
        Timetable.TimeTableArguments args;
        for (int i = 0; i < 2; i++) {
            args = createArgs();
            Timetable tt = Timetable.newInstance(args);

            assertEquals("22Y", tt.name);
            List<Timetable.StopTimes> list = tt.stopTimes;
            assertNotNull(list);
            assertEquals(6, list.size()); // 5 stops plus the station once

            assertEquals(0, list.get(0).id);
            assertEquals(1, list.get(1).id);
            assertEquals(4, list.get(2).id);
            assertEquals(6, list.get(3).id);
            assertEquals(8, list.get(4).id);
            assertEquals(9, list.get(5).id);

            // test at the station
            Timetable.StopTimes st = list.get(0);
            assertEquals(firstLeaveTime.getTimeAsMinutes(), st.times.get(0).getTimeAsMinutes());
            int lastTimeInMinutes = st.times.get(st.times.size() - 1).getTimeAsMinutes();
            int boundaryTimeInMinutes = boundaryTime.getTimeAsMinutes();
            assertTrue(boundaryTimeInMinutes >= lastTimeInMinutes);
            assertTrue(boundaryTimeInMinutes - timeGap < lastTimeInMinutes);

            boundaryTime.setHours(23);
            boundaryTime.setMinutes(50);
        }
        boundaryTime.setHours(backupHours);
        boundaryTime.setMinutes(backupMinutes);
    }

    @Test
    public void testEmptyTimeTable() {
        int backupHours = boundaryTime.getHours();
        int backupMinutes = boundaryTime.getMinutes();
        Timetable.TimeTableArguments args;
        for (int i = 0; i < 2; i++) {
            int[] travelTimesSave = travelTimes;
            int[] stopIdsSave = stopIds;
            travelTimes = new int[0];
            stopIds = new int[]{0};
            Timetable tt = Timetable.newInstance(createArgs());
            travelTimes = travelTimesSave;
            stopIds = stopIdsSave;

            List<Timetable.StopTimes> st = tt.stopTimes;
            assertEquals(1, st.size());

            boundaryTime.setHours(23);
            boundaryTime.setMinutes(50);
        }
        boundaryTime.setHours(backupHours);
        boundaryTime.setMinutes(backupMinutes);
    }

    @Test
    public void testWithInvalidArgs() {
        Timetable.TimeTableArguments args = new Timetable.TimeTableArguments();
        Timetable tt;
        try {
            tt = Timetable.newInstance(args);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
        args.setName("22Y");
        try {
            tt = Timetable.newInstance(args);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
        args.setStopIds(new int[] {1, 4, 6 ,9, 2});
        try {
            tt = Timetable.newInstance(args);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
    }

}//class
