package com.github.joey11111000111.EasyPlan.dao;

import com.github.joey11111000111.EasyPlan.core.exceptions.ObjectReadFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by joey on 3/18/16.
 */
public class ObjectIO implements iObjectIO {

    static final Logger LOGGER = LoggerFactory.getLogger(ObjectIO.class);
    static final String SAVE_PATH = System.getProperty("user.home") + "/.EasyPlan/savedServices";

    @Override
    public void saveObjects(Serializable[] objects) {
        LOGGER.trace("called saveServices");
        int size = objects.length;
        // When there are no services, the reader will know that by not finding the save file
        LOGGER.info("saving " + size + " bus services");
        if (size == 0) {
            File saveFile = new File(SAVE_PATH);
            if (saveFile.exists())
                if (!saveFile.delete())
                    LOGGER.error("couldn't delete save file");
            return;
        }


        FileOutputStream fos;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(SAVE_PATH);
            oos = new ObjectOutputStream(fos);
            for (Serializable object : objects)
                oos.writeObject(object);
        }
        catch (IOException ioe) {
            LOGGER.error("I/O exception happened while saving the services", ioe);
        } finally {
            if (oos != null)
                try {
                    oos.close();
                } catch (IOException ioe) {
                    LOGGER.error("cannot close ObjectOutputStream", ioe);
                }
        }
    }//saveObjects

    @Override
    public <E> List<E> readObjects(Class<E> clazz) throws ObjectReadFailureException {
        LOGGER.trace("called readSavedServices");
        List<E> readObjects = new ArrayList<>();
        FileInputStream fis;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(SAVE_PATH);
            ois = new ObjectInputStream(fis);
            try {
                while (true) {
                    try {
                        readObjects.add((E) ois.readObject());
                    } catch (ClassCastException | ClassNotFoundException e) {
                        throw new ObjectReadFailureException(e.getMessage());
                    }
                }//while
            } catch (EOFException eofe) {
                LOGGER.debug("read " + readObjects.size() + " bus services from the save file");
            }
        } catch (FileNotFoundException fnfe) {
            throw new ObjectReadFailureException("save file is not found");
        } catch (IOException ioe) {
            LOGGER.error("I/O exception happened while reading saves: ", ioe);
            throw new ObjectReadFailureException(ioe.getMessage());
        } finally {
            if (ois != null)
                try {
                    ois.close();
                } catch (IOException ioe) {
                    LOGGER.error("I/O exception happened while trying to close the ObjectInputStream", ioe);
                }
        }

        return readObjects;
    }
}//class
