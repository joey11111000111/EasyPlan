package com.github.joey11111000111.EasyPlan;

import com.github.joey11111000111.EasyPlan.core.Controller;
import com.github.joey11111000111.EasyPlan.core.Core;
import com.github.joey11111000111.EasyPlan.dao.ObjectIO;
import com.github.joey11111000111.EasyPlan.dao.ObjectReadFailureException;
import com.github.joey11111000111.EasyPlan.dao.ObjectSaveFailureException;
import com.github.joey11111000111.EasyPlan.dao.iObjectIO;
import com.github.joey11111000111.EasyPlan.gui.Start;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tha class contains the main method for this project.
 * Links all the modules together, to create a single working
 * desktop application. Uses entry points to all the main packages,
 * which include {@link com.github.joey11111000111.EasyPlan.core core},
 * {@link com.github.joey11111000111.EasyPlan.gui gui}
 * {@link com.github.joey11111000111.EasyPlan.dao dao}.
 */
public class EasyPlan {

    /**
     * The <a href="http://www.slf4j.org/">slf4j</a> logger object for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyPlan.class);

    /**
     * The entry point for the whole project.
     * Links together the individual modules to function as one application.
     * @param args commandline arguments, not used at all in this application
     */
    public static void main(String[] args) {

        LOGGER.trace("called main method");

        iObjectIO objectIO = new ObjectIO();

        LOGGER.trace("iObjectIO created");

        Controller controller;
        try {
            controller = objectIO.readObject(Core.class);
            controller.init();
            LOGGER.debug("Controller was successfully read");
        } catch (ObjectReadFailureException orfe) {
            controller = new Core();
            controller.createNewService();
            LOGGER.debug("a new Controller was created");
        }

        Start.setController(controller);
        Start.start();
        LOGGER.debug("GUI was launched");

        try {
            objectIO.saveObject(controller, Core.class);
            LOGGER.debug("the controller was successfully saved");
        } catch (ObjectSaveFailureException e) {
            LOGGER.warn("couldn't save controller: " + e.getMessage());
        }

        LOGGER.trace("application is safely closed");

    }//main

}//class