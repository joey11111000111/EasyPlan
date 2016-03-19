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

/**
 * Created by joey on 2015.11.10..
 */
public class CoreTest {

    private boolean hasSaves = false;
    private Core core;

    @Before
    public void setUp() {
        core = new Core(new ObjectIO());
    }

    @Before
    public void renameSaves() {
        File saveFile = new File(Core.SAVE_PATH);
        if (saveFile.exists()) {
            if (saveFile.renameTo(new File(Core.SAVE_PATH + "_renamed_")))
                hasSaves = true;
            else
                throw new RuntimeException("cannot rename save file");
        }
    }

    @After
    public void reRenameSaves() {
        if (hasSaves) {
            File saveFile = new File(Core.SAVE_PATH + "_renamed_");
            if (!saveFile.renameTo(new File(Core.SAVE_PATH)))
                throw new RuntimeException("cannot re-rename save file");
        }
    }

    @Test
    public void testWrappersInNullState() {
        // if there is no current service, the null state remains, and if there is, this will lead to a null state
        core.deleteSelectedService();
        // call all methods
        assertFalse(core.hasSelectedService());
        try {
            core.isModified();
            assertTrue(false);
        } catch (NoSelectedServiceException nsse) {}
        try {
            core.discardChanges();
            assertTrue(false);
        } catch (NoSelectedServiceException nsse) {}
        try {
            core.getCurrentTimetable();
            assertTrue(false);
        } catch (NoSelectedServiceException nsse) {}
        try {
            core.getName();
            assertTrue(false);
        } catch (NoSelectedServiceException nsse) {}
        try {
            core.setName("22Y");
            assertTrue(false);
        } catch (NoSelectedServiceException nsse) {}
        try {
            core.getTimeGap();
            assertTrue(false);
        } catch (NoSelectedServiceException nsse) {}
        try {
            core.setTimeGap(99);
            assertTrue(false);
        } catch (NoSelectedServiceException nsse) {}
        try {
            core.getFirstLeaveTime();
            assertTrue(false);
        } catch (NoSelectedServiceException nsse) {}
        try {
            core.setFirstLeaveTime(new DayTime(0, 0));
            assertTrue(false);
        } catch (NoSelectedServiceException nsse) {}
        try {
            core.getBoundaryTime();
            assertTrue(false);
        } catch (NoSelectedServiceException nsse) {}
        try {
            core.setBoundaryTime(new DayTime(1, 1));
            assertTrue(false);
        } catch (NoSelectedServiceException nsse) {}
        try {
            core.setTimeGap(99);
            assertTrue(false);
        } catch (NoSelectedServiceException nsse) {}
        try {
            core.appendStop(1);
            assertTrue(false);
        } catch (NoSelectedServiceException nsse) {}
        try {
            core.isClosed();
            assertTrue(false);
        } catch (NoSelectedServiceException nsse) {}
        try {
            core.canUndo();
            assertTrue(false);
        } catch (NoSelectedServiceException nsse) {}
        try {
            core.getStops();
            assertTrue(false);
        } catch (NoSelectedServiceException nsse) {}
        try {
            core.clearStops();
            assertTrue(false);
        } catch (NoSelectedServiceException nsse) {}
        try {
            core.removeChainFrom(1);
            assertTrue(false);
        } catch (NoSelectedServiceException nsse) {}
        try {
            core.undo();
            assertTrue(false);
        } catch (NoSelectedServiceException nsse) {}
        try {
            core.getReachableStopIds();
            assertTrue(false);
        } catch (NoSelectedServiceException nsse) {}
        try {
            core.getTravelTimes();
            assertTrue(false);
        } catch (NoSelectedServiceException nsse) {}

    }

    @Test
    public void testWrappersInNotNullState() {
        core.createNewService();
        // call all methods
        assertTrue(core.hasSelectedService());
        assertFalse(core.isModified());
        assertFalse(core.isClosed());

        assertFalse(core.isModified());
        assertFalse(core.isClosed());
        core.appendStop(1);
        assertTrue(core.getTravelTimes().length == 1);
        assertTrue(core.getReachableStopIds().length > 0);

        core.appendStop(4);
        assertTrue(core.canUndo());
        assertTrue(core.isModified());
        core.undo();

        assertNotNull(core.getCurrentTimetable());
        core.setName("22Y");
        assertTrue("22Y" == core.getName());
        core.setTimeGap(99);
        assertEquals(99, core.getTimeGap());
        core.setFirstLeaveTime(new DayTime(0, 0));
        assertEquals(0, core.getFirstLeaveTime().getTimeAsMinutes());
        core.setBoundaryTime(new DayTime(0, 0));
        assertEquals(0, core.getBoundaryTime().getTimeAsMinutes());

        assertTrue(core.getStops().length > 0);
        core.removeChainFrom(1);

        core.appendStop(2);
        core.appendStop(3);
        core.clearStops();
        assertTrue(core.isModified());

        assertTrue(core.discardChanges());
        assertFalse(core.isModified());
        core.appendStop(1);
        assertEquals(0, core.getStops()[0]);

        core.clearStops();
        core.appendStop(1);
        core.appendStop(0);
        assertTrue(core.isClosed());
        assertTrue(core.isModified());
    }


/*
    @Test
    public void testEmptyState() {
        Timetable[] tables = core.getAllTimetables();
        assertEquals(0, tables.length);

        // should have no effect
        try {
            core.applyChanges();
            core.applyChanges();
        } catch (NameConflictException nce) {
            System.err.println("nce happened: " + nce);
            assertTrue(false);
        }
        core.deleteSelectedService();

        String[] serviceNames = core.getServiceNames();
        assertEquals(0, serviceNames.length);

        try {
            core.selectService(null);
            assertTrue(false);
        } catch (NullPointerException npe) {}
        try {
            core.selectService("some random name");
            assertTrue(false);
        } catch (IllegalArgumentException iae) {}

        // when saving in empty state, the method deletes the save file, if it exists
        // it surely doesn't exist (either it didn't exist or was renamed)
        File tempFile = new File(Core.SAVE_PATH);
        try {
            if (!tempFile.createNewFile())
                throw new RuntimeException("cannot create temp file");
        } catch (IOException e) {
            e.printStackTrace();
        }

        core.saveServices();
        assertFalse(tempFile.exists());
        core.saveServices();     // should have no effect
    }//testEmptyState
*/


/*
    @Test
    public void testServiceManagement() {
        Core core = new Core();
        core.createNewService();
        core.createNewService();
        assertEquals(1, core.getServiceNames().length);
        assertTrue(core.hasSelectedService());
        assertTrue("new service".equals(core.getName()));

        core.setName("22Y");
        try {
            core.applyChanges();
            // without actual modification it should have no effect
            core.applyChanges();
        } catch (NameConflictException e) {
            assertTrue(false);
        }

        core.createNewService();
        assertTrue("new service".equals(core.getName()));
        String[] names = core.getServiceNames();
        assertEquals(2, names.length);
        assertTrue(names[0].equals("22Y"));
        assertTrue(names[1].equals("new service"));
        // this one should have no effect
        core.selectService("new service");
        assertTrue("new service".equals(core.getName()));

        core.selectService("22Y");
        assertTrue("22Y".equals(core.getName()));
        core.appendStop(1);
        core.appendStop(4);
        core.appendStop(6);
        core.appendStop(4);
        core.appendStop(0);
        core.selectService("new service");
        assertFalse(core.isClosed());
        core.selectService("22Y");
        assertTrue(core.isClosed());

        core.selectService("new service");
        core.deleteSelectedService();
        assertEquals(1, core.getServiceNames().length);
        assertFalse(core.hasSelectedService());

        // should have no effect
        core.deleteSelectedService();
        core.deleteSelectedService();
        core.deleteSelectedService();
        assertEquals(1, core.getServiceNames().length);

        core.selectService("22Y");
        core.deleteSelectedService();
        assertEquals(0, core.getServiceNames().length);

        for (int i = 0; i < 3; i++) {
            core.createNewService();
            core.setName("8" + i);
            try {
                core.applyChanges();
            } catch (NameConflictException nce) {
                assertTrue(false);
            }
        }
        assertEquals(3, core.getServiceNames().length);

        Timetable[] tables = core.getAllTimetables();
        assertEquals(3, tables.length);

        core.createNewService();
        core.setName("80");

        try {
            core.applyChanges();
            assertTrue(false);
        } catch (NameConflictException nce) {}
    }
*/

/*    @Test
    public void saveAndReadTest() {
        assertTrue(core.isSaved());
        for (int i = 0; i < 3; i++) {
            core.createNewService();
            core.setName("1" + i);
            core.setTimeGap((i + 3) * 2);
            core.appendStop(1);
            core.appendStop(4);
            core.appendStop(6);
            core.appendStop(4);
            if (i == 0)
                core.appendStop(0);
            try {
                core.applyChanges();
            } catch (NameConflictException nce) {
                assertTrue(false);
            }
        }

        assertFalse(core.isSaved());
        core.saveServices();
        assertTrue(core.isSaved());

        core = new Core(new ObjectIO());
        assertEquals(3, core.getServiceCount());
        for (String name : core.getServiceNames()) {
            core.selectService(name);
            System.out.println("--------------------");
            System.out.println("name: " + name);
            System.out.println("time gap: " + core.getTimeGap());
            System.out.println("closed: " + core.isClosed());
            System.out.println("stops: " + Arrays.toString(core.getStops()));
        }
    }*/


}//class
