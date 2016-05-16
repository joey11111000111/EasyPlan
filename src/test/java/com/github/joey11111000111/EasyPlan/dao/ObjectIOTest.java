package com.github.joey11111000111.EasyPlan.dao;

import com.github.joey11111000111.EasyPlan.core.Controller;
import com.github.joey11111000111.EasyPlan.core.Core;
import com.github.joey11111000111.EasyPlan.core.exceptions.NameConflictException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ObjectIOTest {

    static iObjectIO objectIO;
    static File saveFile;
    static File renameFile = null;

    @BeforeClass
    public static void initObjectIO() {
        objectIO = new ObjectIO();
    }

    @BeforeClass
    public static void renameSaveFile() {
        String SAVE_PATH;
        StringBuilder sb = new StringBuilder(System.getProperty("user.home"));
        String sep = System.getProperty("file.separator");
        sb.append(sep).append(".EasyPlan").append(sep).append("savedServices.xml");
        SAVE_PATH = sb.toString();

        saveFile = new File(SAVE_PATH);
        if (!saveFile.exists())
            return;

        String RENAME_PATH;
        sb = new StringBuilder(System.getProperty("user.home"));
        sb.append(sep).append(".EasyPlan").append(sep).append("savedServices_backup.xml");
        RENAME_PATH = sb.toString();

        renameFile = new File(RENAME_PATH);
        if (!saveFile.renameTo(renameFile)) {
            System.err.println("cannot rename save file, interrupting...");
            System.exit(1);
        }
    }

    @AfterClass
    public static void getBackSaveFile() {
        if (renameFile == null)
            return;
        if (!renameFile.exists())
            return;

        if (!renameFile.renameTo(saveFile)) {
            System.err.println("cannot name-back save file, interrupting...");
            System.exit(1);
        }
    }

    @After
    public void deleteTempFile() {
        if (saveFile.exists()) {
            if (!saveFile.delete()) {
                System.err.println("cannot delete temporary save file, interrupting...");
                System.exit(1);
            }
        }
    }

    private void fillWithData(Controller controller) {
        controller.createNewService();
        controller.setName("22Y");
        controller.setTimeGap(90);
        controller.setFirstLeaveMinute(9);
        controller.setFirstLeaveHour(2);
        controller.setBoundaryMinute(11);
        controller.setBoundaryHour(12);

        controller.appendStop(1);
        controller.appendStop(4);
        controller.appendStop(6);
        controller.appendStop(0);

        try {
            controller.applyChanges();
        } catch (NameConflictException e) {
            System.err.println("the name " + controller.getName() + " is already in use, interrupting...");
            System.exit(1);
        }

    }

    @Test
    public void saveAndReadObjectTest() {
        Controller original = new Core();
        fillWithData(original);

        try {
            objectIO.saveObject(original, Core.class);
        } catch (ObjectSaveFailureException osfe) {
            System.err.println(osfe.getMessage());
            osfe.printStackTrace();
        }

        Controller loaded;
        try {
            loaded = objectIO.readObject(Core.class);
            loaded.init();
            loaded.selectService(original.getName());
        } catch (ObjectReadFailureException orfe) {
            System.err.println(orfe.getMessage());
            orfe.printStackTrace();
            assertTrue(false);
            return;     // just to prevent not-initialized warning, this is a line of dead code
        }

        assertEquals(original.hasSelectedService(), loaded.hasSelectedService());
        System.out.println("-----has selected +");
        assertEquals(original.isModified(), loaded.isModified());
        System.out.println("-----isModified +");
        assertEquals(original.isClosed(), loaded.isClosed());
        System.out.println("-----isClosed +");
        assertEquals(original.canUndo(), loaded.canUndo());
        System.out.println("-----canUndo +");
        assertEquals(original.getServiceCount(), loaded.getServiceCount());
    }


}
