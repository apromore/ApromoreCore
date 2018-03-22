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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * An <code>ObjectToDoubleMap</code> maintains a mapping from
 * objects to double-valued counters, which may be incremented or set.
 * Objects not in the underlying map's key set are assumed to have
 * value 0.0.  Thus incrementing an object that is not currently
 * set is equivalent to setting it to the increment value.
 *
 * @author  Bob Carpenter
 * @author  Breck Baldwin
 * @version 4.0.1
 * @since   LingPipe2.3.1
 * @param <E> the type of keys for this map
 */
public class ObjectToDoubleMap<E> extends HashMap<E,Double> {

    // inherits serializability from HashMap parent
    static final long serialVersionUID = 2891073039706316972L;

    /**
     * Construct an object to double mapping.
     */
    public ObjectToDoubleMap() {
        /* do nothing */
    }
    
    /**
     * Construct an object to double mapping with the specified
     * initial capacity.
     *
     * @param initialCapacity Initial capacity of the map.
     */
    public ObjectToDoubleMap(int initialCapacity) {
        super(initialCapacity);
    }


    /**
     * Increment the value of the specified key by the specified
     * increment amount.  If the key is not currently in the key set,
     * it is added and set to the increment amount.
     *
     * @param key Object to increment.
     * @param increment Increment amount.
     */
    public void increment(E key, double increment) {
        set(key, increment + getValue(key));
    }

    /**
     * Sets the specified key to the specified value. Will delete
     * key if value is 0
     * @param key Object to set.
     * @param val New value for the object.
     */
    public void set(E key, double val) {
        if (val == 0.0)
            remove(key);
        else
            put(key,Double.valueOf(val));
    }

    /**
     * Sets the specified key to the specified value
     * allowing 0.0
     * @param key Object to set.
     * @param val New value for the object.
     */
    public void setAndKeepZeros(E key, double val) {
	put(key,Double.valueOf(val));
    }


    /**
     * Returns the value of the specified key in the map.
     * If the key is not in the key set, the return value is 0.0.
     *
     * @param key Object whose value is returned.
     * @return Value of specified key.
     */
    public double getValue(E key) {
        Double d = get(key);
        return d == null ? 0.0 : d.doubleValue();
    }

    /**
     * Returns the list of keys ordered in decreasing order of
     * value.
     *
     * @return Key list with keys in decreasing order of value.
     */
    public List<E> keysOrderedByValueList() {
        List<E> keys = new ArrayList<E>(keySet().size());
        keys.addAll(keySet());
        Collections.sort(keys,valueComparator());
        return keys;
    }

    /**
     * Returns a list of scored objects corresponding to the entries
     * in decreasing order of value.
     *
     * @return Scored object list in decreasing order of value.
     */
    public List<ScoredObject<E>> scoredObjectsOrderedByValueList() {
        Set<Map.Entry<E,Double>> entrySet = entrySet();
        List<ScoredObject<E>> sos
            = new ArrayList<ScoredObject<E>>(entrySet.size());
        for (Map.Entry<E,Double> entry : entrySet) {
            E key = entry.getKey();
            double val = entry.getValue().doubleValue();
            sos.add(new ScoredObject<E>(key,val));
        }
        Collections.sort(sos,ScoredObject.reverseComparator());
        return sos;
    }

    /**
     * Returns the comparator that compares objects based on their
     * values.  If the objects being compared have the same value,
     * then the return result depends on their objects.  If the
     * objects are comparable, their natural ordering is returned.
     * If they are not comparable, the result is 0, so they are
     * treated as equal.  Not-a-number values are considered smaller
     * than any other numbers for the purpose of this ordering.
     *
     * @return The comparator that sorts according to score.
     */
    public Comparator<E> valueComparator() {
        return new Comparator<E>() {
                public int compare(E o1, E o2) {
                    double d1 = getValue(o1);
                    double d2 = getValue(o2);
                    if (d1 > d2) {
                        return -1;
                    }
                    if (d1 < d2) {
                        return 1;
                    }
                    if (Double.isNaN(d1) && !Double.isNaN(d2)) {
                        return 1;
                    }
                    if (Double.isNaN(d2)) {
                        return -1;
                    }
                    if (!(o1 instanceof Comparable)
                        || !(o2 instanceof Comparable))
                        return 0;

                    // required for basic comparable
                    @SuppressWarnings("unchecked")
                    Comparable<E> c1 = (Comparable<E>) o1;
                    return c1.compareTo(o2);
                }
            };
    }

}
