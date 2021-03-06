package com.github.joey11111000111.EasyPlan.gui;

import com.github.joey11111000111.EasyPlan.core.BusStop;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
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
import java.util.Arrays;

import static com.github.joey11111000111.EasyPlan.gui.Start.LOGGER;
import static com.github.joey11111000111.EasyPlan.gui.Start.controller;

// CHECKSTYLE:OFF
public class DrawStack {

    private static final int ROW_COL_NUM = 10;
    private DoubleProperty widthProperty;
    private DoubleProperty heightProperty;
    private DoubleProperty cellWidth;
    private DoubleProperty cellHeight;
    private StringProperty stopsStringProperty;
    private BooleanProperty serviceChangeProperty;

    private int padding = 10;
    private Group root;
    private Group background;
    private Group lines;
    private Group directions;
    private Group stops;           // includes the bus station

    private MarkableShape[] allStops;
    private boolean animating;
    private Thread fadeInAllThread;
    
    // Colors
    private static Color PATH_START = Color.rgb(254, 214, 0, .9);
    private static Color PATH_END = Color.rgb(245, 222, 179, .9);

    public DrawStack(ReadOnlyDoubleProperty widthProperty,
                     ReadOnlyDoubleProperty heightProperty) {
        if (widthProperty == null || heightProperty == null)
            throw new NullPointerException();

        animating = false;

        // save sizes
        this.widthProperty = new SimpleDoubleProperty();
        this.widthProperty.bind(widthProperty);
        this.heightProperty = new SimpleDoubleProperty();
        this.heightProperty.bind(heightProperty);

        stopsStringProperty = new SimpleStringProperty();
        serviceChangeProperty = new SimpleBooleanProperty();
        serviceChangeProperty.addListener((observable, oldValue, newValue) -> showServiceData());

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
        // commit undo if the middle mouse button is pressed
        root.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.MIDDLE && !animating && controller.canUndo())
                undo();
        });

        // load image and create background in a different thread
        createBackground();

        // bind the size of the illustration shapes to the size of the root container
        BusStopShape.radiusProperty().bind(widthProperty.divide(45)
                .add(heightProperty.divide(40)));
        // init and fill allStops
        allStops = new MarkableShape[BusStop.getStopCount()];
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

        // use the background image if there is one, otherwise stick with only the rectangle
        Image bkgImage = loadImage();
        if (bkgImage == null)
            return;

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
        if (id == 0)
            shape = new BusStationShape();
        else
            shape = new BusStopShape(id);
        // bind to the specified coordinates
        Group stop = shape.getRoot();
        int x = BusStop.getXCoordOf(id);
        int y = BusStop.getYCoordOf(id);
        DoubleBinding shapeXProperty;
        DoubleBinding shapeYProperty;
        // create coordinate binding for the bus station
        if (id == 0) {
            shapeXProperty = new DoubleBinding() {
                {
                    super.bind(cellWidth, ((BusStationShape)(shape)).widthProperty());
                }
                @Override
                protected double computeValue() {
                    double cw = cellWidth.getValue();
                    double stationWidth = ((BusStationShape)(shape)).widthProperty().getValue();
                    return cw * x - stationWidth / 2;
                }
            };
            shapeYProperty = new DoubleBinding() {
                {
                    super.bind(cellWidth, ((BusStationShape)(shape)).heightProperty());
                }
                @Override
                protected double computeValue() {
                    double ch = cellHeight.getValue();
                    double stationHeight = ((BusStationShape)(shape)).heightProperty().getValue();
                    return ch * y - stationHeight / 2;
                }
            };
        } else {    // create coordinate binding for the bus stops
            shapeXProperty = new DoubleBinding() {
                {
                    super.bind(cellWidth, BusStopShape.radiusProperty());
                }
                @Override
                protected double computeValue() {
                    double cw = cellWidth.getValue();
                    double radius = BusStopShape.radiusProperty().getValue();
                    return cw * x + cw / 2 - radius + padding;
                }
            };
            shapeYProperty = new DoubleBinding() {
                {
                    super.bind(cellHeight, BusStopShape.radiusProperty());
                }
                @Override
                protected double computeValue() {
                    double ch = cellHeight.getValue();
                    double radius = BusStopShape.radiusProperty().getValue();
                    return ch * y + ch / 2 - radius + padding;
                }
            };
        }

        // bind the current shape to the previously created coordinates
        stop.translateXProperty().bind(shapeXProperty);
        stop.translateYProperty().bind(shapeYProperty);

        // add event handler
        stop.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event == null) {
                LOGGER.warn("mouse error, event is null");
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
        clear();
        if (!controller.hasSelectedService())
            return;

        markStops();
        showPath();
        refreshStringProperty();
    }

    private void clear() {
        // if there is already an ongoing "fadeInAllLines" animation thread, then interrupt it
        if (fadeInAllThread != null && fadeInAllThread.isAlive())
            fadeInAllThread.interrupt();

        lines.getChildren().clear();
        directions.getChildren().clear();
        Arrays.stream(allStops).forEach(MarkableShape::markNeutral);
    }
    
    private void markStops() {
        int[] reachableIds = controller.getReachableStopIds();
        // mark natural all stops, including the station
        for (MarkableShape shape : allStops)
                shape.markNeutral();

        if (controller.isClosed())
            return;

        // mark new reachables
        for (int id : reachableIds)
            allStops[id].markReachable();

        allStops[controller.getLastStop()].markCurrent();
    }
    
    private void showPath() {
        int[] serviceStops = controller.getStops();
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
        DoubleBinding xBind = new DoubleBinding() {
            {
                super.bind(cellWidth, xProperty);
            }
            @Override
            protected double computeValue() {
                double cw = cellWidth.getValue();
                return cw * xGrid + cw / 2 + padding;
            }
        };
        DoubleBinding yBind = new DoubleBinding() {
            {
                super.bind(cellHeight, yProperty);
            }
            @Override
            protected double computeValue() {
                double ch = cellHeight.getValue();
                return ch * yGrid + ch / 2 + padding;
            }
        };
        xProperty.bind(xBind);
        yProperty.bind(yBind);
    }//bindToGridCenter

    private void bindLineCoordinates(Line path, Line direction, int fromId, int toId) {
        int fromX = BusStop.getXCoordOf(fromId);
        int toX = BusStop.getXCoordOf(toId);
        int fromY = BusStop.getYCoordOf(fromId);
        int toY = BusStop.getYCoordOf(toId);

        if (fromId == 0) {
            bindToStationCenter(path.startXProperty(), path.startYProperty());
            bindToStationCenter(direction.startXProperty(), direction.startYProperty());
        } else {
            bindToGridCenter(path.startXProperty(), path.startYProperty(), fromX, fromY);
            bindToGridCenter(direction.startXProperty(), direction.startYProperty(), fromX, fromY);
        }
        if (toId == 0) {
            bindToStationCenter(path.endXProperty(), path.endYProperty());
            bindToStationCenter(direction.endXProperty(), direction.endYProperty());
        } else {
            bindToGridCenter(path.endXProperty(), path.endYProperty(), toX, toY);
            bindToGridCenter(direction.endXProperty(), direction.endYProperty(), toX, toY);
        }
    }

    private void addLine(int fromId, int toId){
        Line path = new Line();
        Line direction = new Line();
        bindLineCoordinates(path, direction, fromId, toId);

        // opacity is set to 0, so they can appear by a fade in effect
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
            int fromId = controller.getLastStop();
            controller.appendStop(id);
            addLine(fromId, id);
            markStops();
            refreshStringProperty();
            fadeInLastLine();
        } catch (RuntimeException re) {}    // happens when the stop cannot be added, and that case is ignored
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
                try {
                    animating = true;
                    for (int id = 0; id < lines.getChildren().size(); id++) {
                        createFadeInTransition(id).play();
                        Thread.sleep(300);
                    }
                    animating = false;
                    return true;
                } catch (InterruptedException e) {
                    return false;
                }
            }
        };
        fadeInAllThread = new Thread(fadeInTask);
        fadeInAllThread.setDaemon(true);
        fadeInAllThread.start();
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
        if (lines.getChildren().size() < lnCount)
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
        Arrays.stream(fadeTransitions).forEach(FadeTransition::play);

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
        try {
            int stopCountBefore = controller.getStops().length;
            controller.removeChainFrom(id);
            int stopCountAfter = controller.getStops().length;
            int difference = stopCountBefore - stopCountAfter;
            if (difference > 0) {
                refreshStringProperty();
                fadeOutLastLines(difference);
            }
            markStops();
        } catch (RuntimeException re) {}        // if the stop cannot be removed, and that case is ignored
    }
    private void deleteLastLines(int lnCount) {
        int index = lines.getChildren().size() - 1;
        for (int i = 0; i < lnCount; i++) {
            lines.getChildren().remove(index);
            directions.getChildren().remove(index--);
        }
    }

    private void undo() {
        int[] stopsBefore = controller.getStops();
        controller.undo();
        int[] stopsAfter = controller.getStops();
        if (stopsBefore.length - stopsAfter.length != 0)
            refreshStringProperty();
        // if stop(s) was/were added by the undo operation
        if (stopsBefore.length < stopsAfter.length) {
            int start = stopsBefore.length;
            for (int i = start; i < stopsAfter.length; i++) {
                addLine(stopsAfter[i - 1], stopsAfter[i]);
                fadeInLastLine();
            }
            markStops();
        }
        // if a stop was deleted by the undo operation
        else if (stopsBefore.length - stopsAfter.length == 1) {
            fadeOutLastLines(1);
            markStops();
        }
    }

    private void refreshStringProperty() {
        StringBuilder sb = new StringBuilder();
        int[] stops = controller.getStops();
        for (int id = 0; id < stops.length - 1; id++)
            sb.append(stops[id]).append(" > ");
        sb.append(stops[stops.length - 1]);
        stopsStringProperty.set(sb.toString());
    }

    public Group getRoot() {
        return root;
    }

    public ReadOnlyStringProperty stopsStringProperty() {
        return stopsStringProperty;
    }

    public BooleanProperty serviceChangeProperty() {
        return serviceChangeProperty;
    }
}//class
