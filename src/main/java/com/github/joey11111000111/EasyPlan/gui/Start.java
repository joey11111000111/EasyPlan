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

/*    @Override
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
    }*/

    @Override
    public void init() throws Exception {
        System.out.println("The init method has run!!!");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane gridPane = new GridPane();
        Scene scene = new Scene(gridPane, 700, 700);
        try {
            scene.getStylesheets().add(
                    Start.class.getClassLoader().getResource("control_style.css").toExternalForm());
        } catch (NullPointerException npe) {
            System.err.println("nem sikerült betölteni a css-t");
        }
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(770);


        for (String family: Font.getFamilies())
            System.out.println(family);

        DoubleProperty widthProperty = new SimpleDoubleProperty();
        widthProperty.bind(scene.widthProperty().multiply(2.0 / 3));
        DrawStack drawStack = new DrawStack(widthProperty, scene.heightProperty());
        DoubleProperty widthProperty2 = new SimpleDoubleProperty();
        widthProperty2.bind(scene.widthProperty().multiply(1.0 / 3));
        Pane controlPane = ControlPane.createControlPane(widthProperty2, drawStack.stopsStringProperty());

        gridPane.add(drawStack.getRoot(), 0, 0);
        gridPane.add(controlPane, 1, 0);

        controlPane.widthProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));

        primaryStage.setScene(scene);
        primaryStage.show();
    }


}//class
