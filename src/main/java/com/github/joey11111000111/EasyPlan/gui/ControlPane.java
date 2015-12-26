package com.github.joey11111000111.EasyPlan.gui;

import com.github.joey11111000111.EasyPlan.core.Core;
import com.github.joey11111000111.EasyPlan.core.exceptions.NameConflictException;
import com.github.joey11111000111.EasyPlan.util.DayTime;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Created by joey on 2015.12.17..
 */
public class ControlPane {

    Core controller;
    private VBox root;
    // all the ui controls that need to be wired up later, in the order of appearance from the top
    private TextField nameField;
    private Spinner<Integer> fromHourSpinner;
    private Spinner<Integer> fromMinuteSpinner;
    private Spinner<Integer> toHourSpinner;
    private Spinner<Integer> toMinuteSpinner;
    private Spinner<Integer> timeGapSpinner;
    private TextArea stopsArea;
    private Button applyButton;
    private ComboBox<String> serviceComboBox;
    private Button addNewButton;
    private Button deleteButton;
    private Button timetableButton;
    private Button exitButton;

    private BooleanProperty serviceChangeProperty;

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
        HBox fromBox = createLeaveTimeRow(controller.getFirstLeaveTime(), "from");
        HBox toBox = createLeaveTimeRow(controller.getBoundaryTime(), "to");
        HBox gapBox = new HBox(2);
        gapBox.setAlignment(Pos.CENTER_LEFT);
        gapBox.getChildren().add(createText(TextType.EXPLAIN, "Minutes between buses: "));
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
        nameField.setOnAction(event -> controller.setName(nameField.getText().trim()));

        // spinners
        fromHourSpinner.valueProperty().addListener((observable, oldValue, newValue) ->
                controller.setFirstLeaveHour(newValue));
        fromMinuteSpinner.valueProperty().addListener((observable, oldValue, newValue) ->
                controller.setFirstLeaveMinute(newValue));
        toHourSpinner.valueProperty().addListener((observable, oldValue, newValue) ->
                controller.setBoundaryHour(newValue));
        toMinuteSpinner.valueProperty().addListener((observable, oldValue, newValue) ->
                controller.setBoundaryMinute(newValue));
        timeGapSpinner.valueProperty().addListener((observable, oldValue, newValue) ->
                controller.setTimeGap(newValue));

        applyButton.setOnAction(event -> applyChanges());
        serviceComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            controller.selectService(newValue);
            for (String name : controller.getServiceNames())
                if (name.equals(oldValue)) {
                    showService();
                    return;
                }
        });

        addNewButton.setOnAction(event -> {
            controller.createNewService();
            String newServiceName = controller.getName();
            serviceComboBox.getItems().add(newServiceName);
            serviceComboBox.setValue(newServiceName);
        });
        deleteButton.setOnAction(event -> {
            String deleteName = controller.getName();
            if (controller.getServiceCount() == 1)
                addNewButton.fire();
            else {
                String[] names = controller.getServiceNames();
                if (names[0].equals(deleteName))
                    serviceComboBox.setValue(names[1]);
                else
                    serviceComboBox.setValue(names[0]);
            }
            controller.deleteService(deleteName);
            serviceComboBox.getItems().remove(deleteName);
        });



        exitButton.setOnAction(event -> {
            if (!controller.isSaved())
                controller.saveServices();
            Platform.exit();
        });


    }

    private void showService() {
        if (serviceChangeProperty != null)
            serviceChangeProperty.set(!serviceChangeProperty.get());
        String name = controller.getName();
        nameField.setText(name);
        serviceComboBox.setValue(name);
        // spinners
        DayTime firstLeaveTime = controller.getFirstLeaveTime();
        DayTime boundaryTime = controller.getBoundaryTime();
        fromHourSpinner.getValueFactory().setValue(firstLeaveTime.getHours());
        fromMinuteSpinner.getValueFactory().setValue(firstLeaveTime.getMinutes());
        toHourSpinner.getValueFactory().setValue(boundaryTime.getHours());
        toMinuteSpinner.getValueFactory().setValue(boundaryTime.getMinutes());
        timeGapSpinner.getValueFactory().setValue(controller.getTimeGap());
    }

    private void applyChanges() {
        try {
            controller.applyChanges();
            // if the name was changed, that the service chooser combo box must change too
            String newName = controller.getName();
            String oldName = serviceComboBox.getValue();
            System.out.println("old name: " + oldName + "\tnew name: " + newName);
            if (!oldName.equals(newName)) {
                serviceComboBox.getItems().add(newName);
                serviceComboBox.setValue(newName);
                serviceComboBox.getItems().remove(oldName);
            }
        } catch (NameConflictException nce) {
            // set the background of the name text field to red for 3 seconds
            nameField.getStyleClass().add("error-field");
            Task<Void> sleeper = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ie) {
                    }

                    return null;
                }
            };
            sleeper.setOnSucceeded(event -> {
                if (nameField.getStyleClass().contains("error-field"))
                    nameField.getStyleClass().remove("error-field");
                System.err.println("lefutott");
            });

            Thread thd = new Thread(sleeper);
            thd.setDaemon(true);
            thd.start();
        }
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

    private HBox createLeaveTimeRow(DayTime time, String type) {
        SpinnerValueFactory<Integer> hourFactory = new SpinnerValueFactory.
                IntegerSpinnerValueFactory(0, 23, time.getHours());
        Spinner<Integer> hourSpinner = createSpinner(0, 23, time.getHours());
        Spinner<Integer> minuteSpinner = createSpinner(0, 59, time.getMinutes());
        if (type.equals("from")) {
            fromHourSpinner = hourSpinner;
            fromMinuteSpinner = minuteSpinner;
        } else {
            toHourSpinner = hourSpinner;
            toMinuteSpinner = minuteSpinner;
        }

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

    public void addServiceChangeProperty(BooleanProperty changeProperty) {
        if (changeProperty == null)
            throw new NullPointerException("service change property must not be null");
        serviceChangeProperty = changeProperty;
    }

    public void addTimetableHandler(EventHandler<ActionEvent> handler) {
        if (handler == null)
            throw new NullPointerException("timetable handler can not be null");
        timetableButton.setOnAction(handler);
    }

}//class
