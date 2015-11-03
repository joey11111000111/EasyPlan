package com.github.joey11111000111.EasyPlan.core.util;

/**
 * Created by joey on 2015.11.03..
 */
public class OpenLinkedList<E> {

    static class Node<E> {
        private E object;
        private Node<E> next;

        // only available in the class OpenLinkedList
        private Node(E object, Node<E> next) {
            this.object = object;
            this.next = next;
        }

        private void setNext(Node<E> nextNode) {
            next = nextNode;
        }

        public E getObject() {
            return object;
        }

        public Node<E> next() {
            return next;
        }
    }//static class

}//class
