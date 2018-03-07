/*
 * LingPipe v. 4.1.0
 * Copyright (C) 2003-2011 Alias-i
 *
 * This program is licensed under the Alias-i Royalty Free License
 * Version 1 WITHOUT ANY WARRANTY, without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Alias-i
 * Royalty Free License Version 1 for more details.
 *
 * You should have received a copy of the Alias-i Royalty Free License
 * Version 1 along with this program; if not, visit
 * http://alias-i.com/lingpipe/licenses/lingpipe-license-1.txt or contact
 * Alias-i, Inc. at 181 North 11th Street, Suite 401, Brooklyn, NY 11211,
 * +1 (718) 290-9170.
 */

package com.aliasi.util;

import java.lang.reflect.Array;

import java.util.Arrays;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

/**
 * A {@code CompactHashSet} implements the set interface more tightly in
 * memory and more efficiently than Java's {@link java.util.HashSet}.
 *
 * <h3>Sizing and Resizing</h3>
 *
 * <p>This hash set allows arbitrary capacities sized hash sets to be created,
 * including hash sets of size 0.  When resizing is necessary due to
 * objects being added, it resizes to the next largest capacity that is
 * at least 1.5 times

 * <h3>What's wrong with <big><code>HashSet</code></big>?</h3>
 * 
 * Java's hash set implementation {@link java.util.HashSet} wraps a
 * {@link java.util.HashMap}, requiring a map entry with a dummy value
 * for each set entry.  Map entries are even heavier as the map
 * entries also contain a next field to implement a linked list
 * structure to deal with collisions.  This class represents hash set
 * entries in terms of the entries themselves.  Iterators are based
 * directly on the underlying hash table.
 *
 * <p>Java's hash set implementation requires hash tables with
 * capacities that are even powers of 2.  This allows a very quick
 * modulus operation on hash codes by using a mask, but restricts
 * the size of hash sets to be powers of 2.  
 *
 * <h3>Implementation</h3>
 * 
 * The implementation in this class uses open addressing, which uses a
 * simple array to store all of the set elements based on their hash
 * codes.  Hash codes are taken modulo the capacity of the hash set,
 * so capacities above a certain small level are rounded up so that
 * they are not powers of 2.
 *
 * <p>The current implementation uses linear probing with a step size
 * of 1 for the case of hash code collisions.  This means that if the
 * position for an entry is full, the next position is considered
 * (wrapping to the beginning at the end of the array).
 * 
 * <p>We borrowed the supplemental hashing function from 
 * {@link java.util.HashMap} (version ID 1.73).  If the
 * initial hashFunction is <code>h</code> the supplemental
 * hash function is computed by:
 *
 * <blockquote><pre>
 * static int supplementalHash(int n) {
 *     int n2 = n ^ (n >>> 20) ^ (n >>> 12);
 *     return n2 ^ (n2 >>> 7) ^ (n2 >>> 4);
 * }</pre></blockquote>
 *
 * This is required to scramble the hash code of strings 
 * that share the same prefix with a different final character from
 * being adjacent.  Recall that the hash code for strings <code>s</code>
 * consisting of characters <code>s[0], ..., s[n-1]</code> is
 * defined in {@link String#hashCode()} to be:
 *
 * <blockquote><pre>
 * s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]</pre></blockquote>
 *
 * <h3>Null Elements</h3>
 *
 * Attempts to add, remove, or test null objects for membership will
 * throw null pointer exceptions.
 *
 * <h3>Illegal Types and Class Casts</h3>
 *
 * When adding, removing, or checking for membership, the 
 * {@link Object#equals(Object)} method may throw class cast exceptions
 * if the specified object is not of a type comparable to the elements
 * of the set.
 * 
 * <h3>Resizing</h3>
 * 
 * As more elements are added to the set, it will automatically resize
 * its underlying array of buckets.  
 *
 * <p>As of this release, sets will never be resized downward.
 *
 * <p>Equality and Hash Codes</p>
 *
 * Compact hash sets satisfy the specification of the
 * the equality metho, {@link #equals(Object)}, and hash code
 * method {@link #hashCode()} specified by the {@link java.util.Set} interface.
 * 
 * <p>The implementations are inherited from the superclass
 * {@link AbstractSet}, which uses the underlying iterators.
 * 
 * <h3>Concurrent Modification</h3>
 * 
 * This set implementation does <b>not</b> support concurrent
 * modification.  (Note that this does not exclusively have to do with
 * threading, though multiple threads may lead to concurrent
 * modifications.)  If the set is modified during an iteration, or
 * conversion to array, its behavior will be unpredictable, though it
 * is likely to make errors in basic methods and to throw an array
 * index or null pointer exception.
 *
 * <h3>Thread Safety</h3>
 * 
 * This class is only thread safe with read-write locking. 
 * Any number of read operations may be executed concurrently,
 * but writes must be executed exclusively.  The write
 * operations include any method that may potentially change
 * the set.
 *
 * <h3>Serializability</h3>
 *
 * A small hash set is serializable if all of its elements are
 * serializable. The deserialized object will be an instance of this
 * class.
 *
 * <h3>References</h3>
 *
 * <ul>
 * <li> Wikipedia: <a href="http://en.wikipedia.org/wiki/Open_addressing">Open Addressing</a>
 * <li> Wikipedia: <a href="http://en.wikipedia.org/wiki/Linear_probing">Linear Hash Probing</a>
 * </ul>
 *
 * @author  Bob Carpenter
 * @version 3.9.1
 * @since   LingPipe3.9.1
 * @param <E> the type of element stored in the set
 */
public class CompactHashSet<E> 
    extends AbstractSet<E> 
    implements Serializable {

    static final long serialVersionUID = -2524057065260042957L;

    private E[] mBuckets;
    private int mSize = 0;

    /**
     * Construct a compact hash set with the specified initial
     * capacity.
     * 
     * @throws IllegalArgumentException If the capacity is less than 1.
     * @throws OutOfMemoryException If the initial capacity is too
     * large for the JVM.  
     */
    public CompactHashSet(int initialCapacity) {
        if (initialCapacity < 1) {
            String msg = "Capacity must be positive."
                + " Found initialCapacity=" + initialCapacity;
            throw new IllegalArgumentException(msg);
        }
        alloc(initialCapacity);
    }

    /**
     * Construct a compact hash set containing the specified list
     * of values. It begins with a set of initial capacity 1.
     *
     * @param es Initial values to add to set.
     */
    public CompactHashSet(E... es) {
        this(1);
        for (E e : es)
            add(e);
    }

    /**
     * Add the specified object to the set if it is not already
     * present, returning {@code} true if the set didn't already
     * contain the element.  If the set already contains the
     * object, there is no effect.
     *
     * @param e Object to add to set.
     * @return {@code true} if the set didn't already contain the
     * element.
     * @throws NullPointerException If the specified element is {@code
     * null}.
     * @throws ClassCastException If the class of this object prevents
     * it from being added to the set.
     */
    public boolean add(E e) {
        if (e == null) {
            String msg = "Cannot add null to CompactHashSet";
            throw new NullPointerException(msg);
        }
        int slot = findSlot(e);
        if (mBuckets[slot] != null)
            return false;
        if ((mSize + 1) >= (LOAD_FACTOR * mBuckets.length)) {
            realloc();  
            slot = findSlot(e);
            if (mBuckets[slot] != null)
                throw new IllegalStateException("");
        }
        mBuckets[slot] = e;
        ++mSize;
        return true;
    }


    /**
     * Remove all of the elements from this set.
     *
     * <p>Note that this operation does not affect the
     * underlying capacity of the set itself.
     */
    @Override
    public void clear() {
        alloc(1);
    }

    /**
     * Returns {@code true} if this set contains the specified object.
     *
     * @param o Object to test.
     * @return {@code true} if this set contains the specified object.
     * @throws NullPointerException If the specified object is null.
     * @throws ClassCastException If the type of this object is incompatible
     * with this set.
     */ 
    @Override
    public boolean contains(Object o) {
        if (o == null) {
            String msg = "Compact hash sets do not support null objects.";
            throw new NullPointerException(msg);
        }
        Object o2 = mBuckets[findSlot(o)];
        return o2 != null && o.equals(o2);
    }

    /**
     * Returns an iterator over the elements in this set.  The
     * iterator supports the {@link Iterator#remove()} operation,
     * which is not considered a concurrent modification.  
     *
     * <p>Iteration order for sets is not guaranteed to be
     * stable under adds or deletes, or for sets of different
     * capacities containing the same elements.
     *
     * <p>The set must not be modified by other operations
     * while an iteration is in process.  Doing so may cause
     * an illegal state and unpredictable behavior such as
     * null pointer or array index exceptions.
     * 
     * @return Iterator over the set elements.
     */
    @Override
    public Iterator<E> iterator() {
        return new BucketIterator();
    }

    /**
     * Removes the specified object from the set, returning {@code true}
     * if the object was present before the remove.
     *
     * @param o Object to remove.
     * @return {@code true} if the object was present before the remove
     * operation.
     * @throws ClassCastException If the specified object is not compatible
     * with this set.
     */
    @Override
    public boolean remove(Object o) {
        if (o == null)
            return false;
        @SuppressWarnings("unchecked") // except if doesn't work
        int slot = findSlot((E)o);
        if (mBuckets[slot] == null)
            return false;
        mBuckets[slot] = null;
        tampCollisions(slot);
        --mSize;
        return true;
    }

    // mBuckets[index] == null
    void tampCollisions(int index) {
        for (int i = nextIndex(index) ; mBuckets[i] != null; i = nextIndex(i)) {
            int slot = findSlot(mBuckets[i]);
            if (slot != i) {
                mBuckets[slot] = mBuckets[i];
                mBuckets[i] = null;
            }
        }
    }

    /**
     * Removes all the elements of the specified collection from this
     * set, returning {@code true} if the set was modified as a
     * result.
     * 
     * <p><i>Implementation Note:</i> Unlike the implementation the
     * parent class {@link AbstractSet} inherits from its
     * parent class {@link java.util.AbstractCollection}, this implementation
     * iterates over the argument collection, removing each of its
     * elements.
     *
     * @param collection Collection of objects to remove.
     * @return {@code true} if the set was modified as a result.
     * @throws NullPointerException If any of the collection members is null, or
     * if the specified collection is itself null.
     * @throws ClassCastException If attempting to remove a member of the
     * collection results in a cast exception.
     */
    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean modified = false;
        for (Object o : collection)
            if (remove(o))
                modified = true;
        return modified;
    }

    /**
     * Remove all elements from this set that are not members of the
     * specified collection, returning {@code true} if this set was
     * modified as a result of the operation.  
     *
     * <p><i>Implementation Note:</i> Unlike the implementation that
     * the parent class {@link AbstractSet} inherits from {@link
     * java.util.AbstractCollection}, this implementation directly
     * visits the underlying hash entries rather than invoking the
     * overhead of an iterator.
     * 
     * @param collection Collection of objects to retain.
     * @return {@code true} if this set was modified as a result of
     * the operation.
     * @throws ClassCastException If comparing elements of the
     * specified collection to elements of this set throws a class
     * cast exception.
     * @throws NullPointerException If the specified collection is
     * {@code null}.
     * 
     */
    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean modified = false;
        for (int i = 0; i < mBuckets.length; ++i) {
            if (mBuckets[i] != null && collection.contains(mBuckets[i])) {
                modified = true;
                mBuckets[i] = null;
                tampCollisions(i);
                --mSize;
            }
        }
        return modified;
    }

    /**
     * Returns the number of objects in this set.
     *
     * @return The number of objects in this set.
     */
    @Override
    public int size() {
        return mSize;
    }

    /**
     * Returns an object array containing all of the members of this
     * set.  The order of elements in the array is the iteration
     * order, but this is not guaranteed to be stable under
     * modifications or changes in capacity.
     *
     * <p>The returned array is fresh and may be modified without
     * affect this set.
     *
     * @return Array of objects in this set.
     */
    @Override
    public Object[] toArray() {
        Object[] result = new Object[mSize];
        int nextIndex = 0;
        for (int i = 0; i < mBuckets.length; ++i)
            if (mBuckets[i] != null)
                result[nextIndex++] = mBuckets[i];
        return result;
    }

    /**
     * Returns an array of elements contained in this set, the runtime type
     * of which is that of the specified array argument.  
     *
     * <p>If the specified array argument is long enough to hold all
     * of the elements in this set, it will be filled starting from
     * index 0.  If the specified array is longer than the size of the
     * set, the array entry following the last entry filled by this
     * set will be set to {@code null}.
     *
     * <p>If the specified array is not long enough to hold all of
     * the elements, a new array will be created of the appropriate
     * type through reflection.
     * 
     * @param array Array of values determining type of output and containing
     * output elements if long enough.
     * @param <T> Type of target array.
     * @throws ArrayStoreException If the members of this set cannot be
     * inserted into an array of the specified type.
     * @throws NullPointerException If the specified array is null.
     */
    @Override
    public <T> T[] toArray(T[] array) {
        // construction from java.util.AbstractCollection
        @SuppressWarnings("unchecked")
        T[] result 
            = array.length >= mSize
            ? array 
            : (T[]) Array.newInstance(array.getClass().getComponentType(), 
                                      mSize);
        int nextIndex = 0;
        for (int i = 0; i < mBuckets.length; ++i) {
            if (mBuckets[i] != null) {
                @SuppressWarnings("unchecked") // may bomb at run time according to spec
                T next = (T) mBuckets[i];
                result[nextIndex++] = next;
            }
        }
        if (result.length > mSize)
            result[mSize] = null; // req for interface
        return result;
    }

    Object writeReplace() {
        return new Serializer<E>(this);
    }
    
    int findSlot(Object e) {
        for (int i = firstIndex(e); ; i = nextIndex(i)) {
            if (mBuckets[i] == null)
                return i;
            if (mBuckets[i].equals(e))
                return i;
        }
    }

    int firstIndex(Object e) {
        return java.lang.Math.abs(supplementalHash(e.hashCode())) % mBuckets.length;
    }

    int nextIndex(int index) {
        return (index + 1) % mBuckets.length;
    }

    void alloc(int capacity) {
        if (capacity < 0) {
            String msg = "Capacity must be non-negative."
                + " Found capacity=" + capacity;
            throw new IllegalArgumentException(msg);
        }
        @SuppressWarnings("unchecked") // need for generic array
        E[] buckets = (E[]) new Object[capacity];
        mBuckets = buckets;
        mSize = 0;
    }

    void realloc() {
        E[] oldBuckets = mBuckets;
        long capacity = java.lang.Math.max((long) (RESIZE_FACTOR * mBuckets.length),
                                           mBuckets.length + 1);
        if (capacity > Integer.MAX_VALUE) {
            String msg = "Not enough room to resize."
                + " Last capacity=" + mBuckets.length
                + " Failed New capacity=" + capacity;
            throw new IllegalArgumentException(msg);
        }
        alloc((int)capacity);
        for (int i = 0; i < oldBuckets.length; ++i)
            if (oldBuckets[i] != null)
                add(oldBuckets[i]);
    }

    class BucketIterator implements Iterator<E> {
        int mNextBucket = 0;
        int mRemoveIndex = -1;
        public boolean hasNext() {
            for ( ; mNextBucket < mBuckets.length; ++mNextBucket)
                if (mBuckets[mNextBucket] != null)
                    return true;
            return false;
        }
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            mRemoveIndex = mNextBucket++;
            return mBuckets[mRemoveIndex];
        }
        public void remove() {
            if (mRemoveIndex == -1)
                throw new IllegalStateException();
            mBuckets[mRemoveIndex] = null;
            --mSize;
            mRemoveIndex = -1;
        }
    }

    static final float LOAD_FACTOR = 0.75f;

    static final float RESIZE_FACTOR = 1.5f;

    // function recoded from the Sun JDK's java.util.HashMap version
    // 1.73
    static int supplementalHash(int n) {
        int n2 = n ^ (n >>> 20) ^ (n >>> 12);
        return n2 ^ (n2 >>> 7) ^ (n2 >>> 4);
    }

    static class Serializer<F> extends AbstractExternalizable {
        static final long serialVersionUID = 7799253382220016205L;
        final CompactHashSet<F> mSet;
        public Serializer() {
            this(null);
        }
        public Serializer(CompactHashSet<F> set) {
            mSet = set;
        }
        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(mSet.mBuckets.length);
            out.writeInt(mSet.size());
            for (int i = 0; i < mSet.mBuckets.length; ++i) {
                if (mSet.mBuckets[i] == null) continue;
                out.writeObject(mSet.mBuckets[i]);
            }
        }
        @Override
        public Object read(ObjectInput in) 
            throws IOException, ClassNotFoundException {

            int capacity = in.readInt();
            int size = in.readInt();
            CompactHashSet<F> result = new CompactHashSet<F>(capacity);
            for (int i = 0; i < size; ++i) {
                @SuppressWarnings("unchecked")
                F f = (F) in.readObject();
                result.add(f);
            }
            return result;
        }
    }

}
