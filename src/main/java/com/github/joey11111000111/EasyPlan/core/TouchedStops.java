package com.github.joey11111000111.EasyPlan.core;

import com.github.joey11111000111.EasyPlan.core.util.OpenLinkedList;

import java.util.Stack;

import static com.github.joey11111000111.EasyPlan.core.util.OpenLinkedList.Node;

/**
 * Created by joey on 2015.11.04..
 */
public class TouchedStops {

    private static class UndoOperation<E> {
        private boolean deleteOperation;
        private Node<E> chain;

        private UndoOperation() {
            deleteOperation = true;
            chain = null;
        }
        private UndoOperation(Node<E> chain) {
            this.chain = chain;
            deleteOperation = false;
        }

        public static <E> UndoOperation newDeleteInstance() {
            return new UndoOperation<E>();
        }
        public static <E> UndoOperation newAppendInstance(Node<E> chain) {
            return new UndoOperation<E>(chain);
        }

        public boolean isDelete() {
            return deleteOperation;
        }
        public boolean isAppend() {
            return !deleteOperation;
        }
        public Node<E> getChain() {
            return chain;
        }
    }//private static class


    private OpenLinkedList<BusStop> stops;
    private Stack<UndoOperation> undoStack;
    private boolean modified;

    public TouchedStops() {
        stops = new OpenLinkedList<BusStop>();
        undoStack = new Stack<UndoOperation>();
        modified = false;
    }

    private void markAsModified() {
        if (!modified)
            modified = false;
    }
    public void markAsSaved() {
        if (modified)
            modified = false;
    }

    public boolean isEmpty() {
        return stops.isEmpty();
    }

    public boolean isModified() {
        return modified;
    }

    public void appendStop(BusStop stop) {
        // TODO
    }

    public void removeLast() {
        if (isEmpty())
            throw new IllegalStateException("there is no bus stop to remove");
        // TODO
    }

    public void undo() {
        if (undoStack.isEmpty())
            throw new IllegalStateException("undo stack is empty");
        // TODO
    }

//    public int[] getReachableStopIds() {
//        if (isEmpty())
//            return BusStop.getReachableIdsOfStation();
//
//    }

}//class
