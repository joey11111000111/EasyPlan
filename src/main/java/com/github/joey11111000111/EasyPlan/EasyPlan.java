package com.github.joey11111000111.EasyPlan;

import com.github.joey11111000111.EasyPlan.core.Controller;
import com.github.joey11111000111.EasyPlan.core.Core;
import com.github.joey11111000111.EasyPlan.dao.ObjectIO;
import com.github.joey11111000111.EasyPlan.dao.ObjectReadFailureException;
import com.github.joey11111000111.EasyPlan.dao.ObjectSaveFailureException;
import com.github.joey11111000111.EasyPlan.dao.iObjectIO;
import com.github.joey11111000111.EasyPlan.gui.Start;

public class EasyPlan {

    public static void main(String[] args) {
        // TODO logging
        iObjectIO objectIO = new ObjectIO();

        Controller controller;
        // read the controller object if possible, create a new one otherwise
        try {
            controller = objectIO.readObject(Core.class);
            controller.init();
        } catch (ObjectReadFailureException orfe) {
            controller = new Core();
            controller.createNewService();
        }

        Start.setController(controller);
        Start.start();

        try {
            objectIO.saveObject(controller, Core.class);
        } catch (ObjectSaveFailureException e) {
            System.err.println(e.getMessage());
        }

    }

}