package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ChainedHashMap<K, V> extends AbstractIterableMap<K, V> {
    private static final double DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD = 0.75;
    private static final int DEFAULT_INITIAL_CHAIN_COUNT = 10;
    private static final int DEFAULT_INITIAL_CHAIN_CAPACITY = 10;

    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */
    AbstractIterableMap<K, V>[] chains;

    // You're encouraged to add extra fields (and helper methods) though!
    private int size;
    private final double resizeFactor;
    private final int initialCap;

    /**
     * Constructs a new ChainedHashMap with default resizing load factor threshold,
     * default initial chain count, and default initial chain capacity.
     */
    public ChainedHashMap() {
        this(DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD, DEFAULT_INITIAL_CHAIN_COUNT, DEFAULT_INITIAL_CHAIN_CAPACITY);
    }

    /**
     * Constructs a new ChainedHashMap with the given parameters.
     *
     * @param resizingLoadFactorThreshold the load factor threshold for resizing. When the load factor
     *                                    exceeds this value, the hash table resizes. Must be > 0.
     * @param initialChainCount the initial number of chains for your hash table. Must be > 0.
     * @param chainInitialCapacity the initial capacity of each ArrayMap chain created by the map.
     *                             Must be > 0.
     */
    public ChainedHashMap(double resizingLoadFactorThreshold, int initialChainCount, int chainInitialCapacity) {
        this.chains = createArrayOfChains(initialChainCount);
        this.size = 0;
        this.initialCap = chainInitialCapacity;
        this.resizeFactor = resizingLoadFactorThreshold;
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code AbstractIterableMap<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     * @see ArrayMap createArrayOfEntries method for more background on why we need this method
     */
    @SuppressWarnings("unchecked")
    private AbstractIterableMap<K, V>[] createArrayOfChains(int arraySize) {
        return (AbstractIterableMap<K, V>[]) new AbstractIterableMap[arraySize];
    }

    /**
     * Returns a new chain.
     *
     * This method will be overridden by the grader so that your ChainedHashMap implementation
     * is graded using our solution ArrayMaps.
     *
     * Note: You do not need to modify this method.
     */
    protected AbstractIterableMap<K, V> createChain(int initialSize) {
        return new ArrayMap<>(initialSize);
    }

    @Override
    public V get(Object key) {
        //throw new UnsupportedOperationException("Not implemented yet.");
        int index = Math.abs(key.hashCode()) % this.chains.length;
        if (this.chains[index] != null) {
            return this.chains[index].get(key);
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        //throw new UnsupportedOperationException("Not implemented yet.");

        int index = Math.abs(key.hashCode()) % this.chains.length;

        if (this.chains[index] == null) {
            this.chains[index] = createChain(this.initialCap);
        }

        V old = this.chains[index].get(key);
        //System.out.println("new value: "+old);

        if (!chains[index].containsKey(key)) {
            this.size++;
        }

        this.chains[index].put(key, value);


        if ((double) (this.size/this.chains.length) > this.resizeFactor) {
            System.out.println("resized");
            resize();
        }
        return old;
    }


    private void resize() {
        int newCap = chains.length*2;
        AbstractIterableMap<K, V>[] newChains = createArrayOfChains(newCap);
        for (AbstractIterableMap<K, V> chain : chains) {
            if (chain != null) {
                for (Entry<K, V> map: chain) {
                    if (map != null) { // added this if statement
                        int newIndex = Math.abs(map.getKey().hashCode()) % newCap;

                        if (newChains[newIndex] == null) {
                            newChains[newIndex] = createChain(1);
                        }
                        newChains[newIndex].put(map.getKey(), map.getValue());
                    }
                }
            }
        }
        this.chains = newChains;
    }

    @Override
    public V remove(Object key) {
        int index = Math.abs(key.hashCode()) % this.chains.length;
        if (chains[index] != null) {
            V old = chains[index].get(key);
            if (old != null) {
                chains[index].remove(key);
                size--;
                return old;
            }
        }
        return null;
    }

    @Override
    public void clear() {
        this.chains = createArrayOfChains(chains.length);
        this.size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        int index = Math.abs(key.hashCode()) % this.chains.length;
        //if (chains[index] != null) {
        //    return chains[index].get(key) != null;
        //}
        if (chains[index] == null) {
            return false;
        }
        return chains[index].containsKey(key);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: you won't need to change this method (unless you add more constructor parameters)
        return new ChainedHashMapIterator<>(this.chains);
    }

    // Doing so will give you a better string representation for assertion errors the debugger.
    //@Override
    //public String toString() {
    //return super.toString();
    //}

    /*
    See the assignment webpage for tips and restrictions on implementing this iterator.
     */
    private static class ChainedHashMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private AbstractIterableMap<K, V>[] chains;
        // You may add more fields and constructor parameters
        public int index;
        public int indexArrayMap;
        public int indexBucket;
        private Iterator<Map.Entry<K, V>> bucketIterator;

        public ChainedHashMapIterator(AbstractIterableMap<K, V>[] chains) {
            this.chains = chains;
            this.indexArrayMap = 0;
            this.indexBucket = 0;
            this.bucketIterator = null;
            searchBuckets();
        }


        //check if bucket is empty/null
        // if not empty iterate through array map
        // when iterator = false, check if there is next bucket
        // if there is, keep iterating
        //if not, return false

        private void searchBuckets() {
            while (indexBucket < chains.length) {
                if (chains[indexBucket] != null) {
                    this.bucketIterator = chains[indexBucket].iterator();
                    if (bucketIterator.hasNext()) {
                        return;
                    }
                }
                indexBucket++;
            }
            bucketIterator = null;
        }

        @Override
        public boolean hasNext() {
            return bucketIterator != null && bucketIterator.hasNext();
            // while (chains[indexBucket] == null ||
            // !chains[indexBucket].iterator().hasNext() && indexBucket < chains.length) {
            //  indexBucket++;
            //}
            // if (chains[indexBucket] == null) {
            //     return false;
            // }
            //if (chains[indexBucket] != null && chains[indexBucket].iterator().hasNext()) {
            //  indexArrayMap++;
            //   return chains[indexBucket].iterator().hasNext();
            // }

            //return false;
            // //throw new UnsupportedOperationException("Not implemented yet.");
            // if (chains[indexBucket] == null) {
            //     indexBucket++;
            // } else {
            //     return indexArrayMap  < chains[indexBucket].size();
            // }
            // return false;
        }

        @Override
        public Map.Entry<K, V> next() {
            //throw new UnsupportedOperationException("Not implemented yet.");
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Map.Entry<K, V> nextEntry = bucketIterator.next();

            if (!bucketIterator.hasNext()) {
                indexBucket++;
                searchBuckets();
            }
            return nextEntry;
            // V value = chains[indexChain].get(indexEntry);
            //K key = chains[indexChain].getKey(indexEntry);
            //Map.Entry<K, V> entry = new SimpleEntry<>(indexEntry, value);
            // indexEntry++;
            // return newEntry;
        }
    }
}
