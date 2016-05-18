package hillbillies.model.util;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A Linked List that is kept sorted.
 */
public class SortedLinkedList<T extends Comparable<? super T>> extends AbstractCollection<T> {
    private final LinkedList<T> backend;

    /**
     * Creates a new sorted linked list.
     *
     * @param   other
     *          The sorted linked list where a new sorted linked list is made of.
     */
    public SortedLinkedList(SortedLinkedList<T> other) {
        backend = new LinkedList<>(other);
    }

    /**
     * Creates a new sorted linked list.
     */
    public SortedLinkedList() {
        backend = new LinkedList<>();
    }

    /**
     * Returns the iterator of the sorted linked list.
     *
     * @return  Returns an iterator over all elements in this linked list.
     */
    @Override
    public Iterator<T> iterator() {
        return backend.iterator();
    }

    /**
     * Returns the size of the sorted linked list.
     *
     * @return  Returns the amount of elements in this linked list.
     */
    @Override
    public int size() {
        return backend.size();
    }

    /**
     * Adds an element in the correct position in the sorted linked list.
     *
     * @param   n
     *          The element to add to the list.
     *
     * @post    The new list will be sorted.
     *
     * @return  Returns true if the element is successfully added to the list.
     */
    @Override
    public boolean add(T n) {
        if (backend.isEmpty())
            backend.addFirst(n);
        else if (n.compareTo(backend.getFirst()) <= 0)
            backend.addFirst(n);
        else if (n.compareTo(backend.getLast()) >= 0)
            backend.addLast(n);
        else {
            int idx = 0;
            while (idx < size() && n.compareTo(backend.get(idx)) > 0) {
                idx++;
            }
            backend.add(idx, n);
        }
        return true;
    }

    /**
     *  Returns the element of the list that is occupying the idx position.
     *
     * @param   idx
     *          The position of the element of the list to return.
     *
     * @return  Returns the element on the idx position of the sorted linked list.
     */
    public T get(int idx) {
        return backend.get(idx);
    }

    /**
     * Returns whether the list is sorted.
     *
     * @return  Returns true if the list is sorted.
     */
    public boolean isSorted() {
        T last = backend.getFirst();
        for (T t : backend) {
            if (t.compareTo(last) < 0)
                return false;
            last = t;
        }
        return true;
    }
}
