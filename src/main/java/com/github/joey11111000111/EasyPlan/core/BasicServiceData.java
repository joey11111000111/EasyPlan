package com.github.joey11111000111.EasyPlan.core;

/**
 * Created by joey on 2015.11.04..
 */
class BasicServiceData {

    private class SimpleTime {
        private int hours;
        private int minutes;

        public SimpleTime(int hours, int minutes) {
            this.hours = hours;
            this.minutes = minutes;
        }

        public int getHours() {
            return hours;
        }
        public int getMinutes() {
            return minutes;
        }

        public boolean setHours(int hours) {
            if (hours < 0 || hours > 23)
                throw new IllegalArgumentException("hours must be in range [0, 23]");
            if (this.hours == hours)
                return false;
            this.hours = hours;
            return true;
        }

        public boolean setMinutes(int minutes) {
            if (minutes < 0 || minutes > 59)
                throw new IllegalArgumentException("minutes must be in range [0, 59]");
            if (this.minutes == minutes)
                return false;
            this.minutes = minutes;
            return true;
        }
    }//private class

    private String name;
    private int timeGap;
    private SimpleTime firstLeaveTime;
    private SimpleTime boundaryTime;
    private boolean modified;

    public BasicServiceData() {
        // set to default values, so it's valid even without any modification
        name = "new service";
        timeGap = 10;
        firstLeaveTime = new SimpleTime(8, 0);
        boundaryTime = new SimpleTime(18, 0);
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


    public void setBoundaryTime(int hours, int minutes) {
        boolean hoursModified = boundaryTime.setHours(hours);
        boolean minutesModified = boundaryTime.setMinutes(minutes);
        if (hoursModified || minutesModified)
            markAsModified();
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

}//class
