package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.util.DayTime;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by joey on 2015.11.04..
 */
public class BasicServiceDataTest {

    BasicServiceData bsd;

    @Before
    public void init() {
        String name = "new service";
        int timeGap = 10;
        DayTime firstLT = new DayTime(8, 0);
        DayTime boundaryT = new DayTime(18, 0);
        bsd = new BasicServiceData(name, timeGap, firstLT, boundaryT);
    }

    @Test
    public void testExceptions() {
        try {
            bsd.setName(null);
            assertTrue(false);
        } catch (NullPointerException npe) {}
        try {
            bsd.setName("");
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
        try {
            bsd.setTimeGap(0);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
        try {
            bsd.setTimeGap(4000);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
        try {
            bsd.setFirstLeaveHour(24);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
        try {
            bsd.setFirstLeaveMinutes(-1);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
        try {
            bsd.setFirstLeaveTime(24, 12);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
        try {
            bsd.setFirstLeaveTime(12, 60);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
        try {
            bsd.setBoundaryHours(24);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
        try {
            bsd.setBoundaryMinutes(60);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
        try {
            bsd.setBoundaryTime(12, -1);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
        try {
            bsd.setBoundaryTime(-1, 2);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}
    }

    @Test
    public void testFirstLeaveTimeGettersSetters() {
        int flHour = 9, flMinute = 56;
        bsd.setFirstLeaveTime(flHour, flMinute);
        assertEquals(flHour, bsd.getFirstLeaveHours());
        assertEquals(flMinute, bsd.getFirstLeaveMinutes());
        bsd.setFirstLeaveTime(new DayTime(flHour, flMinute));
        assertEquals(flHour, bsd.getFirstLeaveHours());
        assertEquals(flMinute, bsd.getFirstLeaveMinutes());
        flHour = 12;
        bsd.setFirstLeaveHour(flHour);
        assertEquals(flHour, bsd.getFirstLeaveHours());
        assertEquals(flMinute, bsd.getFirstLeaveMinutes());
        flMinute = 34;
        bsd.setFirstLeaveMinutes(flMinute);
        assertEquals(flHour, bsd.getFirstLeaveHours());
        assertEquals(flMinute, bsd.getFirstLeaveMinutes());
        // no exception, no effect should take place
        bsd.setFirstLeaveTime(flHour, flMinute);
        int setMinutes = new DayTime(flHour, flMinute).getTimeAsMinutes();
        int receivedMinutes = bsd.getFirstLeaveTime().getTimeAsMinutes();
        assertEquals(setMinutes, receivedMinutes);
        // set to the same values
        bsd.setFirstLeaveTime(flHour, flMinute);
        assertEquals(new DayTime(flHour, flMinute), bsd.getFirstLeaveTime());
        bsd.setFirstLeaveHour(flHour);
        bsd.setFirstLeaveMinutes(flMinute);
    }

    @Test
    public void testBoundaryTimeGettersSetters() {
        int bHour = 9, bMinute = 56;
        bsd.setBoundaryTime(bHour, bMinute);
        assertEquals(bHour, bsd.getBoundaryHours());
        assertEquals(bMinute, bsd.getBoundaryMinutes());
        bsd.setBoundaryTime(new DayTime(bHour, bMinute));
        assertEquals(bHour, bsd.getBoundaryHours());
        assertEquals(bMinute, bsd.getBoundaryMinutes());
        bHour = 12;
        bsd.setBoundaryHours(bHour);
        assertEquals(bHour, bsd.getBoundaryHours());
        assertEquals(bMinute, bsd.getBoundaryMinutes());
        bMinute = 34;
        bsd.setBoundaryMinutes(bMinute);
        assertEquals(bHour, bsd.getBoundaryHours());
        assertEquals(bMinute, bsd.getBoundaryMinutes());
        // no exception, no effect should take place
        bsd.setBoundaryTime(bHour, bMinute);
        int setMinutes = new DayTime(bHour, bMinute).getTimeAsMinutes();
        int receivedMinutes = bsd.getBoundaryTime().getTimeAsMinutes();
        assertEquals(setMinutes, receivedMinutes);
        // set the same values
        bsd.setBoundaryTime(bHour, bMinute);
        assertEquals(new DayTime(bHour, bMinute), bsd.getBoundaryTime());
        bsd.setBoundaryHours(bHour);
        bsd.setBoundaryMinutes(bMinute);
    }

    @Test
    public void testNameGettersSetters() {
        String name = "22Y";
        bsd.setName(name);
        assertEquals(name, bsd.getName());
        // setting the same name again should not change the name reference in the object
        assertSame(name, bsd.getName());
        String name2 = new String("22Y");
        bsd.setName(name2);
        assertFalse(name2 == bsd.getName());
    }

    @Test
    public void testTimeGapGettersSetters() {
        int timeGap = 12;
        bsd.setTimeGap(timeGap);
        bsd.setTimeGap(timeGap);    // setting it again should have no effect
        assertEquals(timeGap, bsd.getTimeGap());
    }

    @Test
    public void testMarkers() {
        assertFalse(bsd.isModified());
        bsd.setTimeGap(23);
        assertTrue(bsd.isModified());
        bsd.markAsSaved();
        assertFalse(bsd.isModified());
        // markAsSaved again should have no effect
        bsd.markAsSaved();
        assertFalse(bsd.isModified());
    }

}//class
