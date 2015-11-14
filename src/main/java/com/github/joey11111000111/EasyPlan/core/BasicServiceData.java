package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.util.DayTime;

/**
 * The BasicServiceData class manages the "basic" data of a bus service, which include
 * the name, time gap, first leave time (of the day) and boundary time (of the day, after which
 * no more buses can leave the station).
 * This class helps managing these data separately from the bus service, and helps to achieve
 * that modifications are not registered until they are applied.
 */
class BasicServiceData {

    private String name;
    private int timeGap;
    private DayTime firstLeaveTime;
    private DayTime boundaryTime;
    private boolean modified;

    /**
     * Creates a BasicServiceData object with all the initial data provided
     * @param name name of the service
     * @param timeGap minutes before the next bust of the service leaves the station
     * @param firstLeaveTime indicates the time of the day when the first bus of the service leaves the station
     * @param boundaryTime indicates the time of the day after which
     *                     no more buses of the service can leave the station
     */
    public BasicServiceData(String name, int timeGap, DayTime firstLeaveTime, DayTime boundaryTime) {
        this.name = name;
        this.timeGap = timeGap;
        this.firstLeaveTime = new DayTime(firstLeaveTime);
        this.boundaryTime = new DayTime(boundaryTime);
        modified = false;
    }

    private void markAsModified() {
        if (!modified)
            modified = true;
    }

    /**
     * Indicates that all the data modifications are saved, and there are no
     * unregistered changes
     */
    public void markAsSaved() {
        if (modified)
            modified = false;
    }

    /**
     * Returns true if the data was modified since the last save
     * @return true, if the data was modified since the last save
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Returns the name of the bus service
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the bus service
     * @param name the name of the bus service
     * @throws NullPointerException when the given string is null
     * @throws IllegalArgumentException when received an empty string
     */
    public void setName(String name) {
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
     * Returns the time gap attribute of the service
     * @return the time gap attribute of the service
     */
    public int getTimeGap() {
        return timeGap;
    }

    /**
     * Sets the time gap of the bus service to the given minutes
     * @param timeGap the minutes to set the time gap to
     * @throws IllegalArgumentException when the given minutes are less then 1
     */
    public void setTimeGap(int timeGap) {
        if (timeGap < 1)
            throw new IllegalArgumentException("timeGap must be at least 1");
        if (this.timeGap == timeGap)
            return;
        this.timeGap = timeGap;
        markAsModified();
    }

    /**
     * Sets the time of the day when the first bus of the service leaves the station
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
     * Sets the time of the day when the first bus of the service leaves the station
     * @param time the first leaving time
     */
    public void setFirstLeaveTime(DayTime time) {
        firstLeaveTime.setHours(time.getHours());
        firstLeaveTime.setMinutes(time.getMinutes());
    }

    /**
     * Sets the hour part of the first leave time
     * @param hours the hour part of the first leave time
     */
    public void setFirstLeaveHour(int hours) {
        if (firstLeaveTime.setHours(hours))
            markAsModified();
    }

    /**
     * Sets the minute part of the first leave time
     * @param minutes the minute part of the first leave time
     */
    public void setFirstLeaveMinutes(int minutes) {
        if (firstLeaveTime.setMinutes(minutes))
            markAsModified();
    }

    /**
     * Returns the hour of the first leave time
     * @return the hour of the first leave time
     */
    public int getFirstLeaveHours() {
        return firstLeaveTime.getHours();
    }

    /**
     * Returns the minute of the first leave time
     * @return the minute of the first leave time
     */
    public int getFirstLeaveMinutes() {
        return firstLeaveTime.getMinutes();
    }

    /**
     * Returns the first leave time
     * @return the first leave time
     */
    DayTime getFirstLeaveTime() {
        return new DayTime(firstLeaveTime);
    }

    /**
     * Sets the time of the day after which no more buses of the service can leave the station
     * @param hours the boundary hour
     * @param minutes the boundary minute
     */
    public void setBoundaryTime(int hours, int minutes) {
        boolean hoursModified = boundaryTime.setHours(hours);
        boolean minutesModified = boundaryTime.setMinutes(minutes);
        if (hoursModified || minutesModified)
            markAsModified();
    }
    /**
     * Sets the time of the day after which no more buses of the service can leave the station
     * @param time the boundary time
     */
    public void setBoundaryTime(DayTime time) {
        boundaryTime.setHours(time.getHours());
        boundaryTime.setMinutes(time.getMinutes());
    }

    /**
     * Sets the hour part of the boundary time
     * @param hours the hour part of the boundary time
     */
    public void setBoundaryHours(int hours) {
        if (boundaryTime.setHours(hours))
            markAsModified();
    }
    /**
     * Sets the minute part of the boundary time
     * @param minutes the hour part of the boundary time
     */
    public void setBoundaryMinutes(int minutes) {
        if (boundaryTime.setMinutes(minutes))
            markAsModified();
    }

    /**
     * Returns the hour part of the boundary time
     * @return the hour part of the boundary time
     */
    public int getBoundaryHours() {
        return boundaryTime.getHours();
    }
    /**
     * Returns the minute part of the boundary time
     * @return the minute part of the boundary time
     */
    public int getBoundaryMinutes() {
        return boundaryTime.getMinutes();
    }

    /**
     * Returns the boundary time
     * @return the boundary time
     */
    DayTime getBoundaryTime() {
        return new DayTime(boundaryTime);
    }
}//class
