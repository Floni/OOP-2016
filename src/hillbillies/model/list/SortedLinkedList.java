package hillbillies.model.list;

import java.util.*;

/**
 * A Linked List that is kept sorted.
 */
public class SortedLinkedList<T extends Comparable<? super T>> extends AbstractCollection<T> {
    private LinkedList<T> backend;

    public SortedLinkedList() {
        backend = new LinkedList<>();
    }

    @Override
    public Iterator<T> iterator() {
        return backend.iterator();
    }

    @Override
    public int size() {
        return backend.size();
    }

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

    public T get(int idx) {
        return backend.get(idx);
    }

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
