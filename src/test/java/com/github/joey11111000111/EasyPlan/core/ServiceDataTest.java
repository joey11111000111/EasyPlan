package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.core.util.DayTime;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by joey on 2015.11.11..
 */
public class ServiceDataTest {

    @Test
    public void testNullState() {
        ServiceData sd = new ServiceData();
        // call all methods
        sd.setCurrentService(null);     // doesn't change the state
        assertNull(sd.getSelectedService());
        assertFalse(sd.hasSelectedService());
        try {
            sd.markAsSaved();
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}

        try {
            sd.isModified();
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
        try {
            sd.discardChanges();
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
        try {
            sd.getCurrentTimetable();
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
        try {
            sd.getName();
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
        try {
            sd.setName("22Y");
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
        try {
            sd.getTimeGap();
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
        try {
            sd.setTimeGap(99);
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
        try {
            sd.getFirstLeaveTime();
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
        try {
            sd.setFirstLeaveTime(new DayTime(0, 0));
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
        try {
            sd.getBoundaryTime();
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
        try {
            sd.setBoundaryTime(new DayTime(1, 1));
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
        try {
            sd.setTimeGap(99);
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
        try {
            sd.appendStop(1);
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
        try {
            sd.hasStops();
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
        try {
            sd.isClosed();
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
        try {
            sd.canUndo();
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
        try {
            sd.getStops();
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
//        try {
//            sd.clearStops();
//            assertTrue(false);
//        } catch (ServiceData.NoSelectedServiceException nsse) {}
        try {
            sd.removeChainFrom(1);
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
        try {
            sd.undo();
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
        try {
            sd.isStationReachable();
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
        try {
            sd.getReachableStopIds();
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
        try {
            sd.getTravelTimes();
            assertTrue(false);
        } catch (ServiceData.NoSelectedServiceException nsse) {}
    }

    @Test
    public void testNotNullState() {
        ServiceData sd = new ServiceData();
        BusService bs = new BusService();
         // call all methods
        sd.setCurrentService(bs);
        assertNotNull(sd.getSelectedService());
        assertTrue(sd.hasSelectedService());
        assertFalse(sd.isModified());
        System.out.println(Arrays.toString(sd.getStops()));
        assertFalse(sd.hasStops());
        assertFalse(sd.isClosed());

        assertFalse(sd.isModified());
        assertFalse(sd.isClosed());
        sd.appendStop(1);
        assertTrue(sd.hasStops());
        assertTrue(sd.getTravelTimes().length == 1);
        assertTrue(sd.getReachableStopIds().length > 0);
        assertTrue(sd.isStationReachable());

        sd.appendStop(4);
        assertTrue(sd.canUndo());
        assertTrue(sd.isModified());
        sd.undo();

        assertNotNull(sd.getCurrentTimetable());
        sd.setName("22Y");
        assertTrue("22Y" == sd.getName());
        sd.setTimeGap(99);
        assertEquals(99, sd.getTimeGap());
        sd.setFirstLeaveTime(new DayTime(0, 0));
        assertEquals(0, sd.getFirstLeaveTime().getTimeAsMinutes());
        sd.setBoundaryTime(new DayTime(0, 0));
        assertEquals(0, sd.getBoundaryTime().getTimeAsMinutes());
        assertTrue(sd.hasStops());

        assertTrue(sd.getStops().length > 0);
        sd.removeChainFrom(1);
        assertFalse(sd.hasStops());

        sd.appendStop(2);
        sd.appendStop(3);
        sd.clearStops();
        assertFalse(sd.hasStops());

        assertTrue(sd.discardChanges());
        sd.appendStop(1);
        sd.markAsSaved();
        sd.discardChanges();
        assertFalse(sd.isModified());
        assertTrue(sd.hasStops());
        assertEquals(1, sd.getStops()[0]);

        sd.clearStops();
        sd.appendStop(1);
        sd.closeService();
        assertTrue(sd.isClosed());
        assertTrue(sd.isModified());
    }

}//class
