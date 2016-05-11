package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.util.DayTime;

import java.util.*;

/**
 * Implements all functionality of the {@link iTimetable} interface and does nothing more.
 */
public class Timetable implements iTimetable {

    /**
     * Implements all functionality of the {@link iStopTimes iStopTimes} interface.
     * The only method in addition is a {@link #toString()}.
     */
    public static class StopTimes implements iStopTimes {

        /**
         * The id of the bus stop, whose arrive times are recorded in the list.
         */
        private String id;
        /**
         * Contains all the arrive times to this bus stop, including all the buses of the represented service.
         * This list always has at least one element.
         */
        private List<DayTime> times;

        /**
         * {@inheritDoc}
         */
        @Override
        public String getID() {
            return id;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<DayTime> getTimes() {
            return times;
        }

        /**
         * Creates an instance filled with the given data.
         * All bus services create their own timetable objects, but no one else, that's why this constructor is
         * package private.
         * @param id the id of the bus stop whose arrive times is stored in the instance
         * @param times an unmodifiable list of the arrive times to the given bus stop
         * @throws IllegalArgumentException when the list is empty or modifiable
         */
        StopTimes(int id, List<DayTime> times) {
            if (times.size() == 0)
                throw new IllegalArgumentException("there must be at least one stop time for a stop or a station");
            try {
                times.add(new DayTime(0, 0));
                throw new IllegalArgumentException("given list must be unmodifiable");
            } catch (UnsupportedOperationException uoe) {}

            this.id = Integer.toString(id);
            this.times = times;
        }

        /**
         * Returns a string representing this object in a form of "id: 'id'  stop times: 'time'  'time' ...".
         * @return a string representing this object
         */
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("id: ").append(id).append("  stop times: ");
            for (DayTime st : times)
                sb.append(st).append("  ");
            return sb.toString();
        }
    }

    /**
     * The sole purpose of this class is to help creating {@link Timetable} instances.
     * Since the constructor of the Timetable class requires quite a lot of arguments
     * which can be hard to correctly dealt with, this class can make it easy and safe.
     * It checks the validity of the arguments and whether all the arguments are given or not.
     * Also they can be given in any order, even more than once.
     */
    static class TimetableArguments implements iTimetableArguments {

        /**
         * Buffer variable for {@link Timetable#name}.
         * See {@link #setName(String)} for more information.
         */
        private String name;

        /**
         * The id -s of all the touched stops of the represented service in the order of append.
         * See {@link #setStopIds(int[])} for more information.
         */
        private int[] stopIds;

        /**
         * The time (in minutes) that it takes to reach the stops along way from the bus station.
         * See {@link #setTravelTimes(int[])} for more information.
         */
        private int[] travelTimes;

        /**
         * The {@link BusService#firstLeaveTime firstLeaveTime} of the represented bus service.
         * See {@link #setFirstLeaveTime(DayTime)} for more information.
         */
        private DayTime firstLeaveTime;

        /**
         * The {@link BusService#boundaryTime boundaryTime} of the represented bus service.
         * See {@link #setBoundaryTime(DayTime)} for more information.
         */
        private DayTime boundaryTime;

        /**
         * The time in minutes to wait before the subsequent bus of the service leaves the station.
         * See {@link #setTravelTimes(int[])} for more information.
         */
        private int timeGap;


        /**
         * Creates a new instance completely filled with invalid data.
         * The object gets valid once all data is set with valid arguments.
         */
        public TimetableArguments() {
            name = null;
            stopIds = null;
            travelTimes = null;
            firstLeaveTime = null;
            boundaryTime = null;
            timeGap = -1;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setName(String name) {
            if (name == null)
                throw new NullPointerException("name must not be null");
            if ("".equals(name))
                throw new IllegalArgumentException("empty string cannot be the name of a bus service");
            this.name = name;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setStopIds(int[] stopIds) {
            if (stopIds == null)
                throw new NullPointerException("stopIds must not be null");
            if (stopIds.length == 0)
                throw new IllegalArgumentException("stop list cannot be empty");
            if (stopIds[0] != 0)
                throw new IllegalArgumentException("stop list must start with the bus station");

            if (travelTimes != null && travelTimes.length != stopIds.length - 1)
                throw new IllegalArgumentException("the number of stop id -s must be one more "
                        + "than the number of travel times");

            for (int i = 1; i < stopIds.length; i++)
                if (!BusStop.validId(stopIds[i]))
                    throw new IllegalArgumentException("invalid array element");

            // TODO array-copy
            this.stopIds = new int[stopIds.length];
            for (int i = 0; i < stopIds.length; i++)
                this.stopIds[i] = stopIds[i];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setTravelTimes(int[] travelTimes) {
            if (travelTimes == null)
                throw new NullPointerException("travelTimes must not be null");

            if (stopIds != null && travelTimes.length != stopIds.length - 1)
                throw new IllegalArgumentException("the number of stop id -s must be one more "
                        + "than the number of travel times");

            for (int i = 0; i < travelTimes.length; i++)
                if (travelTimes[i] < 1)
                    throw new IllegalArgumentException("invalid array element");

            // create deep copy
            this.travelTimes = new int[travelTimes.length];
            for (int i = 0; i < travelTimes.length; i++)
                this.travelTimes[i] = travelTimes[i];
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setFirstLeaveTime(DayTime firstLeaveTime) {
            if (firstLeaveTime == null)
                throw new NullPointerException("firstLeaveTime must not be null");
            // create deep copy
            this.firstLeaveTime = new DayTime(firstLeaveTime);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setBoundaryTime(DayTime boundaryTime) {
            if (boundaryTime == null)
                throw new NullPointerException("boundaryTime must not be null");
            // create deep copy
            this.boundaryTime = new DayTime(boundaryTime);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setTimeGap(int timeGap) {
            if (timeGap < 1)
                throw new IllegalArgumentException("timeGap must be positive");
            this.timeGap = timeGap;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getName() {
            return name;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int[] getStopIds() {
            return stopIds;
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public int[] getTravelTimes() {
            return travelTimes;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DayTime getFirstLeaveTime() {
            return firstLeaveTime;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DayTime getBoundaryTime() {
            return boundaryTime;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getTimeGap() {
            return timeGap;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isValid() {
            return name != null && stopIds != null && travelTimes != null
                    && firstLeaveTime != null && boundaryTime != null
                    && timeGap > 0;
        }

    }//class TimetableArguments

    /**
     * Name of the represented bus service.
     */
    private String name;
    /**
     * Contains a StopTime instance for all the touched stops of the bus service (including the bus station).
     * The order of the instances is the same as the order of the touched stops in the bus service.
     */
    private List<iStopTimes> stopTimes;
    /**
     * Contains the number of buses that will leave the station between
     * {@link BusService#firstLeaveTime firstLeaveTime} and {@link BusService#boundaryTime boundaryTime}.
     */
    private int busCount;
    /**
     * The minutes to wait before the next bus of the service leaves the station after the previous one.
     */
    private int timeGap;
    /**
     * The time it takes for one bus of the service to go through all the touched bus stops.
     */
    private DayTime totalTravelTime;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServiceName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<iStopTimes> getStopTimes() {
        return stopTimes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBusCount() {
        return busCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTimeGap() {
        return timeGap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DayTime getTotalTravelTime() {
        return totalTravelTime;
    }

    /**
     * Creates and returns a new iTimetable - instance.
     * Since the iTimetable doesn't give modification methods, all the contained data
     * will be decided at the time of creation.
     * @param tta The data - container object used for the creation
     * @return a new iTimetable instance
     * @throws IllegalArgumentException if the given argument is invalid
     *          See {@link iTimetableArguments#isValid()} for more information.
     */
    static iTimetable createTimetable(iTimetableArguments tta) {
        if (!tta.isValid())
            throw new IllegalArgumentException("given arguments are not in a ready state: "
                    + System.getProperty("line.separator")
                    + tta.toString());

        return new Timetable(
                tta.getName(),
                tta.getStopIds(),
                tta.getTravelTimes(),
                tta.getFirstLeaveTime(),
                tta.getBoundaryTime(),
                tta.getTimeGap()
        );
    }

    /**
     * Constructor only used by the {@link #createTimetable(iTimetableArguments)} static factory method.
     * @param name {@link BusService#name name} of service
     * @param stopIds id -s of bus stops the represented service touches
     * @param travelTimes the minutes it takes to travel to each of the touched stops, relative to the station
     * @param firstLeaveTime the {@link BusService#firstLeaveTime time} of day when the
     *                       first bus of the service leaves the station
     * @param boundaryTime the {@link BusService#boundaryTime time} of day after which
     *                     no more buses of the represented service can leave the station
     * @param timeGap the {@link BusService#timeGap wait time} in minutes between two
     *                subsequent buses of the represented service
     */
    private Timetable(String name, int[] stopIds, int[] travelTimes,
                      DayTime firstLeaveTime, DayTime boundaryTime, int timeGap) {

        this.name = name;
        List<StopTimes> allStopTimes = new ArrayList<StopTimes>(stopIds.length);


        // convert time to minutes
        int firstLeaveMinutes = firstLeaveTime.getTimeAsMinutes();
        int boundaryMinutes = boundaryTime.getTimeAsMinutes();

        // calculate the number of buses to go since firstLeaveTime until boundaryTime
        int busCount;
        if (boundaryMinutes < firstLeaveMinutes)
            busCount = (boundaryMinutes + 24 * 60) - firstLeaveMinutes;
        else
            busCount = boundaryMinutes - firstLeaveMinutes;
        busCount = Math.abs(busCount);
        busCount /= timeGap; // there must not be any bus after the boundaryTime, so the remaining is ignored
        busCount += 1;       // because the first bus leaves at firstLeaveTime, with no time gap

        // add the starting times at the station
        StopTimes st = getStopTimes(0, busCount, timeGap, firstLeaveMinutes, 0);
        allStopTimes.add(st);
        // add the times at the bus stops
        for (int i = 1; i < stopIds.length; i++) {
            st = getStopTimes(stopIds[i], busCount, timeGap, firstLeaveMinutes, travelTimes[i-1]);
            allStopTimes.add(st);
        }

        // everything is added, the list is now completed
        stopTimes = Collections.unmodifiableList(allStopTimes);
        this.busCount = busCount;
        this.timeGap = timeGap;

        // the travel time to the last stop is the total travel time
        if (travelTimes.length > 0)
            totalTravelTime = new DayTime(travelTimes[travelTimes.length - 1]);
        else
            totalTravelTime = new DayTime(0, 0);
    }

    /**
     * Creates and returns a new {@link StopTimes} object that contains the id of the stop and all the the times
     * when a bus of the represented service stops at this bus stop, in ascending order.
     * @param id id of the represented bus stop
     * @param busCount the number of times a bus of the service stops at this bus stop
     * @param timeGap the minutes between two subsequent buses of the service stop at this bus stop
     * @param firstLeaveMinutes the {@link BusService#firstLeaveTime firstLeaveTime} of the represented
     *                          service converted into minutes
     * @param travelTime The time in minutes that it takes to travel from the bus station to this bus stop
     *                   through all the intervening bus stops. Acts as an offset value.
     * @return a new {@link StopTimes} object that contains the id of the given stop and the times
     *      of arriving buses of the represented service.
     */
    private StopTimes getStopTimes(int id, int busCount, int timeGap, int firstLeaveMinutes, int travelTime) {
        List<DayTime> list = new ArrayList<>(busCount);
        for (int i = 0; i < busCount; i++) {
            int minutes = i * timeGap + firstLeaveMinutes + travelTime;
            list.add(new DayTime(minutes, true));
        }
        list = Collections.unmodifiableList(list);
        return new StopTimes(id, list);
    }
}//class
