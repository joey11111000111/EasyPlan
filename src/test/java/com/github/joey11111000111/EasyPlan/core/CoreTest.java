package com.github.joey11111000111.EasyPlan.core;

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

        // should have no effect
        try {
            core.applyChanges();
            core.applyChanges();
        } catch (Core.NameConflictException nce) {
            System.err.println("nce happened: " + nce);
            assertTrue(false);
        }
        core.deleteCurrentService();

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


    @Test
    public void testServiceManagement() {
        Core core = new Core();
        ServiceData sd = core.getServiceData();
        core.createNewService();
        core.createNewService();
        assertEquals(1, core.getServiceNames().length);
        assertTrue(sd.hasSelectedService());
        assertTrue("new service".equals(sd.getName()));

        sd.setName("22Y");
        try {
            core.applyChanges();
            // without actual modification it should have no effect
            core.applyChanges();
        } catch (Core.NameConflictException e) {
            assertTrue(false);
        }

        core.createNewService();
        assertTrue("new service".equals(sd.getName()));
        String[] names = core.getServiceNames();
        assertEquals(2, names.length);
        assertTrue(names[0].equals("22Y"));
        assertTrue(names[1].equals("new service"));
        // this one should have no effect
        core.selectService("new service");
        assertTrue("new service".equals(sd.getName()));

        core.selectService("22Y");
        assertTrue("22Y".equals(sd.getName()));
        sd.appendStop(1);
        sd.appendStop(4);
        sd.appendStop(6);
        sd.appendStop(4);
        sd.closeService();
        core.selectService("new service");
        assertFalse(sd.isClosed());
        core.selectService("22Y");
        assertTrue(sd.isClosed());

        core.selectService("new service");
        core.deleteCurrentService();
        assertEquals(1, core.getServiceNames().length);
        assertFalse(sd.hasSelectedService());

        // should have no effect
        core.deleteCurrentService();
        core.deleteCurrentService();
        core.deleteCurrentService();
        assertEquals(1, core.getServiceNames().length);

        core.selectService("22Y");
        core.deleteCurrentService();
        assertEquals(0, core.getServiceNames().length);

        for (int i = 0; i < 3; i++) {
            core.createNewService();
            sd.setName("1" + i);
            try {
                core.applyChanges();
            } catch (Core.NameConflictException nce) {
                assertTrue(false);
            }
        }
        assertEquals(3, core.getServiceNames().length);

        Timetable[] tables = core.getAllTimetables();
        assertEquals(3, tables.length);

        core.createNewService();
        sd.setName("10");
        try {
            core.applyChanges();
            assertTrue(false);
        } catch (Core.NameConflictException nce) {}
    }

    @Test
    public void saveAndReadTest() {
        Core core = new Core();
        ServiceData sd = core.getServiceData();
        for (int i = 0; i < 3; i++) {
            core.createNewService();
            sd.setName("1" + i);
            sd.setTimeGap((i + 3) * 2);
            sd.appendStop(1);
            sd.appendStop(4);
            sd.appendStop(6);
            sd.appendStop(4);
            if (i == 0)
                sd.closeService();
            try {
                core.applyChanges();
            } catch (Core.NameConflictException nce) {
                assertTrue(false);
            }
        }

        core.saveServices();

        core = new Core();
        assertEquals(3, core.getServiceCount());
        sd = core.getServiceData();
        for (String name : core.getServiceNames()) {
            core.selectService(name);
            System.out.println("--------------------");
            System.out.println("name: " + name);
            System.out.println("time gap: " + sd.getTimeGap());
            System.out.println("closed: " + sd.isClosed());
            System.out.println("stops: " + Arrays.toString(sd.getStops()));
        }
    }


}//class
