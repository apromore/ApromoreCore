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
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A <code>BoundedPriorityQueue</code> implements a priority queue
 * with an upper bound on the number of elements.  If the queue is not
 * full, added elements are always added.  If the queue is full and
 * the added element is greater than the smallest element in the
 * queue, the smallest element is removed and the new element is
 * added.  If the queue is full and the added element is not greater
 * than the smallest element in the queue, the new element is not
 * added.
 *
 * <P>Bounded priority queues are the ideal data structure with which
 * to implement n-best accumulators.  A priority queue of bound
 * <code>n</code> can find the <code>n</code>-best elements of a
 * collection of <code>m</code> elements using <code>O(n)</code> space
 * and <code>O(m n log n)</code> time.

 * <P>Bounded priority queues may also be used as the basis of a
 * search implementation with the bound implementing heuristic
 * n-best pruning.
 *
 * <P>Because bounded priority queues require a comparator and a
 * maximum size constraint, they do not comply with the recommendation
 * in the {@link java.util.Collection} interface in that they neither
 * implement a nullary constructor nor a constructor taking a single
 * collection.  Instead, they are constructed with a comparator and
 * a maximum size bound.
 *
 * <P><i>Implementation Note:</i> Priority queues are implemented on
 * top of {@link TreeSet} with element wrappers to adapt object
 * equality and the priority comparator.  Because tree sets implement
 * balanced trees, the priority queue operations,
 * <code>add(Object)</code>, <code>pop()</code> and
 * <code>peek()</code>, all require <code>O(log n)</code> time where
 * <code>n</code> is the size of the queue.  A standard heap-based
 * implementation of a queue implements peeks in constant time and
 * adds and pops in <code>O(log n)</code> time.  For our intended
 * applications, pops are more likely than peeks and we need access to
 * the worst element in the queue.  An upside-down ordered heap
 * implementation of priority queues implements bounded adds most
 * efficiently, but requires up to <code>O(n)</code> for a pop or peek
 * and an <code>O(n log n)</code> sort before iteration.
 *
 * @author  Bob Carpenter
 * @version 4.0.0
 * @since   LingPipe2.0
 * @param <E> the type of objects stored in the queue
 */
public class BoundedPriorityQueue<E>
    extends AbstractSet<E>
    implements Queue<E>,
               SortedSet<E> {

    final SortedSet<Entry<E>> mQueue;
    private int mMaxSize;
    private final Comparator<? super E> mComparator;

    /**
     * Construct a bounded priority queue which uses the specified
     * comparator to order elements and allows up to the specified
     * maximum number of elements.
     *
     * @param comparator Comparator to order elements.
     * @param maxSize Maximum number of elements in the queue.
     * @throws IllegalArgumentException If the maximum size is less than 1.
     */
    public BoundedPriorityQueue(Comparator<? super E> comparator,
                                int maxSize) {
        if (maxSize < 1) {
            String msg = "Require maximum size >= 1."
                + " Found max size=" + maxSize;
            throw new IllegalArgumentException(msg);
        }
        mQueue = new TreeSet<Entry<E>>(new EntryComparator());
        mComparator = comparator;
        mMaxSize = maxSize;
    }

    /**
     * Returns the highest scoring object in this priority queue,
     * throwing an exception if the queue is empty.
     *
     * @return The highest scoring object in this queue.
     * @throws NoSuchElementException If the queue is empty.
     */
    public E element() {
        E result = peek();
        if (result == null)
            throw new NoSuchElementException();
        return result;
    }

    /**
     * Returns and removes the highest scoring element in this
     * queue, or {@code null} if it is empty.
     *
     * <p>This method differs from {@link #remove()} only
     * in that it returns {@code null} rather than throwing an
     * exception for an empty queue.
     *
     * @return The highest scoring element in this queue, or
     * {@code null} if it is empty.
     */
    public E poll() {
        return pop();
    }
    
    /**
     * Insert the specified element into the queue if possible.
     * Insertion will be possible if the queue has not yet
     * reached its capacity, or if the offered element is greater
     * than the smallest element in the queue.
     *
     * @param o Item offered.
     * @return {@code true} if the item was inserted.
     */
    public boolean offer(E o) {
        if (size() < mMaxSize)
            return mQueue.add(new Entry<E>(o));
        Entry<E> last = mQueue.last();
        E lastObj = last.mObject;
        if (mComparator.compare(o,lastObj) <= 0)
            return false; // worst element better
        if (!mQueue.add(new Entry<E>(o)))
            return false; // already contain elt
        mQueue.remove(last);
        return true;
    }


    /**
     * Returns and removes the highest scoring element in
     * this queue, throwing an exception if it is empty.
     *
     * <p>This method differs from {@link #poll()} only in
     * that it throws an exception for an empty queue
     * instead of returning {@code null}.
     *
     * @return The highest scoring element in this queue.
     */
    public E remove() {
        E result = poll();
        if (result == null)
            throw new NoSuchElementException();
        return result;
    }

    /**
     * Returns <code>true</code> if this bounded priority
     * queue has no elements.
     *
     * @return <code>true</code> if this bounded priority
     * queue has no elements.
     */
    @Override
    public boolean isEmpty() {
        return mQueue.isEmpty();
    }

    /**
     * Returns the lowest scoring object in the priority queue,
     * or <code>null</code> if the queue is empty.
     *
     * <p>The behavior with empty queues is what makes this method
     * different than the sorted set method {@link #last()}.
     *
     * @return Lowest scoring object in the queue.
     */
    public E peekLast() {
        if (isEmpty()) return null;
        return mQueue.last().mObject;
    }

    /**
     * Returns the lowest scoring object in the priority queue,
     * or <code>null</code> if the queue is empty.
     *
     * @return Lowest scoring object in the queue.
     */
    public E last() {
        if (isEmpty()) throw new NoSuchElementException();
        return mQueue.last().mObject;
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
     * Returns the comparator for this sorted set.  The result
     * will always be non-null.
     *
     * @return This set's comparator.
     */
    public Comparator<? super E> comparator() {
        return mComparator;
    }

    /**
     * Returns the highest scoring object in the priority queue.
     *
     * @return The highest scoring object in the queue.
     * @throws NoSuchElementException If the queue is empty.
     */
    public E first() {
        if (isEmpty()) throw new NoSuchElementException();
        return mQueue.first().mObject;
    }

    /**
     * Returns the highest scoring object in the priority queue,
     * or <code>null</code> if the queue is empty.
     *
     * <p>This method is different than the sorted set method
     * {@link #first()}, which throws an exception if there is
     * an empty queue.
     *
     * @return The highest scoring object in the queue.
     */
    public E peek() {
        if (isEmpty()) return null;
        return mQueue.first().mObject;
    }

    /**
     * Return and remove the highest scoring element in
     * this queue, or return {@code null} if it is empty.
     *
     * @return The highest scoring element in this queue,
     * or {@code null} if the queue is empty.
     */
     E pop() {
        if (isEmpty()) return null;
        if (mQueue.isEmpty()) return null;
        Entry<E> entry = mQueue.first();
        mQueue.remove(entry);
        return entry.mObject;
    }

    /**
     * Removes the specified object from the priority queue.  Note
     * that the object is removed using identity conditions defined by
     * the comparator specified for this queue, not the natural
     * equality or comparator.
     *
     * @param obj Object to remove from priority queue.
     * @return <code>true</code> if the object was removed.
     * @throws ClassCastException If the specified object is not
     * compatible with this collection.
     */
    @Override
    public boolean remove(Object obj) {
        // necessary to implement method signature
        @SuppressWarnings("unchecked")
        E eObj = (E) obj;
        Entry<E> entry = new Entry<E>(eObj,-1L);
        boolean result = mQueue.remove(entry);
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Iterator<E> it = iterator(); it.hasNext(); ) {
            if (c.contains(it.next())) {
                it.remove();
                changed = true;
            }
        }
        return changed;
    }


    /**
     * Sets the maximum size of this bounded priority queue to
     * the specified maximum size.  If there are more than the
     * specified number of elements in the queue, they are popped
     * one by one until the queue is of the maximum size.
     *
     * <p>Note that this operation is not thread safe and should not
     * be called concurrently with any other operations on this queue.
     *
     * @param maxSize New maximum size for this queue.
     */
    public void setMaxSize(int maxSize) {
        mMaxSize = maxSize;
        while (mQueue.size() > maxSize())
            mQueue.remove(mQueue.last());
    }

    /**
     * Throw an unsupported operation exception.
     *
     * @param o Object to add to queue (ignored).
     * @return Always throws an exception.
     * @throws UnsupportedOperationException Always.
     */
    @Override
    public boolean add(E o) {
        String msg = "Adds not supported by queue because cannot guarantee addition.\nUse offer() instead.";
        throw new UnsupportedOperationException(msg);
    }

    /**
     * Removes all elements from this queue.  The queue will be
     * empty after this call.
     */
    @Override
    public void clear() {
        mQueue.clear();
    }

    /**
     * Returns the current number of elements in this
     * priority queue.
     *
     * @return Number of elements in this priority queue.
     */
    @Override
    public int size() {
        return mQueue.size();
    }

    /**
     * Returns the maximum size allowed for this queue.
     *
     * @return The maximum size allowed for this queue.
     */
    public int maxSize() {
        return mMaxSize;
    }

    /**
     * Returns an iterator over the elements in this bounded priority
     * queue.  The elements are returned in order.  The returned
     * iterator implements a fail-fast deletion in the same way
     * as Java's collections framework.
     *
     * @return Ordered iterator over the elements in this queue.
     */
    @Override
    public Iterator<E> iterator() {
        return new QueueIterator<E>(mQueue.iterator());
    }

    private class EntryComparator implements Comparator<Entry<E>> {
        public int compare(Entry<E> entry1, Entry<E> entry2) {
            // reverse normal so largest is "first"
            E eObj1 = entry1.mObject;
            E eObj2 = entry2.mObject;
            if (eObj1.equals(eObj2)) return 0;
            int comp = mComparator.compare(eObj1,eObj2);
            if (comp != 0) return -comp;
            // arbitrarily order by earliest
            return entry1.mId < entry2.mId ? 1 : -1;
        }
    }

    // must explore why there's an ID here
    // did this get inserted for debugging -- it's private
    // and not used anywhere.  Otherwise, an Entry
    // could just be replaced with an E
    private static class Entry<E> {
        private final long mId;
        private final E mObject;
        public Entry(E object) {
            this(object,nextId());
        }
        public Entry(E object, long id) {
            mObject = object;
            mId = id;
        }
        private static synchronized long nextId() {
            return sNextId++;
        }
        private static long sNextId = 0;
        @Override
        public String toString() {
            return "qEntry(" + mObject.toString() + "," + mId + ")";
        }
    }

    private static class QueueIterator<E> implements Iterator<E> {
        private final Iterator<Entry<E>> mIterator;
        QueueIterator(Iterator<Entry<E>> iterator) {
            mIterator = iterator;
        }
        public boolean hasNext() {
            return mIterator.hasNext();
        }
        public E next() {
            return mIterator.next().mObject;
        }
        public void remove() {
            mIterator.remove();
        }
    }



}
