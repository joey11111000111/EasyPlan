package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.core.exceptions.NameConflictException;
import com.github.joey11111000111.EasyPlan.core.exceptions.NoSelectedServiceException;
import com.github.joey11111000111.EasyPlan.core.util.DayTime;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by joey on 2015.11.01..
 */
public class Core {

    static final Logger LOGGER = LoggerFactory.getLogger(Core.class);
    static final String SAVE_PATH = System.getProperty("user.home") + "/.EasyPlan/savedServices";
    private List<BusService> services;
    // the three below are for the selected service
    private BusService selectedService;
    private BasicServiceData basicData;
    private TouchedStops touchedStops;

    public Core() {
        LOGGER.debug("creating Core instance...");
        selectedService = null;
        basicData = null;
        touchedStops = null;
        readSavedServices();
        LOGGER.debug("core instance successfully created");
    }

    // Wrapper methods for the selected bus service -----------------------------------------------
    private void setSelectedService(BusService service) {
        LOGGER.trace("called 'setSelectedService' with the service: "
                + (service == null ? "null" : service.getAppliedName()) );
        selectedService = service;
        if (service == null) {
            LOGGER.debug("'null service' is selected");
            basicData = null;
            touchedStops = null;
            return;
        }
        basicData = service.getCurrentServiceData();
        touchedStops = service.getCurrentStops();
        LOGGER.info("selected the '" + service.getAppliedName() + "' bus service");
    }
    public boolean hasSelectedService() {
        LOGGER.trace("called 'hasSelectedService");
        // if the selected service is not null, than the other two are not null too
        return selectedService != null;
    }
    private void checkSelection() {
        if (!hasSelectedService())
            throw new NoSelectedServiceException();
    }
    public boolean isModified() {
        LOGGER.trace("called isModified");
        checkSelection();
        return basicData.isModified() || touchedStops.isModified();
    }
    public boolean discardChanges() {
        LOGGER.trace("called discardChanges");
        checkSelection();
        if (selectedService.discardChanges()) {
            LOGGER.info("discarded changes of the '" + selectedService.getAppliedName()
                    + "' bus service");
            return true;
        }
        LOGGER.debug("tried to discard changes, but there were no modifications");
        return false;
    }
    public Timetable getCurrentTimetable() {
        LOGGER.trace("called getCurrentTimeTable");
        checkSelection();
        return selectedService.getTimeTable();
    }
    // wrapper methods for the basicServiceData instance
    public String getName() {
        LOGGER.trace("called getName");
        checkSelection();
        return basicData.getName();
    }
    public void setName(String name) {
        LOGGER.trace("called setName with name: " + name);
        checkSelection();
        basicData.setName(name);
        LOGGER.info("the name was set to '" + name + "'");
    }
    public int getTimeGap() {
        LOGGER.trace("called getTimeGap");
        checkSelection();
        return basicData.getTimeGap();
    }
    public void setTimeGap(int timeGap) {
        LOGGER.trace("called setTimeGap with timeGap: " + timeGap);
        checkSelection();
        basicData.setTimeGap(timeGap);
        LOGGER.info("the time gap was set to " + timeGap);
    }
    public DayTime getFirstLeaveTime() {
        LOGGER.trace("called getFirstLeaveTime");
        checkSelection();
        return basicData.getFirstLeaveTime();
    }
    public void setFirstLeaveTime(DayTime time) {
        LOGGER.trace("called setFirstLeaveTime with time: " + time);
        checkSelection();
        basicData.setFirstLeaveTime(time);
        LOGGER.info("the first leave time of was set to " + time);
    }
    public DayTime getBoundaryTime() {
        LOGGER.trace("called getBoundaryTime");
        checkSelection();
        return basicData.getBoundaryTime();
    }
    public void setBoundaryTime(DayTime time) {
        LOGGER.trace("called setBoundaryTime with time: " + time);
        checkSelection();
        basicData.setBoundaryTime(time);
        LOGGER.info("the boundary time was set to " + time);
    }

    // wrapper methods for the TouchedStops instance
    public void appendStop(int id) {
        LOGGER.trace("called appendStop with id: " + id);
        checkSelection();
        touchedStops.appendStop(id);
        LOGGER.info("the bus stop '" + id + "' was added to the service");
    }
    public void closeService() {
        LOGGER.trace("called closeService");
        checkSelection();
        touchedStops.closeService();
        LOGGER.info("the service is now closed, it ends at the station");
    }
    public boolean hasStops() {
        LOGGER.trace("called hasStops");
        checkSelection();
        return !touchedStops.isEmpty();
    }
    public boolean isClosed() {
        LOGGER.trace("called isClosed");
        checkSelection();
        return touchedStops.isClosed();
    }
    public boolean canUndo() {
        LOGGER.trace("called canUndo");
        checkSelection();
        return touchedStops.canUndo();
    }
    public int[] getStops() {
        LOGGER.trace("called getStops");
        checkSelection();
        return touchedStops.getStops();
    }
    public void clearStops() {
        LOGGER.trace("called clearStops");
        checkSelection();
        touchedStops.clear();
        LOGGER.info("all bus stops was removed from the bus service");
    }
    public void removeChainFrom(int fromId) {
        LOGGER.trace("called removeChainFrom with fromId: " + fromId);
        checkSelection();
        int count = getStops().length;
        touchedStops.removeChainFrom(fromId);
        if (count - getStops().length > 1)
            LOGGER.info("all the stops were removed starting with the last appearance of '" + fromId + "'");
        else
            LOGGER.info("removed the latest bus stop (" + fromId + ") from the bus service");
    }
    public void undo() {
        LOGGER.trace("called undo");
        checkSelection();
        if (canUndo()) {
            touchedStops.undo();
            LOGGER.info("the last bus stop modification operation was withdrawn");
        }
        LOGGER.debug("tried to undo, but there is nothing to undo");
    }
    public boolean isStationReachable() {
        LOGGER.trace("called isStationReachable");
        checkSelection();
        return touchedStops.isStationReachable();
    }
    public int[] getReachableStopIds() {
        LOGGER.trace("called getReachableStopsIds");
        checkSelection();
        return touchedStops.getReachableStopIds();
    }
    public int[] getTravelTimes() {
        LOGGER.trace("called getTravelTimes");
        checkSelection();
        return touchedStops.getTravelTimes();
    }
    // ---------------------------------------------------------------------------------------



    public int getServiceCount() {
        LOGGER.trace("called getServiceCount");
        return services.size();
    }

    private void readSavedServices() {
        LOGGER.trace("called readSavedServices");
        services = new ArrayList<BusService>();
        FileInputStream fis;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(SAVE_PATH);
            ois = new ObjectInputStream(fis);
            try {
                while (true) {
                    BusService service = (BusService)ois.readObject();
                    service.initTransientFields();
                    services.add(service);
                }
            } catch (EOFException eofe) {
                LOGGER.debug("read " + getServiceCount() + " bus services from the save file");
            } catch (ClassNotFoundException cnfe) {
                services.clear();
                LOGGER.error("save file is corrupted, ignoring saved bus services");
                return;
            }
        } catch (FileNotFoundException fnfe) {
            LOGGER.debug("there aren't any saved bus services");
        } catch (IOException ioe) {
            LOGGER.error("I/O exception happened while reading saves: ", ioe);
        } finally {
            if (ois != null)
                try {
                    ois.close();
                } catch (IOException ioe) {
                    LOGGER.error("I/O exception happened while trying to close the ObjectInputStream", ioe);
                }
        }
    }//readSavedServices

    public void saveServices() {
        LOGGER.trace("called saveServices");
        // When there are no services, the reader will know that by not finding the save file
        LOGGER.info("saving " + getServiceCount() + " bus services");
        if (services.size() == 0) {
            File saveFile = new File(SAVE_PATH);
            if (saveFile.exists())
                if (!saveFile.delete()) {
                    LOGGER.error("couldn't delete save file");
                    return;
                }
            return;
        }


        FileOutputStream fos;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(SAVE_PATH);
            oos = new ObjectOutputStream(fos);
            for (BusService service : services)
                oos.writeObject(service);
        }
         catch (IOException ioe) {
             LOGGER.error("I/O exception happened while saving the services", ioe);
        } finally {
           if (oos != null)
               try {
                   oos.close();
               } catch (IOException ioe) {
                   LOGGER.error("cannot close ObjectOutputStream", ioe);
               }
        }
    }//writeServices

    public void createNewService() {
        LOGGER.trace("called createNewService");
        // if there already is a service called "new service" then do nothing
        for (int i = 0; i < services.size(); i++)
            if (services.get(i).getAppliedName().equals("new service")) {
                LOGGER.warn("there is already one 'new service', cannot add another now");
                return;
            }

        BusService newService = new BusService();
        services.add(newService);
        setSelectedService(newService);
        LOGGER.info("added and selected a new bus service");
    }

    public void deleteSelectedService() {
        LOGGER.trace("called deleteSelectedService");
        if (!hasSelectedService()) {
            LOGGER.warn("there isn't a selected service to delete");
            return;
        }
        LOGGER.info("removing the bus service '" + selectedService.getAppliedName()
                + "' and selecting the 'null service'");
        services.remove(selectedService);
        setSelectedService(null);
    }

    public void selectService(String name) {
        LOGGER.trace("called selectService with name: " + name);
        if (name == null) {
            LOGGER.warn("given name is null");
            throw new NullPointerException("the name of the selected service cannot be null");
        }
        if (hasSelectedService())
            if (name.equals(selectedService.getAppliedName())) {
                LOGGER.debug("the new selected service is the same as the currently selected service");
                return;
            }

        for (int i = 0; i < services.size(); i++) {
            BusService service = services.get(i);
            if (service.getAppliedName().equals(name)) {
                setSelectedService(service);
                return;
            }
        }
        LOGGER.warn("a bus service with the given name '" + name + "' doesn't exist");
        throw new IllegalArgumentException("the bus service with the given name '"
                + name + "' doesn't exist");
    }

    public String[] getServiceNames() {
        LOGGER.trace("called getServiceNames");
        String[] names = new String[services.size()];
        for (int i = 0; i < names.length; i++)
            names[i] = services.get(i).getAppliedName();
        return names;
    }

    public void applyChanges() throws NameConflictException {
        LOGGER.trace("called applyChanges");
        // if there is nothing to change or change to, then do nothing
        if (!hasSelectedService()) {
            LOGGER.warn("cannot apply changes, there isn't a selected bus service");
            return;
        }
        if (!isModified())
            return;

        String name = getName();
        for (int i = 0; i < services.size(); i++) {
            BusService service = services.get(i);
            if (service == selectedService)
                continue;
            String serviceName = service.getAppliedName();
            if (name.equals(serviceName)) {
                LOGGER.warn("name conflict, the new name '" + name + "' is already in use");
                throw new NameConflictException("given name '" + name + "' is already in use");
            }
        }

        LOGGER.info("applying changes to the service '" + selectedService.getAppliedName()
                + "' (old name)");
        selectedService.applyChanges();
    }

    public Timetable[] getAllTimetables() {
        LOGGER.trace("called getAllTimetables");
        Timetable[] tables = new Timetable[services.size()];
        for (int i = 0; i < tables.length; i++)
            tables[i] = services.get(i).getTimeTable();
        return tables;
    }

}//class
