package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.util.DayTime;

import java.util.List;

/**
 * A Timetable gives information about the schedules of a certain bus service.
 * It shows all the time of all the touched stops when a bus of the service arrives.
 * The bus station is also included.
 * All instances are unmodifiable, and no instances can be created outside the package.
 * However, every bus service can return its own timetable.
 */
public interface iTimetable {

    /**
     * Returns the name of the service this timetable represents.
     * @return the name of the service this timetable represents
     */
    String getServiceName();

    /**
     * Returns a list containing a {@link iStopTimes} object for every buffered bus stop the represented
     * service touches on its way.
     * @return a list of {@link iStopTimes} object for all the buffered stops of the represented service
     */
    List<iStopTimes> getStopTimes();

    /**
     * Return the total amount of buses that will leave the station for the represented service
     * between {@link BusService#firstLeaveTime firstLeaveTime} and {@link BusService#boundaryTime boundaryTime}.
     * @return the number of buses that leaves the station for the represented service
     * between {@link BusService#firstLeaveTime firstLeaveTime} and {@link BusService#boundaryTime boundaryTime}
     */
    int getBusCount();

    /**
     * Returns the time (in minutes) between the leave of two subsequent buses of the represented service.
     * See {@link BusService#timeGap timeGap} for more information.
     * @return the minutes between the leave of two subsequent buses of the represented service
     */
    int getTimeGap();

    /**
     * Returns the amount of time that it takes for one bus of the service to get through all the
     * touched bus stops.
     * @return the amount of time that it takes for one bus of the service to get through all the
     * touched bus stops
     */
    DayTime getTotalTravelTime();

    /**
     * Contain all the times when a bus of the represented service stops at the current bus stop.
     */
    interface iStopTimes {

        /**
         * Returns the id of the represented bus stop.
         * @return the id of the represented bus stop.
         */
        String getID();

        /**
         * Returns a list containing all the times of day between {@link BusService#firstLeaveTime firstLeaveTime}
         * and {@link BusService#boundaryTime boundaryTime} when a bus of the represented service stops at the
         * represented bus stop.
         * @return a list of all the times of day when a bus of the represented service stops at the represented
         * bus stop.
         */
        List<DayTime> getTimes();
    }

    /**
     * The sole purpose of this interface is to help to create {@link iTimetable} - implementation instances.
     * Since the constructor of the Timetable class requires quite a lot of arguments
     * which can be hard to correctly dealt with, this class can make it easy and safe.
     * It checks the validity of the arguments and whether all the arguments are given or not.
     * Also they can be given in any order, even more than once.
     */
    interface iTimetableArguments {

        /**
         * Sets the name of the bus service for this {@link iTimetable} object.
         * @param name the name of the represented bus service
         * @throws NullPointerException if the given argument is null
         * @throws IllegalArgumentException if the given string is empty
         */
        void setName(String name);

        /**
         * Sets the id -s of all the buffered bus stop that will be included in this {@link iTimetable}.
         * @param stopIds the id -s of all the included bus stops
         * @throws NullPointerException if the given argument is null
         * @throws IllegalArgumentException in the following cases:<br>
         *     - the given array has zero length
         *     - the first element of the array doesn't represent the bus station (0 by convention)
         *     - The number of given stops is not exactly one more than the number of
         *       {@link #setTravelTimes(int[])} travelTimes},
         *       set by {@link #setTravelTimes(int[])}. If the {@link #setTravelTimes(int[])} travelTimes}
         *       is not set yet at the time of calling this method, this validation doesn't happen here.
         *       However, both this and the {@link #setTravelTimes(int[])} methods check this rule,
         *       so it will surly be checked in one of them.
         */
        void setStopIds(int[] stopIds);

        /**
         * Sets the times it takes to reach all the individual stops of the represented service.
         * See {@link TouchedStops#getTravelTimes() getTravelTimes()} for more information.
         * @param travelTimes the buffered travel times of the represented bus service
         * @throws NullPointerException if the given argument is null
         * @throws IllegalArgumentException in the following cases:<br>
         *     - if there is a negative or zero element in the given array
         *     - The number of given travel times is not exactly one less than the number of
         *       {@link #setStopIds(int[])} stopIds}, set by {@link #setStopIds(int[])}.
         *       If the {@link #setStopIds(int[])} stopIds} is not set yet at the
         *       time of calling this method, this validation doesn't happen here. However, both this
         *       and the {@link #setStopIds(int[])} methods check this rule, so it will surly be checked
         *       in one of them.
         */
        void setTravelTimes(int[] travelTimes);

        /**
         * Sets the {@link BusService#firstLeaveTime firstLeaveTime} of the represented bus service.
         * @param firstLeaveTime the value to represent the {@link BusService#firstLeaveTime firstLeaveTime}
         *                       of the represented bus service
         * @throws NullPointerException if the given argument is null
         */
        void setFirstLeaveTime(DayTime firstLeaveTime);

        /**
         * Sets the {@link BusService#boundaryTime boundaryTime} of the represented bus service.
         * @param boundaryTime the value to represent the {@link BusService#boundaryTime boundaryTime}
         *                       of the represented bus service
         * @throws NullPointerException if the given argument is null
         */
        void setBoundaryTime(DayTime boundaryTime);

        /**
         * Sets the {@link BusService#timeGap timeGap} value as a representation of the bus service.
         * @param timeGap the value to represent the {@link BusService#timeGap timeGap}
         *                       of the represented bus service
         * @throws IllegalArgumentException if the given value is zero or negative
         */
        void setTimeGap(int timeGap);

        /**
         * Returns the name of the represented bus service or null if it wasn't set yet.
         * @return the name of the represented bus service or null
         */
        String getName();

        /**
         * Returns the id -s of all stops included in the represented service or null
         * if they weren't set yet.
         * They are in the order of appending.<br>
         *     See {@link #setStopIds(int[])} for more information.
         * @return the id -s of all stops included in the represented service or null
         */
        int[] getStopIds();

        /**
         * Returns the travel times (in minutes) for each stop of the represented service or null
         * if they weren't set yet.
         * The times are relative to the station.<br>
         *     See {@link #setTravelTimes(int[])} for more information.
         * @return the travel times for each stop of the represented service or null
         */
        int[] getTravelTimes();

        /**
         * Returns the {@link BusService#firstLeaveTime firstLeaveTime} of the represented bus service
         * or null if it wasn't set yet.
         * @return the {@link BusService#firstLeaveTime firstLeaveTime} of the represented service or null
         */
        DayTime getFirstLeaveTime();


        /**
         * Returns the {@link BusService#boundaryTime boundaryTime} of the represented bus service
         * or null if it wasn't set yet.
         * @return the {@link BusService#boundaryTime boundaryTime} of the represented service or null
         */
        DayTime getBoundaryTime();

        /**
         * Returns the {@link BusService#timeGap timeGap} of the represented bus station or -1
         * if it wasn't set yet.
         * @return the {@link BusService#timeGap timeGap} of the represented bus station or -1
         */
        int getTimeGap();


        /**
         * Indicates whether all the variables have been set. It is enough to decide whether this
         * object is valid or not, because the setter methods checks all the rules.
         * These rules consist of the following:<br>
         *     - {@link #setName(String)} name} of service must not be null, nor empty string<br>
         *     - {@link #setStopIds(int[])} stopIds} must not be null<br>
         *     - {@link #setTravelTimes(int[])} travelTimes} must not be null<br>
         *     - {@link #setFirstLeaveTime(DayTime)} firstLeaveTime} must not be null<br>
         *     - {@link #setBoundaryTime(DayTime)} boundaryTime} must not be null<br>
         *     - {@link #setTimeGap(int)} timeGap} must be greater than zero<br>
         *     - the length of {@link #setStopIds(int[])} stopIds} must be exactly one more than the
         *       length of {@link #setTravelTimes(int[])} travelTimes}
         *
         * @return true, if all the variables has been successfully set
         */
        boolean isValid();

    }//interface iTimetableArguments

}//interface
