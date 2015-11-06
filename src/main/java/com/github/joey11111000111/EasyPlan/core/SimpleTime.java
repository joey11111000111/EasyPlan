package com.github.joey11111000111.EasyPlan.core;

/**
 * Created by joey on 2015.11.06..
 */
public class SimpleTime {
    private int hours;
    private int minutes;

    public SimpleTime(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }
    public SimpleTime(SimpleTime rhs) {
        hours = rhs.getHours();
        minutes = rhs.getMinutes();
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
}//class
