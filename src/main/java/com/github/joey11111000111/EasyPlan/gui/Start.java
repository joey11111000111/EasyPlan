package com.github.joey11111000111.EasyPlan.gui;

import com.github.joey11111000111.EasyPlan.core.Core;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by joey on 2015.12.07..
 */
public class Start extends Application {

    static Logger LOGGER = LoggerFactory.getLogger(Start.class);
    private static Core controller;

    public static void setController(Core c) {
        if (c == null)
            throw new NullPointerException("given controller is null");
        controller = c;
    }
    public static Core getController() {
        return controller;
    }

    public static void start() {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("EasyPlan ~ Bus Service Designer");
        GridPane root = new GridPane();
        Scene scene = new Scene(root, 500, 500, Color.GAINSBORO);

        DrawStack drawStack = new DrawStack(scene.widthProperty(), scene.heightProperty());
        root.getChildren().add(drawStack.getRoot());

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}//class
