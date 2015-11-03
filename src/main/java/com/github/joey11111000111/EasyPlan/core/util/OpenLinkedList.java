package com.github.joey11111000111.EasyPlan.core.util;

/**
 * Created by joey on 2015.11.03..
 */
public class OpenLinkedList<E> {

    static class Node<E> {
        private E object;
        private Node<E> next;
        private Node<E> previous;

        // only available in the class OpenLinkedList
        private Node(E object, Node<E> next, Node<E> previous) {
            this.object = object;
            this.next = next;
            this.previous = previous;
        }

        private void setNext(Node<E> nextNode) {
            next = nextNode;
        }
        private void setPrevious(Node<E> prevNode) {
            previous = prevNode;
        }

        // iteration methods --------------
        public boolean hasNext() {
            return next != null;
        }
        public boolean hasPrevious() {
            return previous != null;
        }

        public Node<E> next() {
            if (hasNext())
                return next;
            throw new IllegalStateException("no next element");
        }
        public Node<E> previous() {
            if (hasPrevious())
                return previous;
            throw new IllegalStateException("no previous element");
        }

        public E getObject() {
            return object;
        }
    }//static class

    private Node<E> head;
    private Node<E> tail;
    private int size;

    public OpenLinkedList() {
        head = tail = null;
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void append(E element) {
        Node<E> newNode = new Node<E>(element, null, tail);
        if (tail == null)
            head = tail = newNode;
        else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    public void appendChain(Node<E> chain) {
        if (head == null) {
            head = chain;
            chain.previous = null;
        }
        else {
            tail.next = chain;
            chain.previous = tail;
        }

        int chainLinkNum = 1;
        while (chain.hasNext()) {
            chain = chain.next;
            chainLinkNum++;
        }
        tail = chain;
        size += chainLinkNum;
    }

    public Node<E> removeLast() {
        if (size < 1)
            throw new IllegalStateException("the list is empty");

        Node<E> last = tail;
        if (size == 1)
            head = tail = null;
        else
            tail = tail.previous;

        size--;
        return last;
    }

    private int countChainLinks(Node<E> chain) {
        int chainLinkNum = 1;
        while (chain.hasNext()) {
            chain = chain.next;
            chainLinkNum++;
        }
        return chainLinkNum;
    }

    public Node<E> removeChainFrom(Node<E> node) {
        if (!node.hasPrevious()) {
            head = tail = null;
            size = 0;
            return node;
        }
        else {
            tail = node.previous;
            tail.next = null;
            size -= countChainLinks(node);
            return node;
        }
    }

    public Node<E> removeChainFrom(int index) {
        Node<E> node;
        if (index < size / 2) {
            node = head;
            for (int i = 0; i < index; i++)
                node = node.next;
        }
        else {
            node = tail;
            for (int i = 0; i < index; i++)
                node = node.previous;

        }
        return removeChainFrom(node);
    }

    public Node<E> getHead() {
        return head;
    }
    public Node<E> getTail() {
        return tail;
    }



}//class
