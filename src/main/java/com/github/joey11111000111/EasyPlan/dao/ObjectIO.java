package com.github.joey11111000111.EasyPlan.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by joey on 3/18/16.
 */
public class ObjectIO implements iObjectIO {

    static final Logger LOGGER = LoggerFactory.getLogger(ObjectIO.class);
    static final String SAVE_PATH;

    static {
        StringBuilder sb = new StringBuilder(System.getProperty("user.home"));
	String sep = System.getProperty("file.separator");
	sb.append(sep).append(".EasyPlan").append(sep).append("savedServices.xml");
	SAVE_PATH = sb.toString();
    }

    @Override
    public void saveObject(Object object, Class<?> clazz) throws ObjectSaveFailureException {
	System.out.println("---------: " + SAVE_PATH + " :----------");
        try {
	    File saveFile = new File(SAVE_PATH);
	    if (!saveFile.exists()) {
		saveFile.getParentFile().mkdirs();
	        saveFile.createNewFile();
	    }
	} catch (IOException e) {
	    throw new ObjectSaveFailureException("cannot explicitely create save file: " + e.getMessage());
	}

        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(SAVE_PATH);
        } catch (FileNotFoundException e) {
            throw new ObjectSaveFailureException("save file cannot be created: " + e.getMessage());
        }

        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(object, outputStream);
        } catch (JAXBException e) {
            throw new ObjectSaveFailureException("Object cannot be saved: " + e.getMessage());
        }
    }

    @Override
    public <E> E readObject(Class<E> clazz) throws ObjectReadFailureException {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(SAVE_PATH);
        } catch (FileNotFoundException e) {
            throw new ObjectReadFailureException("save file is not found");
        }

        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (E)unmarshaller.unmarshal(inputStream);
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
