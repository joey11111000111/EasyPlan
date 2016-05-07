package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.dao.CityReader;
import com.github.joey11111000111.EasyPlan.dao.iBusStopData;
import com.github.joey11111000111.EasyPlan.dao.iCityReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * The BusStop class manages all the data and operations (including the xml parsing) that are related to bus stops.
 * This class cannot be instantiated, nor modified in any way. Everything is handled through static methods.
 * All the bus stops have a unique id, which start from 0 and are increasing one by one. Thus the 'id' and 'index'
 * words are almost synonyms.
 */
public final class BusStop implements Comparable<BusStop> {

    static final Logger LOGGER = LoggerFactory.getLogger(BusStop.class);

    // static ----------------------------------------------------------
    private static final BusStop[] allStops;

    static {
        iCityReader cityReader = new CityReader();
        List<iBusStopData> stopsData = cityReader.readCityStops();

        allStops = new BusStop[stopsData.size()];
        for (int i = 0; i < allStops.length; i++) {
            iBusStopData currentStopData = stopsData.get(i);

            // Check whether the data object is valid ---------------------------------------------
            if (!currentStopData.isValid()) {
                LOGGER.error("Invalid bus stop data object! Terminating...");
                System.exit(1);
            }
            int id = currentStopData.getId();
            if (id < 0 || id >= allStops.length) {
                LOGGER.error("Invalid bus stop ID: " + id);
                System.exit(1);
            }
            int x = currentStopData.getX();
            if (x < 0 || x > 9) {
                LOGGER.error("Invalid bus stop X coordinate: " + x + ". Terminating...");
                System.exit(1);
            }
            int y = currentStopData.getY();
            if (y < 0 || y > 9) {
                LOGGER.error("Invalid bus stop Y coordinate: " + y + ". Terminating...");
                System.exit(1);
            }

            Map<Integer, Integer> reachables = currentStopData.getReachableStops();     // never null
            if (reachables.size() < 1) {
                LOGGER.error("No reachables for stop: " + id + ". Terminating...");
                System.exit(1);
            }
            for (Map.Entry<Integer, Integer> entry : reachables.entrySet()) {
                Integer rID = entry.getKey();
                Integer travelMinutes = entry.getValue();
                if (rID < 0 || rID >= allStops.length) {
                    LOGGER.error("Invalid reachable stop ID: " + rID
                            + " from the stop: " + id + ". Terminating...");
                    System.exit(1);
                }
                if (travelMinutes < 1) {
                    LOGGER.error("Invalid travel minutes: " + travelMinutes
                            + "from the stop: " + id + " to the stop: " + rID + ". Terminating...");
                    System.exit(1);
                }
            }

            // At this point the individual data object is valid by itself, ready to create a BusStop object
            allStops[i] = new BusStop(currentStopData);
        }//for

        Arrays.sort(allStops);
        // At this point every bus stop ID must be equal to its array index
        for (int i = 0; i < allStops.length; i++) {
            if (allStops[i].id != i) {
                LOGGER.error("Corrupt bus stop ID: " + allStops[i].id + ". Terminating...");
                System.exit(1);
            }
        }

    }//static


    // TODO: javadoc
    public static int getStopCount() {
        return allStops.length;
    }

    /**
     * Returns the X coordinate of the specified bus stop
     * @param id specifies the bus stops, whose X coordinate will be returned
     * @return the X coordinate of the specified bus stop
     * @throws IndexOutOfBoundsException if there isn't a bus stop with the given id
     */
    public static int getXCoordOf(int id) {
        if (!validId(id))
            throw new IndexOutOfBoundsException("id is out of range: " + id);
        return allStops[id].x;
    }
    /**
     * Returns the Y coordinate of the specified bus stop
     * @param id specifies the bus stops, whose Y coordinate will be returned
     * @return the Y coordinate of the specified bus stop
     * @throws IndexOutOfBoundsException if there isn't a bus stop with the given id
     */
    public static int getYCoordOf(int id) {
        if (!validId(id))
            throw new IndexOutOfBoundsException("id is out of range: " + id);
        return allStops[id].y;
    }

    /**
     * Returns all the bus stop ids that can be the next stop after the given bus stop
     * @param id the current bus stop, from which to go
     * @return all the bus stop ids that can be the next stop after the given bus stop
     */
    public static int[] getReachableIdsOf(int id) {
        Set<java.lang.Integer> keyIds = allStops[id].reachableStops.keySet();
        int[] reachableIds = new int[keyIds.size()];
        int i = 0;
        for (int reId : keyIds)
            reachableIds[i++] = reId;
        return reachableIds;
    }

    /**
     * Returns true if there is a bus stop with the given id
     * @param index the bus stop id whose validity will be checked
     * @return true if there is a bus stop with the given id
     */
    public static boolean validId(int index) {
        return index >= 0 && index < allStops.length;
    }

    /**
     * Returns true, if the bus stop at 'toId' can be the next from 'fromId'
     * @param toId the bus stop to go to
     * @param fromId the bus stop to go from
     * @return true when the bus stop at 'toId' can be the next from 'fromId'
     * @throws IndexOutOfBoundsException if either of the given ids are invalid
     */
    public static boolean isReachableToFrom(int toId, int fromId) {
        if (!validId(toId))
            throw new IndexOutOfBoundsException("toId is out of range: " + toId);
        if (!validId(fromId))
            throw new IndexOutOfBoundsException("fromId is out of range: " + fromId);
        return allStops[fromId].reachableStops.containsKey(toId);
    }

    /**
     * Returns the time (in minutes) that it takes to go from the bus stop with 'fromId' to the
     * bus stop with 'toId'
     * @param toId the bus stop to go to
     * @param fromId the bus stop to go from
     * @return the travel time in minutes
     * @throws IndexOutOfBoundsException if either of the given ids are invalid
     */
    public static int travelTimeToFrom(int toId, int fromId) {
        if (!validId(fromId))
            throw new IndexOutOfBoundsException("fromId is out range: " + fromId);
        if (!validId(toId))
            throw new IndexOutOfBoundsException("toId is out of range: " + toId);
        if (!allStops[fromId].reachableStops.containsKey(toId))
            throw new IllegalArgumentException("the bus stop '" + toId +
                    "'  is not reachable from '" + fromId + "'");

        return allStops[fromId].reachableStops.get(toId);
    }

    // member ----------------------------------------------------------
    public final int id;
    public final int x;
    public final int y;
    private final Map<java.lang.Integer, java.lang.Integer> reachableStops;

    private BusStop(iBusStopData busStopData) {
        id = busStopData.getId();
        x = busStopData.getX();
        y = busStopData.getY();
        reachableStops = Collections.unmodifiableMap(busStopData.getReachableStops());
    }

    public int compareTo(BusStop otherStop) {
        return this.id - otherStop.id;
    }

}//class
