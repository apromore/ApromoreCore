package com.aliasi.util;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * A <code>MinMaxHeap</code> provides a heap-like data structure that
 * provides fast access to both the minimum and maximum elements of
 * the heap.  Each min-max heap is of a fixed maximum size.  A min-max
 * heap holds elements implementing the {@link Scored} interface, with
 * scores being used for sorting.
 *
 * <p>A min-max heap data structure is useful to implement priority
 * queues with fixed numbers of elements, which requires access to
 * both the best and worst elements of the queue.
 *
 * <p>This implementation is based on the paper:
 * <ul>
 * <li>Atkinson, M.D., J.-R. Sack, N. Santoro and T. Strothotte.
 *     1986.
 *     <a href="http://www.cs.otago.ac.nz/staffpriv/mike/Papers/MinMaxHeaps/MinMaxHeaps.pdf">Min-max heaps and generalized priority queues</a>.
 *     <i>Communications of the ACM</i> <b>29</b>(10):996-1000.
 * </li>
 * </ul>
 *
 * @author  Bob Carpenter
 * @version 3.8
 * @since   LingPipe2.4.0
 * @param <E> the type of objects stored in the heap
 */
public class MinMaxHeap<E extends Scored> extends AbstractCollection<E> {

    // index starts at 1 to follow article
    // min at root and at even levels; max at root dtrs
    private final E[] mHeap;

    private final int mHeapLength;

    // true for min levels
    private final boolean[] mLevelTypes;

    private int mNextFreeIndex = 1;

    /**
     * Construct a min-max heap holding up to the specified
     * number of elements.   Min-max heaps do not grow
     * dynamically and attempts to push an element onto a full
     * heap will result in exceptions.
     *
     * @param maxSize Maximum number of elements in the heap.
     */
    public MinMaxHeap(int maxSize) {
        if (maxSize < 1) {
            String msg = "Heaps must be at least one element."
                + " Found maxSize=" + maxSize;
            throw new IllegalArgumentException(msg);
        }
        // required for array
        @SuppressWarnings("unchecked")
        E[] tempHeap = (E[]) new Scored[maxSize+1];
        mHeap = tempHeap;
        mHeapLength = mHeap.length;
        mLevelTypes = new boolean[maxSize+1];
        fillLevelTypes(mLevelTypes);
    }

    /**
     * Returns the current size of this heap.
     *
     * @return The size of the heap.
     */
    @Override
    public int size() {
        return mNextFreeIndex - 1;
    }


    /**
     * Returns an iterator over this heap.  The elements are returned
     * in decreasing order of score.
     *
     * @return Iterator over this heap in decreasing order of score.
     */
    @Override
    public Iterator<E> iterator() {
        ArrayList<E> list = new ArrayList<E>();
        for (int i = 0; i < mNextFreeIndex; ++i)
            list.add(mHeap[i]);
        Collections.<E>sort(list,ScoredObject.reverseComparator());
        return list.iterator();
    }

    /**
     * Add the element to the heap.
     *
     * @param s Element to add to heap.
     * @return <code>true</code> if the element is added.
     */
    @Override
    public boolean add(E s) {
        // space still left
        if (mNextFreeIndex < mHeapLength) {
            mHeap[mNextFreeIndex++] = s;
            bubbleUp(mNextFreeIndex-1);
            return true;
        } else if (s.score() <= peekMin().score()) {
            return false;
        } else {
            popMin();
            mHeap[mNextFreeIndex++] = s;
            bubbleUp(mNextFreeIndex-1);
            return true;
        }
    }

    /**
     * Returns the maximum element in the heap, or <code>null</code>
     * if it is empty.
     *
     * @return The largest element in the heap.
     */
    public E peekMax() {
        return
            ( mNextFreeIndex == 1
              ? null
              : (mNextFreeIndex == 2
                 ? mHeap[1]
                 : (mNextFreeIndex == 3
                    ? mHeap[2]
                    : ( mHeap[2].score() > mHeap[3].score()
                        ? mHeap[2]
                        : mHeap[3] ) ) ) );
    }


    /**
     * Returns the minimum element in the heap, or <code>null</code>
     * if it is empty.
     *
     * @return The smallest element in the heap.
     */
    public E peekMin() {
        return (mNextFreeIndex == 1)
            ? null
            : mHeap[1];
    }

    /**
     * Returns the maximum element in the heap after removing it, or
     * returns <code>null</code> if the heap is empty.
     *
     * @return The largest element in the heap.
     */
    public E popMax() {
        if (mNextFreeIndex == 1) return null;

        // only one element; return it
        if (mNextFreeIndex == 2) {
            --mNextFreeIndex;
            return mHeap[1];
        }

        // two elements, so max is only on second level
        if (mNextFreeIndex == 3) {
            --mNextFreeIndex;
            return mHeap[2];
        }

        // at least three elements, so check level 1 dtrs
        if (mHeap[2].score() > mHeap[3].score()) {
            E max = mHeap[2];
            mHeap[2] = mHeap[--mNextFreeIndex];
            trickleDownMax(2);
            return max;
        } else {
            E max = mHeap[3];
            mHeap[3] = mHeap[--mNextFreeIndex];
            trickleDownMax(3);
            return max;
        }
    }

    /**
     * Returns the minimum element in the heap after removing it, or
     * returns <code>null</code> if the heap is empty.
     *
     * @return The smallest element in the heap.
     */
    public E popMin() {
        if (mNextFreeIndex == 1) return null;

        if (mNextFreeIndex == 2) {
            mNextFreeIndex = 1;
            return mHeap[1];
        }

        E min = mHeap[1];
        mHeap[1] = mHeap[--mNextFreeIndex];
        trickleDownMin(1);
        return min;
    }

    /**
     * Returns a string-based representation of this heap.
     *
     * @return String representation of this heap.
     */
    @Override
    public String toString() {
        if (mNextFreeIndex == 1) return "EMPTY HEAP";
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < mNextFreeIndex; ++i) {
            if (i > 1) sb.append("\n");
            sb.append(i + "=" + mHeap[i]);
        }
        return sb.toString();
    }

    void bubbleUp(int nodeIndex) {
        if (!hasParent(nodeIndex)) return;
        int parentIndex = parentIndex(nodeIndex);
        if (onMinLevel(nodeIndex)) {
            if (mHeap[nodeIndex].score() > mHeap[parentIndex].score()) {
                swap(nodeIndex,parentIndex);
                bubbleUpMax(parentIndex);
            } else {
                bubbleUpMin(nodeIndex);
            }
        } else {  // on max level
            if (mHeap[nodeIndex].score() < mHeap[parentIndex].score()) {
                swap(nodeIndex,parentIndex);
                bubbleUpMin(parentIndex);
            } else {
                bubbleUpMax(nodeIndex);
            }
        }
    }

    void bubbleUpMin(int nodeIndex) {
        while (true) {
            if (!hasParent(nodeIndex)) return;
            int parentIndex = parentIndex(nodeIndex);
            if (!hasParent(parentIndex)) return;
            int grandparentIndex = parentIndex(parentIndex);
            if (mHeap[nodeIndex].score()
                >= mHeap[grandparentIndex].score()) return;
            swap(nodeIndex,grandparentIndex);
            nodeIndex = grandparentIndex;
        }
    }

    void bubbleUpMax(int nodeIndex) {
        while (true) {
            if (!hasParent(nodeIndex)) return;
            int parentIndex = parentIndex(nodeIndex);
            if (!hasParent(parentIndex)) return;
            int grandparentIndex = parentIndex(parentIndex);
            if (mHeap[nodeIndex].score()
                <= mHeap[grandparentIndex].score()) return;
            swap(nodeIndex,grandparentIndex);
            nodeIndex = grandparentIndex;
        }
    }


    boolean onMinLevel(int nodeIndex) {
        return mLevelTypes[nodeIndex];
    }

    void trickleDown(int nodeIndex) {
        if (noChildren(nodeIndex)) return;
        if (onMinLevel(nodeIndex))
            trickleDownMin(nodeIndex);
        else
            trickleDownMax(nodeIndex);
    }

    void trickleDownMin(int nodeIndex) {
        while (leftDaughterIndex(nodeIndex) < mNextFreeIndex) { // has dtrs
            int minDescIndex = minDtrOrGrandDtrIndex(nodeIndex);
            if (isDaughter(nodeIndex,minDescIndex)) {
                if (mHeap[minDescIndex].score() < mHeap[nodeIndex].score())
                    swap(minDescIndex,nodeIndex);
                return;
            } else {  // is grand child
                if (mHeap[minDescIndex].score() >= mHeap[nodeIndex].score())
                    return;
                swap(minDescIndex,nodeIndex);
                int parentIndex = parentIndex(minDescIndex);
                if (mHeap[minDescIndex].score() > mHeap[parentIndex].score())
                    swap(minDescIndex,parentIndex);
                nodeIndex = minDescIndex; // recursive call in paper
            }
        }
    }

    void trickleDownMax(int nodeIndex) {
        while (leftDaughterIndex(nodeIndex) < mNextFreeIndex) {
            int maxDescIndex = maxDtrOrGrandDtrIndex(nodeIndex);
            if (isDaughter(nodeIndex,maxDescIndex)) {
                if (mHeap[maxDescIndex].score() > mHeap[nodeIndex].score())
                    swap(maxDescIndex,nodeIndex);
                return;
            } else {  // is grand child
                if (mHeap[maxDescIndex].score() <= mHeap[nodeIndex].score())
                    return;
                swap(maxDescIndex,nodeIndex);
                int parentIndex = parentIndex(maxDescIndex);
                if (mHeap[maxDescIndex].score() < mHeap[parentIndex].score())
                    swap(maxDescIndex,parentIndex);
                nodeIndex = maxDescIndex; // recursive call in paper
            }
        }
    }


    // requires nodeIndex to have a dtr
    int minDtrOrGrandDtrIndex(int nodeIndex) {
        // start with left dtr; must have a dtr coming in
        int leftDtrIndex = leftDaughterIndex(nodeIndex);
        int minIndex = leftDtrIndex;
        double minScore = mHeap[leftDtrIndex].score();

        int rightDtrIndex = rightDaughterIndex(nodeIndex);
        if (rightDtrIndex >= mNextFreeIndex) return minIndex;
        double rightDtrScore = mHeap[rightDtrIndex].score();
        if (rightDtrScore < minScore) {
            minIndex = rightDtrIndex;
            minScore = rightDtrScore;
        }

        int grandDtr1Index = leftDaughterIndex(leftDtrIndex);
        if (grandDtr1Index >= mNextFreeIndex) return minIndex;
        double grandDtr1Score = mHeap[grandDtr1Index].score();
        if (grandDtr1Score < minScore) {
            minIndex = grandDtr1Index;
            minScore = grandDtr1Score;
        }

        int grandDtr2Index = rightDaughterIndex(leftDtrIndex);
        if (grandDtr2Index >= mNextFreeIndex) return minIndex;
        double grandDtr2Score = mHeap[grandDtr2Index].score();
        if (grandDtr2Score < minScore) {
            minIndex = grandDtr2Index;
            minScore = grandDtr2Score;
        }

        int grandDtr3Index = leftDaughterIndex(rightDtrIndex);
        if (grandDtr3Index >= mNextFreeIndex) return minIndex;
        double grandDtr3Score = mHeap[grandDtr3Index].score();
        if (grandDtr3Score < minScore) {
            minIndex = grandDtr3Index;
            minScore = grandDtr3Score;
        }

        int grandDtr4Index = rightDaughterIndex(rightDtrIndex);
        if (grandDtr4Index >= mNextFreeIndex) return minIndex;
        double grandDtr4Score = mHeap[grandDtr4Index].score();

        return grandDtr4Score < minScore
            ? grandDtr4Index
            : minIndex;
    }


    // requires nodeIndex to have a dtr
    int maxDtrOrGrandDtrIndex(int nodeIndex) {
        // start with left dtr; must have a dtr coming in
        int leftDtrIndex = leftDaughterIndex(nodeIndex);
        int maxIndex = leftDtrIndex;
        double maxScore = mHeap[leftDtrIndex].score();

        int rightDtrIndex = rightDaughterIndex(nodeIndex); // opt to left+1
        if (rightDtrIndex >= mNextFreeIndex) return maxIndex;
        double rightDtrScore = mHeap[rightDtrIndex].score();
        if (rightDtrScore > maxScore) {
            maxIndex = rightDtrIndex;
            maxScore = rightDtrScore;
        }

        int grandDtr1Index = leftDaughterIndex(leftDtrIndex);
        if (grandDtr1Index >= mNextFreeIndex) return maxIndex;
        double grandDtr1Score = mHeap[grandDtr1Index].score();
        if (grandDtr1Score > maxScore) {
            maxIndex = grandDtr1Index;
            maxScore = grandDtr1Score;
        }

        int grandDtr2Index = rightDaughterIndex(leftDtrIndex);
        if (grandDtr2Index >= mNextFreeIndex) return maxIndex;
        double grandDtr2Score = mHeap[grandDtr2Index].score();
        if (grandDtr2Score > maxScore) {
            maxIndex = grandDtr2Index;
            maxScore = grandDtr2Score;
        }

        int grandDtr3Index = leftDaughterIndex(rightDtrIndex);
        if (grandDtr3Index >= mNextFreeIndex) return maxIndex;
        double grandDtr3Score = mHeap[grandDtr3Index].score();
        if (grandDtr3Score > maxScore) {
            maxIndex = grandDtr3Index;
            maxScore = grandDtr3Score;
        }

        int grandDtr4Index = rightDaughterIndex(rightDtrIndex);
        if (grandDtr4Index >= mNextFreeIndex) return maxIndex;
        double grandDtr4Score = mHeap[grandDtr4Index].score();

        return grandDtr4Score > maxScore
            ? grandDtr4Index
            : maxIndex;
    }

    boolean hasParent(int nodeIndex) {
        return nodeIndex > 1;
    }


    boolean noChildren(int nodeIndex) {
        return leftDaughterIndex(nodeIndex) >= mHeapLength;
    }

    boolean isDaughter(int nodeIndexParent, int nodeIndexDescendant) {
        return nodeIndexDescendant <= rightDaughterIndex(nodeIndexParent);
    }

    void swap(int index1, int index2) {
        E temp = mHeap[index1];
        mHeap[index1] = mHeap[index2];
        mHeap[index2] = temp;
    }


    static int parentIndex(int nodeIndex) {
        return nodeIndex/2;   // Java's int arith rounds down
    }

    static int leftDaughterIndex(int nodeIndex) {
        return 2 * nodeIndex;
    }

    static int rightDaughterIndex(int nodeIndex) {
        return 2 * nodeIndex + 1;
    }

    static void fillLevelTypes(boolean[] levelTypes) {
        boolean type = MAX_LEVEL;
        int index = 1;
        for (int numEltsOfType = 1; ; numEltsOfType *= 2) {  // 2**n per level
            type = !type;  // reverse types at each level
            for (int j = 0; j < numEltsOfType; ++j) {
                if (index >= levelTypes.length) return;
                levelTypes[index++] = type;
            }
        }
    }

    static final boolean MIN_LEVEL = true;
    static final boolean MAX_LEVEL = false;

}
