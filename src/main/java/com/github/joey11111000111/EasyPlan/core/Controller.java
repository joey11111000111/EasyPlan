package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.core.exceptions.NameConflictException;
import com.github.joey11111000111.EasyPlan.core.exceptions.NoSelectedServiceException;
import com.github.joey11111000111.EasyPlan.util.DayTime;

/**
 * Wraps the whole {@link com.github.joey11111000111.EasyPlan.core core} package together,
 * to offer services as one module. This interface is the entry point to that module from the outside,
 * all operations happen through here.
 */
public interface Controller {

    /**
     * Prepares the module for usage after being read from the file.
     * Initializes the transient fields of all the contained bus services and
     * selects the service whose name is the first in alphabetical order.
     * This method shell be called after the object is read from the save file.
     */
    void init();

    /**
     * Returns true if there is an actual bus service selected.
     * @return true if there is an actual bus service selected
     */
    boolean hasSelectedService();

    /**
     * Returns true if there are new, unapplied modifications in either of the buffers.
     * @return true, if there are unapplied modifications
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    boolean isModified();

    /**
     * Returns true, if there aren't any applied modifications, that are not saved to file.
     * @return true, if there aren't any applied modifications, that are not saved to file
     */
    boolean isSaved();

    /**
     * Rolls back the selected bus service to the state, where it was last {@link #applyChanges() applied}
     * or {@link #isSaved() saved}.
     * @return true, if there were any new changes to discard
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    boolean discardChanges();

    /**
     * Gets and returns the {@link iTimetable timetable} of the bus service with the given name.
     * @param serviceName the name of the service whose timetable shell be returned
     * @return the {@link iTimetable timetable} of the service with the given name
     * @throws NullPointerException is the given argument is null
     * @throws IllegalArgumentException if there is no service with the given name
     */
    iTimetable getTimetableOf(String serviceName);

    /**
     * Returns the buffered name of the selected bus service. It can differ from the name
     * returned by the {@link #getServiceNames()}, if the name was changed but not applied yet.
     * @return the buffered name of the bus service
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    String getName();

    /**
     * Sets the buffered name of the selected bus service.
     * If the new name is already among the applied names of the other bus services,
     * than calling the {@link #applyChanges()} method will throw a {@link NameConflictException}.
     * @param serviceName the new name of the bus service
     * @throws NullPointerException when the given string is null
     * @throws IllegalArgumentException when received an empty string
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    void setName(String serviceName);

    /**
     * Returns the buffered {@link BusService#timeGap timeGap} of the selected bus service.
     * @return the buffered {@link BusService#timeGap timeGap} of the selected bus service
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    int getTimeGap();

    /**
     * Sets the buffered {@link BusService#timeGap timeGap} of the selected bus service.
     * @param timeGap the value to set the {@link BusService#timeGap timeGap} to
     * @throws IllegalArgumentException when the value of the given argument in less then 1
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    void setTimeGap(int timeGap);

    /**
     * Returns the {@link BusService#firstLeaveTime firstLeaveTime} of the selected bus service.
     * @return the {@link BusService#firstLeaveTime firstLeaveTime} of the selected bus service
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    DayTime getFirstLeaveTime();

    /**
     * Sets the buffered {@link BusService#firstLeaveTime firstLeaveTime} of the selected bus service.
     * @param time the value to set the buffered {@link BusService#firstLeaveTime firstLeaveTime}
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    void setFirstLeaveTime(DayTime time);

    /**
     * Sets the buffered hour part of the {@link BusService#firstLeaveTime firstLeaveTime} of the
     * selected bus service.
     * @param hour the new value of hour of the buffered {@link BusService#firstLeaveTime firstLeaveTime}
     * @throws IllegalArgumentException if the given argument is out of the range: [0-23]
     */
    void setFirstLeaveHour(int hour);

    /**
     * Sets the buffered minute part of the {@link BusService#firstLeaveTime firstLeaveTime} of the
     * selected bus service.
     * @param minute the new value of minute of the buffered {@link BusService#firstLeaveTime firstLeaveTime}
     * @throws IllegalArgumentException if the given argument is out of the range: [0-59]
     */
    void setFirstLeaveMinute(int minute);

    /**
     * Returns the buffered {@link BusService#boundaryTime boundaryTime} of the selected bus service.
     * @return the buffered {@link BusService#boundaryTime boundaryTime} of the selected bus service
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    DayTime getBoundaryTime();

    /**
     * Sets the buffered {@link BusService#boundaryTime boundaryTime} of the selected bus service.
     * @param time the new value to set the buffered {@link BusService#boundaryTime boundaryTime} to
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    void setBoundaryTime(DayTime time);

    /**
     * Sets the buffered hour part of the {@link BusService#boundaryTime boundaryTime} of the
     * selected bus service.
     * @param hour the new value of hour of the buffered {@link BusService#boundaryTime boundaryTime}
     * @throws IllegalArgumentException if the given argument is out of the range: [0-23]
     */
    void setBoundaryHour(int hour);

    /**
     * Sets the buffered minute part of the {@link BusService#boundaryTime boundaryTime} of the
     * selected bus service.
     * @param minute the new value of minute of the buffered {@link BusService#boundaryTime boundaryTime}
     * @throws IllegalArgumentException if the given argument is out of the range: [0-59]
     */
    void setBoundaryMinute(int minute);

    /**
     * Appends the given bus stop id to the end of the stop list of the selected bus service.
     * @param id the id of the bus stop that should be appended
     * @throws IllegalStateException if the bus service is already in a {@link #isClosed() closed} state
     * @throws IllegalArgumentException if the bus stop with the given id:<br>
     *          - doesn't exist<br>
     *          - is not reachable from the previous bus stop<br>
     *          - has already added twice
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    void appendStop(int id);

    /**
     * Indicates whether the selected bus service is in a "closed" state.
     * See {@link TouchedStops#isClosed()} for more information.
     * @return true, if the selected bus service is in a "closed" state.
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    boolean isClosed();

    /**
     * Returns true if there are new, unsaved modifications in the selected bus service.
     * @return true, if there are unsaved modification in the selected bus service
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    boolean canUndo();

    /**
     * Returns an array that contains the id -s of the touched bus stops of the selected bus service.
     * See {@link TouchedStops#getStops()} for more information.
     * @return an array containing the id -s of the touched bus stops in the order of append
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    int[] getStops();

    /**
     * Returns the number of touched stops of the selected service.
     * See {@link TouchedStops#getStopCount()} for more information.
     * @return the number of touched bus stops of the selected bus service
     */
    int getStopCount();

    /**
     * Returns the id of the last touched bus stop of the selected bus service.
     * If there are no bus stops explicitly added, it returns the id of the bus station.
     * @return the id of the last added bus stop of the selected bus service
     */
    int getLastStop();

    /**
     * Removes all the bus stops from the stop list of the selected bus service.
     * Calling this method when there aren't any bus stops added has no effect.
     * It is possible to undo this operation.
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    void clearStops();

    /**
     * Removes the last occurrence of a bus stop from the selected service with all the following
     * bus stops, if there are any.
     * See {@link TouchedStops#removeChainFrom(int)} for more information.
     * @param fromId the id of the bus stop from which the removal shell start (the given bus stop is
     *               included too)
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    void removeChainFrom(int fromId);

    /**
     * Discards the latest unapplied modification of the stop list of the selected service,
     * by committing the exact opposite operation.
     * @throws IllegalStateException if there aren't any unsaved modifications
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    void undo();

    /**
     * Returns the id -s of all the bus stops that can be the next bus stop of the selected bus service.
     * A bus stop is only included if it is reachable and hasn't been added twice already.
     * Possibly returns a zero length array, but never returns null.
     * See {@link TouchedStops#getReachableStopIds()} for more information.
     * @return the id -s of all the bus stops that can be the next bus stop of the selected bus service
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    int[] getReachableStopIds();

    /**
     * Returns an array that contains the minutes that it takes to travel to
     * each bus stops of the selected service, relatively to the bus station.
     * For example traveling to the second stop means the travel from the station to the
     * first bus stop plus to travel time from the first bus stop to the second
     * bus stop.
     * It is possible for this method to return a zero length array, but never returns null.
     * See {@link TouchedStops#getTravelTimes()} for more information.
     * @return an array with the travel times to each touched bus stop, relative
     *           to to bus station
     * @throws NoSelectedServiceException if there isn't a selected bus service
     */
    int[] getTravelTimes();

    /**
     * Returns the number of bus services.
     * @return the number of bus services
     */
    int getServiceCount();

    /**
     * Creates a new bus service, filled with default values.
     * These default values are:<br>
     *     - {@link BusService#BusService() DEFAULT} as a service or name, or DEFAULT*, DEFAULT** ... if the
     *       default name is still is use
     *     - all changes are applied
     *     - the service is not saved
     * The newly created service gets selected automatically.
     */
    void createNewService();

    /**
     * Removes the selected service, without a chance to bring it back.
     * After deletion, all variables related to the selected service will become null.
     * Does nothing when there isn't a selected service.
     * Deleting a service causes this object to be in "modified" state, if it was
     * "saved" before. Otherwise the state doesn't change.
     */
    void deleteSelectedService();

    /**
     * Removes the service with the given name, without a chance to bring it back.
     * After deletion, all variables related to the selected service will become null.
     * Does nothing when the service with the given name doesn't exist.
     * Deleting a service causes this object to be in "modified" state, if it was
     * "saved" before. Otherwise the state doesn't change.
     * @param serviceName the name of the service that shell be deleted
     * @throws NullPointerException if the given argument is null
     */
    void deleteService(String serviceName);

    /**
     * Selects the service that has the given name.
     * @param serviceName the name of the service that should be selected
     * @throws NullPointerException when the given string is null
     * @throws IllegalArgumentException when there isn't a service with the given name
     */
    void selectService(String serviceName);

    /**
     * Returns the applied name of all the existing bus services.
     * The returned names are in alphabetic order.
     * @return the applied name of all the existing bus services
     */
    String[] getServiceNames();

    /**
     * Registers/applies the contents of the buffers if there were any unapplied modifications.
     * After this method call, the saving process will use the new data for the service.
     * @throws NameConflictException when the buffered name of the bus service is
     *          already in use as an applied name of another bus service
     */
    void applyChanges() throws NameConflictException;

}//interface
