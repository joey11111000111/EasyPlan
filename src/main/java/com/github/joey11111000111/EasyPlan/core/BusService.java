package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.core.util.DayTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joey on 2015.11.01..
 */
public class BusService implements Serializable {

    public static class NotSelectedException extends RuntimeException {
        public NotSelectedException(String message) {
            super(message);
        }
    }

    static final long serialVersionUID = 0L;

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
        initTransientFields();
    }

    void initTransientFields() {
        // init CurrentServiceDate
        currentServiceData = new BasicServiceData(name, timeGap, firstLeaveTime, boundaryTime);
        // init currentStops
        currentStops = new TouchedStops();
        if (savedStops.isEmpty())
            return;
        for (Integer i : savedStops)
            currentStops.appendStop(i);
        if (closed)
            currentStops.closeService();
        currentStops.markAsSaved();
    }

    public String getAppliedName() {
        return name;
    }

    public boolean discardChanges() {
        boolean restoreHappened = false;
        // discard basic data changes, if there were any
        if (currentServiceData.isModified()) {
            currentServiceData.setName(name);
            currentServiceData.setTimeGap(timeGap);
            currentServiceData.setFirstLeaveTime(firstLeaveTime);
            currentServiceData.setBoundaryTime(boundaryTime);
            currentServiceData.markAsSaved();
            restoreHappened = true;
        }
        // discard stop changes, if there were any
        if (currentStops.isModified()) {
            currentStops.clear();
            for (int i : savedStops)
                currentStops.appendStop(i);
            if (closed)
                currentStops.closeService();
            currentStops.markAsSaved();
            if (!restoreHappened)
                restoreHappened = true;
        }
        return restoreHappened;
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

    public Timetable getTimeTable() {
        Timetable.TimeTableArguments args = new Timetable.TimeTableArguments();
        args.setName(currentServiceData.getName());
        args.setStopIds(currentStops.getStops());
        args.setTravelTimes(currentStops.getTravelTimes());
        args.setTimeGap(currentServiceData.getTimeGap());
        args.setFirstLeaveTime(currentServiceData.getFirstLeaveTime());
        args.setBoundaryTime(currentServiceData.getBoundaryTime());

        return Timetable.newInstance(args);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BusService service = (BusService) o;

        return name.equals(service.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}//class
