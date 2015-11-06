package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.core.util.OpenLinkedList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joey on 2015.11.01..
 */
public class BusService implements Serializable {

    private List<Integer> savedStops;
    private boolean closed;
    private String name;
    private int timeGap;
    private SimpleTime firstLeaveTime;
    private SimpleTime boundaryTime;

    transient private TouchedStops currentStops;
    transient private BasicServiceData currentServiceData;

    public BusService() {
        savedStops = new ArrayList<Integer>();
        closed = false;
        name = "new service";
        timeGap = 10;
        firstLeaveTime = new SimpleTime(8, 0);
        boundaryTime = new SimpleTime(18, 0);
        initCurrentStops();
        initCurrentServiceData();
    }

    void initCurrentServiceData() {
        currentServiceData = new BasicServiceData(name, timeGap, firstLeaveTime, boundaryTime);
    }

    void initCurrentStops() {
        currentStops = new TouchedStops();
        if (savedStops.isEmpty())
            return;
        for (Integer i : savedStops)
            currentStops.appendStop(i);
        if (closed)
            currentStops.closeService();
        currentStops.markAsSaved();
    }

    private void applyStops() {
        savedStops.clear();
        int[] stops = currentStops.getStops();
        for (int i = 0; i < stops.length; i++)
            savedStops.add(stops[i]);
        closed = currentStops.isClosed();
    }

    private void applyServiceData() {
        name = currentServiceData.getName();
        timeGap = currentServiceData.getTimeGap();
        firstLeaveTime = new SimpleTime(currentServiceData.getFirstLeaveTime());
        boundaryTime = new SimpleTime(currentServiceData.getBoundaryTime());
    }

    public void applyChanges() {
        if (currentStops.isModified())
            applyStops();
        if (currentServiceData.isModified())
            applyServiceData();
        currentStops.markAsSaved();
        currentServiceData.markAsSaved();
    }

    // getter methods
    public TouchedStops getCurrentStops() {
        return currentStops;
    }
    public BasicServiceData getCurrentServiceData() {
        return currentServiceData;
    }

}//class
