package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.util.DayTime;

import java.util.*;

/**
 * A Timetable gives information about the schedules of a certain bus service.
 * It shows all the time of all the touched stops when a bus of the service arrives.
 * The bus station is also included.
 * All instances are unmodifiable, and no instances can be created outside the package.
 * However, every bus service can return its own timetable.
 */
public class Timetable {

    /**
     * An instance of this class contains all the arrive times of the buses of the service
     * to a certain bus stop (can be the station as well). Instances are unmodifiable, and
     * cannot be created outside of the package. However, the returned TimeTable contains one
     * StopTimes instance for all the touched bus stops of the bus service.
     */
    public static class StopTimes {
        /**
         * the id of the bus stop, whose arrive times are recorded in the list
         */
        public final int id;
        /**
         * Contains all the arrive times to this bus stop, starting with the time when
         * the first bus leaves the station. This list always has at least one element.
         */
        public final List<DayTime> times;

        /**
         * Creates an instance filled with the given data.
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

            this.id = id;
            this.times = times;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("id: ").append(id).append("  stop times: ");
            for (DayTime st : times)
                sb.append(st).append("  ");
            return sb.toString();
        }
    }

    /**
     * The sole purpose of this class is to help to create a Timetable instance.
     * Since the constructor of the Timetable class requires quite a lot of arguments
     * which can be hard to correctly dealt with, this class can make it easy and safe.
     * It checks the validity of the arguments and whether all the arguments are given all or not.
     * Also they can be given in any order, even more than once.
     */
    static class TimeTableArguments {
        private String name;
        private int[] stopIds;
        private int[] travelTimes;
        private DayTime firstLeaveTime;
        private DayTime boundaryTime;
        private int timeGap;

        public TimeTableArguments() {
            name = null;
            stopIds = null;
            travelTimes = null;
            firstLeaveTime = null;
            boundaryTime = null;
            timeGap = -1;
        }

        public void setName(String name) {
            if (name == null)
                throw new NullPointerException("name must not be null");
            if ("".equals(name))
                throw new IllegalArgumentException("empty string cannot be the name of a bus service");
            this.name = name;
        }

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


            // create deep copy
            this.stopIds = new int[stopIds.length];
            for (int i = 0; i < stopIds.length; i++)
                this.stopIds[i] = stopIds[i];
        }

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

        public void setFirstLeaveTime(DayTime firstLeaveTime) {
            if (firstLeaveTime == null)
                throw new NullPointerException("firstLeaveTime must not be null");
            // create deep copy
            this.firstLeaveTime = new DayTime(firstLeaveTime);
        }

        public void setBoundaryTime(DayTime boundaryTime) {
            if (boundaryTime == null)
                throw new NullPointerException("boundaryTime must not be null");
            // create deep copy
            this.boundaryTime = new DayTime(boundaryTime);
        }

        public void setTimeGap(int timeGap) {
            if (timeGap < 1)
                throw new IllegalArgumentException("timeGap must be positive");
            this.timeGap = timeGap;
        }

        public boolean isValid() {
            return name != null && stopIds != null && travelTimes != null
                    && firstLeaveTime != null && boundaryTime != null
                    && timeGap > 0;
        }

        @Override
        public String toString() {
            return "TimeTableArguments{" +
                    "name='" + name + '\'' +
                    ", stopIds=" + Arrays.toString(stopIds) +
                    ", travelTimes=" + Arrays.toString(travelTimes) +
                    ", firstLeaveTime=" + firstLeaveTime +
                    ", boundaryTime=" + boundaryTime +
                    ", timeGap=" + timeGap +
                    '}';
        }
    }

    /**
     * name of the bus service
     */
    public final String name;
    /**
     * Contains a StopTime instance for all the touched stops of the bus service (including the bus station).
     * The order of the instances is the same as the order of the touched stops in the bus service.
     */
    public final List<StopTimes> stopTimes;
    /**
     * contains the number of buses that will leave the station in one day
     */
    public final int busCount;
    /**
     * the minutes before the next bus leaves the station after the previous one
     */
    public final int timeGap;
    /**
     * the time that it takes for one bus of the service to go through all the bus stops
     * (return to the station is included if happens in the service)
     */
    public final DayTime totalTravelTime;

    static Timetable newInstance(TimeTableArguments tta) {
        if (!tta.isValid())
            throw new IllegalArgumentException("given arguments are not in a ready state: "
                    + System.getProperty("line.separator")
                    + tta.toString());

        return new Timetable(tta.name, tta.stopIds, tta.travelTimes, tta.firstLeaveTime,
                    tta.boundaryTime, tta.timeGap);
    }


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

    private StopTimes getStopTimes(int id, int busCount, int timeGap, int firstLeaveMinutes, int travelTime) {
        List<DayTime> list = new ArrayList<DayTime>(busCount);
        for (int i = 0; i < busCount; i++) {
            int minutes = i * timeGap + firstLeaveMinutes + travelTime;
            list.add(new DayTime(minutes, true));
        }
        list = Collections.unmodifiableList(list);
        return new StopTimes(id, list);
    }
}//class
