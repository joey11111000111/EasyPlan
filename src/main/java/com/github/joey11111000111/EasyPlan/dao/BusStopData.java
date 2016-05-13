package com.github.joey11111000111.EasyPlan.dao;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the {@link iBusStopData} interface. Does nothing more.
 */
public class BusStopData implements iBusStopData {

    /**
     * ID of the represented bus stop.
     */
    private int id;

    /**
     * X coordinate of the represented bus stop.
     */
    private int x;

    /**
     * Y coordinate of the represented bus stop.
     */
    private int y;

    /**
     * All the reachable stops of the represented bus stop, along with time (in minutes)
     * that it takes to travel there.
     */
    private Map<Integer, Integer> reachableStops;

    /**
     * The summing number that is used for validation.
     */
    private byte sum = 0;

    /**
     * The mask used for validation.
     */
    private byte validationMask = 15;       // 1111  one for each data field

    /**
     * {@inheritDoc}
     */
    @Override
    public void setId(int id) {
        this.id = id;
        sum |= 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setX(int x) {
        this.x = x;
        sum |= 2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setY(int y) {
        this.y = y;
        sum |= 4;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addReachableStop(int id, int travelMinutes) {
        if (reachableStops == null)
            reachableStops = new HashMap<>();
        reachableStops.put(id, travelMinutes);
        sum |= 8;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReachableStops(Map<Integer, Integer> reachableStops) {
        if (reachableStops == null)
            throw new NullPointerException("reachableStops must not be null");
        this.reachableStops = reachableStops;
        sum |= 8;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Integer, Integer> getReachableStops() {
        return reachableStops;
    }

    /**
     * {@inheritDoc}
     * This implementation is using a summing number and a validation mask.
     * Every setter method sets a certain bit of the summing number from 0 to 1.
     * If all the required bits are 1, than all the setter methods were called, thus
     * this object is valid.
     */
    @Override
    public boolean isValid() {
        return (sum & validationMask) == validationMask;
    }
}
