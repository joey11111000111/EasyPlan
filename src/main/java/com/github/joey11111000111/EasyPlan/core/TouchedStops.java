package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.util.OpenLinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.github.joey11111000111.EasyPlan.util.OpenLinkedList.Node;

/**
 * The TouchedStops class manages the modifications to the bus stops of the wrapping bus service, as a buffer.
 * Implements all the modification rules, so it promises that the list of bus stops is always valid.
 * Also implements an undo function, to discard the last change. It is possible
 * to undo the modifications one by one until the last unapplied modification is withdrawn.
 */
public class TouchedStops {

    /**
     * The <a href="http://www.slf4j.org/">slf4j</a> logger object for this class.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(TouchedStops.class);

    /**
     * This class shows what operation shell be done in order to restore a former state of the buffer.
     * An object of this class is created when a modification occurs on the buffer. That object holds
     * the counteraction of that modification. The only possible operations on the buffer are
     * appending a single bus stop or deleting one or multiple stops. Which means that the only
     * counteractions are deleting a single stop or appending one or multiple stops. When appending,
     * the objects of this class stores a chain of stops to append. This class relies on the
     * {@link OpenLinkedList} and its {@link Node Node} classes, that's why it is generic.
     * The type parameter show what type of objects are there to deal with when it comes to appending.
     * @param <E> the type of objects in the append-chain
     */
    private static class UndoOperation<E> {

        /**
         * Indicates which of the two operations the {@link UndoOperation UndoOperation} holds.
         */
        public enum OperationType {

            /**
             * The wrapping {@link UndoOperation UndoOperation} object holds a delete operation.
             * This kind of operation doesn't need the {@link #chain append-chain},
             * because all it does is deleting the last bus stop in the
             * {@link TouchedStops#stops stop-list} of the buffer.
             */
            DELETE,

            /**
             * The wrapping {@link UndoOperation UndoOperation} object holds an append operation.
             * This kind of operation will append the contained {@link #chain append-chain}
             * to the {@link TouchedStops#stops stop-list} of the buffer.
             */
            APPEND
        }

        /**
         * The type of the operation this object holds.
         */
        private OperationType operationType;

        /**
         * When committing an append operation, this chain is appended to the
         * {@link TouchedStops#stops stop-list} of the buffer.
         * See {@link OpenLinkedList} for more information.
         */
        private Node<E> chain;

        /**
         * Creates an object of the given type with the given append-chain.
         * Note that since this is a private class, nothing will interact with it from
         * outside of the wrapping {@link TouchedStops} class. Thus no argument validation
         * is present.
         *
         * @param operationType the type of operation the new object will have
         * @param chain The chain of elements to append to the {@link TouchedStops#stops stop-list} of the buffer.
         *      If the operationType is represents a {@link OperationType#DELETE DELETE} type,
         *      the value of this argument has no effect. That time it can be null.
         *
         */
        private UndoOperation(OperationType operationType, Node<E> chain) {
            this.operationType = operationType;
            this.chain = chain;
        }

        /**
         * Returns the type of operation this objects holds.
         * @return the type of operation this object holds.
         */
        public OperationType getOperationType() {
            return operationType;
        }

        /**
         * Returns the {@link #chain} of elements.
         * @return The {@link #chain} of elements. It is possible for this
         * method to return null.
         */
        public Node<E> getChain() {
            return chain;
        }

        /**
         * Static factory method that returns a {@link OperationType#DELETE DELETE}
         * type operation.
         * @param <E> The type of objects in the {@link #chain append-chain}. That chain has
         *           no importance the returned {@link OperationType#DELETE DELETE} operation.
         * @return a new {@link UndoOperation UndoOperation} instance of type {@link OperationType#DELETE DELETE}
         */
        public static <E> UndoOperation<E> newDeleteInstance() {
            return new UndoOperation<>(OperationType.DELETE, null);
        }

        /**
         * Static factory method that returns an {@link OperationType#APPEND APPEND}
         * type operation using the given append-chain.
         * @param chain the chain of elements that are to be used when committing this operation
         * @param <E> the type of objects held by the chain
         * @return a new {@link UndoOperation UndoOperation} instance of type {@link OperationType#APPEND APPEND}
         */
        public static <E> UndoOperation<E> newAppendInstance(Node<E> chain) {
            return new UndoOperation<>(OperationType.APPEND, chain);
        }
    }//private static class

    /**
     * List of the bus stops that this buffer contains in their append order.
     */
    private OpenLinkedList<Integer> stops;

    /**
     * This stack contains a counteraction for every modification.
     * The order they get out of the stack is the opposite of how
     * the new modifications were made.
     */
    private Stack<UndoOperation<Integer>> undoStack;

    /**
     * Indicates whether this object is in the state of "modified" or "saved".
     * See {@link #markAsModified()} or {@link #markAsSaved()} for more information.
     */
    private boolean modified;

    /**
     * Creates a new buffer instance with a bus stop list that only contains the bus station.
     * It is not closed, not modified, and there is nothing to undo.
     */
    public TouchedStops() {
        LOGGER.trace("called TouchedStops constructor");
        stops = new OpenLinkedList<>();
        stops.append(0);
        undoStack = new Stack<>();
        modified = false;
    }


    /**
     * Changes the state of the object from saved to modified, if it is not already modified.
     * The "modified" state indicates that some content of the buffer was changed, thus might not
     * contain the same data as the wrapping {@link BusService} object.
     */
    private void markAsModified() {
        LOGGER.trace("called markAsModified");
        if (!modified) {
            modified = true;
            LOGGER.debug("now it's marked as modified");
        }
    }

    /**
     * Changes the state of the object from modified to saved, if it is not already saved.
     * The "saved" state indicates that the contents of the buffer are identical to the
     * contents found in the wrapping {@link BusService} object.
     * After this method call all the modifications are considered to be saved,
     * so there is nothing to undo.
     */
    void markAsSaved() {
        LOGGER.trace("called markAsSaved");
        if (modified) {
            modified = false;
            LOGGER.debug("now it's marked as saved");
        }
        undoStack.clear();
    }

    /**
     * Indicates whether this object is in the state of "modified".
     * See {@link #markAsModified()} and {@link #modified} for more information.
     * @return true if there was at least one modification since the last save
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Indicates whether this object is in a "closed" state.
     * Being "closed" means that the last added bus stop is the bus station, and there is at least one
     * intervening bus stop. For example the {@code 0 -> 1 -> 0} bus stop combination is closed.
     * Being in the "closed" state doesn't effect the {@link #markAsModified() modified} or
     * {@link #markAsSaved() saved} state.
     * When in the "closed" state, no more bus stops are allowed to add. The list of bus stops is
     * considered as finished.
     * @return true, if this object is in a closed state
     */
    public boolean isClosed() {
        return stops.getTail().getElement() == 0 && stops.size() > 2;
    }

    /**
     * Indicates whether there is at least one modification that can be undone.
     * Only the unapplied modification can be undone, so this method is equivalent
     * to the {@link #isModified()} method. Only exists for convenience reasons.
     * @return true, if there is at least one modification that can be undone
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * Appends the bus stop with the given id to the buffered list of bus stops.
     * @param id the id of the bus stop that should be appended.
     * @throws IllegalStateException if the bus service is already closed
     * @throws IllegalArgumentException if the bus stop with the given id:<br>
     *          - doesn't exist<br>
     *          - is not reachable from the previous bus stop<br>
     *          - has already added twice
     */
    public void appendStop(int id) {
        LOGGER.trace("called appendStop");
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
        undoStack.push(UndoOperation.newDeleteInstance());
        LOGGER.debug("new stop successfully appended");
    }

    /**
     * Indicates whether the bus stop with the given id has already added twice to this buffer.
     * This method is in use because there is a convention that forbids the adding of a single
     * bus stop more than twice.
     * @param id the id of the bus stop to check
     * @return true, if the given bus stop was added twice already
     */
    private boolean addedTwiceAlready(int id) {
        LOGGER.trace("called addedTwiceAlready");
        if (stops.size() > 4) {
            Node<Integer> node = stops.getTail();
            int counter = 0;
            while (node.hasPrevious()) {
                node = node.previous();
                if (id == node.getElement())
                    if (++counter == 2) {
                        LOGGER.debug("the stop " + id + " was added twice already");
                        return true;
                    }
            }
        }
        LOGGER.debug("the stop " + id + " was added less than twice");
        return false;
    }

    /**
     * Returns an array that contains the id -s of the touched bus stops. The order
     * is the same as in the {@link #stops} list (order of append).
     * @return an array containing the id -s of the touched bus stops in the order of append
     */
    public int[] getStops() {
        LOGGER.trace("called getStops");
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
     * Returns the number of touched stops of the service.
     * It is always at least 1, because the bus station, as the starting point is always in the list,
     * as the first stop.
     * @return the number of touched bus stops, included the bus station
     */
    public int getStopCount() {
        return stops.size();
    }

    /**
     * Returns the id of the last bus stop of the bus service.
     * If there are no bus stops added, it returns the id of the bus station,
     * which is always present as the first bus stop.
     * @return the id of the last bus stop in the list
     */
    public int getLastStop() {
        return stops.getTail().getElement();
    }

    /**
     * Removes all the appended bus stops from the list.
     * This method doesn't delete the first appearance of the bus station (first element).
     * Calling this method when there aren't any bus stops added has no effect.
     */
    public void clear() {
        LOGGER.trace("called clear");
        if (stops.size() == 1) {
            LOGGER.debug("there is nothing to clear");
            return;
        }
        Node<Integer> chain;
        try {
            chain = stops.removeChainFrom(1);
            LOGGER.debug("removing finished successfully");
        } catch (IllegalStateException ise) {
            LOGGER.debug("threw IllegalStateException");
            return;
        }

        undoStack.push(UndoOperation.newAppendInstance(chain));
        LOGGER.debug("undo operation successfully added");
        markAsModified();
    }

    /**
     * Removes the last occurrence of a bus stop from the buffer along with all the following
     * bus stops.
     * This is also a wrapper for the {@link OpenLinkedList#removeChainFrom(Object) removeChainFrom}
     * method. If there are no added bus stops to remove, this method has no effect.
     * @param fromId the bus stop from which the removal shell start (the given bus stop is
     *               included too)
     * @throws IllegalArgumentException when the given bus stop id is not in the list
     */
    public void removeChainFrom(int fromId) {
        LOGGER.trace("called removeChainFrom");
        Node<Integer> chain;
        if (!isClosed() && fromId == 0) {
            if (stops.size() == 1) {
                LOGGER.debug("there is nothing to remove");
                return;
            }
            chain = stops.removeChainFrom(1);
        } else {
            try {
                chain = stops.removeChainFrom(new Integer(fromId));
            } catch (IllegalStateException ise) {
                LOGGER.warn("The program shouldn't have reached this point!");
                throw new IllegalStateException("there is no bus stop to remove");
            } catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException("the given id '" + fromId + "' is invalid");
            }
        }

        undoStack.push(UndoOperation.newAppendInstance(chain));
        LOGGER.debug("undo operation successfully added");
        markAsModified();
    }

    /**
     * Discards the latest modification, if it is not applied already.
     * See {@link #markAsModified()} and {@link #markAsSaved()} for more information.
     * @throws IllegalStateException if there aren't any unapplied modifications
     */
    public void undo() {
        LOGGER.trace("called undo");
        if (!canUndo())
            throw new IllegalStateException("undo stack is empty, there is nothing to undo");

        UndoOperation<Integer> operation = undoStack.pop();
        UndoOperation.OperationType type = operation.getOperationType();
        if (type == UndoOperation.OperationType.DELETE) {
            stops.removeLast();
            LOGGER.debug("delete operation happened");
        } else {
            Node<Integer> chain = operation.getChain();
            stops.appendChain(chain);
            LOGGER.debug("append operation happened");
        }
    }

    /**
     * Returns an array that contains the id -s of all the bus stops that are allowed to be the
     * next stop according to the rules.
     * See {@link #appendStop(int)} for more information.
     * @return an array with all the ids of all the reachable bus stops
     */
    public int[] getReachableStopIds() {
        LOGGER.trace("called getReachableStopIds");
        if (isClosed()) {
            LOGGER.debug("a closed service has no further reachable stops");
            return new int[0];
        }

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
     * Returns an array containing the minutes that it takes to travel from the bus station
     * to each of the added bus stops. All travel times are relative to the
     * {@link BusService#firstLeaveTime firstLeaveTime}.
     * All the bus services start at the bus station, so there is no travel time for the first
     * appearance of the station.
     * @return an array with the travel times to each touched bus stop, relative
     *           to when the bus leaves the station
     */
    public int[] getTravelTimes() {
        LOGGER.trace("called getTravelTimes");
        if (stops.size() < 2) {
            LOGGER.debug("nowhere to travel -> no travel time");
            return new int[0];
        }

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
