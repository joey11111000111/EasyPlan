package com.github.joey11111000111.EasyPlan.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Created by joey on 2015.12.06..
 */
public class BusStopShape implements MarkableShape {

    private static DoubleProperty radiusProperty;
    // Indicator colors
    public static final RadialGradient NEUTRAL_INDICATOR;
    public static final RadialGradient REACHABLE_INDICATOR;
    public static final RadialGradient CURRENT_INDICATOR;
    public static final Color NEUTRAL_TEXT;
    public static final Color REACHABLE_TEXT;
    public static final Color CURRENT_TEXT;

    private Group root;
    private Circle outerCircle;
    private Text idText;

    static {
        radiusProperty = new SimpleDoubleProperty(25);

        NEUTRAL_INDICATOR = new RadialGradient(
                45,
                0.15,
                radiusProperty.get(), radiusProperty.get(),
                120,
                false,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(220, 220, 220, .8)),
                new Stop(1, Color.rgb(50, 50, 255, .8))
        );

        REACHABLE_INDICATOR = new RadialGradient(
                0,
                0.15,
                radiusProperty.get(), radiusProperty.get(),
                120,
                false,
                CycleMethod.NO_CYCLE,
                new Stop(0.3, Color.rgb(0, 230, 0, .8)),
                new Stop(0.1, Color.rgb(100, 230, 0, .8))
        );

        CURRENT_INDICATOR = new RadialGradient(
                90,
                0.15,
                radiusProperty.get(), radiusProperty.get(),
                120,
                false,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(254, 214, 0, .8)),
                new Stop(1, Color.rgb(100, 254, 0, .8))
        );

        NEUTRAL_TEXT = Color.rgb(150, 150, 230);
        REACHABLE_TEXT = Color.LIGHTGREEN;
        CURRENT_TEXT = Color.WHEAT;
    }//static

    public static DoubleProperty radiusProperty() {
        return radiusProperty;
    }


    public BusStopShape(int id) {
        // create outer circle (indicator circle), bind and set fill to neutral
        outerCircle = new Circle();
        outerCircle.radiusProperty().bind(radiusProperty);
        outerCircle.centerXProperty().bind(radiusProperty);
        outerCircle.centerYProperty().bind(radiusProperty);
        outerCircle.setFill(NEUTRAL_INDICATOR);

        // create middle circle and bind
        Circle middleCircle = new Circle();
        middleCircle.radiusProperty().bind(radiusProperty.multiply(0.8));
        middleCircle.centerXProperty().bind(radiusProperty);
        middleCircle.centerYProperty().bind(radiusProperty);
        middleCircle.setFill(Color.rgb(102, 51, 0));

        // create inner circle and bind
        Circle innerCircle = new Circle();
        innerCircle.radiusProperty().bind(radiusProperty.multiply(0.5));
        innerCircle.centerXProperty().bind(radiusProperty);
        innerCircle.centerYProperty().bind(radiusProperty);
        innerCircle.setFill(Color.BLACK);

        // create text, bind and set fill to neutral
        idText = new Text(Integer.toString(id));
        idText.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR, radiusProperty.get() * 0.6));

        radiusProperty.addListener((observable, oldValue, newValue) -> {
            idText.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR, newValue.doubleValue() * 0.6));
        });
        idText.setFill(NEUTRAL_TEXT);
        HBox textContainer = new HBox();
        textContainer.getChildren().add(idText);
        textContainer.setAlignment(Pos.CENTER);
        textContainer.layoutXProperty()
                .bind(radiusProperty.subtract(textContainer.widthProperty().divide(2)));
        textContainer.layoutYProperty()
                .bind(radiusProperty.subtract(textContainer.heightProperty().divide(2)));

        root = new Group();
        root.getChildren().addAll(outerCircle, middleCircle, innerCircle, textContainer);
    }//constructor


    public <T extends Event> void addEventHandler(EventType<T> type,
                                                  EventHandler<? super Event> handler) {
        if (type == null || handler == null)
            throw new NullPointerException();
        root.addEventHandler(type, handler);
    }

    @Override
    public Group getRoot() {
        return root;
    }

    @Override
    public void markNeutral() {
        outerCircle.setFill(NEUTRAL_INDICATOR);
        idText.setFill(NEUTRAL_TEXT);
    }
    @Override
    public void markReachable() {
        outerCircle.setFill(REACHABLE_INDICATOR);
        idText.setFill(REACHABLE_TEXT);
    }
    @Override
    public void markCurrent() {
        outerCircle.setFill(CURRENT_INDICATOR);
        idText.setFill(CURRENT_TEXT);
    }

}//class
