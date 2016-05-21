package com.github.joey11111000111.EasyPlan.gui;

import javafx.scene.Group;

// CHECKSTYLE:OFF
public interface MarkableShape {

    Group getRoot();
    void markNeutral();
    void markReachable();
    void markCurrent();

}//interface
