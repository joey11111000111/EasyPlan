package com.github.joey11111000111.EasyPlan.gui;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

// CHECKSTYLE:OFF
public class BusStationShape implements MarkableShape {

    private final LinearGradient NEUTRAL;
    private final LinearGradient REACHABLE;
    private final LinearGradient CURRENT;
    private LinearGradient INDICATOR_COLOR;
    private DoubleProperty widthProperty;
    private DoubleProperty heightProperty;
    private Group root;
    private Rectangle bkgRect;


    public BusStationShape() {
        widthProperty = new SimpleDoubleProperty();
        widthProperty.bind(BusStopShape.radiusProperty().multiply(4));
        heightProperty = new SimpleDoubleProperty();
        heightProperty.bind(BusStopShape.radiusProperty().multiply(4.5));
        // init gradients
        double width = widthProperty.get();
        double height = heightProperty.get();
        NEUTRAL = new LinearGradient(
                width / 2, 0,
                width / 2, height,
                false,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(220, 220, 220, .8)),
                new Stop(1, Color.rgb(100, 100, 255, .8))
        );
        REACHABLE = new LinearGradient(
                width / 2, 0,
                width / 2, height,
                false,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(0, 200, 0, .8)),
                new Stop(1, Color.rgb(0, 200, 100, .8))
        );
        CURRENT = new LinearGradient(
                width / 2, 0,
                width / 2, height,
                false,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(242, 222, 100, .8)),
                new Stop(1, Color.rgb(254, 214, 0, .8))
        );
        INDICATOR_COLOR = NEUTRAL;


        root = new Group();
        // create and add background with neutral colors
        bkgRect = new Rectangle();
        bkgRect.setFill(INDICATOR_COLOR);
        bkgRect.widthProperty().bind(widthProperty);
        bkgRect.heightProperty().bind(heightProperty);
        bkgRect.arcWidthProperty().bind(widthProperty.divide(5));
        bkgRect.arcHeightProperty().bind(heightProperty.divide(5));
        root.getChildren().add(bkgRect);

        // create and add inner rectangles
        DoubleBinding xStep = widthProperty.divide(4);
        DoubleBinding yStep = heightProperty.divide(2);
        int xPadding = 4;
        int yPadding = 6;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle rect = createInnerRect(widthProperty, heightProperty, xPadding, yPadding);
                rect.translateXProperty().bind(xStep.multiply(j).add(xPadding));
                rect.translateYProperty().bind(yStep.multiply(i).add(yPadding));
                root.getChildren().add(rect);
            }
        }
    }//constructor

    private Rectangle createInnerRect(ReadOnlyDoubleProperty widthProperty,
                                      ReadOnlyDoubleProperty heightProperty, int xPadding, int yPadding) {
        Rectangle rect = new Rectangle();
        rect.widthProperty().bind(widthProperty.divide(4).subtract(xPadding * 2));
        rect.heightProperty().bind(heightProperty.divide(2).subtract(yPadding * 2));
        rect.arcWidthProperty().bind(rect.widthProperty().divide(4));
        rect.arcHeightProperty().bind(rect.heightProperty().divide(4));
        rect.setFill(Color.TRANSPARENT);
        rect.setStrokeWidth(2);
        rect.setStroke(Color.BLACK);
        return rect;
    }

    // public methods ---------------------
    public <T extends Event> void addEventHandler(EventType<T> type,
                                                  EventHandler<? super Event> handler) {
        if (type == null || handler == null)
            throw new NullPointerException();
        root.addEventHandler(type, handler);
    }

    public ReadOnlyDoubleProperty widthProperty() {
        return DoubleProperty.readOnlyDoubleProperty(widthProperty);
    }
    public ReadOnlyDoubleProperty heightProperty() {
        return DoubleProperty.readOnlyDoubleProperty(heightProperty);
    }

    @Override
    public Group getRoot() {
        return root;
    }

    @Override
    public void markNeutral() {
        if (INDICATOR_COLOR == NEUTRAL)
            return;
        INDICATOR_COLOR = NEUTRAL;
        bkgRect.setFill(INDICATOR_COLOR);
    }

    @Override
    public void markReachable() {
        if (INDICATOR_COLOR == REACHABLE)
            return;
        INDICATOR_COLOR = REACHABLE;
        bkgRect.setFill(INDICATOR_COLOR);
    }

    @Override
    public void markCurrent() {
        if (INDICATOR_COLOR == CURRENT)
            return;
        INDICATOR_COLOR = CURRENT;
        bkgRect.setFill(INDICATOR_COLOR);
    }
}//class
