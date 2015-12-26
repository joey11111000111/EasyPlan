package com.github.joey11111000111.EasyPlan.gui;

import com.github.joey11111000111.EasyPlan.EasyPlan;
import com.github.joey11111000111.EasyPlan.core.Core;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

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
        GridPane gridPane = new GridPane();
        Scene scene = new Scene(gridPane, 770, 700);
        try {
            scene.getStylesheets().add(
                    Start.class.getClassLoader().getResource("control_style.css").toExternalForm());
        } catch (NullPointerException npe) {
            System.err.println("nem sikerült betölteni a css-t");
        }
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(770);


        DoubleProperty widthProperty = new SimpleDoubleProperty();
        widthProperty.bind(scene.widthProperty().multiply(2.0 / 3));
        DrawStack drawStack = new DrawStack(widthProperty, scene.heightProperty());

        DoubleProperty widthProperty2 = new SimpleDoubleProperty();
        widthProperty2.bind(scene.widthProperty().multiply(1.0 / 3));
        ControlPane controlPane = new ControlPane(widthProperty2, drawStack.stopsStringProperty());
        controlPane.addServiceChangeProperty(drawStack.serviceChangeProperty());
        controlPane.addTimetableHandler(event -> switchToTimetable());

        gridPane.add(drawStack.getRoot(), 0, 0);
        gridPane.add(controlPane.getRoot(), 1, 0);


        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void switchToTimetable() {
        System.err.println("meg lett hívva a kellő metódus");
    }

    private void switchToEditor() {
        System.err.println("meg lett hívva az editorra váltás");
    }

}//class
