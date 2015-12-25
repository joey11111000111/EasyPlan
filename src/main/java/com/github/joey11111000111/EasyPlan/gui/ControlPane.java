package com.github.joey11111000111.EasyPlan.gui;

import com.github.joey11111000111.EasyPlan.core.Core;
import com.github.joey11111000111.EasyPlan.util.DayTime;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
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

    Core controller;
    private VBox root;
    // all the ui controls that need to be wired up later, in the order of appearance from the top
    TextField nameField;
    Spinner<Integer> fromHourSpinner;
    Spinner<Integer> fromMinuteSpinner;
    Spinner<Integer> toHourSpinner;
    Spinner<Integer> toMinuteSpinner;
    Spinner<Integer> timeGapSpinner;
    TextArea stopsArea;
    Button applyButton;
    ComboBox<String> serviceComboBox;
    Button addNewButton;
    Button deleteButton;
    Button timetableButton;
    Button exitButton;

    private enum TextType {SEPARATOR, EXPLAIN}

    public ControlPane(ReadOnlyDoubleProperty widthProperty, ReadOnlyStringProperty stopsStringProperty) {
        // if there is no service, create a new one, or if no service is selected, select the first
        controller = Start.getController();

        initTopPane(widthProperty);

        // Current service part ------------------------------------------------
        root.getChildren().add(createText(TextType.SEPARATOR, "Current Service"));
        root.getChildren().add(createText(TextType.EXPLAIN, "Name of Service"));

        nameField = new TextField();
        nameField.setText(controller.getName());
        nameField.setAlignment(Pos.CENTER);
        root.getChildren().add(nameField);

        // spinners
        Text firstLeaveText = createText(TextType.EXPLAIN, "First leave time");
        firstLeaveText.setUnderline(true);
        Text boundaryText = createText(TextType.EXPLAIN, "Boundary time");
        boundaryText.setUnderline(true);
        HBox fromBox = createLeaveTimeRow(controller.getFirstLeaveTime(), fromHourSpinner, fromMinuteSpinner);
        HBox toBox = createLeaveTimeRow(controller.getBoundaryTime(), toHourSpinner, toMinuteSpinner);
        HBox gapBox = new HBox(2);
        gapBox.setAlignment(Pos.CENTER_LEFT);
        gapBox.getChildren().add(createText(TextType.EXPLAIN, "Minutes between buses:"));
        timeGapSpinner = createSpinner(1, 23 * 60 + 59, controller.getTimeGap());
        gapBox.getChildren().add(timeGapSpinner);

        root.getChildren().addAll(firstLeaveText, fromBox, boundaryText, toBox, gapBox);

        // show touched stops
        Text stopsText = createText(TextType.EXPLAIN, "Stops of service");
        stopsArea = new TextArea();
        stopsArea.setPrefHeight(100);
        stopsArea.setEditable(false);

        stopsStringProperty.addListener((observable, oldValue, newValue) -> refreshStopsString(newValue));
        widthProperty.addListener((observable, oldValue, newValue) -> refreshStopsString(stopsStringProperty.getValue()));
        refreshStopsString(stopsStringProperty.getValue());
        root.getChildren().addAll(stopsText, stopsArea);

        // apply changes button
        applyButton = createRoundedButton("Apply changes", widthProperty);
        root.getChildren().add(applyButton);

        // All services part ---------------------------------------------
        Region firstSpacer = new Region();
        VBox.setVgrow(firstSpacer, Priority.ALWAYS);
        root.getChildren().add(firstSpacer);
        root.getChildren().add(createText(TextType.SEPARATOR, "All Services"));

        // the row of the service selector
        HBox selectBox = new HBox(2);
        selectBox.setAlignment(Pos.CENTER);
        selectBox.getChildren().add(createText(TextType.EXPLAIN, "Select service:"));
        serviceComboBox = createRoundedComboBox();
        serviceComboBox.getItems().addAll(controller.getServiceNames());
        serviceComboBox.setValue(controller.getName());
        selectBox.getChildren().add(serviceComboBox);

        addNewButton = createRoundedButton("Add new service", widthProperty);
        deleteButton = createRoundedButton("Delete current service", widthProperty);
        root.getChildren().addAll(selectBox, addNewButton, deleteButton);

        // Other part -----------------------------------------------------
        Region secondSpacer = new Region();
        VBox.setVgrow(secondSpacer, Priority.ALWAYS);
        root.getChildren().add(secondSpacer);
        root.getChildren().add(createText(TextType.SEPARATOR, "Other"));
        timetableButton = createRoundedButton("Show timetable", widthProperty);
        exitButton = createRoundedButton("Exit", widthProperty);
        root.getChildren().addAll(timetableButton, exitButton);

        wireUpUiControls();

    }//constructor

    private void wireUpUiControls() {
        // TODO
    }

    public Pane getRoot() {
        return root;
    }

    private void refreshStopsString(String newValue) {
        int maxLine = (int)root.getPrefWidth() / 8;
        String ln = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder(newValue);
        for (int i = maxLine - 1; i < newValue.length(); i += maxLine)
            sb.insert(i, ln);
        stopsArea.setText(sb.toString());
    }

    private HBox createLeaveTimeRow(DayTime time, Spinner<Integer> hourSpinner, Spinner<Integer> minuteSpinner) {
        hourSpinner = createSpinner(0, 23, time.getHours());
        minuteSpinner = createSpinner(0, 59, time.getMinutes());
        Text hourText = createText(TextType.EXPLAIN, "hour: ");
        Text minuteText = createText(TextType.EXPLAIN, "minute: ");


        HBox row = new HBox(2);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.setAlignment(Pos.CENTER);
        row.getChildren().addAll(hourText, hourSpinner, spacer, minuteText, minuteSpinner);
        return row;
    }

    private void initTopPane(ReadOnlyDoubleProperty widthProperty) {
        // create top pane and set its width, padding and alignment
        root = new VBox(5);
        root.prefWidthProperty().bind(widthProperty);
        root.minWidthProperty().bind(widthProperty);
        root.maxWidthProperty().bind(widthProperty);
        root.setPadding(new Insets(5));
        root.setAlignment(Pos.TOP_CENTER);
        // set background of the root container
        LinearGradient bgkGrad = new LinearGradient(
                0, 0,
                0, 700,
                false,
                CycleMethod.NO_CYCLE,
                new Stop(.1, Color.rgb(50, 50, 30)),
                new Stop(1, Color.rgb(50, 50, 15))
        );
        root.setBackground(new Background(new BackgroundFill(bgkGrad, null, null)));
    }

    private Button createRoundedButton(String text, ReadOnlyDoubleProperty widthProperty) {
        int padding = 15;
        Rectangle buttonShape = new Rectangle();
        buttonShape.setArcWidth(50);
        buttonShape.setArcHeight(50);
        buttonShape.setHeight(40);
        buttonShape.widthProperty().bind(widthProperty.subtract(padding));

        Button button = new Button(text);
        button.maxWidthProperty().bind(buttonShape.widthProperty());
        button.minWidthProperty().bind(buttonShape.widthProperty());
        button.prefWidthProperty().bind(buttonShape.widthProperty());
        button.setShape(buttonShape);
        return button;
    }

    private ComboBox<String> createRoundedComboBox() {
        Rectangle comboShape = new Rectangle(150, 40);
        comboShape.setArcWidth(40);
        comboShape.setArcHeight(40);

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setShape(comboShape);
        return comboBox;
    }

    private Spinner<Integer> createSpinner(int min, int max, int initValue) {
        Spinner<Integer> spinner = new Spinner<>(min, max, initValue);
        spinner.setPrefWidth(100);
        spinner.setEditable(true);
        return spinner;
    }

    private Text createText(TextType type, String info) {
        Text text = new Text(info);
        if (type == TextType.EXPLAIN)
            text.getStyleClass().add("explain-text");
        else
            text.getStyleClass().add("separator-text");
        return text;
    }



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
