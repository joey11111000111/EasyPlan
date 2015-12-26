package com.github.joey11111000111.EasyPlan;

import com.github.joey11111000111.EasyPlan.core.Core;
import com.github.joey11111000111.EasyPlan.gui.Start;

public class EasyPlan {

    public static void main(String[] args) {
        Core controller = new Core();
        if (controller.getServiceCount() == 0)
            controller.createNewService();
        else if (!controller.hasSelectedService())
            controller.selectService(controller.getServiceNames()[0]);
        Start.setController(controller);
        Start.start();
    }

}