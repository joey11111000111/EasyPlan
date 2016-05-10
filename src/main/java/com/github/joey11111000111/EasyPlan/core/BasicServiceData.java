package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.util.DayTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class acts as a buffer for the basic data of a bus service.
 * Basic data modifications only happen here, along with the validation of
 * the modifications. Also follows whether there were any new modifications.
 */
class BasicServiceData {

    /**
     * The <a href="http://www.slf4j.org/">slf4j</a> logger object for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicServiceData.class);

    /**
     * Buffered {@link BusService#name name} of the wrapping {@link BusService}.
     */
    private String name;

    /**
     * Buffered {@link BusService#timeGap timeGap} of the wrapping {@link BusService}.
     */
    private int timeGap;

    /**
     * Buffered {@link BusService#firstLeaveTime firstLeaveTime} of the wrapping {@link BusService}.
     */
    private DayTime firstLeaveTime;

    /**
     * Buffered {@link BusService#boundaryTime boundaryTime} of the wrapping {@link BusService}.
     */
    private DayTime boundaryTime;

    /**
     * Indicates whether there were any new modificatons.
     */
    private boolean modified;

    /**
     * Creates a BasicServiceData object with all the initial data provided.
     * @param name name of the service
     * @param timeGap minutes before the next bust of the service leaves the station
     * @param firstLeaveTime indicates the time of the day when the first bus of the service leaves the station
     * @param boundaryTime indicates the time of the day after which
     *                     no more buses of the service can leave the station
     */
    public BasicServiceData(String name, int timeGap, DayTime firstLeaveTime, DayTime boundaryTime) {
        LOGGER.trace("called BasicServiceData constructor");
        this.name = name;
        this.timeGap = timeGap;
        this.firstLeaveTime = new DayTime(firstLeaveTime);
        this.boundaryTime = new DayTime(boundaryTime);
        modified = false;
    }

    /**
     * Changes the state of the object from saved to modified, if it is not already modified.
     * The "modified" state indicates that some content of the buffer was changed, thus might not
     * contain the same data as the wrapping {@link BusService} object.
     */
    private void markAsModified() {
        LOGGER.trace("called markAsModified");
        if (!modified) {
            modified = true;
            LOGGER.debug("now marked as modified");
        }
    }

    /**
     * Changes the state of the object from modified to saved, if it is not already saved.
     * The "saved" state indicates that the contents of the buffer are identical to the
     * contents found in the wrapping {@link BusService} object.
     */
    public void markAsSaved() {
        LOGGER.trace("called markAsSaved");
        if (modified) {
            modified = false;
            LOGGER.debug("now marked as saved");
        }
    }

    /**
     * Indicates whether this object is in the state of "modified".
     * See {@link #markAsModified()} for more information.
     * @return true, if this object is in the state of "modified".
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Returns the buffered {@link BusService#name name} of the wrapping bus service.
     * @return the buffered name of the wrapping bus service.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the buffered {@link BusService#name name} of the wrapping bus service.
     * @param name the new name for the bus service
     * @throws NullPointerException when the given string is null
     * @throws IllegalArgumentException when received an empty string
     */
    public void setName(String name) {
        LOGGER.trace("called setName");
        if (name == null)
            throw new NullPointerException("service name must not be null");
        if (name.length() < 1)
            throw new IllegalArgumentException("empty string must not be the name of a bus service");
        if (name.equals(this.name))
            return;
        this.name = name;
        markAsModified();
    }

    /**
     * Returns the buffered {@link BusService#timeGap timeGap} of the wrapping {@link BusService} object.
     * @return the buffered timeGap of the wrapping service
     */
    public int getTimeGap() {
        return timeGap;
    }

    /**
     * Sets the buffered {@link BusService#timeGap timeGap} to the given value.
     * @param timeGap the minutes to set the time gap to
     * @throws IllegalArgumentException when the given minutes are out of the conventional interval:<br>
     *     - less than 1<br>
     *     - greater than or equal 24 * 60
     */
    public void setTimeGap(int timeGap) {
        LOGGER.trace("called setTimeGap");
        if (timeGap < 1)
            throw new IllegalArgumentException("timeGap must be at least 1");
        if (timeGap >= 24 * 60)
            throw new IllegalArgumentException("timeGap must be less than one whole day");
        if (this.timeGap == timeGap)
            return;
        this.timeGap = timeGap;
        markAsModified();
    }

    /**
     * Sets the buffered {@link BusService#firstLeaveTime firstLeaveTime} to the given value.
     * Note that there is no argument validation here, but there is in the underlying
     * {@link DayTime} object, so this method might throw an {@link IllegalArgumentException}.
     * @param hours the first leaving hour
     * @param minutes the first leaving minute
     */
    public void setFirstLeaveTime(int hours, int minutes) {
        boolean hoursModified = firstLeaveTime.setHours(hours);
        boolean minutesModified = firstLeaveTime.setMinutes(minutes);
        if (hoursModified || minutesModified)
            markAsModified();
    }

    /**
     * Sets the buffered time of the day when the first bus of the service leaves the station.
     * In opposition of it's overloaded pair, {@link #setFirstLeaveTime(int, int)}, this
     * method never throws any exceptions.
     * See {@link #setFirstLeaveTime(int, int)} and {@link BusService#firstLeaveTime firstLeaveTime}
     * for more information.
     * @param time the first leaving time
     */
    public void setFirstLeaveTime(DayTime time) {
        firstLeaveTime.setHours(time.getHours());
        firstLeaveTime.setMinutes(time.getMinutes());
    }

    /**
     * Sets the hour part of the buffered first leave time.
     * See {@link #setFirstLeaveTime(int, int)} for more information.
     * @param hours the new hour part of the buffered first leave time
     */
    public void setFirstLeaveHour(int hours) {
        if (firstLeaveTime.setHours(hours))
            markAsModified();
    }

    /**
     * Sets the minute part of the buffered first leave time.
     * See {@link #setFirstLeaveTime(int, int)} for more information.
     * @param minutes the new minute part of the buffered first leave time
     */
    public void setFirstLeaveMinutes(int minutes) {
        if (firstLeaveTime.setMinutes(minutes))
            markAsModified();
    }

    /**
     * Returns the hour part of the buffered {@link BusService#firstLeaveTime firstLeaveTime}.
     * @return the hour part of the first leave time.
     */
    public int getFirstLeaveHours() {
        return firstLeaveTime.getHours();
    }

    /**
     * Returns the minute part of the buffered {@link BusService#firstLeaveTime firstLeaveTime}.
     * @return the minute part of the first leave time
     */
    public int getFirstLeaveMinutes() {
        return firstLeaveTime.getMinutes();
    }

    /**
     * Returns the buffered {@link BusService#firstLeaveTime firstLeaveTime} of the wrapping service.
     * @return the buffered value of first leave time.
     */
    DayTime getFirstLeaveTime() {
        return new DayTime(firstLeaveTime);
    }

    /**
     * Sets the buffered time of the day after which no more buses of the service can leave the station.
     * See {@link BusService#boundaryTime boundaryTime} for more information.
     * Note that there is no argument validation here, but there is in the underlying
     * {@link DayTime} object, thus this method might throw and {@link IllegalArgumentException}.
     * @param hours the hour part of the new boudary time
     * @param minutes the minute part of the new boundary time
     */
    public void setBoundaryTime(int hours, int minutes) {
        boolean hoursModified = boundaryTime.setHours(hours);
        boolean minutesModified = boundaryTime.setMinutes(minutes);
        if (hoursModified || minutesModified)
            markAsModified();
    }

    /**
     * Sets the buffered time of the day after which no bus of the service is allowed to leave the station.
     * In opposition of it's overloaded pair, {@link #setBoundaryTime(int, int)}, this
     * method never throws any exceptions.
     * See {@link #setBoundaryTime(int, int)} and {@link BusService#boundaryTime boundaryTime} for more information.
     * @param time the first leaving time
     */
    public void setBoundaryTime(DayTime time) {
        boundaryTime.setHours(time.getHours());
        boundaryTime.setMinutes(time.getMinutes());
    }

    /**
     * Sets the hour part of the buffered {@link BusService#boundaryTime boundaryTime}.
     * @param hours the new hour part of the buffered boundary time
     */
    public void setBoundaryHours(int hours) {
        if (boundaryTime.setHours(hours))
            markAsModified();
    }
    /**
     * Sets the minute part of the buffered {@link BusService#boundaryTime boundaryTime}.
     * @param minutes the new minute part of the buffered boundary time
     */
    public void setBoundaryMinutes(int minutes) {
        if (boundaryTime.setMinutes(minutes))
            markAsModified();
    }

    /**
     * Returns the hour part of the buffered {@link BusService#boundaryTime boundryTime}.
     * @return the hour part of the buffered boundary time
     */
    public int getBoundaryHours() {
        return boundaryTime.getHours();
    }
    /**
     * Returns the minute part of the buffered {@link BusService#boundaryTime boundaryTime}.
     * @return the minute part of the buffered boundary time
     */
    public int getBoundaryMinutes() {
        return boundaryTime.getMinutes();
    }

    /**
     * Returns the buffered {@link BusService#boundaryTime boundaryTime}.
     * @return the buffered boundary time
     */
    DayTime getBoundaryTime() {
        return new DayTime(boundaryTime);
    }
}//class
