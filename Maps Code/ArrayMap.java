package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ArrayMap<K, V> extends AbstractIterableMap<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 4;
    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */
    SimpleEntry<K, V>[] entries;
    private int size;

    // You may add extra fields or helper methods though!

    /**
     * Constructs a new ArrayMap with default initial capacity.
     */
    public ArrayMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Constructs a new ArrayMap with the given initial capacity (i.e., the initial
     * size of the internal array).
     *
     * @param initialCapacity the initial capacity of the ArrayMap. Must be > 0.
     */
    public ArrayMap(int initialCapacity) {
        this.entries = this.createArrayOfEntries(initialCapacity);
        this.size = 0;
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code Entry<K, V>} objects.
     * Note that each element in the array will initially be null.
     * Note: You do not need to modify this method.
     */
    @SuppressWarnings("unchecked")
    private SimpleEntry<K, V>[] createArrayOfEntries(int arraySize) {
        /*
        It turns out that creating arrays of generic objects in Java is complicated due to something
        known as "type erasure."

        We've given you this helper method to help simplify this part of your assignment. Use this
        helper method as appropriate when implementing the rest of this class.

        You are not required to understand how this method works, what type erasure is, or how
        arrays and generics interact.
        */
        return (SimpleEntry<K, V>[]) (new SimpleEntry[arraySize]);
    }

    @Override
    public V get(Object key) {
        for (int i = 0; i < this.size; i++) {
            SimpleEntry<K, V> curr = entries[i];
            if (curr.getKey().equals(key)) {
                return curr.getValue();
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        // first check is key is already there
        // if it is, replace it and return previous value
        for (int i = 0; i < this.size; i++) {
            SimpleEntry<K, V> curr = entries[i];
            if (curr.getKey().equals(key)) {
                V previousValue = curr.getValue();
                entries[i] = new SimpleEntry<>(key, value);
                return previousValue;
            }
        }

        // if it isn't a there's a null add it to the null and return null
        if (this.size < entries.length) {
            entries[this.size] = new SimpleEntry<>(key, value);
            this.size++;
            return null;
        }

        // if array is full, create new array double size
        // copy over all elements to new array
        // put it at the end

        SimpleEntry<K, V>[] newArray = createArrayOfEntries((entries.length * 2) + 1);
        System.arraycopy(entries, 0, newArray, 0, entries.length);
        entries = newArray;
        entries[this.size] = new SimpleEntry<>(key, value);
        this.size++;
        return null;
    }

    @Override
    public V remove(Object key) {
        if (this.size == 0) {
            return null;
        }
        int removedEntry = -1;
        V originalVal = null;

        for (int i = 0; i < this.size; i++) {
            SimpleEntry<K, V> curr = entries[i];
            if (curr.getKey().equals(key)) {
                originalVal = curr.getValue();
                entries[i] = null;
                removedEntry = i;
                this.size--;
                break;
            }
        }
        if (removedEntry == -1) { // case that the key wasn't found
            return null;
        }
        entries[removedEntry] = entries[this.size];
        entries[this.size] = null;
        return originalVal;
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.size; i++) {
            entries[i] = null;
        }
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        for (int i = 0; i < size; i++) {
            SimpleEntry<K, V> curr = entries[i];
            if (curr.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: You may or may not need to change this method, depending on whether you
        // add any parameters to the ArrayMapIterator constructor.
        return new ArrayMapIterator<>(this.entries, this.size);
    }

    // Doing so will give you a better string representation for assertion errors the debugger.
    // REMOVE THIS
    @Override
    public String toString() {
        return super.toString();
    }

    private static class ArrayMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private final SimpleEntry<K, V>[] entries;
        private final int size;
        private int next;

        // You may add more fields and constructor parameters
        public ArrayMapIterator(SimpleEntry<K, V>[] entries, int size) {
            this.entries = entries;
            this.size = size;
            this.next = 0;
        }

        @Override
        public boolean hasNext() {
            return next < size;
        }

        @Override
        public Map.Entry<K, V> next() {
            if (next >= size) {
                throw new NoSuchElementException();
            }
            return entries[next++];
        }
    }
}
