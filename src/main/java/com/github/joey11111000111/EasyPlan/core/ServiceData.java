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

    private BusService currentService;
    private BasicServiceData basicData;
    private TouchedStops touchedStops;

    public ServiceData() {
        currentService = null;
        basicData = null;
        touchedStops = null;
    }

    private void checkSelection() {
        if (!hasSelectedService())
            throw new NoSelectedServiceException();
    }

    void setCurrentService(BusService service) {
        currentService = service;
        if (service == null) {
            basicData = null;
            touchedStops = null;
            return;
        }
        basicData = service.getCurrentServiceData();
        touchedStops = service.getCurrentStops();
    }
    BusService getSelectedService() {
        return currentService;
    }

    public boolean hasSelectedService() {
        return currentService != null && basicData != null && touchedStops != null;
    }

    public void markAsSaved() {
        checkSelection();
        basicData.markAsSaved();
        touchedStops.markAsSaved();
    }
    public boolean isModified() {
        checkSelection();
        return basicData.isModified() || touchedStops.isModified();
    }

    public boolean discardChanges() {
        checkSelection();
        return currentService.discardChanges();
    }

    public Timetable getCurrentTimetable() {
        checkSelection();
        return currentService.getTimeTable();
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

    public void closeService() {
        checkSelection();
        touchedStops.closeService();
    }

    public boolean hasStops() {
        checkSelection();
        return !touchedStops.isEmpty();
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
