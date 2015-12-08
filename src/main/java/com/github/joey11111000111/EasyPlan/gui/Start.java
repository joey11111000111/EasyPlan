package com.github.joey11111000111.EasyPlan.gui;

import com.github.joey11111000111.EasyPlan.core.Core;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
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
        root.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
        ColumnConstraints c1 = new ColumnConstraints();

        Scene scene = new Scene(root, 700, 700, Color.GAINSBORO);

        DoubleProperty widthProperty = new SimpleDoubleProperty();
        widthProperty.bind(scene.widthProperty().divide(2));

        DrawStack drawStack = new DrawStack(scene.widthProperty(), scene.heightProperty());
        root.add(drawStack.getRoot(), 0, 0);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}//class
