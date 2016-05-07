package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.core.exceptions.NameConflictException;
import com.github.joey11111000111.EasyPlan.util.DayTime;

/**
 * Created by joey on 3/24/16.
 */
public interface Controller {

    void selectService(String serviceName);
    boolean hasSelectedService();
    boolean deleteSelectedService();
    boolean deleteService(String serviceName);
    void createNewService();

    void saveServices();
    boolean isSaved();
    boolean isModified();
    void applyChanges() throws NameConflictException;
    boolean discardChanges();

    iTimetable getTimetableOf(String serviceName);
    String getName();
    void setName(String serviceName);
    int getTimeGap();
    void setTimeGap(int timeGap);

    DayTime getFirstLeaveTime();
    void setFirstLeaveTime(DayTime time);
    void setFirstLeaveHour(int hour);
    void setFirstLeaveMinute(int minute);
    DayTime getBoundaryTime();
    void setBoundaryTime(DayTime time);
    void setBoundaryHour(int hour);
    void setBoundaryMinute(int minute);

    void appendStop(int id);
    int[] getStops();
    int getLastStop();
    void clearStops();
    void removeChainFrom(int id);
    int[] getReachableStopIds();

    boolean isClosed();
    boolean canUndo();
    void undo();

    int getStopCount();
    int[] getTravelTimes();
    int getServiceCount();
    String[] getServiceNames();


}//interface
