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

/**
 * A <code>ScoredObject</code> provides an implementation of the
 * <code>Scored</code> interface with an attached object.  Scored
 * objects are immutable and identity is reference.  The object
 * returned by the getter {@link #getObject()} is the actual object
 * stored, so changes to it will affect the scored object of which it
 * is a part.
 *
 * @author  Bob Carpenter
 * @version 3.8.3
 * @since   LingPipe2.0
 * @param <E> the type of object that is scored
 */
public class ScoredObject<E> implements Scored {

    private final E mObj;
    private final double mScore;

    /**
     * Construct a scored object from the specified object
     * and score.
     *
     * @param obj Object for the constructed scored object.
     * @param score Score for the constructed scored object.
     */
    public ScoredObject(E obj, double score) {
        mObj = obj;
        mScore = score;
    }

    /**
     * Returns the object attached to this scored object.
     *
     * @return The object attached to this scored object.
     */
    public E getObject() {
        return mObj;
    }

    /**
     * Returns the score for this scored object.
     *
     * @return The score for this scored object.
     */
    public double score() {
        return mScore;
    }

    /**
     * Returns a string-based representation of this object consisting
     * of the score followed by a colon (<code>':'</code>), followed
     * by the object converted to a string.
     *
     * @return The string-based representation of this object.
     */
    @Override
    public String toString() {
        return mScore + ":" + getObject();
    }

    /**
     * Returns <code>true</code> if the specified object is
     * a scored object with an object equal to this object's
     * and equal scores.
     *
     * @param that Object to compare to this scored object.
     * @return <code>true</code> if the object is a scored object
     * equal to this one.
     */
    @Override
    @SuppressWarnings("rawtypes") // required for instanceof
    public boolean equals(Object that) {
        if (!(that instanceof ScoredObject))
            return false;
        ScoredObject<?> thatSo = (ScoredObject<?>) that;
        return mObj.equals(thatSo.mObj)
            && mScore == thatSo.mScore;
    }

    /**
     * Returns a comparator that sorts in ascending order of score.
     *
     * <p>This comparator may not be consistent with equality on
     * the objects being compared, as it only depends on the score.

     * <p>The returned comparator may be used as the priority ordering
     * for a priority queue of objects sorted by score.  It may also
     * be passed to {@link
     * java.util.Arrays#sort(Object[],Comparator)}.
     *
     * <p>This implementation is a singleton -- the same comparator
     * is used for all instances.
     *
     * @return The ascending score comparator.
     * @param <E> the type of scored objects being compared
     */
    public static <E extends Scored> Comparator<E> comparator() {
        @SuppressWarnings({"unchecked","deprecation"})
        Comparator<E> result = (Comparator<E>) SCORE_COMPARATOR;
        return result;
    }

    /**
     * Returns a comparator that sorts in descending order of score.
     * This is just the inverse ordering of {@link #comparator()}; see
     * that method's documentation for more details.
     *
     * @return The descending score comparator.
     * @param <E> the type of scored objects being compared
     */
    public static <E extends Scored> Comparator<E> reverseComparator() {
        @SuppressWarnings({"unchecked", "deprecation"})
        Comparator<E> result = (Comparator<E>) REVERSE_SCORE_COMPARATOR;
        return result;
    }


    static final Comparator<Scored> REVERSE_SCORE_COMPARATOR
        = new ScoredObject.ReverseScoredComparator();

    static final Comparator<Scored> SCORE_COMPARATOR
        = new ScoredObject.ScoredComparator();

    // package privates can't go in interface, so park them here

    static class ScoredComparator implements Comparator<Scored> {
        public int compare(Scored obj1, Scored obj2) {
            return (obj1.score() > obj2.score())
                ? 1
                : ( (obj1.score() < obj2.score())
                    ? -1
                    : 0 );
        }
    };

    static class ReverseScoredComparator
        implements Comparator<Scored> {

        public int compare(Scored obj1, Scored obj2) {
            return (obj1.score() > obj2.score())
                ? -1
                : ( (obj1.score() < obj2.score())
                    ? 1
                    : 0 );
        }
    };



}
