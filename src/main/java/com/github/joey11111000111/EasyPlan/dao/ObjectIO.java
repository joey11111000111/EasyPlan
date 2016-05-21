package com.github.joey11111000111.EasyPlan.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Paths;
/**
 * Implementation of the {@link iObjectIO} interface. Logging is included.
 */
public class ObjectIO implements iObjectIO {

    /**
     * The <a href="http://www.slf4j.org/">slf4j</a> logger object for this class.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(ObjectIO.class);

    /**
     * The string that represents the path to the save file.
     */
    static final File SAVE_FILE;

    /**
     * Initializes the {@link #SAVE_PATH} in a platform independent way.
     */
    static {
        SAVE_FILE = Paths.get(System.getProperty("user.home"),
                              ".EasyPlan",
                              "savedServices.xml")
                              .toFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveObject(Object object, Class<?> clazz) throws ObjectSaveFailureException {
        LOGGER.trace("called saveObject");
        if (!SAVE_FILE.exists()) {
            LOGGER.debug("save file doesn't exist");
            if (!SAVE_FILE.getParentFile().exists() && !SAVE_FILE.getParentFile().mkdirs())
                    throw new ObjectSaveFailureException("cannot create parent library");
            try {
                if (!SAVE_FILE.createNewFile())
                    throw new ObjectSaveFailureException("cannot explicitly create save file");
            } catch (IOException ioe) {
                throw new ObjectSaveFailureException("cannot explicitly create save file: " + ioe.getMessage());
            }
            LOGGER.debug("save file successfully created");
        }

        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(SAVE_FILE);
        } catch (FileNotFoundException e) {
            throw new ObjectSaveFailureException("save file cannot be created: " + e.getMessage());
        }
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(object, outputStream);
            LOGGER.debug("successfully saved object into save file");
        } catch (JAXBException e) {
            throw new ObjectSaveFailureException("Object cannot be saved: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <E> E readObject(Class<E> clazz) throws ObjectReadFailureException {
        LOGGER.trace("called readObject");
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(SAVE_FILE);
        } catch (FileNotFoundException e) {
            throw new ObjectReadFailureException("save file is not found");
        }

        LOGGER.debug("save file found");

        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            E loadedObject = (E)unmarshaller.unmarshal(inputStream);
            LOGGER.debug("object successfully loaded from save file");
            return loadedObject;
        } catch (JAXBException jaxbe) {
            throw new ObjectReadFailureException("Cannot read object: " + jaxbe.getMessage());
        }
    }

    /*    @Override
    public void saveObjects(Serializable[] objec) {
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
    }*/
}//class
