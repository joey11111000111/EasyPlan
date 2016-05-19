package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.core.exceptions.NameConflictException;
import com.github.joey11111000111.EasyPlan.core.exceptions.NoSelectedServiceException;
import com.github.joey11111000111.EasyPlan.util.DayTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Implementation of the {@link Controller} interface. Offers no more public services.
 * Logging is included. This is the root object to save.
 */
@XmlRootElement(name = "AllServices")
@XmlAccessorType(XmlAccessType.FIELD)
public class Core implements Controller {

    /**
     * The <a href="http://www.slf4j.org/">slf4j</a> logger object for this class.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(Core.class);

    /**
     * True, if the {@link #applyChanges() applied} data is the same as the data
     * in the save file.
     */
    @XmlTransient private boolean saved;

    /**
     * All services are stored in this variable.
     * The key is the name of the service which is stored in the value part.
     */
    private SortedMap<String, BusService> services;

    /**
     * The currently checked out service. If there isn't a selected service,
     * no modifications can be done. All modifications apply to the selected service.
     */
    @XmlTransient private BusService selectedService;

    /**
     * The basic data buffer of the selected bus service.
     */
    @XmlTransient private BasicServiceData basicData;

    /**
     * The touched stops buffer of the selected bus service.
     */
    @XmlTransient private TouchedStops touchedStops;

    /**
     * Creates a new instance filled with default values.
     * These values are:<br>
     *   - no service is selected (null)<br>
     *   - the buffers of the selected service are also null<br>
     *   - the services are considered to be saved<br>
     *   - empty map for the services
     */
    public Core() {
        LOGGER.trace("creating Core instance...");

        saved = true;
        services = new TreeMap<>();
        selectedService = null;
        basicData = null;
        touchedStops = null;
        LOGGER.debug("core instance successfully created");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        services.values().stream().forEach(BusService::initTransientFields);
        selectFirstService();
    }

    // Methods for the selected bus service -----------------------------------------------

    /**
     * Selects the given service, if not already selected.
     * Note that there is no argument validation due to being a private method.
     * @param service the service to be selected
     */
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
     * {@inheritDoc}
     */
    @Override
    public boolean hasSelectedService() {
        LOGGER.trace("called 'hasSelectedService");
        // if the selected service is not null, than the other two are not null too
        return selectedService != null;
    }

    /**
     * Checks whether there is a selected service and throws a {@link NoSelectedServiceException} if
     * the answer is no. Only exist for convenience reasons.
     */
    private void checkSelection() {
        if (!hasSelectedService())
            throw new NoSelectedServiceException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isModified() {
        LOGGER.trace("called isModified");
        checkSelection();
        return basicData.isModified() || touchedStops.isModified();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSaved() {
        return saved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public iTimetable getTimetableOf(String serviceName) {
        LOGGER.trace("called getTimetableOf");
        if (serviceName == null)
            throw new NullPointerException("the given service name is null");
        BusService service = services.get(serviceName);
        if (service == null)
            throw new IllegalArgumentException("the bus service with the given name '" + serviceName
                    + "' does not exist");
        return service.getTimetable();
    }
    // ---------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        LOGGER.trace("called getName");
        checkSelection();
        return basicData.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(String serviceName) {
        LOGGER.trace("called setName with name: " + serviceName);
        checkSelection();
        basicData.setName(serviceName);
        LOGGER.info("the name was set to '" + serviceName + "'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTimeGap() {
        LOGGER.trace("called getTimeGap");
        checkSelection();
        return basicData.getTimeGap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTimeGap(int timeGap) {
        LOGGER.trace("called setTimeGap with timeGap: " + timeGap);
        checkSelection();
        basicData.setTimeGap(timeGap);
        LOGGER.info("the time gap was set to " + timeGap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DayTime getFirstLeaveTime() {
        LOGGER.trace("called getFirstLeaveTime");
        checkSelection();
        return basicData.getFirstLeaveTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFirstLeaveTime(DayTime time) {
        LOGGER.trace("called setFirstLeaveTime with time: " + time);
        checkSelection();
        basicData.setFirstLeaveTime(time);
        LOGGER.info("the first leave time was set to " + time);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFirstLeaveHour(int hour) {
        LOGGER.trace("called setFirstLeaveHour with hour: " + hour);
        checkSelection();
        basicData.setFirstLeaveHour(hour);
        LOGGER.info("the first leave hour was set to: " + hour);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFirstLeaveMinute(int minute) {
        LOGGER.trace("called setFirstLeaveMinute with minute: " + minute);
        checkSelection();
        basicData.setFirstLeaveMinutes(minute);
        LOGGER.info("the first leave minute was set to: " + minute);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DayTime getBoundaryTime() {
        LOGGER.trace("called getBoundaryTime");
        checkSelection();
        return basicData.getBoundaryTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBoundaryTime(DayTime time) {
        LOGGER.trace("called setBoundaryTime with time: " + time);
        checkSelection();
        basicData.setBoundaryTime(time);
        LOGGER.info("the boundary time was set to " + time);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBoundaryHour(int hour) {
        LOGGER.trace("called setBoundaryHour with hour: " + hour);
        checkSelection();
        basicData.setBoundaryHours(hour);
        LOGGER.info("the boundary hour was set to " + hour);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBoundaryMinute(int minute) {
        LOGGER.trace("called setBoundaryMinute with minute: " + minute);
        checkSelection();
        basicData.setBoundaryMinutes(minute);
        LOGGER.info("the boundary minute was set to " + minute);
    }

    // wrapper methods for the TouchedStops instance --------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public void appendStop(int id) {
        LOGGER.trace("called appendStop with id: " + id);
        checkSelection();
        touchedStops.appendStop(id);
        LOGGER.info("the bus stop '" + id + "' was added to the service");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClosed() {
        LOGGER.trace("called isClosed");
        checkSelection();
        return touchedStops.isClosed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canUndo() {
        LOGGER.trace("called canUndo");
        checkSelection();
        return touchedStops.canUndo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getStops() {
        LOGGER.trace("called getStops");
        checkSelection();
        return touchedStops.getStops();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStopCount() {
        LOGGER.trace("called getStopCount");
        checkSelection();
        return touchedStops.getStopCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLastStop() {
        LOGGER.trace("called getLastStop");
        checkSelection();
        return touchedStops.getLastStop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearStops() {
        LOGGER.trace("called clearStops");
        checkSelection();
        touchedStops.clear();
        LOGGER.info("all bus stops was removed from the bus service");
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public int[] getReachableStopIds() {
        LOGGER.trace("called getReachableStopsIds");
        checkSelection();
        return touchedStops.getReachableStopIds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getTravelTimes() {
        LOGGER.trace("called getTravelTimes");
        checkSelection();
        return touchedStops.getTravelTimes();
    }
    // ---------------------------------------------------------------------------------------


    /**
     * {@inheritDoc}
     */
    @Override
    public int getServiceCount() {
        LOGGER.trace("called getServiceCount");
        return services.size();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void createNewService() {
        LOGGER.trace("called createNewService");

        BusService newService = new BusService();
        StringBuilder sb = new StringBuilder(newService.getCurrentServiceData().getName());
        while (services.containsKey(sb.toString()))
            sb.append('*');
        newService.getCurrentServiceData().setName(sb.toString());
        newService.applyChanges();
        services.put(newService.getAppliedName(), newService);
        LOGGER.info("added a new bus service");
        setSelectedService(newService);
    }

    /**
     * Selects the first bus service, according to the natural ordering of their names.
     * Sets all selected service - related variable to null if there isn't a service to select.
     */
    private void selectFirstService() {
        if (services.size() == 0) {
            setSelectedService(null);
            return;
        }
        setSelectedService(services.values().iterator().next());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteSelectedService() {
        LOGGER.trace("called deleteSelectedService");
        if (!hasSelectedService()) {
            LOGGER.warn("there isn't a selected service to delete");
            return;
        }

        String name = selectedService.getAppliedName();
        if (services.remove(name) != null)
            LOGGER.info("removed the bus service '" + name + "'");
        else {
            LOGGER.warn("could not remove the bus service '" + name + "'");
            return;
        }

        selectFirstService();
        saved = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteService(String serviceName) {
        LOGGER.trace("called deleteService");
        if (serviceName == null)
            throw new NullPointerException("name of service is never null");
        if (!services.containsKey(serviceName))
            return;
        if (getName().equals(serviceName)) {
            deleteSelectedService();
            return;
        }

        services.remove(serviceName);
        saved = false;
        LOGGER.info("removed the bus service '" + serviceName + "'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectService(String serviceName) {
        LOGGER.trace("called selectService with name: " + serviceName);
        if (serviceName == null) {
            throw new NullPointerException("the name of the selected service cannot be null");
        }
        if (hasSelectedService())
            if (serviceName.equals(selectedService.getAppliedName()))
                return;

        if (!services.containsKey(serviceName)) {
            LOGGER.warn("a bus service with the given name '" + serviceName + "' doesn't exist");
            throw new IllegalArgumentException("the bus service with the given name '"
                    + serviceName + "' doesn't exist");
        }

        setSelectedService(services.get(serviceName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getServiceNames() {
        LOGGER.trace("called getServiceNames");
        return services.keySet().toArray(new String[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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


}//class
