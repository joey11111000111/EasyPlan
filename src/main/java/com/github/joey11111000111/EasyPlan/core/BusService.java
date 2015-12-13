package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.util.DayTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The BusService class manages data associated with a certain bus service.
 * It holds the data in two separate forms simultaneously, which allows new
 * modifications to be discarded.
 */
public class BusService implements Serializable {

    static final long serialVersionUID = 0L;
    /**
     * The name of a newly created, unmodified bus service
     */
    static final String DEFAULT_NAME = "new service";

    private List<Integer> savedStops;
    private String name;
    private int timeGap;
    private DayTime firstLeaveTime;
    private DayTime boundaryTime;

    transient private TouchedStops currentStops;
    transient private BasicServiceData currentServiceData;

    /**
     * Creates a bus service filled with default values. These values are:
     *   - default name
     *   - empty stop list
     *   - 10 minutes time gap
     *   - first leaves the station at 08:00
     *   - no bus leaves after 18:00
     */
    public BusService() {
        savedStops = new ArrayList<Integer>();
        name = DEFAULT_NAME;
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
        for (int i = 1; i < savedStops.size(); i++)
            currentStops.appendStop(savedStops.get(i));
        currentStops.markAsSaved();
    }

    /**
     * Returns the applied name of the service. The applied name is not the newly
     * modified name, it can not be discarded.
     * @return the applied name of the bus service
     */
    public String getAppliedName() {
        return name;
    }

    /**
     * Discards all the new modifications, and gets the service back to the last
     * saved (applied) state
     * @return true, if there were discarded changes
     */
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
    }

    private void applyServiceData() {
        name = currentServiceData.getName();
        timeGap = currentServiceData.getTimeGap();
        firstLeaveTime = new DayTime(currentServiceData.getFirstLeaveTime());
        boundaryTime = new DayTime(currentServiceData.getBoundaryTime());
    }

    /**
     * Registers all the new modifications if there were any.
     */
    public void applyChanges() {
        if (currentStops.isModified())
            applyStops();
        if (currentServiceData.isModified())
            applyServiceData();
        currentStops.markAsSaved();
        currentServiceData.markAsSaved();
    }

    // getter methods

    /**
     * Returns the object that manages the modifications of the stop list of the bus service.
     * @return the object that manages the modification of the stop list
     */
    public TouchedStops getCurrentStops() {
        return currentStops;
    }

    /**
     * Returns the object that manages the modifications of the basic data of the bus service
     * @return the object that manages the modifications of the basic data
     */
    public BasicServiceData getCurrentServiceData() {
        return currentServiceData;
    }

    /**
     * Returns a Timetable object that shows the arrive times to all the touched
     * stops of the bus service.
     * @return the Timetable of the bus service
     */
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

    /**
     * Bus services must have unique names, so they are equal is their name are the same
     * @param o the bus service to compare to
     * @return true if the given bus service equals to this one
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BusService service = (BusService) o;

        return name.equals(service.name);

    }

    /**
     * Returns the hash code of the name of the service
     * @return the hash code of the name of the service
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}//class
