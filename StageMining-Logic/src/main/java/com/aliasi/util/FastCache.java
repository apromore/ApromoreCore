package com.aliasi.util;

import com.aliasi.util.AbstractExternalizable;

import java.lang.ref.SoftReference;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;


/**
 * A <code>FastCache</code> is a map implemented with soft references,
 * optimistic copy-on-write updates, and approximate count-based
 * pruning.  It is intended for scalable multi-threaded caches.  It
 * sacrifices recall of mappings for speed of put and get operations
 * along with conservative memory guarantees.
 *
 * <p><i>Note:</i>The class {@link HardFastCache} is nearly identical
 * to this class, but with no soft references around hash buckets.
 *
 * <p>The basis of the cache is a fixed-size hash map, based on values
 * returned by objects' <code>hashCode()</code> and
 * <code>equals(Object)</code> methods.
 *
 * <p>The map entries in the hash map are stored in buckets held by
 * soft references.  Thus entries in the mapping may be garbage
 * collected.  In the implementation, whole hash buckets are
 * collected, which is safe and highly efficient but may require some
 * additional recomputation of values that were removed by pruning.
 *
 * <p>Entries are stored in the map using a very optimistic update.
 * No synchronization at all is performed on the cache entries or
 * their counts.  A copy-on-write strategy coupled with Java's memory
 * model for references ensures that the cache remains consistent, if
 * not complete.  What this means is that multiple threads may both
 * try to cache a mapping and only one will be saved and/or
 * incremented in count.
 *
 * <p>When the table approximately exceeds the specified load factor,
 * the thread performing the add will perform a garbage collection by
 * reducing reference counts by half, rounding down, and removing
 * entries with zero counts.  Pruning is subject to the caveats
 * mentioned in the last paragraph.  Counts are not guaranteed to be
 * accurate.  Pruning itself is synchronized and more conservatively
 * copy-on-write.  By setting the load factor to
 * <code>Double.POSITIVE_INFINITY</code> there will be never be any
 * pruning done by this class; all pruning will take place by soft
 * reference garbage collection.
 *
 * <p>A fast cache acts as a mapping with copy-on-write semantics.
 * Equality and iteration are defined as usual, with the caveat that
 * the snapshot taken of the elements may not be up to date.  Iterators
 * may be used concurrently, but their remove methods do not affect
 * the underlying cache.
 *
 * <p><b>Serialization</b></p>
 *
 * <p>A fast cache may be serialized if the keys and values it
 * contains are serializable.  It may always be serialized if
 * it is first cleared using {@link #clear()}.
 *
 * <p><b>References</b></p>
 *
 * <p>For more information on soft references, see:
 *
 * <ul>
 * <li> Peter Hagar. 2002. <a href="http://www-128.ibm.com/developerworks/library/j-refs/">Guidelines for using the Java 2 reference classes</a>. IBM DeveloperWorks.
 * <li> Jeff Friesen. 2002. <a href="http://www.javaworld.com/javaworld/jw-01-2002/jw-0104-java101.html">Trash talk, part 2: reference objects API</a>. JavaWorld.
 * </ul>
 *
 * For information on copy-on-write and optimistic updates,
 * see section 2.4 of:
 *
 * <ul>
 * <li> Doug Lea.  2000.
 * <i>Concurrent Programming in Java. Second Edition.</i>
 * Addison-Wesley.
 * </ul>
 *
 * @author  Bob Carpenter
 * @version 3.8.3
 * @since   LingPipe2.2
 * @param <K> the type of keys in the map
 * @param <V> the type of values in the map
 */
public class FastCache<K,V>
    extends AbstractMap<K,V>
    implements Serializable {

    static final long serialVersionUID = 3003326726041067827L;

    private static final double DEFAULT_LOAD_FACTOR = 0.5;

    private final SoftReference<Record<K,V>>[] mBuckets;

    private volatile int mNumEntries = 0;

    private int mMaxEntries;

    /**
     * Constrcut a fast cache of the specified size and default load
     * factor.  The default load factor is 0.5.  See {@link
     * #FastCache(int,double)} for more information.
     *
     * @param size Number of buckets in cache
     * @throws IllegalArgumentException if the size is less than 2.
     */
    public FastCache(int size) {
        this(size,DEFAULT_LOAD_FACTOR);
    }


    FastCache(int maxEntries, int numBuckets, boolean ignoreMe) {
        mMaxEntries = maxEntries;
        // cut-and-paste from below, must be in-constructor for final
        @SuppressWarnings({"unchecked","rawtypes"})
        SoftReference<Record<K,V>>[] bucketsTemp
            = (SoftReference<Record<K,V>>[]) new SoftReference[numBuckets];
        mBuckets = bucketsTemp;
    }

    /**
     * Construct a fast cache of the specified size (measured in
     * number of hash buckets) and load factor.  The size times the
     * load factor must be greater than or equal to 1.  When the
     * (approximate) number of entries exceeds the load factor times
     * the size, the cache is pruned.
     *
     * @param size Number of buckets in the cache.
     * @param loadFactor Load factor of the cache.
     * @throws IllegalArgumentException If the size is less than one or the load
     * factor is not a positive finite value.
     */
    public FastCache(int size, double loadFactor) {
        if (size < 1) {
            String msg = "Cache size must be at least 1."
                + " Found cache size=" + size;
            throw new IllegalArgumentException(msg);
        }
        if (loadFactor < 0.0 || Double.isNaN(loadFactor) || Double.isInfinite(loadFactor)) {
            String msg = "Load factor must be finite and positive."
                + " found loadFactor=" + loadFactor;
            throw new IllegalArgumentException(msg);
        }
        mMaxEntries = (int) (loadFactor * (double) size);
        if (mMaxEntries < 1) {
            String msg = "size * loadFactor must be > 0."
                + " Found size=" + size
                + " loadFactor=" + loadFactor;
            throw new IllegalArgumentException(msg);
        }
        // required for array
        @SuppressWarnings({"unchecked","rawtypes"})
        SoftReference<Record<K,V>>[] bucketsTemp
            = (SoftReference<Record<K,V>>[]) new SoftReference[size];
        mBuckets = bucketsTemp;
    }


    Record<K,V> getFirstRecord(int bucketId) {
        SoftReference<Record<K,V>> ref = mBuckets[bucketId];
        return ref == null ? null : ref.get();
    }

    void setFirstRecord(int bucketId, Record<K,V> record) {
        SoftReference<Record<K,V>> ref = new SoftReference<Record<K,V>>(record);
        mBuckets[bucketId] = ref;
    }

    /**
     * Returns the value of the specified key or <code>null</code> if
     * there is no value attached.  Note that the argument is not
     * the generic <code>&lt;K&gt;</code> key type, but <code>Object</code>
     * to match the requirements of <code>java.util.Map</code>.
     *
     * <p><i>Warning:</i> Because of the approximate cache-like
     * behavior of this class, key-value pairs previously added
     * by the {@link #put(Object,Object)} method may disappear.
     *
     * @param key Mapping key.
     * @return The value for the specified key.
     */
    @Override
    public V get(Object key) {
        int bucketId = bucketId(key);
        for (Record<K,V> record = getFirstRecord(bucketId);
             record != null;
             record = record.mNextRecord) {

            if (record.mKey.equals(key)) {
                ++record.mCount;
                return record.mValue;
            }
        }
        return null;
    }

    int bucketId(Object key) {
        return java.lang.Math.abs(key.hashCode() % mBuckets.length);
    }

    /**
     * Sets the value of the specified key to the specified value.
     * If there is already a value for the specified key, the count
     * is incremented, but the value is not replaced.
     *
     * <p><i>Warning:</i> Because of the approximate cache-like
     * behavior of this class, setting the value of a key with this
     * method is not guaranteed to replace older values or remain in
     * the mapping until the next call to {@link #get(Object)}.
     *
     * @param key Mapping key.
     * @param value New value for the specified key.
     * @return <code>null</code>, even if there is an existing
     * value for the specified key.
     */
    @Override
    public V put(K key, V value) {
        int bucketId = bucketId(key);
        Record<K,V> firstRecord = getFirstRecord(bucketId);
        for (Record<K,V> record = firstRecord;
             record != null;
             record = record.mNextRecord) {
            if (record.mKey.equals(key)) {
                ++record.mCount; // increment instead
                return null; // already there
            }
        }
        prune();
        firstRecord = getFirstRecord(bucketId); // may've been pruned
        Record<K,V> record = new Record<K,V>(key,value,firstRecord);
        setFirstRecord(bucketId,record);
        ++mNumEntries;
        return null;
    }

    /**
     * Removes all entries from this cache.
     */
    public void clear() {
        synchronized (this) {
            for (SoftReference<Record<K,V>> ref : mBuckets)
                if (ref != null)
                    ref.clear();
        }
    }

    /**
     * Prunes this cache by (approximately) dividing the counts of
     * entries by two and removing the ones with zero counts.  This
     * operation is approximate in the sense that the optimistic
     * update strategy applied is not guaranteed to actually do any
     * pruning or decrements of counts.
     */
    public void prune() {
        // only synchronized versus other prunes;
        // other puts, etc. may interfere, which is OK
        synchronized (this) {
            if (mNumEntries < mMaxEntries) return;
            int count = 0;
            for (int i = 0; i < mBuckets.length; ++i) {
                Record<K,V> record = getFirstRecord(i);
                Record<K,V> prunedRecord = prune(record);
                setFirstRecord(i,prunedRecord);
                for (Record<K,V> r = prunedRecord;
                     r != null;
                     r = r.mNextRecord)
                    ++count;
            }
            mNumEntries = count;
        }
    }

    final Record<K,V> prune(Record<K,V> inRecord) {
        Record<K,V> record = inRecord;
        while (record != null && (record.mCount = (record.mCount >>> 1)) == 0)
            record = record.mNextRecord;
        if (record == null) return null;
        record.mNextRecord = prune(record.mNextRecord);
        return record;
    }

    // for serialization
    Object writeReplace() {
        return new Serializer<K,V>(this);
    }


    /**
     * Returns a snapshot of the entries in this map.
     * This set is not backed by this cache, so that changes
     * to the cache do not affect the cache and vice-versa.
     *
     * @return The set of entries in this cache.
     */
    @Override
    public Set<Map.Entry<K,V>> entrySet() {
        HashSet<Map.Entry<K,V>> entrySet = new HashSet<Map.Entry<K,V>>();
        for (int i = 0; i < mBuckets.length; ++i)
            for (Record<K,V> record = getFirstRecord(i);
                 record != null;
                 record = record.mNextRecord)
                entrySet.add(record);
        return entrySet;
    }

    static final class Record<K,V> implements Map.Entry<K,V> {
        final K mKey;
        final V mValue;
        volatile Record<K,V> mNextRecord;
        volatile int mCount;
        Record(K key, V value) {
            this(key,value,null);
        }
        Record(K key, V value, Record<K,V> nextRecord) {
            this(key,value,nextRecord,1);
        }
        Record(K key, V value, Record<K,V> nextRecord, int count) {
            mKey = key;
            mValue = value;
            mNextRecord = nextRecord;
            mCount = count;
        }
        public K getKey() {
            return mKey;
        }
        public V getValue() {
            return mValue;
        }
        @Override
        public int hashCode() {
            return (mKey==null   ? 0 : mKey.hashCode()) ^
                (mValue==null ? 0 : mValue.hashCode());
        }
        @Override
        @SuppressWarnings("rawtypes") // for instanceof
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) return false;
            Map.Entry<?,?> e2 = (Map.Entry<?,?>) o;
            return (mKey==null
                    ? e2.getKey()==null
                    : mKey.equals(e2.getKey()))
                && (mValue==null
                    ? e2.getValue()==null
                    : mValue.equals(e2.getValue()));
        }
        public V setValue(V value) {
            String msg = "Cache records may not be set.";
            throw new UnsupportedOperationException(msg);
        }
    }

    static class Serializer<L,W> extends AbstractExternalizable {
        static final long serialVersionUID = 318542520894659209L;
        final FastCache<L,W> mCache;
        public Serializer() {
            this(null);
        }
        public Serializer(FastCache<L,W> cache) {
            mCache = cache;
        }
        public Object read(ObjectInput in)
            throws IOException, ClassNotFoundException {
            int numBuckets = in.readInt();
            int maxEntries = in.readInt();
            int numEntries = in.readInt();
            FastCache<L,W> cache = new FastCache<L,W>(maxEntries,numBuckets,true);
            for (int i = 0; i < numEntries; ++i) {
                @SuppressWarnings("unchecked") // ok because of serialize
                L l = (L) in.readObject();
                @SuppressWarnings("unchecked") // ok because of serialize
                W w = (W) in.readObject();
                cache.put(l,w);
            }
            return cache;
        }
        public void writeExternal(ObjectOutput out)
            throws IOException {
            out.writeInt(mCache.mBuckets.length);
            out.writeInt(mCache.mMaxEntries);
            out.writeInt(mCache.size());
            for (Map.Entry<L,W> entry : mCache.entrySet()) {
                out.writeObject(entry.getKey());
                out.writeObject(entry.getValue());
            }
        }
    }

}
