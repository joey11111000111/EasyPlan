package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.util.DayTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class TimetableTest {

    @Test
    public void testValidCreationAndGetters() {
        iTimetable.iTimetableArguments args = new Timetable.TimetableArguments();
        String name = "22Y";
        DayTime firstLeaveTime = new DayTime(10, 50);
        DayTime boundaryTime = new DayTime(11, 0);
        int timeGap = 15;
        int[] travelTimes = new int[]{12, 21, 29, 44, 54};
        int[] stopIds = new int[]{0, 4, 6, 8, 9, 10};

        args.setName(name);
        args.setFirstLeaveTime(firstLeaveTime);
        args.setBoundaryTime(boundaryTime);
        args.setTimeGap(timeGap);
        args.setTravelTimes(travelTimes);
        args.setStopIds(stopIds);

        iTimetable tt = Timetable.createTimetable(args);

        assertEquals(name, tt.getServiceName());
        assertEquals(timeGap, tt.getTimeGap());
        assertEquals(1, tt.getBusCount());

        assertEquals(new DayTime(travelTimes[travelTimes.length-1]), tt.getTotalTravelTime());

        // test a different creation
        args = new Timetable.TimetableArguments();
        firstLeaveTime = new DayTime(12, 0);
        boundaryTime = new DayTime(11, 50);
        timeGap = 23 * 60 + 59;
        travelTimes = new int[0];
        stopIds = new int[] {0};

        args.setName(name);
        args.setFirstLeaveTime(firstLeaveTime);
        args.setBoundaryTime(boundaryTime);
        args.setTimeGap(timeGap);
        args.setTravelTimes(travelTimes);
        args.setStopIds(stopIds);

        tt = Timetable.createTimetable(args);

        assertEquals(1, tt.getBusCount());
        assertEquals(new DayTime(0), tt.getTotalTravelTime());

        List<iTimetable.iStopTimes> allTimes = tt.getStopTimes();
        assertEquals(1, allTimes.size());

        iTimetable.iStopTimes stopTimes = allTimes.get(0);
        assertEquals(Integer.toString(0), stopTimes.getID());

        List<DayTime> times = stopTimes.getTimes();
        assertEquals(1, times.size());
        assertEquals(firstLeaveTime, times.get(0));
    }

    @Test
    public void testWithInvalidArgs() {
        iTimetable.iTimetableArguments args = new Timetable.TimetableArguments();
        iTimetable tt;
        try {
            tt = Timetable.createTimetable(args);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
    }


}//class
