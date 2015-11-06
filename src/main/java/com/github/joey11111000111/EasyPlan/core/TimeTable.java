package com.github.joey11111000111.EasyPlan.core;

import java.util.*;

/**
 * Created by joey on 2015.11.06..
 */
public class TimeTable {

    public static class StopTimes {
        public final int id;
        public final List<SimpleTime> times;

        private StopTimes(int id, List<SimpleTime> times) {
            try {
                times.add(new SimpleTime(0, 0));
                throw new IllegalArgumentException("given list must be unmodifiable");
            } catch (UnsupportedOperationException uoe) {}

            this.id = id;
            this.times = times;
        }
    }

    public static class TimeTableArguments {
        private String name;
        private int[] stopIds;
        private int[] travelTimes;
        private SimpleTime firstLeaveTime;
        private SimpleTime boundaryTime;
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
            this.name = name;
        }

        public void setStopIds(int[] stopIds) {
            if (stopIds == null)
                throw new NullPointerException("stopIds must not be null");
            for (int i = 0; i < stopIds.length; i++)
                if (!BusStop.validId(stopIds[i]))
                    throw new IllegalArgumentException("invalid array element");
            this.stopIds = stopIds;
        }

        public void setTravelTimes(int[] travelTimes) {
            if (travelTimes == null)
                throw new NullPointerException("travelTimes must not be null");
            for (int i = 0; i < travelTimes.length; i++)
                if (travelTimes[i] < 1)
                    throw new IllegalArgumentException("invalid array element");
            this.travelTimes = travelTimes;
        }

        public void setFirstLeaveTime(SimpleTime firstLeaveTime) {
            if (firstLeaveTime == null)
                throw new NullPointerException("firstLeaveTime must not be null");
            // create deep copy
            this.firstLeaveTime = new SimpleTime(firstLeaveTime);
        }

        public void setBoundaryTime(SimpleTime boundaryTime) {
            if (boundaryTime == null)
                throw new NullPointerException("boundaryTime must not be null");
            // create deep copy
            this.boundaryTime = new SimpleTime(boundaryTime);
        }

        public void setTimeGap(int timeGap) {
            if (timeGap < 1)
                throw new IllegalArgumentException("timeGap must be positive");
            this.timeGap = timeGap;
        }

        public boolean isValid() {
            return name != null && stopIds != null && travelTimes != null
                    && firstLeaveTime != null && boundaryTime != null && timeGap > 0;
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

    public final String name;
    public final List<StopTimes> stopTimes;

    public static TimeTable newInstance(TimeTableArguments tta) {
        if (!tta.isValid())
            throw new IllegalArgumentException("given arguments are not in a ready state: "
                    + System.getProperty("line.separator")
                    + tta.toString());

        return new TimeTable(tta.name, tta.stopIds, tta.travelTimes, tta.firstLeaveTime,
                    tta.boundaryTime, tta.timeGap);
    }


    private TimeTable(String name, int[] stopIds, int[] travelTimes,
                     SimpleTime firstLeaveTime, SimpleTime boundaryTime, int timeGap) {

        this.name = name;
        boolean closed = travelTimes.length > stopIds.length;
        int size = travelTimes.length + 1;
        List<StopTimes> allStopTimes = new ArrayList<StopTimes>(size);

        // convert time to minutes
        int firstLeaveMinutes = firstLeaveTime.getTimeAsMinutes();
        int boundaryMinutes = boundaryTime.getTimeAsMinutes();

        // calculate the number as buses to go since firstLeaveTime until boundaryTime
        int busCount = boundaryMinutes - firstLeaveMinutes;
        busCount = Math.abs(busCount);
        busCount /= timeGap; // there must not be any bus after the boundaryTime, so the remaining is ignored
        if (busCount == 0)   // at least one bus will go, the first bus is independent of the time gap
            busCount = 1;

        // add the starting times at the station
        StopTimes st = getStopTimes(BusStop.getIdOfStation(), busCount,
                timeGap, firstLeaveMinutes, 0);
        allStopTimes.add(st);
        // add the times at the bus stops
        for (int i = 0; i < stopIds.length; i++) {
            st = getStopTimes(stopIds[i], busCount, timeGap, firstLeaveMinutes, travelTimes[i]);
            allStopTimes.add(st);
        }
        // add the 'return to the station', if the service is closed
        if (closed) {
            st = getStopTimes(BusStop.getIdOfStation(), busCount, timeGap,
                    firstLeaveMinutes, travelTimes[travelTimes.length - 1]);
            allStopTimes.add(st);
        }

        stopTimes = Collections.unmodifiableList(allStopTimes);
    }

    private StopTimes getStopTimes(int id, int busCount, int timeGap, int firstLeaveMinutes, int travelTime) {
        List<SimpleTime> list = new ArrayList<SimpleTime>(busCount);
        for (int i = 0; i < busCount; i++) {
            int minutes = i * timeGap + firstLeaveMinutes + travelTime;
            list.add(new SimpleTime(minutes));
        }
        list = Collections.unmodifiableList(list);
        return new StopTimes(id, list);
    }
}//class
