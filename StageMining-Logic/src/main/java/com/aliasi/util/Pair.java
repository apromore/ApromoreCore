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
 * The <code>Pair</code> class represents an immutable pair of objects
 * of heterogeneous type.  This class is useful as a return type for
 * functions computing more than one object.
 *
 * <p>Identity conditions are that the elements are the
 * same.
 *
 * @author  Bob Carpenter
 * @version 3.8.3
 * @since   LingPipe3.5
 * @param <A> the type of the first object in the pair
 * @param <B> the type of the second object in the pair
 */
public class Pair<A,B> {

    private final A mA;
    private final B mB;

    /**
     * Construct a pair consisting of the two specified elements.
     *
     * @param a First element of the pair.
     * @param b Second element of the pair.
     */
    public Pair(A a, B b) {
        mA = a;
        mB = b;
    }

    /**
     * Return the first value of the pair.
     *
     * @return The first value of the pair.
     */
    public A a() {
        return mA;
    }

    /**
     * Return the second value of the pair.
     *
     * @return The second value of the pair.
     */
    public B b() {
        return mB;
    }


    /**
     * Returns a string representation of this pair, including
     * the results of converting each of its components to a string.
     *
     * @return A string representation for this pair.
     */
    @Override
    public String toString() {
        return "(" + a() + "," + b() + ")";
    }

    /**
     * Returns <code>true</code> if the specified object
     * is a pair that has objects equal to this pair's.
     *
     * <p><b>Warning:</b> The generic type specifications
     * of the objects being compared do not need to match
     * as long as the elements match.
     *
     * @param that Object to compare to this pair.
     * @return <code>true</code> if the specified object
     * is a pair that has objects equal to this pair's.
     */
    @Override
    @SuppressWarnings("rawtypes") // req for instanceof
    public boolean equals(Object that) {
        if (!(that instanceof Pair))
            return false;
        Pair<?,?> thatPair = (Pair<?,?>) that;
        return mA.equals(thatPair.mA)
            && mB.equals(thatPair.mB);
    }

    /**
     * Returns the hash code of the pair.  The hash code
     * is defined to be consistent with equality:
     *
     * <blockquote><pre>
     * hashCode() = 31 * a().hashCode() + b().hashCode();
     */
    @Override
    public int hashCode() {
        return 31 * mA.hashCode() + mB.hashCode();
    }

}
