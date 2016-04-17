package hillbillies.model.list;

import java.util.*;

/**
 * Created by timo on 4/17/16.
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
        int idx = 0;
        while (idx < size() && n.compareTo(backend.get(idx)) > 0) {
            idx++;
        }
        backend.add(idx, n);
        return true;
    }
}
