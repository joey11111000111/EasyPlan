package com.github.joey11111000111.EasyPlan.gui;

import com.github.joey11111000111.EasyPlan.core.Controller;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by joey on 2015.12.07..
 */
public class Start extends Application {

    static Controller controller;
    static Logger LOGGER = LoggerFactory.getLogger(Start.class);
    Stage stage;
    private Scene editorScene;

    public static void setController(Controller c) {
        if (c == null)
            throw new NullPointerException("given controller is null");
        controller = c;
    }

    public static void start() {
        if (controller == null)
            throw new IllegalStateException("controller is null, no connection to the underlying layers");
        Application.launch();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setMinHeight(600);
        stage.setMinWidth(770);

        GridPane root = new GridPane();
        editorScene = new Scene(root, 770, 700);
        // try to add the css styling to the editor scene
        try {
            editorScene.getStylesheets().add(Start.class.getClassLoader().
                    getResource("control_style.css").toExternalForm());
        } catch (Throwable t) {
            LOGGER.warn("could not read css file for the control scene");
        }

        // create draw stack for the bus service illustrations
        DoubleProperty widthProperty = new SimpleDoubleProperty();
        widthProperty.bind(editorScene.widthProperty().multiply(2.0 / 3));
        DrawStack drawStack = new DrawStack(widthProperty, editorScene.heightProperty());

        // create the control pane
        widthProperty = new SimpleDoubleProperty();
        widthProperty.bind(editorScene.widthProperty().multiply(1.0 / 3));
        ControlPane controlPane = new ControlPane(widthProperty, drawStack.stopsStringProperty());
        controlPane.addServiceChangeProperty(drawStack.serviceChangeProperty());
        controlPane.addTimetableHandler(event -> switchToTimetable());

        root.add(drawStack.getRoot(), 0, 0);
        root.add(controlPane.getRoot(), 1, 0);

        primaryStage.setScene(editorScene);
        primaryStage.show();
    }

    private void switchToTimetable() {
        stage.setScene(TimetableSceneCreator.createScene(event -> switchToEditor()));
    }

    private void switchToEditor() {
        stage.setScene(editorScene);
    }

}//class
