package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.core.util.OpenLinkedList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import static com.github.joey11111000111.EasyPlan.core.util.OpenLinkedList.Node;

/**
 * Created by joey on 2015.11.04..
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

    public TouchedStops() {
        stops = new OpenLinkedList<Integer>();
        undoStack = new Stack<UndoOperation<Integer>>();
        modified = false;
        closed = false;
    }

    private void markAsModified() {
        if (!modified)
            modified = false;
    }
    void markAsSaved() {
        if (modified)
            modified = false;
        undoStack.clear();
    }

    public boolean isEmpty() {
        return stops.isEmpty();
    }

    public boolean isModified() {
        return modified;
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

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
        // check reachable rule 2 if needed
        if (stops.size() > 3) {
            Node<Integer> node = stops.getTail();
            int counter = 0;
            while (node.hasPrevious()) {
                node = node.previous();
                if (id == node.getElement())
                    if (++counter == 2)
                        throw new IllegalArgumentException("given bus stop '" + id
                                + "' has already appeared twice in the list");
            }
        }

        // append bus stop and create the undo operation for this append operation
        stops.append(id);
        markAsModified();
        undoStack.push(UndoOperation.newDeleteInstance(new Integer(0)));
    }

    public int[] getStops() {
        if (isEmpty())
            throw new IllegalStateException("list is empty, no stops to return");

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

    public void clear() {
        Node<Integer> chain;
        try {
            chain = stops.removeChainFrom(0);
        } catch (IllegalStateException ise) {
            return;
        }

        addUndoOfRemoval(chain);
        markAsModified();
    }

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

    public void undo() {
        if (!canUndo())
            throw new IllegalStateException("undo stack is empty, there is nothing to undo");

        UndoOperation<Integer> operation = undoStack.pop();
        UndoOperation.OperationType type = operation.getOperationType();
        if (type == UndoOperation.OperationType.OPEN) {
            closed = false;
        } else if (type == UndoOperation.OperationType.DELETE) {
            if (closed)
                closed = false;
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

    public int[] getReachableStopIds() {
        if (isEmpty())
            return BusStop.getReachableIdsOfStation();

        int lastId = stops.getTail().getElement();
        int[] ids = BusStop.getReachableIdsOf(lastId);
        if (stops.size() < 3)
            return ids;

        List<Integer> validIds = new ArrayList<Integer>(ids.length);
        FOR:
        for (int i : ids) {
            // the bus station will not be included
            if (BusStop.isStation(i))
                continue;
            // iterate backwords to find one more appearance
            Node<Integer> node = stops.getTail();
            while (node.hasPrevious()) {
                node = node.previous();
                int id = node.getElement();
                if (i == id)
                    continue FOR;
            }
            validIds.add(i);
        }

        int[] resultIds = new int[validIds.size()];
        for (int i = 0; i < resultIds.length; i++)
            resultIds[i] = validIds.get(i);
        return resultIds;
    }

    public int[] getTravelTimes() {
        if (isEmpty())
            throw new IllegalStateException("stop list is empty, cannot get any travel time");

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
