package com.github.joey11111000111.EasyPlan.core;

import java.util.*;

/**
 * Created by joey on 2015.11.06..
 */
public class TimeTable {

    public static class StopTimes {
        public final int id;
        public final List<DayTime> times;

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

    public static class TimeTableArguments {
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
            this.name = name;
        }

        public void setStopIds(int[] stopIds) {
            if (stopIds == null)
                throw new NullPointerException("stopIds must not be null");

            for (int i = 0; i < stopIds.length; i++)
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
                    && travelTimes.length - stopIds.length < 2
                    && travelTimes.length - stopIds.length >= 0
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

    public final String name;
    public final List<StopTimes> stopTimes;
    public final int busCount;
    public final int timeGap;
    public final DayTime totalTravelTime;

    public static TimeTable newInstance(TimeTableArguments tta) {
        if (!tta.isValid())
            throw new IllegalArgumentException("given arguments are not in a ready state: "
                    + System.getProperty("line.separator")
                    + tta.toString());

        return new TimeTable(tta.name, tta.stopIds, tta.travelTimes, tta.firstLeaveTime,
                    tta.boundaryTime, tta.timeGap);
    }


    private TimeTable(String name, int[] stopIds, int[] travelTimes,
                     DayTime firstLeaveTime, DayTime boundaryTime, int timeGap) {

        this.name = name;
        boolean closed = travelTimes.length > stopIds.length;
        int size = travelTimes.length + 1;
        List<StopTimes> allStopTimes = new ArrayList<StopTimes>(size);


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

        // everything is added, the list is now completed
        stopTimes = Collections.unmodifiableList(allStopTimes);
        this.busCount = busCount;
        this.timeGap = timeGap;

        // calculate the total travel time
        int firstMinutes = stopTimes.get(0).times.get(0).getTimeAsMinutes();
        int lastIndex = stopTimes.size() - 1;
        int lastMinutes = stopTimes.get(lastIndex).times.get(0).getTimeAsMinutes();
        totalTravelTime = new DayTime(lastMinutes - firstMinutes);
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
