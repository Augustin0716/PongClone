package game.menu;


import java.util.*;

/**
 * A collection <b>but not a list</b> implementation made to access elements where order is important but specific index
 * are not. Items are presented from a {@code head} which is the oldest object present in the list to the latest
 * addition of the list. It can be browsed in a relative way, which means every object can lead to the previous object
 * or the next object, although it's not adapted to access any object other than the {@code head} in a {@code O(1)} time,
 * but rather simplify the incrementation of index :
 * {@code myArray[i]} becomes {@code myLoopingList.current()},
 * {@code myArray[++i]} becomes {@code myLoopingList.next()} and
 * {@code myArray[--i]} becomes {@code myLoopingList.previous()}.<br>
 * The other advantage of the looping list is to avoid {@link IndexOutOfBoundsException} and tests to "loop" such as :
 * <pre>{@code
 * /.../
 * i += goingUp? 1:-1;
 * if (i == -1) i = myArray.length - 1;
 * if (i == myArray.length) i = 0;}</pre>
 * Which is simply replaced by :
 * <pre>{@code
 * if (goingUp) myLoopingList.next();
 * else myLoopingList.previous();}</pre>
 * @param <E> the type of the element stocked in the looping list
 */
public class LoopingList<E> implements Collection<E> {
    /**
     * The nodes are the objects that allow the looping list to modify its size at the will of the user and navigate,
     * while holding an object that can be called by the looping list. Every node can lead to the "previous" node and
     * the "next" node, so nodes altogether create a chain that can be browsed. The first node created is the "head" and
     * is used as a beacon whenever needed. The last node (the "tail") has as its "next" node the head, so users can
     * "loop" within the list without ever throwing an IndexOutOfBoundsException. The head also has as previous node the
     * tail, so both "directions" work properly. A new node can be added by simply being created then assigned as the
     * "previous" node and "next" node of 2 sequent nodes as such :
     * <pre>{@code
     * Node<E> A = new Node(itemA, null); // previous can be null, although not recommended
     * Node<E> C = new Node(itemC, A); // A is now the previous node of C but C is not yet the next node of A : [A] ← [C]
     * A.next = C; // C is now the next node of A : [A] ↔ [C]
     * Node<E> B = new Node(itemB, A); // new node to insert, it "knows" A but has yet to be known...
     * B.next = C; // First C is made the next node of B : [A] ← [B] → [C] / [A] ↔ [C]
     * A.next = B; // Then B is made the next node of A : [A] ↔ [B] → [C] / [A] ← [C]
     * C.previous = B; // Finally B is made the previous node of C : [A] ↔ [B] ↔ [C] }</pre>
     * @param <E> The class of the objects being held (same as LoopingList by definition).
     */
    public static class Node<E> {
        private final E item;
        private Node<E> next = null;
        private Node<E> previous;
        private Node(E item, Node<E> previous) {
            this.item = item;
            this.previous = previous;
        }
    }
    private int size = 0;
    /**
     * An integer that counts the number of modifications, mainly used to perform fail-fast with the iterators
     */
    private int modCount = 0;
    /**
     * The anchor of the LoopingList that can be regarded as the first node of the least, although there are no clear
     * indexes.
     */
    private Node<E> head = null;
    private Node<E> current;

    /**
     * Default constructor. The newly created looping list will be empty.
     */
    public LoopingList() {}

    /**
     * Constructor that takes varargs as parameter (safe). This allows for direct addition upon construction of one or
     * multiple elements while not having to construct another Collection object nor adding elements with
     * {@link LoopingList#add(Object)}/{@link LoopingList#addAll}.
     * @param e varargs or array of elements E that will be added directly to the looping list. The order is conserved.
     */
    @SafeVarargs
    public LoopingList(E... e) {
        this(Arrays.asList(e));
    }

    /**
     * Constructor that directly add a collection to the looping list. Will not count as a modification, so the newly
     * created looping list will have a modCount = 0. The objects within the collection will be cast to E, so it's best
     * to use the actual type of the collection as parameter for LoopingList.
     * @param c the collection to add to the looping list
     */
    @SuppressWarnings("unchecked")
    public LoopingList(Collection<? extends E> c) {
        if (!c.isEmpty()) {
            size = c.size();
            Iterator<E> iterator = (Iterator<E>) c.iterator();
            this.head = new Node<>(iterator.next(), null);
            current = head.previous = head.next = head;
            Node<E> tail = head;
            while (iterator.hasNext()) {
                Node<E> n = new Node<>(iterator.next(), tail);
                tail.next = n;
                head.previous = n;
                n.next = head;
                tail = n;
            }
        }
    }

    /**
     * Constructor optimized for making a looping list out of another looping list. The newly created looping list is
     * a deep copy but not a total copy of the original looping list as its modification count is reinitialized. It has
     * low to no real effect in practice, but it's worth knowing since it means
     * <pre>{@code loopingList.equals(new loopingList(loopingList))}</pre> will be false unless loopingList.modCount = 0.
     * @param l the copied looping list
     * @see Object#equals(Object)
     */
    public LoopingList(LoopingList<E> l) {
        if (l == null) throw new NullPointerException("LoopingList l can't be null");
        if (l.isEmpty()) return; // nothing else to see

        size = l.size; // the copy is safe since either everything goes well or it's over anyway

        Node<E> old = l.head;
        head = new Node<>(old.item, null);
        if (l.head == l.current) current = head;

        old = old.next;

        Node<E> tail = head;
        while(old != l.head) {
            Node<E> n = new Node<>(old.item, tail);
            n.next = head;
            head.previous = n;
            tail.next = n;

            tail = n;

            if (old == l.current) current = n;
            old = old.next;
        }

    }

    /**
     * Get the item of the currently selected node or null if the list is empty. It's advised to test whether the
     * looping list is empty before using this method to avoid getting an unsuspected null.
     * @return the object of the selected node or null if the list is empty
     */
    public E current() {
        return (size != 0)? current.item:null;
    }

    /**
     * Set the next node to the previous node (relative to the current one) and return its item. Calling this method on
     * an empty list will return null directly and a 1-element list will simply return the current item, since current =
     * next = previous.
     * @return the item of the next node (relative to the current one) or null if the list is empty
     */
    public E next() {
        if (size == 0) return null;
        if (size > 1) current = current.next;
        return current();
    }

    /**
     * Set the selected node to the previous node (relative to the current one) and return its item. Calling this method on
     * an empty list will return null directly and a 1-element list will simply return the current item, since current =
     * next = previous.
     * @return the item of the previous node (relative to the current one) or null if the list is empty
     */
    public E previous() {
        if (size == 0) return null;
        if (size > 1) current = current.previous;
        return current();
    }

    /**
     * Add an item by addition of a node in the "last" position, which is the previous node of the head and the next
     * node of the latest added node.
     * @param item element whose presence in this collection is to be ensured
     * @return true if the element has been added and the list has been modified, else false
     */
    @Override
    public boolean add(E item) {
        if (head == null) {
            head = current = new Node<>(item, null);
            head.previous = head.next = head;
        } else {
            Node<E> tail = head.previous;
            Node<E> newNode = new Node<>(item, tail);
            newNode.next = head;
            tail.next = newNode;
            head.previous = newNode;
        }
        modCount++;
        size++;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E o : c) {
            add(o);
            modified = true;
            modCount++;
        }
        return modified;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public boolean contains(Object o) {
        for(E c : this) {
            if (o == c) return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c)  {
            if (!contains(o)) return false;
        }
        return true;
    }

    @Override
    public Object[] toArray() {
        Object[] o = new Object[size];
        int i = 0;
        for(E c : this) {
            o[i++] = c;
        }
        return o;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        int i = 0;
        for(E o : this) {
            a[i++] = (T) o;
        }
        return a;
    }

    @Override
    public boolean remove(Object o) {
        if (size == 0) return false;
        Node<E> n = head;
        for (int i = 0; i < size; i++) {
            if (n.item == o) {
                if (size == 1) head = null;
                else {
                    n.previous.next = n.next;
                    n.next.previous = n.previous;
                }
                if (n == head) head = n.next;
                if (n == current) current = n.next;

                size--;
                modCount++;
                return true;
            }
            n = n.next;
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (size == 0) return false;
        boolean modified = false;
        for(Object o : c) {
            while(remove(o)) modified = true;
        }
        return modified;
    }

    /**
     * Remove all items from the looping list. The items are not deleted, the looping list simply get rid of the pointer
     * to the first node, but it can exist elsewhere if it was stocked somewhere else.
     */
    @Override
    public void clear() {
        // beheading the list and setting size to 0 is sufficient to clear it
        head = current = null;
        size = 0;
        modCount++;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (size == 0) return false;
        boolean modified = false;
        Node<E> n = head;
        Node<E> next;
        for (int i = 0; i < size; i++) {
            next = n.next;
            if (!c.contains(n.item)) {
                remove(n.item);
                modCount++;
                modified = true;
            }
            n = next;
        }
        return modified;
    }

    /**
     * Return an iterator over the elements of this list. The order is always from the head node to the "tail", which is
     * the latest added node, FIFO-style.
     * @return an Iterator object with the objects of this list.
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private int visited = 0;
            private final int expectedModCount = modCount;
            private final Node<E> h = head;
            private Node<E> c = h;

            @Override
            public boolean hasNext() {
                return visited < size;
            }

            @Override
            public E next() {
                if (expectedModCount != modCount) throw new ConcurrentModificationException();
                if (!hasNext()) throw new NoSuchElementException();
                E obj = c.item;
                c = c.next;
                visited++;
                return obj;
            }
        };

    }
}
