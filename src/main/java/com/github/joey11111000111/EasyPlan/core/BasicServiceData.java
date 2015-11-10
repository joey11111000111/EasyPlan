package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.core.util.DayTime;

/**
 * Created by joey on 2015.11.04..
 */
class BasicServiceData {

    private String name;
    private int timeGap;
    private DayTime firstLeaveTime;
    private DayTime boundaryTime;
    private boolean modified;

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
    public void markAsSaved() {
        if (modified)
            modified = false;
    }
    public boolean isModified() {
        return modified;
    }

    public String getName() {
        return name;
    }

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

    public int getTimeGap() {
        return timeGap;
    }

    public void setTimeGap(int timeGap) {
        if (timeGap < 1)
            throw new IllegalArgumentException("timeGap must be at least 1");
        if (this.timeGap == timeGap)
            return;
        this.timeGap = timeGap;
        markAsModified();
    }


    public void setFirstLeaveTime(int hours, int minutes) {
        boolean hoursModified = firstLeaveTime.setHours(hours);
        boolean minutesModified = firstLeaveTime.setMinutes(minutes);
        if (hoursModified || minutesModified)
            markAsModified();
    }
    public void setFirstLeaveTime(DayTime time) {
        firstLeaveTime.setHours(time.getHours());
        firstLeaveTime.setMinutes(time.getMinutes());
    }

    public void setFirstLeaveHour(int hours) {
        if (firstLeaveTime.setHours(hours))
            markAsModified();
    }

    public void setFirstLeaveMinutes(int minutes) {
        if (firstLeaveTime.setMinutes(minutes))
            markAsModified();
    }

    public int getFirstLeaveHours() {
        return firstLeaveTime.getHours();
    }

    public int getFirstLeaveMinutes() {
        return firstLeaveTime.getMinutes();
    }

    DayTime getFirstLeaveTime() {
        return new DayTime(firstLeaveTime);
    }

    public void setBoundaryTime(int hours, int minutes) {
        boolean hoursModified = boundaryTime.setHours(hours);
        boolean minutesModified = boundaryTime.setMinutes(minutes);
        if (hoursModified || minutesModified)
            markAsModified();
    }
    public void setBoundaryTime(DayTime time) {
        boundaryTime.setHours(time.getHours());
        boundaryTime.setMinutes(time.getMinutes());
    }

    public void setBoundaryHours(int hours) {
        if (boundaryTime.setHours(hours))
            markAsModified();
    }
    public void setBoundaryMinutes(int minutes) {
        if (boundaryTime.setMinutes(minutes))
            markAsModified();
    }

    public int getBoundaryHours() {
        return boundaryTime.getHours();
    }
    public int getBoundaryMinutes() {
        return boundaryTime.getMinutes();
    }

    DayTime getBoundaryTime() {
        return new DayTime(boundaryTime);
    }
}//class
