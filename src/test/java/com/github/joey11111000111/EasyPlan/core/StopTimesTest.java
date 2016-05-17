package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.util.DayTime;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StopTimesTest {

    @Test
    public void testConstructorExceptions() {
        iTimetable.iStopTimes st;
        try {
            st = new Timetable.StopTimes(1, new ArrayList<>());
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}

        try {
            List<DayTime> list = new ArrayList<>();
            list.add(new DayTime(12, 0));
            st = new Timetable.StopTimes(1, list);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
    }

    @Test
    public void testCreationAndGetters() {
        int id = 1;
        List<DayTime> times = new ArrayList<>();
        times.add(new DayTime(12, 0));
        times.add(new DayTime(12, 10));
        times.add(new DayTime(12, 20));
        times = Collections.unmodifiableList(times);
        iTimetable.iStopTimes st = new Timetable.StopTimes(id, times);

        assertEquals(Integer.toString(id), st.getID());
        assertEquals(times, st.getTimes());
    }

    @Test
    public void testToString() {
        int id = 1;
        List<DayTime> times = new ArrayList<>();
        DayTime time = new DayTime(14, 30);
        times.add(time);
        times = Collections.unmodifiableList(times);
        iTimetable.iStopTimes st = new Timetable.StopTimes(id, times);

        String result = st.toString();
        String expected = "id: 1  stop times: 14:30  ";
        assertEquals(expected, result);
    }



}
