package com.github.joey11111000111.EasyPlan.gui;

import com.github.joey11111000111.EasyPlan.core.BusStop;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * Created by joey on 2015.12.06..
 */
public class DrawStack {

    private static final int ROW_COL_NUM = 10;
    private DoubleProperty widthProperty;
    private DoubleProperty heightProperty;
    private DoubleProperty cellWidth;
    private DoubleProperty cellHeight;

    private Group root;
    private Group background;
    private Group lines;
    private Group stops;           // includes the bus station

    private MarkableShape[] allStops;

    public DrawStack(ReadOnlyDoubleProperty widthProperty,
                     ReadOnlyDoubleProperty heightProperty) {
        if (widthProperty == null || heightProperty == null)
            throw new NullPointerException();
        // save sizes
        this.widthProperty = new SimpleDoubleProperty();
        this.widthProperty.bind(widthProperty);
        this.heightProperty = new SimpleDoubleProperty();
        this.heightProperty.bind(heightProperty);

        // init cell sizes
        cellWidth = new SimpleDoubleProperty();
        cellWidth.bind(widthProperty.divide(ROW_COL_NUM));
        cellHeight = new SimpleDoubleProperty();
        cellHeight.bind(heightProperty.divide(ROW_COL_NUM));

        // init containers
        root = new Group();
        background = new Group();
        lines = new Group();
        stops = new Group();

        // load image and create background in a different thread
        createBackground();

        // init and fill allStops
        allStops = new MarkableShape[BusStop.getStopCount() + 1];   // the bus station is the +1

        root.getChildren().addAll(background);

    }

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
        bkgImageView.setOpacity(.1);
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

    private BusStationShape createStationShape() {
        BusStationShape bss = new BusStationShape();
        // bind to the specified coordinates

    }

    // public methods -------------
    public Group getRoot() {
        return root;
    }

}//class
