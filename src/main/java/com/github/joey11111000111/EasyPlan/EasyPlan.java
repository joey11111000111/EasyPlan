package com.github.joey11111000111.EasyPlan;

import com.github.joey11111000111.EasyPlan.core.Controller;
import com.github.joey11111000111.EasyPlan.core.Core;
import com.github.joey11111000111.EasyPlan.dao.ObjectIO;
import com.github.joey11111000111.EasyPlan.gui.Start;

public class EasyPlan {

    public static void main(String[] args) {
        Controller controller = new Core(new ObjectIO());
        Start.setController(controller);
        Start.start();
    }

}