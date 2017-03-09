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
ectends */

package com.aliasi.util;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * The <code>BinaryMap</code> class implements a map from objects to
 * integer objects where the only value is the integer with value 1.
 * Instances of this class are typically returned by boolean feature
 * extractors, who return a map with only 1 values, because the 0
 * values are implicit.
 *
 * <p>Binary maps are based on a set of keys that map to 1.  Thus
 * they are more space efficient than Java's utility maps such
 * as tree maps or hash maps.  The underlying set implementation is
 * pluggable, but must be mutable if the resulting binary map
 * is to be mutable.
 *
 * <p>Modifiability through the entry set, key set, and values
 * collection is fully supported through their respective iterators
 * and through the collections themselves.  The map entries making
 * up the entry set may not have their values modified.
 *
 * <h3>Equality and Hash Codes</h3>
 *
 * Binary maps satisfy the requirements laid out for hash codes
 * and object equality specified in {@link Map}.
 * 
 * <h3>Thread Safety</h3>
 *
 * Binary maps have the same thread safety as their underlying
 * sets.  
 *
 * <h3>Serialization</h3>
 *
 * A binary map may be serialized if its underlying positive
 * set is serializable.
 *
 * @author  Bob Carpenter
 * @version 3.9.1
 * @since   LingPipe3.9.1
 * @param <E> the type of keys in the map
 */
public class BinaryMap<E> 
    extends AbstractMap<E,Integer> 
    implements Serializable {

    static final long serialVersionUID = -6965494782866980478L;

    private final Set<E> mPositiveSet;

    /**
     * Construct a binary map with an initial capacity of zero.
     *
     * <p>This is a convenience method delegating to {@link
     * #BinaryMap(int)} with an initial capacity argument of
     * <code>1</code>; see that constructor's documentation for more
     * information
     */
    public BinaryMap() {
        this(1);
    }

    /**
     * Construct a binary map with the specified initial capacity.
     *
     * <p>This is a convenience method delegating to {@link
     * #BinaryMap(Set)} with a new instance of {@link CompactHashSet}
     * of the specified initial capacity.  Compact hash sets support
     * the full range of add and remove operations, but does not
     * support {@code null} elements.
     *
     * @param initialCapacity Initial size of backing array for the
     * binary map's entries.
     * @throws IllegalArgumentException If the initial capacity is negative.
     * @throws OutOfMemoryError If the JVM cannot allocate an object array with
     * the specified initial capacity.
     */
    public BinaryMap(int initialCapacity) {
        this(new CompactHashSet<E>(initialCapacity));
    }

    /**
     * Construct a binary map based on the specified set of positive
     * values.  The set is stored as is rather than copied.  The
     * set must be mutable if the constructed map is to be mutable.
     *
     * @param positiveSet
     */
    public BinaryMap(Set<E> positiveSet) {
        mPositiveSet = positiveSet;
    }

    /**
     * Adds the specified element to the map with value 1.
     *
     * <p>Note that this method is not part of the {@link Map}
     * interface.
     *
     * @param e Element added to the map with value 1.
     * @return {@code true} if the map didn't already contain the
     * element.
     * @throws UnsupportedOperationException If the underlying set of
     * positive elements does not support {@code Collection.add}.
     */
    public boolean add(E e) {
        return mPositiveSet.add(e);
    }

    /**
     * Returns the set of keys mapping to 1.  This set is backed by
     * this map, so changes to the set are reflected in the map and
     * vice-versa.  
     *
     * <p>If the underlying set is not modifiable, attempts to
     * modify the key set will raise unsupported operation exceptions.
     *
     * <p>Note that results are undefined in the middle of an
     * iterator, which will likely throw concurrent modification, null
     * pointer, or array index out of bounds exceptions.  Therefore,
     * access to the key set must be synchronized in the same way
     * access to the underlying set.
     *
     * <p>Further note that unlike the specification in {@link Map},
     * the returned keyset supports the {@link Collection#add(Object)} and
     * {@link Collection#addAll(Collection)}.  Adding elements to the key set
     * is the same as adding them through the {@link #add(Object)}
     * method of this class.
     *
     * @return The set of keys mapping to one.
     */
    public Set<E> keySet() {
        return mPositiveSet;
    }

    /**
     * Return a set view of the mappings in this map.  This set is
     * backed by this map, so changes to the return set affect
     * this set and vice-versa.  The resulting set supports deletions
     * through {@code Iterator.remove}, {@code Collection.remove},
     * {@code Collection.removeAll}, {@code Collection.retainAll},
     * and {@code Collection.clear} operations, but does not support
     * the add operations.
     *
     * <p>If the underlying set of positive elements does not
     * support these modification operations, they will throw an
     * unsupported operation exception.
     *
     * <p><i>Implementation Note:</i> The {@code Map.Entry} elements
     * are created as necessary by the entry set using a relatively
     * efficient implementation of entries that only stores the key.
     * Accessing the key set is more efficient.
     *
     * @return The set of mappings for this map. 
     */
    @Override
    public Set<Map.Entry<E,Integer>> entrySet() {
        return new EntrySet();
    }

    /**
     * Returns the {@code Integer} with value 1 if the specified
     * argument is mapped to 1 by this map, and returns null
     * otherwise.
     *
     * <p>The constant {@link #ONE} is used for the return
     * value.
     *
     * @param key The element whose value is returned.
     * @return 1 if the element is mapped to 1, and {@code null}
     * otherwise.
     */
    @Override
    public Integer get(Object key) {
        return mPositiveSet.contains(key)
            ? ONE
            : null;
    }

    /**
     * Remove the mapping with the specified key if it is present, returning
     * the previous value, or {@code null} if it was previously undefined.
     *
     * @param key Key of mapping to remove.
     * @return The value 1 if the key is already present, and {@code
     * null} otherwise.
     * @throws UnsupportedOperationException If the underlying set for
     * this map does not support the {@link Set#remove(Object)} operation.
     */
    @Override
    public Integer remove(Object key) {
        return mPositiveSet.remove(key)
            ? ONE
            : null;
    }

    /**
     * Returns the size of this mapping.
     *
     * @return Size of this mapping.
     */
    @Override
    public int size() {
        return mPositiveSet.size();
    }

    /**
     * Returns an immutable collection view of the values for this
     * map.  The resulting collecton will be empty if the map is
     * entry, or contain the single value {@link #ONE} if the map is
     * not empty.
     */
    @Override
    public Collection<Integer> values() {
        return new Values();
    }

    /**
     * Removes all of the mappings from this map.
     *
     * <p>The implementation just delegates the clear operation to the
     * contained set.
     *
     * @throws UnsupportedOperationException If the clear operation
     * is not supported by the contained set.
     */
    @Override
    public void clear() {
        mPositiveSet.clear();
    }

    /**
     * Returns {@code true} if this mapping contains a mapping
     * for the specified object.
     *
     * <p>This method delegates to the underlying positive set's
     * {@code Collection.contains} method.
     *
     * @param o Object to teset.
     * @return {@code true} if it is mapped by this mapping.
     * @throws ClassCastException If the underlying set throws
     * a class cast when checking the specified object for
     * membership.
     */
    @Override
    public boolean containsKey(Object o) {
        return mPositiveSet.contains(o);
    }

    /**
     * Returns {@code true} if this map contains a mapping
     * from some key to the specified value.  
     *
     * <p>Note that the only object for which this map may
     * return true is the {@code Integer} with value 1.
     *
     * @param o Object to test.
     * @return {@code true} if this map contains a mapping
     * from some object to this value.
     */
    @Override
    public boolean containsValue(Object o) {
        return ONE.equals(o) && !isEmpty();
    }

    /**
     * Returns {@code true} if this mapping is empty.
     *
     * @return {@code true} if this mapping is empty.
     */
    @Override
    public boolean isEmpty() {
        return mPositiveSet.isEmpty();
    }

    /**
     * Adds the mapping of the specified object to the specified
     * value if the specified number is the {@code Integer} with
     * value 1.
     *
     * @param e Key for the mapping.
     * @param n Value for the mapping.
     * @throws IllegalArgumentException If the specified integer
     * does not have value 1.
     */
    @Override
    public Integer put(E e, Integer n) {
        if (!ONE.equals(n))
            throw new IllegalArgumentException();
        return  mPositiveSet.add(e) ? null : ONE;
    }

    Object writeReplace() {
        return new Serializer<E>(this);
    }
    

    class Values extends AbstractCollection<Integer> {
        @Override
        public Iterator<Integer> iterator() {
            return new ValuesIterator();
        }
        @Override
        public int size() {
            return isEmpty() ? 0 : 1;
        }
        @Override
        public void clear() {
            mPositiveSet.clear();
        }
        @Override
        public boolean contains(Object o) {
            return ONE.equals(o) && !isEmpty();
        }
        @Override
        public boolean isEmpty() {
            return mPositiveSet.isEmpty();
        }
        @Override
        public boolean remove(Object o) {
            if (!ONE.equals(o))
                return false;
            boolean removedSomething = !isEmpty();
            mPositiveSet.clear();
            return removedSomething;
        }
        @Override
        public boolean removeAll(Collection<?> c) {
            if (!c.contains(ONE))
                return false;
            boolean removedSomething = !isEmpty();
            mPositiveSet.clear();
            return removedSomething;
        }
        @Override
        public boolean retainAll(Collection<?> c) {
            if (isEmpty()) return false;
            if (c.contains(ONE)) return false;
            mPositiveSet.clear();
            return true;
        }
        @Override
        public Object[] toArray() {
            return isEmpty() 
                ? EMPTY_OBJECT_ARRAY 
                : new Object[] { ONE };
        }
    }

    class ValuesIterator implements Iterator<Integer> {
        boolean finished = mPositiveSet.isEmpty();
        boolean mayRemove = false;
        public boolean hasNext() {
            return !finished;
        }
        public Integer next() {
            if (!hasNext())
                throw new NoSuchElementException();
            finished = true;
            mayRemove = true;
            return ONE;
        }
        public void remove() {
            if (!mayRemove)
                throw new IllegalStateException();
            mayRemove = false;
            mPositiveSet.clear();
        }
    }


    class EntrySet extends AbstractSet<Map.Entry<E,Integer>> {
        @Override
        public int size() {
            return mPositiveSet.size();
        }
        @Override
        public Iterator<Map.Entry<E,Integer>> iterator() {
            return new EntrySetIterator();
        }
        @Override
        public void clear() {
            mPositiveSet.clear();
        }
        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry<?,?>))
                return false;
            @SuppressWarnings("unchecked") // checked above
            Map.Entry<?,?> entry = (Map.Entry<?,?>) o;
            return ONE.equals(entry.getValue())
                && mPositiveSet.contains(entry.getKey());
        }
        @Override
        public boolean isEmpty() {
            return mPositiveSet.isEmpty();
        }
        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry<?,?>))
                return false;
            @SuppressWarnings("unchecked") // checked above
            Map.Entry<?,?> entry = (Map.Entry<?,?>) o;
            return ONE.equals(entry.getValue())
                && mPositiveSet.remove(entry.getKey());
        }
    }

    class EntrySetIterator implements Iterator<Map.Entry<E,Integer>> {
        private final Iterator<E> mIterator = mPositiveSet.iterator();;
        public boolean hasNext() {
            return mIterator.hasNext();
        }
        public Map.Entry<E,Integer> next() {
            return new PositiveMapEntry();
        }
        public void remove() {
            mIterator.remove();
        }

        class PositiveMapEntry implements Map.Entry<E,Integer> {
            private final E mE = mIterator.next();
            public E getKey() {
                return mE;
            }
            public Integer getValue() {
                return ONE;
            }
            public Integer setValue(Integer value) {
                throw new UnsupportedOperationException();
            }
            public boolean equals(Object that) {
                if (!(that instanceof Map.Entry<?,?>))
                    return false;
                @SuppressWarnings("unchecked") // checked above
                    Map.Entry<?,?> e1 = (Map.Entry<?,?>) that;
                Map.Entry<?,?> e2 = this;
                return
                    (e1.getKey()==null
                     ? e2.getKey()==null
                     : e1.getKey().equals(e2.getKey()))
                    &&
                    (e1.getValue()==null
                     ? e2.getValue()==null
                     : e1.getValue().equals(e2.getValue()));
            }
            public int hashCode() {
                return (getKey()==null ? 0 : getKey().hashCode())
                    ^ (getValue()==null ? 0 : getValue().hashCode());
            }
        }
    }

    /**
     * The constant used for the {@code Integer} with value 1.  The
     * value is defined by {@code Integer.valueOf(1)}, so may be the
     * same object as returned by other calls to {@code valueOf()}.
     */
    public static final Integer ONE = Integer.valueOf(1);

    static final Object[] EMPTY_OBJECT_ARRAY
        = new Object[0];

    static class Serializer<F> extends AbstractExternalizable {
        static final long serialVersionUID = -1922361159364699771L;
        final BinaryMap<F> mMap;
        public Serializer() {
            this(null);
        }
        public Serializer(BinaryMap<F> map) {
            mMap = map;
        }
        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(mMap.mPositiveSet);
        }
        @Override
        public Object read(ObjectInput in) throws IOException, ClassNotFoundException {
            @SuppressWarnings("unchecked") // required for deser
            Set<F> positiveSet = (Set<F>) in.readObject();                
            return new BinaryMap<F>(positiveSet);
        }
    }
}
