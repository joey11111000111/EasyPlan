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
            DELETE, APPEND
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

        public static <E> UndoOperation<E> newDeleteInstance(E typeObject) {
            return new UndoOperation<E>(OperationType.DELETE, null);
        }
        public static <E> UndoOperation<E> newAppendInstance(Node<E> chain) {
            return new UndoOperation<E>(OperationType.APPEND, chain);
        }
    }//private static class


    private OpenLinkedList<Integer> stops;
    private Stack<UndoOperation<Integer>> undoStack;
    private boolean modified;

    /**
     * Creates a new instance with a bus stop list that only contains the bus station.
     * It is not closed, not modified, and there is nothing to undo.
     */
    public TouchedStops() {
        stops = new OpenLinkedList<Integer>();
        stops.append(0);
        undoStack = new Stack<UndoOperation<Integer>>();
        modified = false;
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
        return stops.getTail().getElement() == 0 && stops.size() > 1;
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
        if (isClosed())
            throw new IllegalStateException("bus service is closed, cannot add new stop to the list");
        // id validation
        if (!BusStop.validId(id))
            throw new IllegalArgumentException("given id '" + id + "' is not valid");
        // check reachable rule 1
        int lastId = stops.getTail().getElement();
        if (!BusStop.isReachableToFrom(id, lastId))
            throw new IllegalArgumentException("given stop '" + id + "' is not "
                    + "reachable from the stop '" + lastId + "'");
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
        if (stops.size() > 4) {
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
     * Returns the number of touched stops of the service. It can never be less then 0,
     * because the bus station, as the starting point is always in the list, as the first stop.
     * @return the number of touched bus stops, included the start from the bus station
     */
    public int getStopCount() {
        return stops.size();
    }

    /**
     * Returns the id of the last bus stop of the bus service. If there are no bus stops added,
     * it returns the id of the bus station
     * @return the id of the last bus stop in the list
     */
    public int getLastStop() {
        return stops.getTail().getElement();
    }

    /**
     * Removes all the bus stops from the list. Calling this method when there aren't
     * any bus stops added has no effect.
     */
    public void clear() {
        if (stops.size() == 1)
            return;
        Node<Integer> chain;
        try {
            chain = stops.removeChainFrom(1);
        } catch (IllegalStateException ise) {
            return;
        }

        if (!modified)
            modified = true;
        undoStack.push(UndoOperation.newAppendInstance(chain));
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
        if (!isClosed() && fromId == 0) {
            if (stops.size() == 1)
                return;
            chain = stops.removeChainFrom(1);
        } else {
            try {
                chain = stops.removeChainFrom(new Integer(fromId));
            } catch (IllegalStateException ise) {
                throw new IllegalStateException("there is no bus stop to remove");
            } catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException("the given id '" + fromId + "' is invalid");
            }
        }

        undoStack.push(UndoOperation.newAppendInstance(chain));
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
        if (type == UndoOperation.OperationType.DELETE) {
            stops.removeLast();
        } else {
            Node<Integer> chain = operation.getChain();
            stops.appendChain(chain);
        }
    }

    /**
     * Returns an array that contains the ids of all the bus stops that can be the
     * next stop according to the rules
     * @return an array with all the ids of all the reachable bus stops
     */
    public int[] getReachableStopIds() {
        if (isClosed())
            return new int[0];

        int lastId = stops.getTail().getElement();
        int[] ids = BusStop.getReachableIdsOf(lastId);

        List<Integer> validIds = new ArrayList<Integer>(ids.length);
        for (int i : ids)
            if (!addedTwiceAlready(i))
                validIds.add(i);

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
        if (stops.size() < 2)
            return new int[0];

        int[] times = new int[stops.size() - 1];       // there is one more stop than travel time
        Node<Integer> node = stops.getHead();
        int counter = 0;
        while (true) {
            int from = node.getElement();
            node = node.next();
            int to = node.getElement();
            times[counter++] = BusStop.travelTimeToFrom(to, from);
            if (!node.hasNext())
                break;
        }

        // now I have to individual travel times, from stop to stop
        // convert them to be relative to the station
        for (int i = 1; i < times.length; i++)
            times[i] += times[i-1];

        return times;
    }

}//class
