package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.util.DayTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The BusService class manages data associated with a certain bus service.
 * It holds the data in two separate forms simultaneously, which allows new
 * modifications to be discarded.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BusService {

    /**
     * The <a href="http://www.slf4j.org/">slf4j</a> logger object for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BusService.class);

    /**
     The base name of a newly created, unmodified bus service.
     */
    static final String DEFAULT_NAME = "new service";

    /**
     * The ID -s of the already applied bus stops.
     * The ID -s of the newly added bus stops will only be included in this list
     * after the {@link #applyChanges()} method was called.
     */
    @XmlElement(name = "touchedStop") private List<Integer> savedStops;

    /**
     * The applied name of the bus service.
     * A modified name will only be applied after the call of
     * {@link #applyChanges()} method.
     */
    @XmlElement(name = "serviceName") private String name;

    /**
     * This many minutes are spend before the next bus leaves the station.
     * By the convention the value of <code>timeGap</code> must be greater than 0 and less than 24 * 60.
     */
    private int timeGap;

    /**
     * The exact time when the first bus of the day leaves the station.
     */
    private DayTime firstLeaveTime;

    /**
     * In the same day no bus can leave the station after this time.
     */
    private DayTime boundaryTime;

    /**
     * The buffer for the bus stops of this service.
     * Every modification of the stops happen here first, and get "committed"
     * after the call of the {@link #applyChanges()} method.
     * The validation of every modification is made in the buffer.
     */
    @XmlTransient private TouchedStops currentStops;

    /**
     * The buffer for the basic data of this service.
     * Every modification of the basic data happen here first, and get "committed"
     * after the call of the {@link #applyChanges()} method.
     * The validation of every modification is made in the buffer.
     */
    @XmlTransient private BasicServiceData currentServiceData;

    /**
     * Creates a bus service filled with default values. These values are:<br>
     *   - default name<br>
     *   - empty stop list<br>
     *   - 10 minutes time gap<br>
     *   - first leaves the station at 08:00<br>
     *   - no bus leaves after 18:00
     */
    public BusService() {
        LOGGER.trace("called BusService constructor");
        savedStops = new ArrayList<>();
        name = DEFAULT_NAME;
        timeGap = 10;
        firstLeaveTime = new DayTime(8, 0);
        boundaryTime = new DayTime(18, 0);
        initTransientFields();
    }

    /**
     * Fills the buffers with the saved data.
     * Only the applied data are saved to file, so after reading
     * the file, the buffers don't get any valid value. This method
     * fills them with valid data.
     */
    void initTransientFields() {
        LOGGER.trace("called initTransientFields");
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
     * Returns the applied name of the service.
     * The applied name is not the newly
     * modified name, it can not be discarded.
     * @return the applied name of the bus service
     */
    public String getAppliedName() {
        return name;
    }

    /**
     * Discards all the new modifications, and gets the service back to the last
     * saved (applied) state.
     * @return true, if there were discarded changes.
     */
    public boolean discardChanges() {
        LOGGER.trace("called discardChanges");
        boolean restoreHappened = false;
        // discard basic data changes, if there were any
        if (currentServiceData.isModified()) {
            currentServiceData.setName(name);
            currentServiceData.setTimeGap(timeGap);
            currentServiceData.setFirstLeaveTime(firstLeaveTime);
            currentServiceData.setBoundaryTime(boundaryTime);
            currentServiceData.markAsSaved();
            restoreHappened = true;
            LOGGER.debug("there are discarded basic data changes");
        }
        // discard stop changes, if there were any
        if (currentStops.isModified()) {
            currentStops.clear();
            for (int i = 1; i < savedStops.size(); i++)
                currentStops.appendStop(savedStops.get(i));
            currentStops.markAsSaved();
            if (!restoreHappened)
                restoreHappened = true;
            LOGGER.debug("there are discarded touched stops changes");
        }
        return restoreHappened;
    }

    /**
     * Replaces the content of the {@link #savedStops} with the content
     * of the buffer called {@link #currentStops}.
     * This way the new bus stop modifications get saved, and the state of the buffer
     * will be set to "saved".
     */
    private void applyStops() {
        LOGGER.trace("called applyStops");
        savedStops.clear();
        int[] stops = currentStops.getStops();
        for (int i = 0; i < stops.length; i++)
            savedStops.add(stops[i]);
    }

    /**
     * Replaces the saved basic data with the content of the buffer
     * called {@link #currentServiceData}.
     * This way the new basic data modifications get saved, and the state of the
     * buffer will be set to "saved".
     */
    private void applyServiceData() {
        LOGGER.trace("called applyServiceData");
        name = currentServiceData.getName();
        timeGap = currentServiceData.getTimeGap();
        firstLeaveTime = new DayTime(currentServiceData.getFirstLeaveTime());
        boundaryTime = new DayTime(currentServiceData.getBoundaryTime());
    }

    /**
     * Registers all the new modifications if there were any.
     * After the execution of this method all the new modified
     * changes become applied changes, and the state of the buffers
     * will be set to "saved". After this point, the {@link #discardChanges()}
     * method won't rollback these changes.
     * This method is built on the {@link #applyStops()} and {@link #applyServiceData()} methods.
     */
    public void applyChanges() {
        LOGGER.trace("called applyChanges");
        if (currentStops.isModified())
            applyStops();
        if (currentServiceData.isModified())
            applyServiceData();
        currentStops.markAsSaved();
        currentServiceData.markAsSaved();
    }


    /**
     * Returns the buffer object that manages the modifications of the stop list of the bus service.
     * @return the buffer object that manages the modification of the stop list.
     */
    public TouchedStops getCurrentStops() {
        return currentStops;
    }

    /**
     * Returns the buffer object that manages the modifications of the basic data of the bus service.
     * @return the buffer object that manages the modifications of the basic data.
     */
    public BasicServiceData getCurrentServiceData() {
        return currentServiceData;
    }

    /**
     * Creates and returns a filled {@link Timetable} object that represents all the statistical
     * data of this bus service.
     * @return the {@link Timetable} of this bus service.
     */
    public iTimetable getTimetable() {
        LOGGER.trace("called getTimetable");
        iTimetable.iTimetableArguments args = new Timetable.TimetableArguments();
        args.setName(currentServiceData.getName());
        args.setStopIds(currentStops.getStops());
        args.setTravelTimes(currentStops.getTravelTimes());
        args.setTimeGap(currentServiceData.getTimeGap());
        args.setFirstLeaveTime(currentServiceData.getFirstLeaveTime());
        args.setBoundaryTime(currentServiceData.getBoundaryTime());

        return Timetable.createTimetable(args);
    }

    /**
     * Bus services must have unique names, so they are equal if their names are the same.
     * @param o the bus service to compare to.
     * @return true if the given bus service equals to this one.
     */
    @Override
    public boolean equals(Object o) {
        LOGGER.trace("called equals");
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BusService service = (BusService) o;

        return name.equals(service.name);

    }

    /**
     * Returns the hash code of the name of this service.
     * @return the hash code of the name of this service.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}//class
