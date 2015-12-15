package com.github.joey11111000111.EasyPlan.gui;

import com.github.joey11111000111.EasyPlan.core.Core;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
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

/*    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("EasyPlan ~ Bus Service Designer");
        VBox root = new VBox(2);
        root.setPadding(new Insets(5));
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        Scene scene = new Scene(root, 200, 700);

        // aktuális járat jelölő
        Text currentServiceLabel = new Text("Current Service");
        currentServiceLabel.setFont(Font.font("IncisedBlackWide", FontWeight.NORMAL, FontPosture.REGULAR, 20));
        currentServiceLabel.setFill(Color.rgb(150, 150, 230));
        root.getChildren().add(currentServiceLabel);

        // Járat név jelölő és az őt csomagoló hbox
        HBox nameWrapper = new HBox();
        nameWrapper.setBackground(new Background(new BackgroundFill(Color.WHEAT, null, null)));
        currentServiceLabel.set



        primaryStage.setScene(scene);
        primaryStage.show();
    }*/
}//class
