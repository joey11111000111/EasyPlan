package com.github.joey11111000111.EasyPlan.gui;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.logging.FileHandler;

/**
 * Created by joey on 2015.12.17..
 */
public class ControlPane {

    public static Pane createControlPane(DoubleProperty widthProperty, ReadOnlyStringProperty stopsText) {
        VBox root = new VBox(5);
        root.prefWidthProperty().bind(widthProperty);
        root.minWidthProperty().bind(widthProperty);
        root.maxWidthProperty().bind(widthProperty);
        root.setPadding(new Insets(5));
        root.setAlignment(Pos.TOP_CENTER);

        LinearGradient bgkGrad = new LinearGradient(
                0, 0,
                0, 700,
                false,
                CycleMethod.NO_CYCLE,
                new Stop(.1, Color.rgb(50, 50, 30)),
                new Stop(1, Color.rgb(50, 50, 15))
        );
        root.setBackground(new Background(new BackgroundFill(bgkGrad, null, null)));

        // aktuális járat jelölő
        Text currentServiceLabel = new Text("Current Service");
        currentServiceLabel.getStyleClass().add("separator-text");
        root.getChildren().add(currentServiceLabel);


        // Járat név felirat
        Text nameLabel = new Text("Name of service");
        nameLabel.getStyleClass().add("explain-text");
        root.getChildren().add(nameLabel);

        // járat név text field
        TextField serciveName = new TextField();
        serciveName.setAlignment(Pos.CENTER);
        root.getChildren().add(serciveName);

        HBox fromBox = new HBox(2);
        HBox toBox = new HBox(2);
        fromBox.setAlignment(Pos.CENTER_LEFT);
        toBox.setAlignment(Pos.CENTER_LEFT);
        Spinner<Integer> fromHour = new Spinner<>(0, 23, 6);
        fromHour.setPrefWidth(100);

        Spinner<Integer> toHour = new Spinner<>(0, 23, 18, 1);
        toHour.setPrefWidth(100);
        toHour.setEditable(true);
        Circle spinnerShape = new Circle(60);
        spinnerShape.setFill(Color.BLACK);
        toHour.setShape(null);

        Text fromText = new Text("First leave hour: ");
        fromText.getStyleClass().add("explain-text");
        Text toText = new Text("Boundary hour:  ");
        toText.getStyleClass().add("explain-text");
        fromBox.getChildren().addAll(fromText, fromHour);
        toBox.getChildren().addAll(toText, toHour);
        root.getChildren().addAll(fromBox, toBox);

        // Time gap text
        HBox timeGapBox = new HBox(2);
        timeGapBox.setAlignment(Pos.CENTER_LEFT);
        Text timeGapText = new Text("Minutes between two buses: ");
        timeGapText.getStyleClass().add("explain-text");
        TextField timeGapField = new TextField();
        timeGapBox.getChildren().addAll(timeGapText, timeGapField);
        root.getChildren().add(timeGapBox);


        HBox stopsLabelBox = new HBox();
        stopsLabelBox.setAlignment(Pos.CENTER);
        Text stopsLabel = new Text("Stops of service:");
        stopsLabel.getStyleClass().add("explain-text");
        stopsLabelBox.getChildren().add(stopsLabel);
        root.getChildren().add(stopsLabelBox);

        TextArea stopsArea = new TextArea();
        stopsArea.setPrefHeight(100);
        stopsArea.setEditable(false);

        stopsText.addListener((observable, oldValue, newValue) -> {
            int maxLine = (int)stopsArea.getWidth() / 8;
            StringBuilder sb = new StringBuilder(newValue);
            for (int i = maxLine - 1; i < newValue.length(); i += maxLine) {
                sb.insert(i, "\n");
            }
            stopsArea.setText(sb.toString());
        });
        root.getChildren().add(stopsArea);

        Rectangle rectangle4 = new Rectangle();
        rectangle4.setArcWidth(50);
        rectangle4.setArcHeight(50);
        rectangle4.widthProperty().bind(root.widthProperty().subtract(10));
        rectangle4.setHeight(40);
        Button applyChangesButton = new Button("Apply changes");
        applyChangesButton.maxWidthProperty().bind(root.widthProperty().subtract(15));
        applyChangesButton.minWidthProperty().bind(root.widthProperty().subtract(15));
        applyChangesButton.prefWidthProperty().bind(root.widthProperty().subtract(15));
        applyChangesButton.setShape(rectangle4);
        root.getChildren().add(applyChangesButton);


        // All services -------------------
        Text allServiceLabel = new Text("All services");
        allServiceLabel.getStyleClass().add("separator-text");
        root.getChildren().add(allServiceLabel);

        HBox selectServiceBox = new HBox(2);
        selectServiceBox.setAlignment(Pos.CENTER_LEFT);
        Text selectLabel = new Text("Select service: ");
        selectLabel.getStyleClass().add("explain-text");
        ComboBox<String> selectBox = new ComboBox<>();
        Rectangle comboShape1 = new Rectangle(100, 40);
        comboShape1.setArcWidth(40);
        comboShape1.setArcHeight(40);
        selectBox.setShape(comboShape1);
        selectBox.getItems().addAll("22Y", "24", "24A", "4", "8", "Tesco");
        selectServiceBox.getChildren().addAll(selectLabel, selectBox);
        root.getChildren().add(selectServiceBox);

        Rectangle rect = new Rectangle();
        rect.setArcWidth(50);
        rect.setArcHeight(50);
        rect.widthProperty().bind(root.widthProperty().subtract(10));
        rect.setHeight(40);
        Button newServiceButton = new Button("Add new service");
        newServiceButton.maxWidthProperty().bind(root.widthProperty().subtract(15));
        newServiceButton.minWidthProperty().bind(root.widthProperty().subtract(15));
        newServiceButton.prefWidthProperty().bind(root.widthProperty().subtract(15));
        newServiceButton.setShape(rect);
        root.getChildren().add(newServiceButton);


        Rectangle rectangle = new Rectangle();
        rectangle.setArcWidth(50);
        rectangle.setArcHeight(50);
        rectangle.widthProperty().bind(root.widthProperty().subtract(10));
        rectangle.setHeight(40);
        Button deleteServiceButton = new Button("Delete current service");
        deleteServiceButton.maxWidthProperty().bind(root.widthProperty().subtract(15));
        deleteServiceButton.minWidthProperty().bind(root.widthProperty().subtract(15));
        deleteServiceButton.prefWidthProperty().bind(root.widthProperty().subtract(15));
        deleteServiceButton.setShape(rectangle);
        root.getChildren().add(deleteServiceButton);



        Text othersText = new Text("Other");
        othersText.getStyleClass().add("separator-text");
        root.getChildren().add(othersText);

        Rectangle rectangle2 = new Rectangle();
        rectangle2.setArcWidth(50);
        rectangle2.setArcHeight(50);
        rectangle2.widthProperty().bind(root.widthProperty().subtract(10));
        rectangle2.setHeight(40);
        Button timeTableButton = new Button("Show timetable");
        timeTableButton.maxWidthProperty().bind(root.widthProperty().subtract(15));
        timeTableButton.minWidthProperty().bind(root.widthProperty().subtract(15));
        timeTableButton.prefWidthProperty().bind(root.widthProperty().subtract(15));
        timeTableButton.setShape(rectangle2);
        root.getChildren().add(timeTableButton);


        Rectangle rectangle3 = new Rectangle();
        rectangle3.setArcWidth(50);
        rectangle3.setArcHeight(50);
        rectangle3.widthProperty().bind(root.widthProperty().subtract(10));
        rectangle3.setHeight(40);
        Button exitButton = new Button("Exit");
        exitButton.maxWidthProperty().bind(root.widthProperty().subtract(15));
        exitButton.minWidthProperty().bind(root.widthProperty().subtract(15));
        exitButton.prefWidthProperty().bind(root.widthProperty().subtract(15));
        exitButton.setShape(rectangle3);
        exitButton.setOnAction(event -> Platform.exit());
        root.getChildren().add(exitButton);

        return root;
    }

}//class
