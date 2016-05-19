package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.util.DayTime;
import org.junit.Test;
import static org.junit.Assert.*;

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
        assertTrue(ts.getStopCount() > 1);
        assertTrue(ts.isClosed());
        assertTrue("22Y".equals(bsd.getName()));
        assertEquals(25, bsd.getTimeGap());

        // 'apply changes' without actual modification should have no effect
        service.applyChanges();
        service.applyChanges();
        service.applyChanges();
    }

    @Test
    public void testDiscardChanges() {
        BusService bs = new BusService();
        TouchedStops ts = bs.getCurrentStops();
        BasicServiceData bsd = bs.getCurrentServiceData();

        ts.appendStop(1);
        ts.appendStop(4);
        ts.appendStop(6);

        assertEquals(4, ts.getStopCount());
        bs.discardChanges();
        assertEquals(1, ts.getStopCount());
        bs.discardChanges();
        assertEquals(1, ts.getStopCount());

        bsd.setName("22Y");
        bsd.setTimeGap(44);

        assertEquals("22Y", bsd.getName());
        bs.discardChanges();
        assertEquals("new service", bsd.getName());
        bs.discardChanges();
        assertEquals("new service", bsd.getName());

        bsd.setFirstLeaveTime(12, 20);
        bsd.setTimeGap(20);
        ts.appendStop(1);
        ts.appendStop(4);
        ts.appendStop(6);
        bs.applyChanges();
        bsd.setTimeGap(30);
        ts.appendStop(4);

        bs.discardChanges();
        assertEquals(20, bsd.getTimeGap());
        assertEquals(6, ts.getLastStop());
    }
    
    @Test
    public void testTimetableCreation() {
        BusService bs = new BusService();
        bs.getCurrentServiceData().setFirstLeaveTime(12, 12);
        bs.getCurrentServiceData().setBoundaryTime(12, 13);
        bs.getCurrentServiceData().setName("22Y");

        bs.getCurrentStops().appendStop(4);
        bs.getCurrentStops().appendStop(6);
        bs.applyChanges();

        iTimetable timetable = bs.getTimetable();
        // no exceptions means pass
    }

    @Test
    public void testEqualsAndHashCode() {
        BusService bs1 = new BusService();
        BusService bs2 = new BusService();
        assertFalse(bs1.equals(new Object()));
        assertFalse(bs1.equals(null));
        assertTrue(bs1.equals(bs1));

        assertTrue(bs1.equals(bs2));
        assertTrue(bs2.equals(bs1));
        assertEquals(bs1.hashCode(), bs2.hashCode());

        bs1.getCurrentServiceData().setName("22Y");
        bs2.getCurrentServiceData().setName("Körjárat");
        bs1.applyChanges();
        bs2.applyChanges();
        assertFalse(bs1.equals(bs2));
        assertFalse(bs2.equals(bs1));
        assertTrue(bs1.hashCode() != bs2.hashCode());
    }

}//class
