package com.github.joey11111000111.EasyPlan.gui;

import com.github.joey11111000111.EasyPlan.core.BusStop;
import com.github.joey11111000111.EasyPlan.core.Core;
import javafx.animation.FadeTransition;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joey on 2015.12.06..
 */
public class DrawStack {

    private static final int ROW_COL_NUM = 10;
    private DoubleProperty widthProperty;
    private DoubleProperty heightProperty;
    private DoubleProperty cellWidth;
    private DoubleProperty cellHeight;

    private int padding = 10;
    private Group root;
    private Group background;
    private Group lines;
    private Group directions;
    private Group stops;           // includes the bus station

    private MarkableShape[] allStops;
    private List<Line> pathLines;
    private List<Line> directionLines;
    private Core controller;
    
    // Colors
    private static Color PATH_START = Color.rgb(254, 214, 0, .9);
    private static Color PATH_END = Color.rgb(245, 222, 179, .9);

    public DrawStack(ReadOnlyDoubleProperty widthProperty,
                     ReadOnlyDoubleProperty heightProperty) {
        if (widthProperty == null || heightProperty == null)
            throw new NullPointerException();

        controller = Start.getController();
        pathLines = new ArrayList<>();
        directionLines = new ArrayList<>();

        // save sizes
        this.widthProperty = new SimpleDoubleProperty();
        this.widthProperty.bind(widthProperty);
        this.heightProperty = new SimpleDoubleProperty();
        this.heightProperty.bind(heightProperty);

        // init cell sizes
        cellWidth = new SimpleDoubleProperty();
        cellWidth.bind(widthProperty.subtract(padding * 2).divide(ROW_COL_NUM));
        cellHeight = new SimpleDoubleProperty();
        cellHeight.bind(heightProperty.subtract(padding * 2).divide(ROW_COL_NUM));

        // init containers
        root = new Group();
        background = new Group();
        lines = new Group();
        directions = new Group();
        stops = new Group();

        // load image and create background in a different thread
        createBackground();

        // init and fill allStops
        BusStopShape.radiusProperty().bind(widthProperty.divide(45)
                .add(heightProperty.divide(40)));
        allStops = new MarkableShape[BusStop.getStopCount() + 1];   // the bus station is the +1
        for (int i = 0; i < allStops.length; i++) {
            allStops[i] = createStopShape(i);
            stops.getChildren().add(allStops[i].getRoot());
        }

        showServiceData();

        root.getChildren().addAll(background, lines, directions, stops);
    }//constructor

    private void createBackground() {
        // create and add dark brown rectangle
        Rectangle bkgRect = new Rectangle();
        bkgRect.widthProperty().bind(widthProperty);
        bkgRect.heightProperty().bind(heightProperty);
        bkgRect.arcWidthProperty().bind(widthProperty.divide(15));
        bkgRect.arcHeightProperty().bind(heightProperty.divide(15));
        bkgRect.setFill(Color.rgb(30, 30, 0));
        background.getChildren().add(bkgRect);

        // load and add background image, if there is one
        Image bkgImage = loadImage();
        if (bkgImage == null) {
            System.out.println("null lett");
            return;
        }
        ImageView bkgImageView = new ImageView(bkgImage);
        bkgImageView.fitWidthProperty().bind(widthProperty);
        bkgImageView.fitHeightProperty().bind(heightProperty);
        bkgImageView.setOpacity(.2);
        background.getChildren().add(bkgImageView);
    }

    private Image loadImage() {
        InputStream is = DrawStack.class.getClassLoader().getResourceAsStream("background.jpg");
        if (is == null) {
            Start.LOGGER.warn("cannot read background image");
            return null;
        }

        Image bkgImage = new Image(is);
        return bkgImage;
    }

    private MarkableShape createStopShape(int id) {
        MarkableShape shape;
        int stationId = BusStop.getIdOfStation();
        if (id == stationId)
            shape = new BusStationShape();
        else
            shape = new BusStopShape(id);
        // bind to the specified coordinates
        Group stop = shape.getRoot();
        int x = BusStop.getXCoordOf(id);
        int y = BusStop.getYCoordOf(id);
        System.out.println("x: " + x + "\ty: " + y);
        DoubleProperty shapeXProperty = new SimpleDoubleProperty();
        DoubleProperty shapeYProperty = new SimpleDoubleProperty();
        if (id == stationId) {
            shapeXProperty.bind(cellWidth.multiply(x).subtract(((BusStationShape)shape)
                    .widthProperty().divide(2))
                    .add(padding));
            shapeYProperty.bind(cellHeight.multiply(y).subtract(((BusStationShape)shape)
                    .heightProperty().divide(2))
                    .add(padding));
        } else {
            shapeXProperty.bind(cellWidth.multiply(x)
                    .add(cellWidth.divide(2).subtract(BusStopShape.radiusProperty()))
                    .add(padding));
            shapeYProperty.bind(cellHeight.multiply(y)
                    .add(cellHeight.divide(2).subtract(BusStopShape.radiusProperty()))
                    .add(padding));
        }

        stop.translateXProperty().bind(shapeXProperty);
        stop.translateYProperty().bind(shapeYProperty);

        return shape;
    }

    public void showServiceData() {
//        if (!controller.hasSelectedService())
//            return;
        controller.createNewService();
        controller.appendStop(1);
        controller.appendStop(4);
        controller.appendStop(6);
        controller.appendStop(9);
        controller.appendStop(12);
        controller.appendStop(15);
        controller.appendStop(10);
        controller.appendStop(5);
        controller.appendStop(3);
        controller.appendStop(2);
        controller.appendStop(3);
        controller.appendStop(2);
        controller.appendStop(5);
        controller.appendStop(10);
        controller.appendStop(9);
        controller.appendStop(8);
        controller.appendStop(13);
        controller.appendStop(11);
        controller.appendStop(7);
        controller.appendStop(14);
        controller.appendStop(11);
        controller.appendStop(8);
        controller.appendStop(6);
        controller.appendStop(4);
        controller.appendStop(1);
        controller.appendStop(7);
        controller.appendStop(14);
        controller.appendStop(13);
        controller.appendStop(12);
        controller.appendStop(15);

        clear();
        markStops();
        showPath();
        // create path - lines
    }

    private void clear() {
        lines = new Group();
        pathLines.clear();
        directionLines.clear();
        for (MarkableShape shape : allStops)
            shape.markNeutral();
    }
    
    private void markStops() {
        int[] reachableIds = controller.getReachableStopIds();
        for (int id : reachableIds)
            allStops[id].markReachable();
        if (!controller.hasStops()) {
            allStops[0].markCurrent();
            return;
        }

        int[] stops = controller.getStops();
        allStops[stops[stops.length - 1]].markCurrent();
    }
    
    private void showPath() {
        int[] serviceStops = controller.getStops();
        for (int i = 0; i < serviceStops.length - 1; i++) {
            int fromX = BusStop.getXCoordOf(serviceStops[i]);
            int toX = BusStop.getXCoordOf(serviceStops[i+1]);
            int fromY = BusStop.getYCoordOf(serviceStops[i]);
            int toY = BusStop.getYCoordOf(serviceStops[i+1]);
            Line path = new Line();
            Line direction = new Line();
            // bind starting end ending coordinates
            path.startXProperty().bind(cellWidth.multiply(fromX).add(cellWidth.divide(2)).add(padding));
            path.startYProperty().bind(cellHeight.multiply(fromY).add(cellHeight.divide(2)).add(padding));
            direction.startXProperty().bind(cellWidth.multiply(fromX).add(cellWidth.divide(2)).add(padding));
            direction.startYProperty().bind(cellHeight.multiply(fromY).add(cellHeight.divide(2)).add(padding));
            
            path.endXProperty().bind(cellWidth.multiply(toX).add(cellWidth.divide(2)).add(padding));
            path.endYProperty().bind(cellHeight.multiply(toY).add(cellHeight.divide(2)).add(padding));
            direction.endXProperty().bind(cellWidth.multiply(toX).add(cellWidth.divide(2)).add(padding));
            direction.endYProperty().bind(cellHeight.multiply(toY).add(cellHeight.divide(2)).add(padding));
            // create and set the gradient for the path line
            LinearGradient pathGradient = new LinearGradient(
                    path.getStartX(), path.getStartY(),
                    path.getEndX(), path.getEndY(),
                    false,
                    CycleMethod.NO_CYCLE,
                    new Stop(0, PATH_START),
                    new Stop(2, PATH_END)
            );
            path.strokeWidthProperty().bind(BusStopShape.radiusProperty().divide(10));
            path.setOpacity(0);
            path.setStroke(pathGradient);
            // create and set the gradient for the direction line
            LinearGradient directionGradient = new LinearGradient(
                    path.getStartX(), path.getStartY(),
                    path.getEndX(), path.getEndY(),
                    false,
                    CycleMethod.NO_CYCLE,
                    new Stop(.4, Color.BLACK),
                    new Stop(1, Color.TRANSPARENT)
            );
            direction.setOpacity(0);
            direction.strokeWidthProperty().bind(path.strokeWidthProperty().multiply(1.3));
            direction.setStroke(directionGradient);
            directions.getChildren().add(direction);

            lines.getChildren().add(path);
        }

        Task fadeInTask = new Task() {
            @Override
            protected Object call() throws Exception {
                for (int i = 0; i < lines.getChildren().size(); i++) {
                    FadeTransition fadeInPath = new FadeTransition(Duration.millis(800));
                    fadeInPath.setNode(lines.getChildren().get(i));
                    fadeInPath.setFromValue(0);
                    fadeInPath.setToValue(1);

                    FadeTransition fadeInDirection = new FadeTransition(Duration.millis(800));
                    fadeInDirection.setNode(directions.getChildren().get(i));
                    fadeInDirection.setFromValue(0);
                    fadeInDirection.setToValue(.65);

                    fadeInPath.play();
                    fadeInDirection.play();
                    Thread.sleep(300);
                }
                return true;
            }
        };
        new Thread(fadeInTask).start();
    }
    

    public Group getRoot() {
        return root;
    }



}//class
