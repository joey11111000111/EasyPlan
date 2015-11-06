package com.github.joey11111000111.EasyPlan.core;

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
        SimpleTime firstLT = new SimpleTime(8, 0);
        SimpleTime boundaryT = new SimpleTime(18, 0);
        bsd = new BasicServiceData(name, timeGap, firstLT, boundaryT);
    }

    @Test
    public void testExceptions() {
        try {
            bsd.setName(null);
            assertTrue(false);
        } catch (NullPointerException npe) {}
        try {
            bsd.setTimeGap(0);
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
    public void testGettersSettersMarkers() {
        assertFalse(bsd.isModified());

        // set and get name
        String name = "22Y";
        bsd.setName(name);
        assertEquals(name, bsd.getName());
        // set and get timeGap
        int timeGap = 12;
        bsd.setTimeGap(timeGap);
        assertEquals(timeGap, bsd.getTimeGap());
        // test getters and setters for firstLeaveTime
        int flHour = 9, flMinute = 56;
        bsd.setFirstLeaveTime(flHour, flMinute);
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
        // test getters for boundaryTime
        int bHour = 9, bMinute = 56;
        bsd.setBoundaryTime(bHour, bMinute);
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

        assertTrue(bsd.isModified());
        bsd.markAsSaved();
        assertFalse(bsd.isModified());
    }

}//class
