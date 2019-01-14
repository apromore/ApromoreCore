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

import java.util.AbstractList;

/**
 * A tuple is an immutable ordered list of objects or
 * <code>null</code> values with equality conditions determined
 * pointwise.
 *
 * @author  Bob Carpenter
 * @version 3.0
 * @since   LingPipe1.0
 * @param <E> the type of object stored in the tuple
 */
public abstract class Tuple<E> extends AbstractList<E> {

    /**
     * Caches value of hashCode, with <code>0</code> indicating
     * still needs to be computed.
     */
    private int mHashCode = 0;

    /**
     * Do not allow instance construction.
     */
    private Tuple() { /* empty hiding impl */ }

    /**
     * Returns the length of the tuple.
     *
     * @return Number of elements in the tuple.
     */
    @Override
    abstract public int size();

    /**
     * Returns the object at the specified index in the tuple.
     *
     * @param index Index of element to return.
     * @return Object at the specified index.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    @Override
    abstract public E get(int index);

    /**
     * Returns the hash code for this list.  Hash codes are cached,
     * because tuples are immutable.  Subclasses that override this
     * method must follow the specification in {@link java.util.List}.
     *
     * @return Hash code value for this tuple.
     */
    @Override
    public int hashCode() {
	// thread safe due to immutability and atomicity of int assigns
        if (mHashCode == 0)
            mHashCode = super.hashCode();
        return mHashCode;
    }

    /**
     * Returns a zero-length tuple.
     *
     * @return Zero-length tuple.
     * @param <E> the type of object stored in the tuple
     */
    public static <E> Tuple<E> create() {
	return new EmptyTuple<E>();
    }

    /**
     * Returns a tuple of length one containing the specified
     * object.
     *
     * @param obj Object to include in tuple.
     * @return Length one tuple containing specified object.
     * @param <E> the type of object stored in the tuple
     */
    public static <E> Tuple<E> create(E obj) {
	return new SingletonTuple<E>(obj);
    }


    /**
     * Creates a tuple from the specified array
     * of objects.
     *
     * @param objs Objects on which to base tuple.
     * @return Tuple representing specified array of objects.
     * @param <E> the type of object stored in the tuple
     */
    public static <E> Tuple<E> create(E[] objs) {
        switch (objs.length) {
        case 0: return new EmptyTuple<E>();
        case 1: return new SingletonTuple<E>(objs[0]);
        case 2: return new PairTuple<E>(objs[0],objs[1]);
        default: return new ArrayTuple<E>(objs);
        }
    }

    /**
     * Returns a new tuple representing a pair of objects.
     *
     * @param o1 First object in pair.
     * @param o2 Second object in pair.
     * @return Tuple consisting of the specified pair of objects.
     * @param <E> the type of object stored in the tuple
     */
    public static <E> Tuple<E> create(E o1, E o2) {
        return new PairTuple<E>(o1,o2);
    }

    /**
     * Instances represent the unique empty tuple.  Should
     * be accessed through {@link #EMPTY_TUPLE}.
     *
     * @param <E> the type of object stored in the tuple
     */
    private static class EmptyTuple<E> extends Tuple<E> {

        /**
         * Construct a new empty tuple.
         */
        private EmptyTuple() { 
            /* do nothing */
        }

        /**
         * Returns <code>0</code>, the size of this tuple.
         *
         * @returns <code>0</code>, the size of this tuple.
         */
        @Override
        public int size() { return 0; }

        /**
         * Returns the object at the specified index in the tuple.
         *
         * @param index Index of element to return.
         * @return Object at the specified index.
         * @throws IndexOutOfBoundsException If the index is out of bounds.
         */
        @Override
        public E get(int index) {
            String msg = "No elements in empty tuple.";
            throw new IndexOutOfBoundsException(msg);
        }

        /**
         * Returns <code>1</code>, the unique hash code for the empty
         * tuple.
         *
         * @return <code>1</code>, the unique hash code for the empty
         * tuple.
         */
        @Override
        public int hashCode() {
            return 1;
        }

    }


    /**
     * Instances of <code>SingletonTuple</code> represent tuples
     * of one element.
     *
     * @param <E> the type of object stored in the tuple
     */
    private static class SingletonTuple<E> extends Tuple<E> {

        /**
         * The single object in this tuple.
         */
        private final E mObject;

        /**
         * Construct a singleton tuple from the specified object.
         *
         * @param object Object from which to construct the singleton.
         */
        private SingletonTuple(E object) {
            mObject = object;
        }

        /**
         * Return <code>1</code>, the size of a singleton tuple.
         *
         * @return <code>1</code>, the size of a singleton tuple.
         */
        @Override
        public int size() { return 1; }

        /**
         * Returns the object at the specified index in the tuple.
         *
         * @param index Index of element to return.
         * @return Object at the specified index.
         * @throws IndexOutOfBoundsException If the index is out of
         * bounds, namely other than <code>0</code>.
         */
        @Override
        public E get(int index) {
            if (index < 0)
                throw new IndexOutOfBoundsException("Index must be > 0, was="
                                                    + index);
            if (index > 0)
                throw new IndexOutOfBoundsException("Index must be < 1, was="
                                                    + index);
            return mObject;
        }

    }

    /**
     * @param <E> the type of object stored in the tuple
     */
    private static class PairTuple<E> extends Tuple<E> {

        /**
         * The first object in the pair.
         */
        private final E mObject0;

        /**
         * The second object in the pair.
         */
        private final E mObject1;

        /**
         * Construct a pair tuple from the specified pair of objects.
         *
         * @param object0 First object in the pair.
         * @param object1 Second object in the pair.
         */
        private PairTuple(E object0, E object1) {
            mObject0 = object0;
            mObject1 = object1;
        }

        /**
         * Return <code>2</code>, the size of a pair tuple.
         *
         * @return <code>2</code>, the size of a pair tuple.
         */
        @Override
        public int size() { return 2; }

        /**
         * Returns the object at the specified index in the tuple.
         *
         * @param index Index of element to return.
         * @return Object at the specified index.
         * @throws IndexOutOfBoundsException If the index is out of
         * bounds, namely not <code>0</code> or <code>1</code>.
         */
        @Override
        public E get(int index) {
            if (index < 0)
                throw new IndexOutOfBoundsException("Index must be > 0, was="
                                                    + index);
            if (index > 1)
                throw new IndexOutOfBoundsException("Index must be < 2, was="
                                                    + index);
            return index == 0 ? mObject0 : mObject1;
        }

    }

    /**
     * Instances of <code>ArrayTuple</code> provide concrete, array-backed
     * extensions of the <code>Tuple</code> class.
     *
     * @param <E> the type of object stored in the tuple
     */
    private static class ArrayTuple<E> extends Tuple<E> {

        /**
         * The objects backing this tuple.
         */
        private final E[] mObjs;

        /**
         * Construct a tuple backed by the specified array of objects.
         *
         * @param objs Objects on which to back the constructed tuple.
         */
        private ArrayTuple(E[] objs) {
            mObjs = objs;
        }

        /**
         * Returns the object at the specified index in the tuple.
         *
         * @param index Index of element to return.
         * @return Object at the specified index.
         * @throws IndexOutOfBoundsException If the index is out of bounds.
         */
        @Override
        public E get(int index) {
            return mObjs[index];
        }

        /**
         * Returns the length of the tuple.
         *
         * @return Number of elements in the tuple.
         */
        @Override
        public int size() {
            return mObjs.length;
        }

    }


}
