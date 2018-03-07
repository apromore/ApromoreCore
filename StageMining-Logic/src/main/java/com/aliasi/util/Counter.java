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

/**
 * A reassignable integer usable for counting.  Counters do not
 * override <code>Obect's</code> methods <code>hashCode()</code> or
 * <code>equals()</code>, so two counters with the same value are not
 * necessarily equal.
 *
 * <p>The value of a counter is stored as an integer.  No checks
 * are done in the increment methods to ensure that the results
 * remain within bounds.
 *
 * @author  Bob Carpenter
 * @version 1.0.7
 * @since   LingPipe1.0
 */
public class Counter extends Number {

    static final long serialVersionUID = -6794167256664036748L;

    /**
     * The count.
     */
    private int mCount;

    /**
     * Create a counter with initial count <code>0</code>.
     */
    public Counter() {
        this(0);
    }

    /**
     * Create a counter with the specified count value.
     *
     * @param count Initial value of counter.
     */
    public Counter(int count) {
        mCount = count;
    }

    /**
     * Returns the current count value.
     *
     * @return Current count value.
     */
    public int value() {
        return mCount;
    }

    /**
     * Increments the current count value.
     */
    public void increment() {
        ++mCount;
    }

    /**
     * Increment the count value by the specified amount.
     *
     * @param n Amount by which to increase the count value.
     */
    public void increment(int n) {
        mCount += n;
    }


    /**
     * Sets the count to the specified value.
     *
     * @param count New value of the counter.
     */
    public void set(int count) {
        mCount = count;
    }

    /**
     * Returns the value of this counter as a double.
     *
     * @return The value of this counter as a double.
     */
    @Override
    public double doubleValue() {
        return (double) mCount;
    }

    /**
     * Returns the value of this counter as a float.
     *
     * @return The value of this counter as a float.
     */
    @Override
    public float floatValue() {
        return (float) mCount;
    }

    /**
     * Returns the value of this counter as an int.
     *
     * @return The value of this counter as an int.
     */
    @Override
    public int intValue() {
        return mCount;
    }

    /**
     * Returns the value of this counter as a long.
     *
     * @return The value of this counter as an int.
     */
    @Override
    public long longValue() {
        return (long) mCount;
    }

    /**
     * Return a string representation of this counter's value.
     *
     * @return String representation of this counter's value.
     */
    @Override
    public String toString() {
        return String.valueOf(value());
    }

}
