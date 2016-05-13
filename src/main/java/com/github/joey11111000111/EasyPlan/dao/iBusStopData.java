package com.github.joey11111000111.EasyPlan.dao;

import java.util.Map;

/**
 * This interface is used in the data flow from the city.xml file to the
 * {@link com.github.joey11111000111.EasyPlan.core.BusStop BusStop} class.
 * The purpose of this interface is to collect all the data of a read
 * {@link com.github.joey11111000111.EasyPlan.core.BusStop BusStop} object in a form that is
 * expected by the {@link com.github.joey11111000111.EasyPlan.core.BusStop BusStop} class.
 */
public interface iBusStopData {

    /**
     * Sets the id of the stop.
     * @param id the id of the stop
     */
    void setId(int id);

    /**
     * Sets the X coordinate of the stop.
     * @param x the X coordinate of the stop
     */
    void setX(int x);

    /**
     *
     * Sets the Y coordinate of the stop.
     * @param y the Y coordinate of the stop
     */
    void setY(int y);

    /**
     * Adds one reachable stop along with the travel time to get there.
     * @param id the id of the reachable stop
     * @param travelMinutes the time in minutes that it takes to get to the given reachable stop
     */
    void addReachableStop(int id, int travelMinutes);

    /**
     * Sets all the reachable stops for this stop along with all the travel times.
     * Calling this method will replace all the previously added reachable stops.
     * However, it is possible to add more reachable stops after this method call
     * using the {@link #addReachableStop(int, int)} method.
     * @param reachableStops all the reachable stops with their travel times
     */
    void setReachableStops(Map<Integer, Integer> reachableStops);

    /**
     * Returns the previously added id of the bus stop.
     * @return the id of the bus stop
     */
    int getId();

    /**
     * Returns the previously added X coordinate of the bus stop.
     * @return the X coordinate of the bus stop
     */
    int getX();

    /**
     * Returns the previously added Y coordinate of the bus stop.
     * @return the Y coordinate of the bus stop
     */
    int getY();

    /**
     * Returns a {@link Map map} containing all the reachable stops from this stop
     * along with all the travel times.
     * @return a {@link Map map} containing all the reachable stops from this stop
     */
    Map<Integer, Integer> getReachableStops();

    /**
     * Indicates whether all the data fields of this object was set.
     * Further data validation is not included here.
     * @return true if every setter method was called at least once
     */
    boolean isValid();


}
