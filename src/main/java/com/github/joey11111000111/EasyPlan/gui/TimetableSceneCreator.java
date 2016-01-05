package com.github.joey11111000111.EasyPlan.gui;

import com.github.joey11111000111.EasyPlan.core.Core;
import com.github.joey11111000111.EasyPlan.core.Timetable;
import com.github.joey11111000111.EasyPlan.util.DayTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

import static com.github.joey11111000111.EasyPlan.core.Timetable.StopTimes;

/**
 * Created by joey on 2016.01.02..
 */
public class TimetableSceneCreator {

    private static Core controller;
    private static ScrollPane table;
    private static String css;

    static {
        css = TimetableSceneCreator.class.getClassLoader().getResource("timetable.css").toExternalForm();
        // TEMP
        System.out.println("font family names");
        for (String name : Font.getFamilies())
            System.out.println(name);
        // TEMP
    }

    public static Scene createScene(EventHandler<ActionEvent> backButtonAction) {
        if (controller == null)
            return null;

        HBox root = new HBox(5);
        root.setBackground(new Background(new BackgroundFill(Color.rgb(30, 30, 0), null, null)));
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
        namesView.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
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
//        selectionChanged(controller.getServiceNames()[0]);

        return scene;
    }

    private static void selectionChanged(String serviceName) {
        Timetable timetable = controller.getTimetableOf(serviceName);
        HBox allColumns = new HBox(10);
        for (int i = 0; i < timetable.stopTimes.size(); i++) {
            VBox column = new VBox(2);
            column.setAlignment(Pos.TOP_CENTER);

            // data for the current column
            StopTimes stopTimes = timetable.stopTimes.get(i);
            // create title
            Text colTitle = new Text(Integer.toString(stopTimes.id));
            colTitle.getStyleClass().add("column-title");
            column.getChildren().add(colTitle);

            // add all stop times
            for (DayTime time : stopTimes.times) {
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
        Text nameText = new Text("service name: " + serviceName);
        Text travelTimeText = new Text("total travel time: " + timetable.totalTravelTime);
        Text busCountText = new Text("bus count: " + Integer.toString(timetable.busCount));
        nameText.getStyleClass().add("info-text");
        travelTimeText.getStyleClass().add("info-text");
        busCountText.getStyleClass().add("info-text");

        infoCol.getChildren().addAll(infoTitle, nameText, travelTimeText, busCountText);
        allColumns.getChildren().add(infoCol);

        table.setContent(allColumns);

    }//selectionChanged


    public static void setController(Core controller) {
        TimetableSceneCreator.controller = controller;
    }

}//class
