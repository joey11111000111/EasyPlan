package com.github.joey11111000111.EasyPlan.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.geometry.Insets;
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
        currentServiceLabel.setFont(Font.font("IncisedBlackWide", FontWeight.NORMAL, FontPosture.REGULAR, 20));
        currentServiceLabel.setFill(Color.rgb(150, 150, 230));
        root.getChildren().add(currentServiceLabel);


        // Járat név felirat
        Text nameLabel = new Text("Name of service");
        nameLabel.setFont(Font.font("Monospaced", FontWeight.NORMAL, FontPosture.ITALIC, 15));
        nameLabel.setFill(Color.SILVER);
        root.getChildren().add(nameLabel);

        // járat név text field
        TextField serciveName = new TextField();
        serciveName.setStyle("-fx-background-color: rgba(60, 65, 100, 0.9); " +
                "-fx-text-fill: rgba(150, 150, 230, 0.9);");
        serciveName.setAlignment(Pos.CENTER);
        serciveName.setPromptText("Name of service");
        root.getChildren().add(serciveName);

        HBox fromBox = new HBox(2);
        HBox toBox = new HBox(2);
        fromBox.setAlignment(Pos.CENTER_LEFT);
        toBox.setAlignment(Pos.CENTER_LEFT);
        ComboBox<Integer> fromHour = new ComboBox<>();
        Rectangle comboShape1 = new Rectangle(100, 40);
        comboShape1.setArcWidth(40);
        comboShape1.setArcHeight(40);
        fromHour.setShape(comboShape1);
        fromHour.setBackground(new Background(new BackgroundFill(Color.SILVER, null, null)));

        Spinner<Integer> toHour = new Spinner<>(0, 23, 18, 1);
        toHour.setPrefWidth(100);
        toHour.setEditable(true);
//        Rectangle spinnerShape = new Rectangle(150, 40);
//        spinnerShape.setArcWidth(40);
//        spinnerShape.setArcHeight(40);
        Circle spinnerShape = new Circle(60);
        spinnerShape.setFill(Color.BLACK);
        toHour.setShape(null);

        Text fromText = new Text("First leave hour: ");
        Text toText = new Text("Boundary hour:  ");
        fromText.setFill(Color.SILVER);
        toText.setFill(Color.SILVER);
        fromBox.getChildren().addAll(fromText, fromHour);
        toBox.getChildren().addAll(toText, toHour);
        root.getChildren().addAll(fromBox, toBox);

        // Time gap text
        HBox timeGapBox = new HBox(2);
        timeGapBox.setAlignment(Pos.CENTER_LEFT);
        Text timeGapText = new Text("Minutes between two buses: ");
        timeGapText.setFill(Color.SILVER);
        TextField timeGapField = new TextField();
        timeGapBox.getChildren().addAll(timeGapText, timeGapField);
        root.getChildren().add(timeGapBox);


        HBox stopsLabelBox = new HBox();
        stopsLabelBox.setAlignment(Pos.CENTER);
        Text stopsLabel = new Text("Stop of service:");
        stopsLabel.setFill(Color.SILVER);
        stopsLabelBox.getChildren().add(stopsLabel);
        root.getChildren().add(stopsLabelBox);

        TextArea stopsArea = new TextArea();
        stopsArea.setEditable(false);

        Label stops = new Label();
        stops.setPrefHeight(100);
        stops.setAlignment(Pos.TOP_LEFT);
        stops.setBackground(new Background(new BackgroundFill(Color.DARKOLIVEGREEN, null, null)));
        stopsText.addListener((observable, oldValue, newValue) -> {
            StringBuilder sb = new StringBuilder(newValue);
            for (int i = 24; i < newValue.length(); i += 25) {
                sb.insert(i, "\n");
            }
            stops.setText(sb.toString());
            stopsArea.setText(sb.toString());
        });
        stops.setTextFill(Color.SILVER);
//        root.getChildren().add(stops);
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
        allServiceLabel.setFont(Font.font("IncisedBlackWide", FontWeight.NORMAL, FontPosture.REGULAR, 20));
        allServiceLabel.setFill(Color.rgb(150, 150, 230));
        root.getChildren().add(allServiceLabel);

        HBox selectServiceBox = new HBox(2);
        selectServiceBox.setAlignment(Pos.CENTER_LEFT);
        Text selectLabel = new Text("Select service: ");
        selectLabel.setFill(Color.SILVER);
        ComboBox<String> selectBox = new ComboBox<>();
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

        return root;
    }

}//class
