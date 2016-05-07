package com.github.joey11111000111.EasyPlan.gui;

import com.github.joey11111000111.EasyPlan.core.iTimetable;
import com.github.joey11111000111.EasyPlan.util.DayTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import static com.github.joey11111000111.EasyPlan.core.iTimetable.iStopTimes;
import static com.github.joey11111000111.EasyPlan.gui.Start.controller;

/**
 * Created by joey on 2016.01.02..
 */
public class TimetableSceneCreator {

    private static ScrollPane table;
    private static String css;

    static {
        try {
            css = TimetableSceneCreator.class.getClassLoader().getResource("timetable.css").toExternalForm();
        } catch (Throwable t) {
            Start.LOGGER.warn("could not read css file for the timetable scene");
        }
    }

    public static Scene createScene(EventHandler<ActionEvent> backButtonAction) {
        if (controller == null)
            return null;

        HBox root = new HBox(5);
        root.getStyleClass().add("timetable-root");
        Scene scene = new Scene(root, Color.BLUE);
        if (css != null)
            scene.getStylesheets().add(css);

        // initialize the root pane of the table
        table = new ScrollPane();

        // get the applied service names and store them in a list
        ObservableList<String> serviceNames = FXCollections.observableArrayList();
        serviceNames.addAll(controller.getServiceNames());

        // create and wire up the list view
        ListView<String> namesView = new ListView<>(serviceNames);
        namesView.setMinWidth(200.0);
        namesView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selectionChanged(newValue));

        // create the "back" button
        Button backButton = new Button(">");
        HBox.setHgrow(backButton, Priority.NEVER);
        backButton.setMinWidth(50.0);
        backButton.setMaxWidth(50.0);
        backButton.prefHeightProperty().bind(scene.heightProperty());
        backButton.setOnAction(backButtonAction);

        root.getChildren().addAll(table, namesView, backButton);
        HBox.setHgrow(table, Priority.ALWAYS);

        // create the table for the first service in the list
        namesView.getSelectionModel().select(0);

        return scene;
    }//createScene

    private static void selectionChanged(String serviceName) {
        iTimetable timetable = controller.getTimetableOf(serviceName);
        HBox allColumns = new HBox(10);

        // create, fill and add all the stop columns
        for (int i = 0; i < timetable.getStopTimes().size(); i++) {
            VBox column = new VBox(2);
            column.setAlignment(Pos.TOP_CENTER);

            // data for the current column
            iStopTimes stopTimes = timetable.getStopTimes().get(i);
            // create and add title
            Text colTitle = new Text(stopTimes.getID());
            colTitle.getStyleClass().add("column-title");
            column.getChildren().add(colTitle);

            // add all stop times
            for (DayTime time : stopTimes.getTimes()) {
                Text timeText = new Text(time.toString());
                timeText.getStyleClass().add("time-text");
                column.getChildren().add(timeText);
            }

            // add the freshly created column to the hbox
            allColumns.getChildren().add(column);
        }//for

        // create the last column, that gives other info to the user
        VBox infoCol = new VBox(2);
        infoCol.setAlignment(Pos.TOP_LEFT);
        Text infoTitle = new Text("Info");
        infoTitle.getStyleClass().add("column-title");
        Text nameText = new Text("Service name: " + serviceName);
        Text travelTimeText = new Text("Total travel time: " + timetable.getTotalTravelTime());
        Text busCountText = new Text("Bus count: " + Integer.toString(timetable.getBusCount()));
        nameText.getStyleClass().add("info-text");
        travelTimeText.getStyleClass().add("info-text");
        busCountText.getStyleClass().add("info-text");

        infoCol.getChildren().addAll(infoTitle, nameText, travelTimeText, busCountText);
        allColumns.getChildren().add(infoCol);

        table.setContent(allColumns);
    }//selectionChanged


}//class
