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

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A {@code ShortPriorityQueue<E>} is a length-bounded priority queue
 * optimized for short lengths.  Rather than maintaining tree or heap
 * data structures, it keeps elements in an array and uses a simple
 * bubble sort.
 *
 * <p>This class is different than {@link BoundedPriorityQueue} in
 * that it does not attempt enforce the uniqueness required by
 * the {@link java.util.Set} interface.
 *
 * <p>For large queues, use {@link BoundedPriorityQueue}.
 * For large queues of {@link Scored} instances, use {@link
 * MinMaxHeap}.
 *
 * <p>The head-, tail- and sub-set operations violate the sorted set
 * interface's requirements that they be views onto the queue itself;
 * instead, they are static snapshots.

 * <h3>Implementation Notes</h3>
 *
 * The queue allocates an array for the elements of the maximum size
 * specified in the constructor.  Items are added using bubblesort, so
 * that adding or removing elements may take up the current size's
 * number of operations (that is, it's {@code O(n)} where {@code n} is
 * the number of elements in the queue).  The peek methods and first-
 * and last-element methods are all constant time, as is the
 * iterator's next method.
 *
 *
 * @author  Bob Carpenter
 * @version 4.0.0
 * @since   Lingpipe3.8
 * @param <E> the type of objects stored in this queue
 */
public class ShortPriorityQueue<E>
    extends AbstractSet<E>
    implements Queue<E>, SortedSet<E> {

    private final Comparator<? super E> mComparator;
    private final E[] mElts;
    private int mSize = 0;

    /**
     * Construct a short priority queue that compares elements using
     * the specified comparator with the specified maximum size.
     * If adding an element would exceed the maximum size of the
     * queue, the lowest ordered element is dropped.
     *
     * @param comparator Element comparator for this queue.
     * @param maxSize Maximum number of elements in the queue.
     */
    public ShortPriorityQueue(Comparator<? super E> comparator,
                              int maxSize) {

        @SuppressWarnings("unchecked")
        E[] elts = (E[]) new Object[maxSize];
        mElts = elts;
        mComparator = comparator;
    }

    /**
     * Returns the maximum size of this queue.
     *
     * @return The maximum size of this queue.
     */
    public int maxSize() {
        return mElts.length;
    }

    /**
     * Returns the current number of elements in the queue.
     *
     * @return The size of the queue.
     */
    @Override
    public int size() {
        return mSize;
    }

    /**
     * Returns an iterator over the elements in this queue.
     * The elements are returned from largest to smallest
     * according to the comparator.
     *
     * <p>The iterator is not concurrent-modification safe, nor are
     * there any heuristic checks to check for modifications and throw
     * exceptions.
     *
     * @return The iterator over elements of this queue.
     */
    @Override
    public Iterator<E> iterator() {
        return Iterators.<E>arraySlice(mElts,0,mSize);
    }

    /**
     * Removes all the elements from the priority queue.
     * This takes time proportional to the size of the queue
     * in order to null out the references to elements in the
     * queue so as not to leak memory.
     */
    @Override
    public void clear() {
        for (int i = 0; i < mSize; ++i)
            mElts[i] = null;
        mSize = 0;
    }


    /**
     * Returns the largest element in this queue according to
     * the comparator, or {@code null} if the queue is empty.
     *
     * <p>This method differs from {@link #element()} only in that it
     * throws an returns {@code null} if the queue is empty rather
     * than throwing an exception.
     *
     * @return The largest element in the queue.
     */
    public E peek() {
        return isEmpty() ? null : mElts[0];
    }

    /**
     * Returns the largest element in this queue according to
     * the comparator, throwing an exception if the queue is
     * empty.
     *
     * <p>This method differs from {@link #peek()} only in
     * that it throws an exception if the queue is empty
     * rather than returning {@code null}.
     *
     * @return The largest element in this queue.
     * @throws NoSuchElementException If the queue is empty.
     */
    public E element() {
        E result = peek();
        if (result == null)
            throw new NoSuchElementException("");
        return result;
    }

    /**
     * Returns the smallest element in this queue according to
     * the comparator, or {@code null} if the queue is empty.
     *
     * @return The smallest element in the queue.
     */
    public E peekLast() {
        return isEmpty() ? null : mElts[mSize-1];
    }

    /**
     * Returns the largest element in the queue.  Unlike {@link #peek()},
     * this method throws an exception if the queue is empty.
     *
     * @return The largest element in the queue.
     * @throws IllegalArgumentException If the queue is empty.
     */
    public E first() {
        if (isEmpty()) throw new NoSuchElementException();
        return mElts[0];
    }

    /**
     * Returns the smallest element in the queue.  Unlike {@link
     * #peekLast()}, this method throws an exception if the queue is
     * empty.
     *
     * @return The smallest element in the queue.
     * @throws IllegalArgumentException If the queue is empty.
     */
    public E last() {
        if (isEmpty()) throw new NoSuchElementException();
        return mElts[mSize-1];
    }

    /**
     * Return the set of elements in this queue strictly less than the
     * specified element according to the comparator for this queue.
     *
     * <p>In violation of the {@link SortedSet} interface
     * specification, the result of this method is <b>not</b> a view
     * onto this queue, but rather a static snapshot of the queue.
     *
     * @param toElement Exclusive upper bound on returned elements.
     * @return The set of elements less than the upper bound.
     * @throws ClassCastException If the upper bound is not compatible with
     * this queue's comparator.
     * @throws NullPointerException If the upper bound is null.
     */
    public SortedSet<E> headSet(E toElement) {
        SortedSet<E> result = new TreeSet<E>();
        for (E e : this) {
            if (mComparator.compare(e,toElement) < 0)
                result.add(e);
            else
                break;
        }
        return result;
    }

    /**
     * Return the set of elements in this queue grearer than or equal
     * to the specified element according to the comparator for this
     * queue.
     *
     * <p>In violation of the {@link SortedSet} interface
     * specification, the result of this method is <b>not</b> a view
     * onto this queue, but rather a static snapshot of the queue.
     *
     * @param fromElement Inclusive lower bound on returned elements.
     * @return The set of elements greater than or equal to the lower bound.
     * @throws ClassCastException If the lower bound is not compatible with
     * this queue's comparator.
     * @throws NullPointerException If the lower bound is null.
     */
    public SortedSet<E> tailSet(E fromElement) {
        SortedSet<E> result = new TreeSet<E>();
        for (E e : this) {
            if (mComparator.compare(e,fromElement) >= 0)
                result.add(e);
        }
        return result;
    }

    /**
     * Returns the comparator for this priority queue.
     *
     * @return The comparator for this priority queue.
     */
    public Comparator<? super E> comparator() {
        return mComparator;
    }

    /**
     * Return the set of elements in this queue greater than or equal
     * to the specified lower bound and strictly less than the upper bound
     * element according to the comparator for this queue.
     *
     * <p>In violation of the {@link SortedSet} interface
     * specification, the result of this method is <b>not</b> a view
     * onto this queue, but rather a static snapshot of the queue.
     *
     * @param fromElement Inclusive lower bound on returned elements.
     * @param toElement Exclusive upper bound on returned elements.
     * @return The set of elements greater than or equal to the lower bound.
     * @throws ClassCastException If the lower bound is not compatible with
     * this queue's comparator.
     * @throws NullPointerException If the lower bound is null.
     * @throws IllegalArgumentException If the lower bound is greater than the upper bound.
     */
    public SortedSet<E> subSet(E fromElement,
                               E toElement) {
        int c = mComparator.compare(fromElement,toElement);
        if (c >= 0) {
            String msg = "Lower bound must not be greater than the upper bound."
                + " Found fromElement=" + fromElement
                + " toElement=" + toElement;
            throw new IllegalArgumentException(msg);
        }
        SortedSet<E> result = new TreeSet<E>();
        for (E e : this) {
            if (mComparator.compare(e,fromElement) >= 0) {
                if (mComparator.compare(e,toElement) < 0)
                    result.add(e);
                else
                    break;
            }
        }
        return result;
    }


    /**
     * Removes and returns the largest element in this queue, or returns
     * {@code null} if the queue is empty.
     *
     * <p>This method differs from {@link #remove()} only in that
     * it returns {@code null} if the queue is empty rather than
     * throwing an exception.
     *
     * @return Remove and return the largest element in the queue, or
     * {@code null} if the queue is empty.
     */
    public E poll() {
        if (isEmpty()) return null;
        E result = mElts[0];
        for (int i = 1; i < mSize; ++i)
            mElts[i-1] = mElts[i];
        --mSize;
        return result;
    }

    /**
     * Returns and removes the head of this queue, throwing an
     * exception if it is empty.
     *
     * <p>This method differs from {@link #poll()} only in that
     * it throws an exception if the queue is empty rather than
     * returning {@code null}.
     *
     * @return The head of this queue.
     * @throws NoSuchElementException If the queue is emtpy.
     */
    public E remove() {
        E result = poll();
        if (result == null)
            throw new NoSuchElementException();
        return result;
    }

    /**
     * Returns {@code true} if the queue is empty.
     *
     * @return {@code true} if the queue is size zero.
     */
    @Override
    public boolean isEmpty() {
        return mSize == 0;
    }

    /**
     * Return {@code true} if the specified element may be added to
     * the queue.  An element may be added to the queue if the queue
     * is not full (its size is less than the maximum size), or if
     * the smallest element in the queue is strictly smaller than the
     * specified element according to this queue's comparator.
     *
     * @param elt Element to try to add to the queue.
     * @return {@code true} if the element was added.
     */
    public boolean offer(E elt) {
        if (mSize == mElts.length) {
            int c = mComparator.compare(mElts[mElts.length-1],elt);
            if (c >= 0)
                return false;
            mElts[mElts.length-1] = elt;
        }
        if (mSize < mElts.length) {
            mElts[mSize] = elt;
            ++mSize;
        }
        for (int i = mSize - 1; --i >= 0 && mComparator.compare(mElts[i],mElts[i+1]) < 0; ) {
            E temp = mElts[i];
            mElts[i] = mElts[i+1];
            mElts[i+1] = temp;
        }
        return true;
    }



    /**
     * Return {@code true} and remove the largest element in the
     * queue equal to the specified object, or return {@code false} if
     * the specified object is not in the queue.
     *
     * @param obj Object to remove.
     * @return {@code true} if the object was in the queue and was
     * removed.
     */
    @Override
    public boolean remove(Object obj) {
        for (int i = 0; i < mSize; ++i) {
            if (obj.equals(mElts[i])) {
                for (++i; i < mSize; ++i)
                    mElts[i-1] = mElts[i];
                --mSize;
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a string-based representation of the elements in this queue.
     *
     * @return A string-based representation of the elements in this queue.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ShortPriorityQueue(comparator=" + mComparator.getClass());
        sb.append(" maxLength=" + mElts.length + ")\n");
        for (int i = 0; i < mSize; ++i)
            sb.append("  [" + i + "]=" + mElts[i] + "\n");
        return sb.toString();
    }

}
