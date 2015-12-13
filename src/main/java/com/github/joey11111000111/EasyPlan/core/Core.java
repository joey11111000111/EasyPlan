package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.core.exceptions.NameConflictException;
import com.github.joey11111000111.EasyPlan.core.exceptions.NoSelectedServiceException;
import com.github.joey11111000111.EasyPlan.util.DayTime;

import java.io.*;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Core is the controller class for all the background actions of the application.
 * Except for the user interface, every change is made through this class.
 * It offer methods to manage the bus services, including saving and reading them.
 */
public class Core {

    static final Logger LOGGER = LoggerFactory.getLogger(Core.class);
    static final String SAVE_PATH = System.getProperty("user.home") + "/.EasyPlan/savedServices";
    private boolean saved;
    private SortedMap<String, BusService> services;
    // the three below are for the selected service
    private BusService selectedService;
    private BasicServiceData basicData;
    private TouchedStops touchedStops;

    /**
     * Creates a new instance filled with default settings. The reading of saved bus services
     * happens here as well. The default settings are:
     *   - the 'null service' is selected
     *   - the services are considered to be saved
     */
    public Core() {
        saved = true;
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

    /**
     * Returns true if there is an actual bus service selected
     * @return true if there is an actual bus service selected
     */
    public boolean hasSelectedService() {
        LOGGER.trace("called 'hasSelectedService");
        // if the selected service is not null, than the other two are not null too
        return selectedService != null;
    }
    private void checkSelection() {
        if (!hasSelectedService())
            throw new NoSelectedServiceException();
    }
    /**
     * Return true if there are new, unapplied modifications in either of the stop list
     * or in the basic data in the selected bus service.
     * @return true, if there are unapplied modifications
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    public boolean isModified() {
        LOGGER.trace("called isModified");
        checkSelection();
        return basicData.isModified() || touchedStops.isModified();
    }

    /**
     * Returns true, if there aren't any applied modifications, that are not saved to the save file
     * @return true, if there aren't any applied modifications, that are not saved to the save file
     */
    public boolean isSaved() {
        return saved;
    }

    /**
     * Returns the selected bus service to the state, where the last call to 'applyChanges' happened.
     * @return true, if there were any new changes to discard
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
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
    /**
     * Returns a Timetable object that shows the arrive times to all the touched
     * stops of the selected bus service.
     * @return the Timetable of the selected bus service
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    public Timetable getCurrentTimetable() {
        LOGGER.trace("called getCurrentTimeTable");
        checkSelection();
        return selectedService.getTimeTable();
    }
    // wrapper methods for the basicServiceData instance ---------------------------------

    /**
     * Returns the (possibly unapplied) name of the selected bus service. It can differ from the name
     * returned by the "getServiceNames", if the name was changes but not applied yet.
     * @return the latest name of the bus service
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    public String getName() {
        LOGGER.trace("called getName");
        checkSelection();
        return basicData.getName();
    }

    /**
     * Sets the name of the selected bus service. The new name is not registered to the
     * bus service until the call of the "applyChanges" method.
     * If the new name is already among the applied names of the other bus services,
     * than the "applyChanges" will throw a NameConflictException.
     * @param name the new name of the bus service
     * @throws NullPointerException when the given string is null
     * @throws IllegalArgumentException when received an empty string
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    public void setName(String name) {
        LOGGER.trace("called setName with name: " + name);
        checkSelection();
        basicData.setName(name);
        LOGGER.info("the name was set to '" + name + "'");
    }

    /**
     * Returns the latest (possibly unapplied) value of the minutes until the following
     * bus of the selected service leaves the station.
     * @return the time gap between two following buses in minutes
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    public int getTimeGap() {
        LOGGER.trace("called getTimeGap");
        checkSelection();
        return basicData.getTimeGap();
    }

    /**
     * Sets the minutes to wait before the following bus of the selected service leaves the station
     * @param timeGap the new time gap between two following buses in minutes
     * @throws IllegalArgumentException when the given minutes are less then 1
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    public void setTimeGap(int timeGap) {
        LOGGER.trace("called setTimeGap with timeGap: " + timeGap);
        checkSelection();
        basicData.setTimeGap(timeGap);
        LOGGER.info("the time gap was set to " + timeGap);
    }

    /**
     * Returns the (possibly unapplied) time of the day when the first bus of the
     * selected bus service leaves the station.
     * @return the leave time of the first bus of the service
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    public DayTime getFirstLeaveTime() {
        LOGGER.trace("called getFirstLeaveTime");
        checkSelection();
        return basicData.getFirstLeaveTime();
    }

    /**
     * Sets the time of the day when the first bus of the selected bus service leaves the bus station.
     * @param time the new time of the day when the first bus of the bus service leaves the station.
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    public void setFirstLeaveTime(DayTime time) {
        LOGGER.trace("called setFirstLeaveTime with time: " + time);
        checkSelection();
        basicData.setFirstLeaveTime(time);
        LOGGER.info("the first leave time of was set to " + time);
    }

    /**
     * Returns the (possibly unapplied) time of the day after which no buses
     * of the selected bus service will leave the bus station.
     * @return the time of the day after which no more buses will leave the station
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    public DayTime getBoundaryTime() {
        LOGGER.trace("called getBoundaryTime");
        checkSelection();
        return basicData.getBoundaryTime();
    }

    /**
     * Sets the time of the day after which no buses of the selected bus service
     * will leave the bus station.
     * @param time the new time of the day after which no more buses will leave the station
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    public void setBoundaryTime(DayTime time) {
        LOGGER.trace("called setBoundaryTime with time: " + time);
        checkSelection();
        basicData.setBoundaryTime(time);
        LOGGER.info("the boundary time was set to " + time);
    }

    // wrapper methods for the TouchedStops instance --------------------------

    /**
     * Appends the given bus stop at the end of the stop list of the selected bus service.
     * @param id the id of the bus stop that should be appended
     * @throws IllegalStateException if the bus service is already closed (finished)
     * @throws IllegalArgumentException if the bus stop with the given id:
     *          - doesn't exist
     *          - represents the bus station (it must be added in a separate way)
     *          - is not reachable from the previous bus stop
     *          - has already added twice
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    public void appendStop(int id) {
        LOGGER.trace("called appendStop with id: " + id);
        checkSelection();
        touchedStops.appendStop(id);
        LOGGER.info("the bus stop '" + id + "' was added to the service");
    }

    /**
     * Returns true if the selected bus service returns to the station at the end of the way, it is the last
     * (but not only) bus stop.
     * @return true, if there is it least one bus stop in the selected bus service
     * and the last bus stop is the bus station
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    public boolean isClosed() {
        LOGGER.trace("called isClosed");
        checkSelection();
        return touchedStops.isClosed();
    }

    /**
     * Returns true if there are new, unsaved modifications in the selected bus service
     * @return true, if there are unsaved modification in the selected bus service
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    public boolean canUndo() {
        LOGGER.trace("called canUndo");
        checkSelection();
        return touchedStops.canUndo();
    }

    /**
     * Returns an array that contains the ids of the touched bus stops of the selected bus service.
     * The order is the same as in the bus stop list (order of append).
     * @return an array containing the ids of the touched bus stops in the order of append
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    public int[] getStops() {
        LOGGER.trace("called getStops");
        checkSelection();
        return touchedStops.getStops();
    }

    /**
     * Returns the number of touched stops of the service. It can never be less then 0,
     * because the bus station, as the starting point is always in the list, as the first stop.
     * @return the number of touched bus stops, included the start from the bus station
     */
    public int getStopCount() {
        LOGGER.trace("called getStopCount");
        checkSelection();
        return touchedStops.getStopCount();
    }

    /**
     * Returns the id of the last bus stop of the bus service. If there are no bus stops added,
     * it returns the id of the bus station
     * @return the id of the last bus stop in the list
     */
    public int getLastStop() {
        LOGGER.trace("called getLastStop");
        checkSelection();
        return touchedStops.getLastStop();
    }

    /**
     * Removes all the bus stops from the stop list of the selected bus service.
     * Calling this method when there aren't any bus stops added has no effect.
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    public void clearStops() {
        LOGGER.trace("called clearStops");
        checkSelection();
        touchedStops.clear();
        LOGGER.info("all bus stops was removed from the bus service");
    }

    /**
     * Removes the last occurence of a bus stop from the selected service with all the following
     * bus stops.
     * @param fromId the bus stop from which the removal starts (the given bus stop is
     *               included too)
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
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

    /**
     * Discards the latest modification of the stop list of the selected service, that is not saved already.
     * @throws IllegalStateException if there aren't any unsaved modifications
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    public void undo() {
        LOGGER.trace("called undo");
        checkSelection();
        if (canUndo()) {
            touchedStops.undo();
            LOGGER.info("the last bus stop modification operation was withdrawn");
        }
        LOGGER.debug("tried to undo, but there is nothing to undo");
    }

    /**
     * Returns the ids of all the bus stops that can be the next bus stop of the selected bus service.
     * A bus stop is only included if it is reachable and hasn't been added twice already.
     * @return the ids of all the bus stops that can be the next bus stop of the selected bus service
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    public int[] getReachableStopIds() {
        LOGGER.trace("called getReachableStopsIds");
        checkSelection();
        return touchedStops.getReachableStopIds();
    }

    /**
     * Returns an array that contains the minutes that it takes to travel to
     * each bus stop of the selected service. The station is included, when the bus service is closed.
     * All the travel times are relative to when the bus leaves the bus station.
     * So the travel to the second stop means the travel from the station to the
     * first bus stop plus to travel time from the first bus stop to the second
     * bus stop.
     * @return an array with the travel times to each touched bus stops, relative
     *           to when the bus leaves the station.
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    public int[] getTravelTimes() {
        LOGGER.trace("called getTravelTimes");
        checkSelection();
        return touchedStops.getTravelTimes();
    }
    // ---------------------------------------------------------------------------------------


    /**
     * Returns the number of bus services.
     * @return the number of bus services
     */
    public int getServiceCount() {
        LOGGER.trace("called getServiceCount");
        return services.size();
    }

    private void readSavedServices() {
        LOGGER.trace("called readSavedServices");
        services = new TreeMap<String, BusService>();
        FileInputStream fis;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(SAVE_PATH);
            ois = new ObjectInputStream(fis);
            try {
                while (true) {
                    BusService service = (BusService)ois.readObject();
                    service.initTransientFields();
                    services.put(service.getAppliedName(), service);
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

    /**
     * Saves all the bus services into the save file. If a bus service was modified, than only the applied
     * changes are saved.
     */
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
            for (BusService service : services.values())
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
        saved = true;
    }//saveServices

    /**
     * Creates a new bus service, filled with default values. The default name value is always the same,
     * and since all applied service names must be unique, it is not possible to create more new bus services
     * without modifying the name of the previous bus services.
     * Trying to do it has no effect.
     */
    public void createNewService() {
        LOGGER.trace("called createNewService");
        // if there already is a service with the default service name then do nothing
        if (services.containsKey(BusService.DEFAULT_NAME)) {
            LOGGER.warn("there is already one '" + BusService.DEFAULT_NAME + "', cannot add another now");
            return;
        }

        BusService newService = new BusService();
        services.put(newService.getAppliedName(), newService);
        setSelectedService(newService);
        LOGGER.info("added and selected a new bus service");
    }

    /**
     * Removes the selected service, without a chance to bring it back.
     * After deletion, the null service is selected.
     * Does nothing when there isn't a selected service.
     */
    public void deleteSelectedService() {
        LOGGER.trace("called deleteSelectedService");
        if (!hasSelectedService()) {
            LOGGER.warn("there isn't a selected service to delete");
            return;
        }

        String name = selectedService.getAppliedName();
        if (services.remove(name) != null)
            LOGGER.info("removed the bus service '" + name
                    + "' and selecting the 'null service'");
        else
            LOGGER.warn("could not remove the bus service '" + name + "'");

        setSelectedService(null);
    }

    /**
     * Selects the service that has the given name.
     * @param name the name of the service that should be selected.
     * @throws NullPointerException when the given string is null
     * @throws IllegalArgumentException when there isn't a service with the given name
     */
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

        if (!services.containsKey(name)) {
            LOGGER.warn("a bus service with the given name '" + name + "' doesn't exist");
            throw new IllegalArgumentException("the bus service with the given name '"
                    + name + "' doesn't exist");
        }

        setSelectedService(services.get(name));
    }

    /**
     * Returns the applied name of all the existing bus services.
     * The returned names are in alphabetic order.
     * @return the applied name of all the existing bus services.
     */
    public String[] getServiceNames() {
        LOGGER.trace("called getServiceNames");
        return services.keySet().toArray(new String[0]);
    }

    /**
     * Applies/registers all the new modifications is there were any.
     * @throws NameConflictException when the new name of the bus service is
     *          already in use as an applied name of another bus service.
     */
    public void applyChanges() throws NameConflictException {
        LOGGER.trace("called applyChanges");
        // if there is nothing to change or change to, then do nothing
        if (!hasSelectedService()) {
            LOGGER.warn("cannot apply changes, there isn't a selected bus service");
            return;
        }
        if (!isModified())
            return;

        // if the old and new names are different, than the new name's uniqueness must be checked
        String oldName = selectedService.getAppliedName();
        String newName = getName();
        if (!oldName.equals(newName)) {
            if (services.containsKey(newName)) {
                LOGGER.warn("name conflict, the new name '" + newName + "' is already in use");
                throw new NameConflictException("given name '" + newName + "' is already in use");
            }
            // change the key of the service
            services.put(newName, services.remove(oldName));
        }

        selectedService.applyChanges();
        saved = false;
        LOGGER.info("applied changes to the service '" + oldName + "' (old name)");
    }

    /**
     * Returns a Timetable object for every bus service that shows the arrive times to all the touched
     * stops of the bus services. If there aren't any bus services, then it returns a zero-length array.
     * @return the Timetables of the bus services or a zero-length array if there aren't any
     */
    public Timetable[] getAllTimetables() {
        LOGGER.trace("called getAllTimetables");
        Timetable[] tables = new Timetable[services.size()];
        BusService[] allServices = services.values().toArray(new BusService[0]);
        for (int i = 0; i < tables.length; i++)
            tables[i] = allServices[i].getTimeTable();
        return tables;
    }

}//class
