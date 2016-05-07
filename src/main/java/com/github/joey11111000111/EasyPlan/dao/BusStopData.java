package com.github.joey11111000111.EasyPlan.dao;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by joey on 3/21/16.
 */
public class BusStopData implements iBusStopData {

    private int id;
    private int x;
    private int y;
    private Map<Integer, Integer> reachableStops;

    private byte sum = 0;
    private byte validationMask = 15;       // 1111  one for each data field


    @Override
    public void setId(int id) {
        this.id = id;
        sum |= 1;
    }

    @Override
    public void setX(int x) {
        this.x = x;
        sum |= 2;
    }

    @Override
    public void setY(int y) {
        this.y = y;
        sum |= 4;
    }

    @Override
    public void addReachableStop(int id, int travelMinutes) {
        if (reachableStops == null)
            reachableStops = new HashMap<>();
        reachableStops.put(id, travelMinutes);
        sum |= 8;
    }

    @Override
    public void setReachableStops(Map<Integer, Integer> reachableStops) {
        if (reachableStops == null)
            throw new NullPointerException("reachableStops must not be null");
        this.reachableStops = reachableStops;
        sum |= 8;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public Map<Integer, Integer> getReachableStops() {
        return reachableStops;
    }

    @Override
    public boolean isValid() {
        return (sum & validationMask) == validationMask;
    }
}
