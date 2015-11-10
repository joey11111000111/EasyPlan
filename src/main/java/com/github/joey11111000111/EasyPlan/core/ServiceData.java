package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.core.util.DayTime;

/**
 * Created by joey on 2015.11.10..
 */
public class ServiceData {

    public static class NoSelectedServiceException extends RuntimeException {
        public NoSelectedServiceException() {
            super();
        }
    }

    private BasicServiceData basicData = null;
    private TouchedStops touchedStops = null;

    private void checkSelection() {
        if (!hasSelectedService())
            throw new NoSelectedServiceException();
    }

    void setBasicData(BasicServiceData bsd) {
        checkSelection();
        basicData = bsd;
    }
    void setTouchedStops(TouchedStops ts) {
        checkSelection();
        touchedStops = ts;
    }

    public boolean hasSelectedService() {
        return basicData == null || touchedStops == null;
    }

    public void markAsSaved() {
        checkSelection();
        basicData.markAsSaved();
        touchedStops.markAsSaved();
    }

    // basic service data wrapper methods
    public String getName() {
        checkSelection();
        return basicData.getName();
    }
    public void setName(String name) {
        checkSelection();
        basicData.setName(name);
    }

    public int getTimeGap() {
        checkSelection();
        return basicData.getTimeGap();
    }
    public void setTimeGap(int timeGap) {
        checkSelection();
        basicData.setTimeGap(timeGap);
    }

    public DayTime getFirstLeaveTime() {
        checkSelection();
        return basicData.getFirstLeaveTime();
    }
    public void setFirstLeaveTime(DayTime time) {
        checkSelection();
        basicData.setFirstLeaveTime(time);
    }
    public DayTime getBoundaryTime() {
        checkSelection();
        return basicData.getBoundaryTime();
    }
    public void setBoundaryTime(DayTime time) {
        checkSelection();
        basicData.setBoundaryTime(time);
    }

    // touched stops wrapper methods
    public void appendStop(int i) {
        checkSelection();
        touchedStops.appendStop(i);
    }

    public boolean hasStops() {
        checkSelection();
        return touchedStops.isEmpty();
    }

    public boolean isClosed() {
        checkSelection();
        return touchedStops.isClosed();
    }

    public boolean canUndo() {
        checkSelection();
        return touchedStops.canUndo();
    }

    public int[] getStops() {
        checkSelection();
        return touchedStops.getStops();
    }

    public void clearStops() {
        checkSelection();
        touchedStops.clear();
    }

    public void removeChainFrom(int fromId) {
        checkSelection();
        touchedStops.removeChainFrom(fromId);
    }

    public void undo() {
        checkSelection();
        touchedStops.undo();
    }

    public boolean isStationReachable() {
        checkSelection();
        return touchedStops.isStationReachable();
    }

    public int[] getReachableStopIds() {
        checkSelection();
        return touchedStops.getReachableStopIds();
    }

    public int[] getTravelTimes() {
        checkSelection();
        return touchedStops.getTravelTimes();
    }



}//class
