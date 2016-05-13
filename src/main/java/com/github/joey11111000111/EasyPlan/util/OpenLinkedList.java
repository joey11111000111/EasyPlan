package com.github.joey11111000111.EasyPlan.util;

/**
 * The OpenLinkedList class represents a linked list data structure, with
 * the capability of appending or removing a part chain to or from the end of the list.
 * It is "open", because it gives a little insight to the inner structure, in the form of
 * {@link com.github.joey11111000111.EasyPlan.util.OpenLinkedList.Node Nodes}.
 * Besides the stored data, the wrapping node can also be received, with all the possible
 * following nodes. The received part chain is removed from the list, and also unmodifiable,
 * so it is safe to use. This chain can also be appended to the list at a later time, independent
 * of the current state of the list. All data modifications happen at the end of the list.
 * @param <E> the type of elements this list will contain
 */
public class OpenLinkedList<E> {

    /**
     * Represents one link in a linked list, with references to the next and previous
     * links, and the stored data of the given type. After creation, all instances are
     * unmodifiable outside of the container class.
     * @param <E> the type of the stored element
     */
    public static class Node<E> {

        /**
         * The element that this link contains. Not allowed to be null.
         */
        private E element;

        /**
         * A reference to the following link, possible to contain null.
         */
        private Node<E> next;

        /**
         * A reference to the subsequent link, possible to contain null.
         */
        private Node<E> previous;

        // only available in the class OpenLinkedList

        /**
         * Creates a new instance that contains the given element and the two
         * given references.
         * @param element the element to store
         * @param next a reference to the next link in the chain
         * @param previous a reference to the previous link in the chain
         * @throws NullPointerException if the element to store is null
         */
        private Node(E element, Node<E> next, Node<E> previous) {
            if (element == null)
                throw new NullPointerException("Node element must not be null");
            this.element = element;
            this.next = next;
            this.previous = previous;
        }

        // iteration methods --------------

        /**
         * Returns true, when there is at least one following node in the cain.
         * @return true, when there is at least one following node in the chain
         */
        public boolean hasNext() {
            return next != null;
        }
        /**
         * Returns true, when there is at least one previous node in the cain.
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
         * Returns the stored element of this link.
         * @return the stored element of the given type
         */
        public E getElement() {
            return element;
        }
    }//static class

    /**
     * A reference to the first link of the chain.
     * Contains null if the list is empty
     */
    private Node<E> head;

    /**
     * A reference to the last link of the chain.
     * Contains null if the list is empty
     */
    private Node<E> tail;

    /**
     * Stores the number of elements in the list.
     */
    private int size;

    /**
     * Creates an empty list.
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
     * Returns the number of stored elements.
     * @return the number of stored elements
     */
    public int size() {
        return size;
    }

    /**
     * Appends the new element to the end of the list.
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
     * Appends the given node to the end of the list.
     * If the node has following nodes than the following elements are added with it too.
     * @param chain the chain to add to the end of the list
     * @throws NullPointerException if the given argument is null
     */
    public void appendChain(Node<E> chain) {
        if (chain == null)
            throw new NullPointerException("cannot append null to the list");

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
     * Returns true, if there is an element at the specified index.
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

    /**
     * Calculates the total number of links that this chain has.
     * The starting link is included too.
     * For example if the given chain has two more following links,
     * than the result is 3.
     * @param chain the chain whose link count shell be returned
     * @return the total number of links that the given chain has
     */
    private int countChainLinks(Node<E> chain) {
        int chainLinkNum = 1;
        while (chain.hasNext()) {
            chain = chain.next;
            chainLinkNum++;
        }
        return chainLinkNum;
    }

    /**
     * Removes and returns the specified node from the list, with all its
     * following nodes.
     * @param node the node to remove and return
     * @return the removed node with all its following nodes.
     */
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
     * @return the removed node, which can have following nodes
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
     * @return the node that contains the given element and has the highest index
     * @throws IllegalStateException when the list is empty
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
     * Removes all elements of the list.
     */
    public void clear() {
        if (isEmpty())
            return;
        head = tail = null;
        size = 0;
    }

    /**
     * Returns the first node of the list.
     * @return the first node of the list
     */
    public Node<E> getHead() {
        return head;
    }
    /**
     * Returns the last node of the list.
     * @return the last node of the list
     */
    public Node<E> getTail() {
        return tail;
    }

}//class
