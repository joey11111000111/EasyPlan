package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.util.OpenLinkedList;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.github.joey11111000111.EasyPlan.util.OpenLinkedList.Node;

/**
 * The TouchedStops class manages the modifications to the bus stops of a certain bus service.
 * Implements all the modification rules, so it promises that every state of bus stop list
 * is valid. Also implements an undo function, to discard the last change. It is possible
 * to undo the modification one by one until the last unapplied modification is withdrawn.
 */
public class TouchedStops {

    private static class UndoOperation<E> {
        public enum OperationType {
            OPEN, DELETE, APPEND, APPEND_CLOSE
        }

        private OperationType operationType;
        private Node<E> chain;

        private UndoOperation(OperationType operationType, Node<E> chain) {
            this.operationType = operationType;
            this.chain = chain;
        }

        public OperationType getOperationType() {
            return operationType;
        }

        public Node<E> getChain() {
            return chain;
        }

        public static <E> UndoOperation<E> newOpenInstance(E typeObject) {
            return new UndoOperation<E>(OperationType.OPEN, null);
        }
        public static <E> UndoOperation<E> newDeleteInstance(E typeObject) {
            return new UndoOperation<E>(OperationType.DELETE, null);
        }
        public static <E> UndoOperation<E> newAppendInstance(Node<E> chain) {
            return new UndoOperation<E>(OperationType.APPEND, chain);
        }

        public static <E> UndoOperation<E> newAppendCloseInstance(Node<E> chain) {
            return new UndoOperation<E>(OperationType.APPEND_CLOSE, chain);
        }
    }//private static class


    private OpenLinkedList<Integer> stops;
    private Stack<UndoOperation<Integer>> undoStack;
    private boolean modified;
    private boolean closed;

    /**
     * Creates a new instance with an empty bus stop list.
     * It is not closed, not modified, and there is nothig to undo.
     */
    public TouchedStops() {
        stops = new OpenLinkedList<Integer>();
        undoStack = new Stack<UndoOperation<Integer>>();
        modified = false;
        closed = false;
    }

    private void markAsModified() {
        if (!modified)
            modified = true;
    }

    /**
     * Informs the object that the current state was registered.
     * After this method call all the modifications are considered to be saved,
     * so there is nothing to undo.
     */
    void markAsSaved() {
        if (modified)
            modified = false;
        undoStack.clear();
    }

    /**
     * Returns true is there aren't any added bus stops.
     * @return true, if the bus stop list is empty
     */
    public boolean isEmpty() {
        return stops.isEmpty();
    }

    /**
     * Returns true if there aren't any new modifications.
     * @return true if there was at least one modification since the last save
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Returns true if the bus service returns to the station at the end of the way, it is the last
     * (but not only) bus stop.
     * @return true, if there is it least one bus stop, and the last bus stop is the bus station
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Returns true if there are new, unsaved modifications
     * @return true, if there are unsaved modification
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * Appends the bus stop with the given id to the bus stop list.
     * @param id the id of the bus stop that should be appended.
     * @throws IllegalStateException if the bus service is already closed (finished)
     * @throws IllegalArgumentException if the bus stop with the given id:
     *          - doesn't exist
     *          - represents the bus station (it must be added in a separate way)
     *          - is not reachable from the previous bus stop
     *          - has already added twice
     */
    public void appendStop(int id) {
        if (closed)
            throw new IllegalStateException("bus service is closed, cannot add new stop to the list");
        // id validation
        if (!BusStop.validId(id))
            throw new IllegalArgumentException("given id '" + id + "' is not valid");
        if (BusStop.isStation(id))
            throw new IllegalArgumentException("bus station must not be added like a simple stop");
        // check reachable rule 1
        if (isEmpty()) {
            if (!BusStop.isReachableFromStation(id))
                throw new IllegalArgumentException("given stop '" + id + "' is not reachable from the station");
        }
        else {
                int lastId = stops.getTail().getElement();
                if (!BusStop.isReachableToFrom(id, lastId))
                    throw new IllegalArgumentException("given stop '" + id + "' is not "
                            + "reachable from the stop '" + lastId + "'");
        }
        // check reachable rule 2
        if (addedTwiceAlready(id))
            throw new IllegalArgumentException("given bus stop '" + id
                + "' has already appeared twice in the list");

        // append bus stop and create the undo operation for this append operation
        stops.append(id);
        markAsModified();
        undoStack.push(UndoOperation.newDeleteInstance(new Integer(0)));
    }

    private boolean addedTwiceAlready(int id) {
        if (stops.size() > 3) {
            Node<Integer> node = stops.getTail();
            int counter = 0;
            while (node.hasPrevious()) {
                node = node.previous();
                if (id == node.getElement())
                    if (++counter == 2)
                        return true;
            }
        }
        return false;
    }

    /**
     * Returns an array that contains the ids of the touched bus stops. The order
     * is the same as in the bus stop list (order of append).
     * @return an array containing the ids of the touched bus stops in the order of append
     */
    public int[] getStops() {
        if (isEmpty())
            return new int[0];

        int[] stopIds = new int[stops.size()];
        Node<Integer> node = stops.getHead();
        stopIds[0] = node.getElement();
        int counter = 1;
        while (node.hasNext()) {
            node = node.next();
            stopIds[counter++] = node.getElement();
        }
        return stopIds;
    }

    /**
     * Appends the bus station to the bus service as the last bus stop. Closing a
     * bus service also means finishing it, so appending any more bus stops is
     * not possible.
     * @throws IllegalStateException if the bus service can not be closed, because
     *          the bus station is not reachable from te last bus stop, or the service
     *          is already closed
     */
    public void closeService() {
        if (!isStationReachable())
            throw new IllegalStateException("bus service cannot be closed now");
        closed = true;
        markAsModified();
        undoStack.push(UndoOperation.newOpenInstance(new Integer(0)));
    }

    private void addUndoOfRemoval(Node<Integer> chain) {
        if (closed) {
            closed = false;
            undoStack.push(UndoOperation.newAppendCloseInstance(chain));
        }
        else
            undoStack.push(UndoOperation.newAppendInstance(chain));
    }

    /**
     * Removes all the bus stops from the list. Calling this method when there aren't
     * any bus stops added has no effect.
     */
    public void clear() {
        Node<Integer> chain;
        try {
            chain = stops.removeChainFrom(0);
        } catch (IllegalStateException ise) {
            return;
        }

        if (!modified)
            modified = true;
        addUndoOfRemoval(chain);
        markAsModified();
    }

    /**
     * Removes the last occurence of a bus stop from the service with all the following
     * bus stops.
     * @param fromId the bus stop from which the removal starts (the given bus stop is
     *               included too)
     */
    public void removeChainFrom(int fromId) {
        Node<Integer> chain;
        try {
            chain = stops.removeChainFrom(new Integer(fromId));
        } catch (IllegalStateException ise) {
            throw new IllegalStateException("there is no bus stop to remove");
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("the given id '" + fromId + "' is invalid");
        }

        addUndoOfRemoval(chain);
        markAsModified();
    }

    /**
     * Discards the latest modification, that is not saved already.
     * @throws IllegalStateException if there aren't any unsaved modifications
     */
    public void undo() {
        if (!canUndo())
            throw new IllegalStateException("undo stack is empty, there is nothing to undo");

        UndoOperation<Integer> operation = undoStack.pop();
        UndoOperation.OperationType type = operation.getOperationType();
        if (type == UndoOperation.OperationType.OPEN) {
            closed = false;
        } else if (type == UndoOperation.OperationType.DELETE) {
            stops.removeLast();
        } else if (type == UndoOperation.OperationType.APPEND) {
            Node<Integer> chain = operation.getChain();
            stops.appendChain(chain);
        } else { // (type == UndoOperation.OperationType.APPEND_CLOSE)
            Node<Integer> chain = operation.getChain();
            stops.appendChain(chain);
            closed = true;
        }
    }

    /**
     * Returns true if the bus station is reachable from the last bus stop of the bus service
     * @return true if the bus station is reachable from the last bus stop of the bus service
     */
    public boolean isStationReachable() {
        if (closed)
            return false;
        if (isEmpty())
            return false;
        int lastId = stops.getTail().getElement();
        if (!BusStop.isStationReachableFrom(lastId))
            return false;
        return true;
    }

    /**
     * Returns an array that contains the ids of all the bus stops that can be the
     * next stop according to the rules
     * @return an array with all the ids of all the reachable bus stops
     */
    public int[] getReachableStopIds() {
        if (isEmpty())
            return BusStop.getReachableIdsOfStation();
        if (isClosed())
            throw new IllegalStateException("service is closed, thus there are no reachable stops");

        int lastId = stops.getTail().getElement();
        int[] ids = BusStop.getReachableIdsOf(lastId);

        List<Integer> validIds = new ArrayList<Integer>(ids.length);
        for (int i : ids) {
            // the bus station will not be included
            if (BusStop.isStation(i))
                continue;
            if (!addedTwiceAlready(i))
                validIds.add(i);
        }

        int[] resultIds = new int[validIds.size()];
        for (int i = 0; i < resultIds.length; i++)
            resultIds[i] = validIds.get(i);
        return resultIds;
    }

    /**
     * Returns an array that contains the minutes that it takes to travel to
     * each bus stops. The station is included, when the bus service is closed.
     * All the travel times are relative to when the bus leaves the bus station.
     * So the travel to the second stop means the travel from the station to the
     * first bus stop plus to travel time from the first bus stop to the second
     * bus stop.
     * @return an array with the travel times to each touched bus stops, relative
     *           to when the bus leaves the station.
     */
    public int[] getTravelTimes() {
        if (isEmpty())
            return new int[0];

        int size = (closed) ? stops.size() + 1 : stops.size();
        int[] times = new int[size];
        Node<Integer> node = stops.getHead();
        int from;
        int to;
        // first from the station
        to = node.getElement();
        times[0] = BusStop.travelTimeToFromStation(to);

        int counter = 0;
        while (node.hasNext()) {
            node = node.next();
            from = to;
            to = node.getElement();
            times[++counter] = BusStop.travelTimeToFrom(to, from);
        }

        if (closed) {
            from = to;
            times[++counter] = BusStop.travelTimeToStationFrom(from);
        }

        // now I have to individual travel times, from stop to stop
        // convert them to be relative to the station
        for (int i = 1; i < times.length; i++)
            times[i] += times[i-1];

        return times;
    }

}//class
