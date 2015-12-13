package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.util.DayTime;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by joey on 2015.11.09..
 */
public class BusServiceTest {

    @Test
    public void testCreation() {
        BusService service = new BusService();
        // test basic service data
        BasicServiceData bsd = service.getCurrentServiceData();
        assertTrue("new service".equals(bsd.getName()));
        assertEquals(10, bsd.getTimeGap());
        int flMInutes = new DayTime(8, 0).getTimeAsMinutes();
        assertEquals(flMInutes, bsd.getFirstLeaveTime().getTimeAsMinutes());
        int boundaryMInutes = new DayTime(18, 0).getTimeAsMinutes();
        assertEquals(boundaryMInutes, bsd.getBoundaryTime().getTimeAsMinutes());
        assertFalse(bsd.isModified());

        // test touched stops
        TouchedStops ts = service.getCurrentStops();
        assertEquals(1, ts.getStopCount());
        assertFalse(ts.isModified());
        assertFalse(ts.isClosed());
        assertFalse(ts.canUndo());

        // test time table
        Timetable table = service.getTimeTable();
        assertTrue("new service".equals(table.name));
        assertEquals(1, table.stopTimes.size());
        Timetable.StopTimes st = table.stopTimes.get(0);
        assertEquals(0, st.id);
        assertEquals(flMInutes, st.times.get(0).getTimeAsMinutes());
        assertEquals(0, table.totalTravelTime.getTimeAsMinutes());
        assertEquals(61, table.busCount);
    }

    @Test
    public void testApplyAndInitials() {
        BusService service = new BusService();
        TouchedStops ts = service.getCurrentStops();
        ts.appendStop(1);
        ts.appendStop(4);
        ts.appendStop(0);
        BasicServiceData bsd = service.getCurrentServiceData();
        bsd.setName("22Y");
        // test init without apply
        service.initTransientFields();
        ts = service.getCurrentStops();
        bsd = service.getCurrentServiceData();
        assertEquals(1, ts.getStopCount());
        assertFalse(ts.isClosed());
        assertFalse("22Y".equals(bsd.getName()));

        // test with apply
        ts.appendStop(1);
        ts.appendStop(4);
        ts.appendStop(0);
        bsd.setName("22Y");
        bsd.setTimeGap(25);
        assertTrue(ts.isModified());
        assertTrue(bsd.isModified());
        service.applyChanges();
        service.initTransientFields();
        ts = service.getCurrentStops();
        bsd = service.getCurrentServiceData();
        assertNotEquals(1, ts.getStopCount());
        assertTrue(ts.isClosed());
        assertTrue("22Y".equals(bsd.getName()));
        assertEquals(25, bsd.getTimeGap());

        // 'apply changes' without actual modification should have no effect
        service.applyChanges();
        service.applyChanges();
        service.applyChanges();
    }

}//class
