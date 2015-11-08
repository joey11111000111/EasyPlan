package com.github.joey11111000111.EasyPlan.core;

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
    private DayTime firstLeaveTime;
    private DayTime boundaryTime;

    transient private TouchedStops currentStops;
    transient private BasicServiceData currentServiceData;

    public BusService() {
        savedStops = new ArrayList<Integer>();
        closed = false;
        name = "new service";
        timeGap = 10;
        firstLeaveTime = new DayTime(8, 0);
        boundaryTime = new DayTime(18, 0);
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
        firstLeaveTime = new DayTime(currentServiceData.getFirstLeaveTime());
        boundaryTime = new DayTime(currentServiceData.getBoundaryTime());
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

    public TimeTable getTimeTable() {
        TimeTable.TimeTableArguments args = new TimeTable.TimeTableArguments();
        args.setName(currentServiceData.getName());
        args.setStopIds(currentStops.getStops());
        args.setTravelTimes(currentStops.getTravelTimes());
        args.setTimeGap(currentServiceData.getTimeGap());
        args.setFirstLeaveTime(currentServiceData.getFirstLeaveTime());
        args.setBoundaryTime(currentServiceData.getBoundaryTime());

        return TimeTable.newInstance(args);
    }

}//class
