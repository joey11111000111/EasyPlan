package com.github.joey11111000111.EasyPlan.gui;

import com.github.joey11111000111.EasyPlan.core.BusStop;
import com.github.joey11111000111.EasyPlan.core.Core;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
    private Core controller;
    private boolean animating;
    
    // Colors
    private static Color PATH_START = Color.rgb(254, 214, 0, .9);
    private static Color PATH_END = Color.rgb(245, 222, 179, .9);

    public DrawStack(ReadOnlyDoubleProperty widthProperty,
                     ReadOnlyDoubleProperty heightProperty) {
        if (widthProperty == null || heightProperty == null)
            throw new NullPointerException();

        controller = Start.getController();
        animating = false;

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
        root.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.MIDDLE && !animating && controller.canUndo())
                undo();
        });

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

        // add event handler
        stop.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event == null) {
                System.err.println("mouse error");
                return;
            }
            if (event.getButton() == MouseButton.PRIMARY && !controller.isClosed())
                addStop(id);
            else if (event.getButton() == MouseButton.SECONDARY && !animating) {
                removeStop(id);
            }
        });

        return shape;
    }

    public void showServiceData() {
//        if (!controller.hasSelectedService())
//            return;
        controller.createNewService();
        controller.appendStop(1);
        controller.appendStop(4);
        controller.appendStop(6);
//        controller.appendStop(9);
//        controller.appendStop(12);
//        controller.appendStop(15);
//        controller.appendStop(10);
//        controller.appendStop(5);
//        controller.appendStop(3);
//        controller.appendStop(2);
//        controller.appendStop(3);
//        controller.appendStop(2);
//        controller.appendStop(5);
//        controller.appendStop(10);
//        controller.appendStop(9);
//        controller.appendStop(8);
//        controller.appendStop(13);
//        controller.appendStop(11);
//        controller.appendStop(7);
//        controller.appendStop(14);
//        controller.appendStop(11);
//        controller.appendStop(8);
//        controller.appendStop(6);
//        controller.appendStop(4);
//        controller.appendStop(1);
//        controller.appendStop(7);
//        controller.appendStop(14);
//        controller.appendStop(13);
//        controller.appendStop(12);
//        controller.appendStop(15);

        clear();
        markStops();
        showPath();
        // create path - lines
    }

    private void clear() {
        lines.getChildren().clear();
        directions.getChildren().clear();
        for (MarkableShape shape : allStops)
            shape.markNeutral();
    }
    
    private void markStops() {
        int[] reachableIds = controller.getReachableStopIds();
        // unmark previously marked stops
        for (MarkableShape shape : allStops)
                shape.markNeutral();

        // mark new reachables and current
        for (int id : reachableIds)
            allStops[id].markReachable();
        if (!controller.hasStops()) {
            allStops[0].markCurrent();
            return;
        }
        if (controller.isStationReachable())
            allStops[0].markReachable();

        int[] stops = controller.getStops();
        if (!controller.isClosed())
            allStops[stops[stops.length - 1]].markCurrent();
    }
    
    private void showPath() {
        int[] serviceStops = controller.getStops();
        if (serviceStops.length > 0)
            addLine(0, serviceStops[0]);
        for (int i = 0; i < serviceStops.length - 1; i++)
            addLine(serviceStops[i], serviceStops[i + 1]);

        fadeInAllLines();
    }

    private void bindToStationCenter(DoubleProperty xProperty, DoubleProperty yProperty) {
        BusStationShape station = (BusStationShape)allStops[0];
        xProperty.bind(station.getRoot().translateXProperty()
                .add(station.widthProperty().divide(2)));
        yProperty.bind(station.getRoot().translateYProperty()
                .add(station.heightProperty().divide(2)));
    }
    private void bindToGridCenter(DoubleProperty xProperty, DoubleProperty yProperty, int xGrid, int yGrid) {
        xProperty.bind(cellWidth.multiply(xGrid).add(cellWidth.divide(2)).add(padding));
        yProperty.bind(cellHeight.multiply(yGrid).add(cellHeight.divide(2)).add(padding));
    }

    private void bindLineCoordinates(Line path, Line direction, int fromId, int toId) {
        int fromX = BusStop.getXCoordOf(fromId);
        int toX = BusStop.getXCoordOf(toId);
        int fromY = BusStop.getYCoordOf(fromId);
        int toY = BusStop.getYCoordOf(toId);

        if (fromId == 0) {
            bindToStationCenter(path.startXProperty(), path.startYProperty());
            bindToStationCenter(direction.startXProperty(), direction.startYProperty());
            bindToGridCenter(path.endXProperty(), path.endYProperty(), toX, toY);
            bindToGridCenter(direction.endXProperty(), direction.endYProperty(), toX, toY);
            return;
        }
        if (toId == 0) {
            bindToStationCenter(path.endXProperty(), path.endYProperty());
            bindToStationCenter(direction.endXProperty(), direction.endYProperty());
            bindToGridCenter(path.startXProperty(), path.startYProperty(), fromX, fromY);
            bindToGridCenter(direction.startXProperty(), direction.startYProperty(), fromX, fromY);
            System.out.println("startX: " + path.getStartX() + "\tstartY: " + path.getStartY()
                    + "\tendX: " + path.getEndX() + "\tendY: " + path.getEndY());
            return;
        }

        bindToGridCenter(path.startXProperty(), path.startYProperty(), fromX, fromY);
        bindToGridCenter(path.endXProperty(), path.endYProperty(), toX, toY);
        bindToGridCenter(direction.startXProperty(), direction.startYProperty(), fromX, fromY);
        bindToGridCenter(direction.endXProperty(), direction.endYProperty(), toX, toY);
    }

    private void addLine(int fromId, int toId) {
        Line path = new Line();
        Line direction = new Line();
        bindLineCoordinates(path, direction, fromId, toId);

        path.setOpacity(0);
        direction.setOpacity(0);
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
        direction.strokeWidthProperty().bind(path.strokeWidthProperty().multiply(1.3));
        direction.setStroke(directionGradient);

        directions.getChildren().add(direction);
        lines.getChildren().add(path);
    }

    private void addStop(int id) {
        try {
            // if the new stop is the station, than it needs to be handled differently
            if (id == 0) {
                controller.closeService();
                int[] stops = controller.getStops();
                markStops();
                addLine(stops[stops.length - 1], 0);
                fadeInLastLine();
                return;
            }

            controller.appendStop(id);
            int[] stops = controller.getStops();
            markStops();
            int fromId;
            if (stops.length == 1) {
                fromId = 0;
            } else
                fromId = stops[stops.length - 2];
            addLine(fromId, id);
            fadeInLastLine();
        } catch (RuntimeException re) {}
    }

    private void fadeInLastLine() {
        Transition fadeIn = createFadeInTransition(lines.getChildren().size() - 1);
        fadeIn.setOnFinished(event -> animating = false);
        animating = true;
        fadeIn.play();
    }

    private void fadeInAllLines() {
        Task fadeInTask = new Task() {
            @Override
            protected Object call() throws Exception {
                animating = true;
                for (int id = 0; id < lines.getChildren().size(); id++) {
                    createFadeInTransition(id).play();
                    Thread.sleep(300);
                }
                animating = false;
                return true;
            }
        };
        Thread fadeInThread = new Thread(fadeInTask);
        fadeInThread.setDaemon(true);
        fadeInThread.start();
    }

    private Transition createFadeInTransition(int lineIndex) {
        if (lineIndex >= lines.getChildren().size() || lineIndex < 0)
            throw new IndexOutOfBoundsException("there is no line at the index: " + lineIndex);

        Node line = lines.getChildren().get(lineIndex);
        FadeTransition fadeInPath = new FadeTransition(Duration.millis(800));
        fadeInPath.setNode(line);
        fadeInPath.setFromValue(0);
        fadeInPath.setToValue(1);

        line = directions.getChildren().get(lineIndex);
        FadeTransition fadeInDirection = new FadeTransition(Duration.millis(800));
        fadeInDirection.setNode(line);
        fadeInDirection.setFromValue(0);
        fadeInDirection.setToValue(.65);

        return new ParallelTransition(fadeInPath, fadeInDirection);
    }//fadeInLastLine

    private void fadeOutLastLines(int lnCount) {
        ObservableList<Node> pathLineList = lines.getChildren();
        ObservableList<Node> dirLineList = directions.getChildren();
        if (pathLineList.size() < lnCount)
            throw new IllegalArgumentException("there aren't " + lnCount + " lines");

        // create and play fade out animations
        int duration = 800;
        int lastLineIndex = lines.getChildren().size() - 1;
        FadeTransition[] fadeTransitions = new FadeTransition[lnCount * 2];
        for (int i = 0; i < fadeTransitions.length; i += 2) {
            for (int j = i; j < i + 2; j++) {
                fadeTransitions[j] = new FadeTransition(Duration.millis(duration));
                fadeTransitions[j].setFromValue(1);
                fadeTransitions[j].setToValue(0);
            }
            fadeTransitions[i].setNode(lines.getChildren().get(lastLineIndex));
            fadeTransitions[i+1].setNode(directions.getChildren().get(lastLineIndex--));
        }

        fadeTransitions[0].setOnFinished(event -> animating = false);
        animating = true;
        for (FadeTransition fadeOut : fadeTransitions)
            fadeOut.play();

        // delete lines after the animation ends
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> {
            try {
                deleteLastLines(lnCount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread thd = new Thread(sleeper);
        thd.setDaemon(true);
        thd.start();

    }//fadeOutLastLines


    private void removeStop(int id) {
        if (id == 0) {
            if (controller.isClosed())
                return;
            id = controller.getStops()[0];
            removeStop(id);
        }
        try {
            int stopCountBefore = controller.getStops().length;
            int extraLine = controller.isClosed() ? 1 : 0;
            controller.removeChainFrom(id);
            markStops();
            int stopCountAfter = controller.getStops().length;
            fadeOutLastLines(stopCountBefore - stopCountAfter + extraLine);
        } catch (RuntimeException re) {
        }
    }
    private void deleteLastLines(int lnCount) {
        int index = lines.getChildren().size() - 1;
        for (int i = 0; i < lnCount; i++) {
            lines.getChildren().remove(index);
            directions.getChildren().remove(index--);
        }
    }

    private void undo() {
         if (controller.isClosed()) {
             controller.undo();
             fadeOutLastLines(1);
             markStops();
             return;
         }

        int[] stopsBefore = controller.getStops();
        controller.undo();
        int[] stopsAfter = controller.getStops();
        System.out.println("before: " + stopsBefore.length + "\tafter: " + stopsAfter.length);
        // if stop(s) was/were added
        if (stopsBefore.length < stopsAfter.length) {
            // if the line from the bus stop is also missing
            if (stopsBefore.length == 0) {
                addLine(0, stopsAfter[0]);
                fadeInLastLine();
            }
            int start = stopsBefore.length == 0 ? 1 : stopsBefore.length;
            for (int i = start; i < stopsAfter.length; i++) {
                addLine(stopsAfter[i - 1], stopsAfter[i]);
                fadeInLastLine();
            }
            markStops();
        }
        // if a stop was deleted
        else if (stopsBefore.length - stopsAfter.length == 1) {
            fadeOutLastLines(1);
            markStops();
        }
    }
    

    public Group getRoot() {
        return root;
    }



}//class
