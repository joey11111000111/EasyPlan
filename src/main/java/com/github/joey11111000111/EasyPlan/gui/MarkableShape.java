package com.github.joey11111000111.EasyPlan.gui;

import javafx.scene.Group;

/**
 * Created by joey on 2015.12.06..
 */
public interface MarkableShape {

    Group getRoot();
    void markNeutral();
    void markReachable();
    void markCurrent();

}//interface
