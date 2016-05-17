package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.util.DayTime;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Consumer;

import static org.junit.Assert.*;

public class TimetableArgumentsTest {

    iTimetable.iTimetableArguments args;

    @Before
    public void init() {
        args = new Timetable.TimetableArguments();
    }

    private <P, E extends Exception> void testForException(Consumer<P> method, P parameter, Class<E> exceptionClass) {
        try {
            method.accept(parameter);
            assertTrue(false);
        } catch (Exception e) {
            if (exceptionClass.isInstance(e))
                return;
            throw e;
        }
    }

    @Test
    public void testAllExceptions() {
        testForException(args::setName, null, NullPointerException.class);
        testForException(args::setName, "", IllegalArgumentException.class);

        testForException(args::setStopIds, null, NullPointerException.class);
        testForException(args::setStopIds, new int[0], IllegalArgumentException.class);
        testForException(args::setStopIds, new int[] {1}, IllegalArgumentException.class);
        testForException(args::setStopIds, new int[] {0, 1, 4, 56}, IllegalArgumentException.class);

        testForException(args::setTravelTimes, null, NullPointerException.class);
        testForException(args::setTravelTimes, new int[] {12, 1, 0, -2}, IllegalArgumentException.class);

        testForException(args::setFirstLeaveTime, null, NullPointerException.class);
        testForException(args::setBoundaryTime, null, NullPointerException.class);

        testForException(args::setTimeGap, -1, IllegalArgumentException.class);

        int[] travelTimes = {12, 13, 22, 9, 10, 6};
        int[] stopIds = {0, 1, 4, 6};
        args.setTravelTimes(travelTimes);
        testForException(args::setStopIds, stopIds, IllegalArgumentException.class);

        args = new Timetable.TimetableArguments();
        args.setStopIds(stopIds);
        testForException(args::setTravelTimes, travelTimes, IllegalArgumentException.class);
    }

    @Test
    public void testGettersSetters() {
        String name = "22Y";
        int timeGap = 15;
        DayTime firstLeaveTime = new DayTime(12, 10);
        DayTime boundaryTime = new DayTime(20, 0);
        int[] stopIds = {0, 1, 4, 6, 8};
        int[] travelTimes = {12, 9, 10, 15};

        args.setName(name);
        args.setTimeGap(timeGap);
        args.setFirstLeaveTime(firstLeaveTime);
        args.setBoundaryTime(boundaryTime);
        args.setStopIds(stopIds);
        args.setTravelTimes(travelTimes);

        assertEquals(name, args.getName());
        assertEquals(timeGap, args.getTimeGap());
        assertEquals(firstLeaveTime, args.getFirstLeaveTime());
        assertEquals(boundaryTime, args.getBoundaryTime());
        assertArrayEquals(stopIds, args.getStopIds());
        assertArrayEquals(travelTimes, args.getTravelTimes());
    }

    @Test
    public void testValidation() {
        assertFalse(args.isValid());
        args.setName("22Y");
        assertFalse(args.isValid());
        int[] stopIds = {0, 1, 4, 6, 8};
        int[] travelTimes = {12, 9, 10, 15};
        args.setStopIds(stopIds);
        assertFalse(args.isValid());
        args.setTravelTimes(travelTimes);
        assertFalse(args.isValid());
        args.setTimeGap(15);
        assertFalse(args.isValid());
        args.setFirstLeaveTime(new DayTime(12, 0));
        assertFalse(args.isValid());
        args.setBoundaryTime(new DayTime(15, 9));

        assertTrue(args.isValid());
    }


}
