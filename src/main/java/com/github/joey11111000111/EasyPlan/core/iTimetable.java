package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.util.DayTime;

import java.util.List;

/**
 * Created by joey on 3/24/16.
 */
public interface iTimetable {

    String getServiceName();
    List<iStopTimes> getStopTimes();
    int getBusCount();
    int getTimeGap();
    DayTime getTotalTravelTime();


    interface iStopTimes {
        String getID();
        List<DayTime> getTimes();
    }




}//interface
