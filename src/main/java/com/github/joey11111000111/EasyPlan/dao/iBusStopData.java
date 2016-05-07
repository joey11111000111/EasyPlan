package com.github.joey11111000111.EasyPlan.dao;

import java.util.Map;

/**
 * Created by joey on 3/21/16.
 */
public interface iBusStopData {

    void setId(int id);
    void setX(int x);
    void setY(int y);
    void addReachableStop(int id, int travelMinutes);
    void setReachableStops(Map<Integer, Integer> reachableStops);

    int getId();
    int getX();
    int getY();
    Map<Integer, Integer> getReachableStops();

    /**
     * Indicates whether all the data fields of this object was set.
     * @return true if every setter method was called at least once
     */
    boolean isValid();


}
