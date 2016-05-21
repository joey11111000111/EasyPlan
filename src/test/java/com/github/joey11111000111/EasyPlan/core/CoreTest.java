package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.core.exceptions.NameConflictException;
import com.github.joey11111000111.EasyPlan.core.exceptions.NoSelectedServiceException;
import com.github.joey11111000111.EasyPlan.dao.ObjectIO;
import com.github.joey11111000111.EasyPlan.util.DayTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
interface VoidNoArgsFunction {
    void invoke();
}

public class CoreTest {

    Controller core;

    private <P> void testForException(Consumer<P> method, P parameter, Class<?> exceptionClass) {
        try {
            method.accept(parameter);
            assertTrue(false);
        } catch (Exception e) {
            if (!exceptionClass.isInstance(e))
                throw e;
        }
    }

    private void testForException(VoidNoArgsFunction method, Class<?> exceptionClass) {
        try {
            method.invoke();
            assertTrue(false);
        } catch (Exception e) {
            if (!exceptionClass.isInstance(e))
                throw e;
        }
    }

    private final boolean SHOULD_THROW = true;
    private final boolean SHOULD_PASS = false;
    private void tryApplyChanges(boolean shouldThrow) {
        try {
            core.applyChanges();
            if (shouldThrow)
                assertTrue(false);
        } catch (NameConflictException nce) {
            if (!shouldThrow)
                assertTrue(false);
        }
    }

    @Before
    public void setUp() {
        core = new Core();
    }


    @Test
    public void testSelectionExceptions() {
        testForException(core::getName, NoSelectedServiceException.class);
        testForException(core::getBoundaryTime, NoSelectedServiceException.class);
        testForException(core::getFirstLeaveTime, NoSelectedServiceException.class);
        testForException(core::getTimeGap, NoSelectedServiceException.class);
        testForException(core::getLastStop, NoSelectedServiceException.class);
        testForException(core::getReachableStopIds, NoSelectedServiceException.class);
        testForException(core::getStops, NoSelectedServiceException.class);
        testForException(core::getTravelTimes, NoSelectedServiceException.class);
        testForException(core::getStopCount, NoSelectedServiceException.class);

        testForException(core::isModified, NoSelectedServiceException.class);
        testForException(core::isClosed, NoSelectedServiceException.class);
        testForException(core::canUndo, NoSelectedServiceException.class);
        testForException(core::clearStops, NoSelectedServiceException.class);
        testForException(core::discardChanges, NoSelectedServiceException.class);
        testForException(core::undo, NoSelectedServiceException.class);
        testForException(core::removeChainFrom, 6, NoSelectedServiceException.class);

        testForException(core::appendStop, 1, NoSelectedServiceException.class);
        testForException(core::setTimeGap, 15, NoSelectedServiceException.class);
        testForException(core::setBoundaryTime, new DayTime(), NoSelectedServiceException.class);
        testForException(core::setBoundaryHour, 2, NoSelectedServiceException.class);
        testForException(core::setBoundaryMinute, 12, NoSelectedServiceException.class);
        testForException(core::setFirstLeaveTime, new DayTime(), NoSelectedServiceException.class);
        testForException(core::setFirstLeaveHour, 2, NoSelectedServiceException.class);
        testForException(core::setFirstLeaveMinute, 12, NoSelectedServiceException.class);
        testForException(core::setName, "22Y", NoSelectedServiceException.class);
    }

    @Test
    public void testOtherExceptions() {
        core.createNewService();
        testForException(core::getTimetableOf, null, NullPointerException.class);
        testForException(core::deleteService, null, NullPointerException.class);
        testForException(core::selectService, null, NullPointerException.class);

        testForException(core::getTimetableOf, "this is not in the list", IllegalArgumentException.class);
        testForException(core::selectService, "this is not in the list", IllegalArgumentException.class);

        core.setName("22Y");
        tryApplyChanges(SHOULD_PASS);

        core.createNewService();
        core.setName("22Y");
        tryApplyChanges(SHOULD_THROW);
    }

    @Test
    public void testAddStopDeleteStopAndUndo() {
        core.createNewService();
        core.appendStop(1);
        core.appendStop(4);
        core.appendStop(6);
        testForException(core::appendStop, 15, IllegalArgumentException.class);
        assertEquals(4, core.getStopCount());
        assertEquals(6, core.getLastStop());

        core.removeChainFrom(4);
        assertEquals(2, core.getStopCount());
        assertEquals(1, core.getLastStop());

        assertTrue(core.canUndo());
        core.undo();
        assertEquals(4, core.getStopCount());
        assertEquals(6, core.getLastStop());

        assertTrue(core.canUndo());
        tryApplyChanges(SHOULD_PASS);

        assertFalse(core.canUndo());

        core.undo();                                    // should have no effect

        core.removeChainFrom(1);
        assertEquals(1, core.getStopCount());
        assertEquals(0, core.getLastStop());
        core.removeChainFrom(0);                        // should have no effect

        core.appendStop(4);
        core.appendStop(6);
        core.appendStop(8);
        core.removeChainFrom(0);
        assertEquals(0, core.getLastStop());
    }

    @Test
    public void testStates() {
        core.createNewService();
        assertFalse(core.isModified());
        assertTrue(core.isSaved());
        assertFalse(core.isClosed());

        core.setName("22Y");
        assertTrue(core.isModified());
        assertTrue(core.isSaved());

        core.appendStop(1);
        core.appendStop(4);
        core.appendStop(0);
        assertTrue(core.isClosed());
        tryApplyChanges(SHOULD_PASS);

        assertFalse(core.isModified());
        assertFalse(core.isSaved());
    }

    @Test
    public void testApplyAndDiscard() {
        tryApplyChanges(SHOULD_PASS);
        testForException(core::discardChanges, NoSelectedServiceException.class);

        core.createNewService();
        assertFalse(core.discardChanges());
        tryApplyChanges(SHOULD_PASS);       // should have no effect

        core.setTimeGap(15);
        core.setName("22Y");
        tryApplyChanges(SHOULD_PASS);
        assertFalse(core.discardChanges());

        core.setTimeGap(20);
        core.setName("24A");
        assertTrue(core.discardChanges());
        assertEquals(15, core.getTimeGap());
        assertEquals("22Y", core.getName());
    }

    @Test
    public void testGettersSetters() {
        core.createNewService();
        String name = "Tesco circle";
        int timeGap = 25;
        DayTime firstLeaveTime = new DayTime(8, 0);
        DayTime boundaryTime = new DayTime(19, 30);
        
        core.setName(name);
        core.setTimeGap(timeGap);
        core.setFirstLeaveTime(firstLeaveTime);
        core.setBoundaryTime(boundaryTime);
        
        assertEquals(name, core.getName());
        assertEquals(timeGap, core.getTimeGap());
        assertEquals(firstLeaveTime, core.getFirstLeaveTime());
        assertEquals(boundaryTime, core.getBoundaryTime());
        
        firstLeaveTime = new DayTime(6, 30);
        boundaryTime = new DayTime(20, 0);
        core.setFirstLeaveHour(firstLeaveTime.getHours());
        core.setFirstLeaveMinute(firstLeaveTime.getMinutes());
        core.setBoundaryHour(boundaryTime.getHours());
        core.setBoundaryMinute(boundaryTime.getMinutes());

        assertEquals(firstLeaveTime, core.getFirstLeaveTime());
        assertEquals(boundaryTime, core.getBoundaryTime());
    }

    @Test
    public void testCreateDeleteAndSelectService() {
        testForException(core::selectService, "not contained service name", IllegalArgumentException.class);
        core.deleteSelectedService();
        core.deleteService("not existing service name");    // should have no effect

        core.createNewService();
        assertEquals("new service", core.getName());
        core.createNewService();
        assertEquals("new service*", core.getName());
        core.createNewService();
        assertEquals("new service**", core.getName());

        core.deleteSelectedService();
        assertEquals("new service", core.getName());

        core.deleteService(core.getName());
        assertEquals("new service*", core.getName());

        core.deleteSelectedService();
        assertFalse(core.hasSelectedService());

        core.createNewService();
        core.setName("1");
        tryApplyChanges(SHOULD_PASS);
        core.createNewService();
        core.setName("2");
        tryApplyChanges(SHOULD_PASS);
        core.createNewService();
        core.setName("3");
        tryApplyChanges(SHOULD_PASS);

        String[] names = new String[] {"1", "2", "3"};
        assertArrayEquals(names, core.getServiceNames());

        core.deleteService("2");
        assertEquals(2, core.getServiceNames().length);

        core.createNewService();
        core.selectService("1");
        assertEquals("1", core.getName());
    }

    @Test
    public void testClearAndStopInfos() {
        core.createNewService();
        core.appendStop(1);
        core.appendStop(4);
        core.appendStop(6);
        core.appendStop(8);
        core.appendStop(9);

        core.clearStops();
        assertEquals(1, core.getStopCount());

        // should have no effect
        core.clearStops();
        core.clearStops();
        core.clearStops();

        int[] reachableOfStation = BusStop.getReachableIdsOf(0);
        assertArrayEquals(reachableOfStation, core.getReachableStopIds());

        assertEquals(0, core.getTravelTimes().length);
    }



    @Test
    public void testOther() {
        core.createNewService();
        core.appendStop(1);
        iTimetable timetable = core.getTimetableOf(core.getName());
        // no problem means pass



    }
    
}//class
