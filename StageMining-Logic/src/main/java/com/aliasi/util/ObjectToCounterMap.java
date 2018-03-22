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

import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.ArrayList;
/**
 * An <code>ObjectToCounterMap</code> maintains a mapping from objects
 * to integer counters, which may be incremented or set.  Objects not
 * in the underlying map are assumed to have count <code>0</code>, so
 * that incrementing an object that is not mapped sets it to the
 * increment amount.
 *
 * @author  Bob Carpenter
 * @version 3.8
 * @since   LingPipe1.0
 * @param <E> the type of objects used as keys for the map
 */
public class ObjectToCounterMap<E> extends HashMap<E,Counter> {

    // inherits serializability from parent HashMap
    static final long serialVersionUID = -4735380145915633564L;

    /**
     * Construct an object to counter mapping.
     */
    public ObjectToCounterMap() {
        /* do nothing */
    }

    /**
     * Construct an object to counter mapping with the specified
     * initial size.
     *
     * @param initialSize Initial size of map.
     */
    public ObjectToCounterMap(int initialSize) {
        super(initialSize);
    }

    /**
     * Increment the value of the specified key by <code>1</code>.
     * Removes the key from the underlying map if the value after
     * incrementing is <code>0</code>.  Sets the value of the key to
     * <code>1</code> if it is not currently set in the underlying
     * map.
     *
     * @param key Object whose count is incremented by <code>1</code>
     */
    public void increment(E key) {
        increment(key,1);
    }

    /**
     * Increment the value of the specified key by the specified
     * amount.  Sets the value of the key to the specified increment
     * amount if it is not currently set in the underlying map.  If
     * the value after incrementing is <code>0</code>, the key is
     * removed from the map.
     *
     * @param key Object whose count is incremented by the specified
     * amount.
     * @param n Amount to increment the object by.
     */
    public void increment(E key, int n) {
        if (!containsKey(key)) {
            put(key,new Counter(n));
            return;
        }
        Counter counter = get(key);
        counter.increment(n);
        if (counter.value() == 0) remove(key);
    }

    /**
     * Sets the value of the specified key to be the specified amount.
     * If the amount is <code>0</code>, the key is removed from the mapping.
     *
     * @param key Object whose count is incremented by the specified
     * amount.
     * @param n Amount to increment the object by.
     */
    public void set(E key, int n) {
        if (n == 0) {
            remove(key);
            return;
        }
        if (!containsKey(key)) {
            put(key,new Counter(n));
            return;
        }
        Counter counter = get(key);
        counter.set(n);
    }

    /**
     * Returns the current count value for the specified key.
     * If the key is not in the underlying map, the value
     * returned is <code>0</code>.
     *
     * @return Current value of count for the key.
     */
    public int getCount(E key) {
        if (!containsKey(key)) return 0;
        return get(key).value();
    }

    /**
     * Returns a string representation of this map.
     *
     * @return String representation of this map.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<E> keyList = keysOrderedByCountList();
        for (E key : keyList) {
            sb.append(key);
            sb.append('=');
            sb.append(getCount(key));
            sb.append('\n');
        }
        return sb.toString();
    }



    /**
     * Returns a list of keys for this object to counter map sorted
     * in descending order of count.  Modifying the resulting list
     * does not affect this counter.
     *
     * @return List of keys for this object to counter map sorted
     * in descending order of count.
     */
    public List<E> keysOrderedByCountList() {
        Set<E> keySet = keySet();
        List<E> result = new ArrayList<E>(keySet().size());
        result.addAll(keySet);
        Collections.sort(result,countComparator());
        return result;
    }

    /**
     * Returns the array of keys for this object to counter
     * map sorted in decreasing order of their value counts.
     *
     * @return The array of keys for this counter ordered in
     * descending order of count.
     */
    public Object[] keysOrderedByCount() {
        return keysOrderedByCountList().toArray();
    }

    /**
     * Removes all entries in this counter that have less than
     * the specified minimum count.
     *
     * @param minCount Minimum count of objects to retain.
     */
    public void prune(int minCount) {
        Iterator<Map.Entry<E,Counter>> it = entrySet().iterator();
        while (it.hasNext())
            if (it.next().getValue().value() < minCount)
                it.remove(); // remove this entry
    }

    /**
     * Returns a <code>CountComparator</code> that compares objects
     * based on their counts in this object to counter map.  It first
     * compares objects based on their count, and if the counts are
     * the same and the objects are comparable, on the objects
     * themselves.  Thus <code>compare(obj1,obj2)</code> method will
     * not be <i>consistent with equals</i> (see {@link Comparable})
     * unless the set of keys has a natural ordering that is
     * consistent with <code>equals</code>.  This inconsistency with
     * <code>equals</code> is tolerable in cases where the comparator
     * is used for sorting, as in {@link
     * java.util.Arrays#sort(Object[],Comparator)}, but not for
     * backing a sorted collection such as {@link java.util.TreeMap}
     * or {@link java.util.TreeSet}.
     *
     * @return Comparator based on this object to counter map's
     * counts.
     */
    public Comparator<E> countComparator() {
        return new Comparator<E>() {
            public int compare(E o1, E o2) {
                int count1 = getCount(o1);
                int count2 = getCount(o2);
                if (count1 < count2) return 1;
                if (count1 > count2) return -1;
                if (!(o1 instanceof Comparable)
                    || !(o2 instanceof Comparable))
                    return 0;
                // must be instances given above, but may still fail in compareTo
                @SuppressWarnings("unchecked") Comparable<? super E> c1
                = (Comparable<? super E>) o1;
                return c1.compareTo(o2);
            }
        };
    }

}
