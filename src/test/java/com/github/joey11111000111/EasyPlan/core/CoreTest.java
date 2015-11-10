package com.github.joey11111000111.EasyPlan.core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

/**
 * Created by joey on 2015.11.10..
 */
public class CoreTest {

    private boolean hasSaves = false;

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
    public void testEmptyState() {
        Core core = new Core();
        Timetable[] tables = core.getAllTimetables();
        assertEquals(0, tables.length);
        assertNull(core.getCurrentTimetable());
        assertFalse(core.discardChanges());

        // should have no effect
        try {
            core.applyChanges();
        } catch (Core.NameConflictException nce) {
            System.err.println("nce happened: " + nce);
            assertTrue(false);
        }
        core.deleteCurrentService();

        assertNull(core.getCurrentStops());
        assertNull(core.getCurrentServiceData());
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
        File tempFile = new File(Core.SAVE_PATH);
        // it surely doesn't exist (either it didn't exist or was renamed)
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


    @Test
    public void testCurrentServiceFunctions() {
        Core core = new Core();
        core.createNewService();
        TouchedStops ts = core.getCurrentStops();
        BasicServiceData bsd = core.getCurrentServiceData();

        for (int i = 0; i < 4; i++) {
            assertTrue(ts.isEmpty());
            assertFalse(ts.isClosed());
            assertFalse(ts.isModified());
            assertFalse(ts.canUndo());

            assertTrue("new service".equals(bsd.getName()));
            assertFalse(bsd.isModified());

            // these should have no effect
            if (i == 0)
                try {
                    core.applyChanges();
                } catch (Core.NameConflictException nce) {
                    System.err.println("nce happened: " + nce);
                    assertTrue(false);
                }
            if (i == 1)
                core.discardChanges();
            if (i > 1) {
                ts.appendStop(1);
                ts.appendStop(4);
                ts.closeService();
                bsd.setTimeGap(39);
                bsd.setName("22Y");
                if (i == 2)
                    core.discardChanges();
            }
        }//for

        try {
            core.applyChanges();
        } catch (Core.NameConflictException nce) {
            System.err.println("nce happened: " + nce);
            assertTrue(false);
        }
        ts = core.getCurrentStops();
        bsd = core.getCurrentServiceData();
        assertFalse(ts.isEmpty());
        assertFalse(ts.isModified());
        assertTrue(ts.isClosed());
        assertTrue("22Y".equals(bsd.getName()));
        assertEquals(39, bsd.getTimeGap());

        Timetable table = core.getCurrentTimetable();
        assertNotNull(table);
        assertEquals(4, table.stopTimes.size());

        core.deleteCurrentService();
        assertNull(core.getCurrentServiceData());
        assertNull(core.getCurrentStops());
        String[] names = core.getServiceNames();
        assertEquals(0, names.length);
    }

    @Test
    public void testServiceManagement() {
        Core core = new Core();
        core.createNewService();
        core.createNewService();
        assertEquals(1, core.getServiceNames().length);
        BasicServiceData bsd = core.getCurrentServiceData();
        bsd.setName("22Y");
        core.createNewService();

        assertEquals(2, core.getServiceNames().length);
        bsd.setName("new service");


    }


}//class
