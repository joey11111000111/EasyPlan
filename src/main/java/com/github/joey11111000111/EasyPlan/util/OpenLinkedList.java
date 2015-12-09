package com.github.joey11111000111.EasyPlan.util;

/**
 * The OpenLinkedList class represents a linked list data structure, with
 * the capability of appending or removing a part chain to or from the end of the list.
 * It is "open", because it gives a little insight to the inner structure, in the form of
 * Nodes. Besides the stored data, the wrapping node can also be received, with all the possible
 * following nodes. The received part chain is removed from the list, and also unmodifiable,
 * so it is safe to use. This chain can also be appended to the list at a later time, independent
 * of the current state of the list. All data modifications happen at the end of the list.
 */
public class OpenLinkedList<E> {

    /**
     * Represents one link in a linked list, with references to the next and previous
     * links, and the stored data of the given type. After creation, all instances are
     * unmodifiable outside of the container class.
     * @param <E> the type of the stored element
     */
    public static class Node<E> {
        private E element;
        private Node<E> next;
        private Node<E> previous;

        // only available in the class OpenLinkedList
        private Node(E element, Node<E> next, Node<E> previous) {
            if (element == null)
                throw new NullPointerException("Node element must not be null");
            this.element = element;
            this.next = next;
            this.previous = previous;
        }

        // iteration methods --------------

        /**
         * Returns true, when there is at least one following node in the cain
         * @return true, when there is at least one following node in the chain
         */
        public boolean hasNext() {
            return next != null;
        }
        /**
         * Returns true, when there is at least one previous node in the cain
         * @return true, when there is at least one previous node in the chain
         */
        public boolean hasPrevious() {
            return previous != null;
        }

        /**
         * Returns the following node in the chain.
         * @return the next node in the chain
         * @throws IllegalStateException when the current is the last node in the chain
         */
        public Node<E> next() {
            if (hasNext())
                return next;
            throw new IllegalStateException("no next element");
        }
        /**
         * Returns the previous node in the chain.
         * @return the previous node in the chain
         * @throws IllegalStateException when the current is the first node in the chain
         */
        public Node<E> previous() {
            if (hasPrevious())
                return previous;
            throw new IllegalStateException("no previous element");
        }

        /**
         * Returns the stored element
         * @return the stored element of the given type
         */
        public E getElement() {
            return element;
        }
    }//static class

    private Node<E> head;
    private Node<E> tail;
    private int size;

    /**
     * Creates an empty list
     */
    public OpenLinkedList() {
        head = tail = null;
        size = 0;
    }

    /**
     * Returns true, when the list hasn't got any elements.
     * @return true, when the list is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of stored elements
     * @return the number of stored elements
     */
    public int size() {
        return size;
    }

    /**
     * Creates a wrapping node for the element, and appends that node to the end of the list.
     * @param element the element to store at the end of the list
     */
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

    /**
     * Appends the given node to the end of the list. If the node has following nodes (it is a chain)
     * than the following elements are added too.
     * @param chain the chain to add to the end of the list
     */
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

    /**
     * Returns true, when there is an element at the specified index
     * @param index the index to specify the position of an element
     * @return true, when the index is not negative and smaller than the
     * number of elements in the list
     */
    public boolean validIndex(int index) {
        return index >= 0 && index < size;
    }

    /**
     * Removes the last node from the list and returns it.
     * @return the last node of the list
     * @throws IllegalStateException when the list is empty
     */
    public Node<E> removeLast() {
        if (isEmpty())
            throw new IllegalStateException("the list is empty");

        Node<E> last = tail;
        if (size == 1)
            head = tail = null;
        else {
            tail = tail.previous;
            tail.next = null;
        }
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

    private Node<E> removeChainFrom(Node<E> node) {
        if (!node.hasPrevious()) {
            head = tail = null;
            size = 0;
            return node;
        }
        else {
            tail = node.previous;
            tail.next = null;
            size -= countChainLinks(node);
            node.previous = null;
            return node;
        }
    }

    /**
     * Removes the node at the specified index, with all the following nodes,
     * and returns this node. The index of the last element in the modified list
     * will be 'index - 1'.
     * @param index specifies which node of the list will be removed by its index
     * @return the removed node, which can have following nodes (it can be a chain)
     * @throws IllegalStateException when the list is empty, thus there aren't any elements to remove
     * @throws IndexOutOfBoundsException when the specified index is out of range
     */
    public Node<E> removeChainFrom(int index) {
        if (isEmpty())
            throw new IllegalStateException("the list is empty");
        if (!validIndex(index))
            throw new IndexOutOfBoundsException("invalid list index: " + index);

        Node<E> node;
        if (index < size / 2) {
            node = head;
            for (int i = 0; i < index; i++)
                node = node.next;
        }
        else {
            node = tail;
            for (int i = 0; i < (size - 1) - index; i++)
                node = node.previous;

        }
        return removeChainFrom(node);
    }

    /**
     * Removes and returns the last node that contains the specified element. All the following
     * elements are removed and returned, as a chain.
     * @param element the last node will be removed that contains an element that is equal with this
     * @return the node (possibly chain) that contains the given element and has the highest index
     * @throws IllegalArgumentException when the list is empty
     * @throws IllegalArgumentException when the given element is not contained by any of the nodes
     */
    public Node<E> removeChainFrom(E element) {
        if (isEmpty())
            throw new IllegalStateException("list is empty");
        // search for the last node that has the given element
        Node<E> node = tail;
        while (true) {
            if (node.getElement().equals(element))
                return removeChainFrom(node);
            if (node.hasPrevious())
                node = node.previous;
            else
                break;
        }
        throw new IllegalArgumentException("the element to remove is not in the list");
    }

    /**
     * Removes all elements of the list
     */
    public void clear() {
        if (isEmpty())
            return;
        head = tail = null;
        size = 0;
    }

    /**
     * Returns the first node of the list
     * @return the first node of the list
     */
    public Node<E> getHead() {
        return head;
    }
    /**
     * Returns the last node of the list
     * @return the last node of the list
     */
    public Node<E> getTail() {
        return tail;
    }

}//class
