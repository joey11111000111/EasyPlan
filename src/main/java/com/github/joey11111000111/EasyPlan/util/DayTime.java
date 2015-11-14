package com.github.joey11111000111.EasyPlan.util;

import java.io.Serializable;

/**
 * The DayTime class represents the time of one day in HH:MM format,
 * where HH is in range 0-23 and MM is in range 0-59
 */
public class DayTime implements Serializable {

    static final long serialVersionUID = 0L;

    private int hours;
    private int minutes;

    /**
     * Creates a DayTime instance by converting the given minutes into a HH:MM format.
     * The minutes must be in range of one day or it can overflow when the
     * canOverflow permits it. Underflow is never permitted.
     * @param minutes the day-time to represent in the form of minutes, in range 0 - 1439
     * @param canOverflow decides whether the overflow is permitted or not
     * @throws IllegalArgumentException - when the 'minutes' is less then 0 or
     * more then 1439 and the 'canOverflow' is false
     */
    public DayTime(int minutes, boolean canOverflow) {
        if (!canOverflow)
            if (minutes < 0 || minutes > (23 * 60 + 59))
                throw new IllegalArgumentException("time day-overflow is not allowed, must represent one day from "
                        + "0:0 to 23:59");
        this.hours = minutes / 60;
        this.minutes = minutes - hours * 60;
        this.hours %= 24;
    }

    /**
     * Creates a DayTime instance by converting the given minutes into a HH:MM format.
     * @param minutes the day-time to represent in the form of minutes, must be in range 0 - 1439
     * @throws IllegalArgumentException - when the 'minutes' is out of the range 0 - 1439
     */
    public DayTime(int minutes) {
        this(minutes, false);
    }

    /**
     * Creates a new DayTime instance based on the given parameters
     * @param hours the hour part of the time, must be in range 0 - 23
     * @param minutes the minute part of the time, must be in range 0 - 59
     * @throws IllegalArgumentException - when the 'minutes' is out of the range 0 - 1439
     */
    public DayTime(int hours, int minutes) {
        setHours(hours);
        setMinutes(minutes);
    }

    /**
     * Creates a new DayTime instance that represents the exact same time as the given instance.
     * The created object is a deep copy of the given object
     * @param rhs the object that will be copied
     */
    public DayTime(DayTime rhs) {
        hours = rhs.getHours();
        minutes = rhs.getMinutes();
    }

    /**
     * Returns the hour part of the represented time
     * @return the hour part of the represented time
     */
    public int getHours() {
        return hours;
    }
    /**
     * Returns the minute part of the represented time
     * @return the minute part of the represented time
     */
    public int getMinutes() {
        return minutes;
    }

    /**
     * Converts the represented time into minute and returns the result
     * @return the represented time converted to minutes (hours * 24 + minutes)
     */
    public int getTimeAsMinutes() {
        return hours * 60 + minutes;
    }

    /**
     * Sets the hour part of the time to the given value
     * @param hours the new hour part of the time, must be in range 0 - 23
     * @return true, if the contained hour part was modified
     * @throws IllegalArgumentException - if the 'hours' parameter is out of range
     */
    public boolean setHours(int hours) {
        if (hours < 0 || hours > 23)
            throw new IllegalArgumentException("hours must be in range [0, 23]");
        if (this.hours == hours)
            return false;
        this.hours = hours;
        return true;
    }

    /**
     * Sets the minute part of the time to the given value
     * @param minutes the new minute part of the time, must be in range 0 - 23
     * @return true, if the contained hour part was modified
     * @throws IllegalArgumentException - if the 'hours' parameter is out of range
     */
    public boolean setMinutes(int minutes) {
        if (minutes < 0 || minutes > 59)
            throw new IllegalArgumentException("minutes must be in range [0, 59]");
        if (this.minutes == minutes)
            return false;
        this.minutes = minutes;
        return true;
    }

    /**
     * Returns the represented time in a HH:MM format
     * @return a string representation of the time in HH:MM format
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (hours < 10)
            sb.append(0);
        sb.append(hours).append(':');
        if (minutes < 10)
            sb.append(0);
        sb.append(minutes);

        return sb.toString();
    }
}//class
