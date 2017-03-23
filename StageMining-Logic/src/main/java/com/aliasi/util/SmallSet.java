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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * The <code>SmallSet</code> class implements immutable instances the
 * {@link Set} interface tailored for sets with a small number of
 * members.  The sets created are included as member variables for
 * very small sets and backed by arrays for larger sets.  Although
 * <code>SmallSet</code> does not support the {@link Set#add(Object)}
 * method, it provides its own {@link #union(SmallSet)} operation,
 * which produces a new small set that is the result of unioning the
 * set to which it is applied to the small set in the argument.  There
 * are no public constructors.  Instaces are created using a family of
 * <code>create</code> factory method in order to construct an
 * appropriate representation based on the size of set being created.
 *
 * @author  Bob Carpenter
 * @version 3.9.1
 * @since   LingPipe1.0
 * @param <E> the type of object stored in the set
 */
public abstract class SmallSet<E> extends AbstractSet<E> {

    SmallSet() {
        /* no external instances */
    }

    /**
     * Returns the union of this set with the specified set,
     * without modifying either this set or the specified set.
     *
     * @param that Small set of elements to union with this set.
     * @return The small set consisting of all of the elements
     * in this set and the specified set.
     */
    public abstract SmallSet<E> union(SmallSet<? extends E> that);


    // generically usable small empty set
    @SuppressWarnings("rawtypes") // required for reusable immut generic
    static final SmallSet SMALL_EMPTY_SET = new SmallEmptySet();

    /**
     * Return an empty small set.
     *
     * @return Empty small set.
     * @param <F> the type of object stored in the set
     */
    public static <F> SmallSet<F> create() {
        @SuppressWarnings("unchecked")
        SmallSet<F> result = (SmallSet<F>) SMALL_EMPTY_SET;
        return result;
    }

    /**
     * Return a singleton empty set containing the specified member.
     *
     * @param member Single member of the returned set.
     * @return Singleton small set consisting of the specified member.
     * @param <F> the type of object stored in the set
     */
    public static <F> SmallSet<F> create(F member) {
        return new SingletonSet<F>(member);
    }

    /**
     * Return a set containing the two specified members.  The result
     * will be a singleton or a pair, depending on whether the
     * specified object are equal.
     *
     * @param member1 First member of set.
     * @param member2 Second member of set.
     * @return Small set containing only the specified members.
     * @param <F> the type of object stored in the set
     */
    public static <F> SmallSet<F> create(F member1, F member2) {
        if (member1.equals(member2)) return SmallSet.<F>create(member1);
        return new PairSet<F>(member1,member2);
    }

    /**
     * Return a set consisting of members drawn from the specified
     * array of members.  The cardinality of the resulting set depends
     * on whether pairs of specified members are equal.  For zero,
     * one and two-element input arrays, it returns the same
     * result as {@link #create()}, {@link #create(Object)}, and
     * {@link #create(Object,Object)}, respectively.
     *
     * @param members Array of members for the resulting set.
     * @return Small set consisting of specified members.
     * @param <F> the type of object stored in the set
     */
    public static <F> SmallSet<F> create(F[] members) {
        switch (members.length) {
        case 0:
            return SmallSet.<F>create();
        case 1:
            return SmallSet.<F>create(members[0]);
        case 2:
            return SmallSet.<F>create(members[0],members[1]);
        default:
            HashSet<F> set = new HashSet<F>();
            Collections.addAll(set,members);
            return SmallSet.<F>create(set);
        }
    }

    /**
     * Return a small set containing the members specified in the set
     * argument.  The members of the set will be copied, so subsequent
     * changes to the specified member set will not be reflected in
     * the created set.
     *
     * @param members Set of members of the resulting set.
     * @return Small set consisting of the specified members.
     * @param <F> the type of object stored in the set
     */
    public static <F> SmallSet<F> create(Set<? extends F> members) {
        switch (members.size()) {
        case 0:
            return SmallSet.<F>create();
        case 1:
            return SmallSet.<F>create(members.iterator().next());
        case 2:
            Iterator<? extends F> it = members.iterator();
            F obj1 = it.next();
            F obj2 = it.next();
            return SmallSet.<F>create(obj1,obj2);
        default: return new ListSet<F>(members);
        }
    }

    /**
     * Return a small set containing the member and set of
     * members specified in the arguments.
     *
     * @param member Member to add to result set.
     * @param set Set of members to add to the result set.
     * @return Small set created of the specified member and set of
     * members.
     * @param <F> the type of object stored in the set
     */
    public static <F> SmallSet<F> create(F member, Set<? extends F> set) {
        switch (set.size()) {
        case 0: return SmallSet.<F>create(member);
        case 1: return SmallSet.<F>create(member, set.iterator().next());
        default:
            return set.contains(member)
                ? SmallSet.<F>create(set)
                : new ListSet<F>(member,set);
        }
    }

    /**
     * Return a small set containing all of the members of
     * both specified sets.
     *
     * @param set1 First set of members of result set.
     * @param set2 Second set of members of result set.
     * @return Samll set consisting of the specified members.
     * @param <F> the type of object stored in the set
     */
    public static <F> SmallSet<F> create(Set<? extends F> set1,
                                         Set<? extends F> set2) {
        HashSet<F> union = new HashSet<F>(set1.size() + set2.size());
        union.addAll(set1);
        union.addAll(set2);
        return SmallSet.<F>create(union);
    }

    /**
     * Class defining small empty sets, providing a concrete
     * implementation of {@ #iterator()}, {@link #contains(Object)},
     * and {@link #size()} for {@link AbstractSet}, and {@link
     * #union(SmallSet)} for {@link SmallSet}.
     *
     * @param <F> the type of object stored in the set
     */
    private static class SmallEmptySet<F> extends SmallSet<F> {

        /**
         * Construct a small empty set.
         */
        public SmallEmptySet() {
            /* do nothing */
        }

        /**
         * Returns the size of the set, <code>0</code>.
         *
         * @return Size of the set, <code>0</code>.
         */
        @Override
        public int size() { return 0; }

        /**
         * Returns an iterator over the empty set.
         *
         * @return Iterator over the empty set.
         */
        @Override
        public Iterator<F> iterator() {
            return Iterators.<F>empty();
        }

        /**
         * Returns <code>true</code> if this set contains the
         * specified object.
         *
         * @param o Object to test for membership.
         * @return <code>true</code> if this set contains the
         * specified object.
         */
        @Override
        public boolean contains(Object o) {
            return false;
        }

        /**
         * Returns the union of this set with the specified
         * set, which is just the specified set.
         *
         * @param that Set to union with this set.
         * @retrun The union of this set with the specified set.
         */
        @Override
        public SmallSet<F> union(SmallSet<? extends F> that) {
            return SmallSet.create(that);  // should've never made this <? extends F>!
        }

    }


    /**
     * Class defining singleton empty sets, providing a concrete
     * implementation of {@ #iterator()}, {@link #contains(Object)},
     * and {@link #size()} for {@link AbstractSet}, and {@link
     * #union(SmallSet)} for {@link SmallSet}.
     *
     * @param <F> the type of object stored in the set
     */
    private static class SingletonSet<F> extends SmallSet<F> {

        /**
         * The single member of this set.
         */
        private final F mMember;

        /**
         * Construct a singleton set with the specified member.
         *
         * @param member Member of the resulting set.
         */
        public SingletonSet(F member) {
            mMember = member;
        }

        /**
         * Returns an iterator over this singleton set.
         *
         * @return Iterator over this singleton set.
         */
        @Override
        public Iterator<F> iterator() {
            return Iterators.<F>singleton(mMember);
        }

        /**
         * Returns the size of this set, <code>1</code>.
         *
         * @return The size of this set, <code>1</code>.
         */
        @Override
        public int size() {
            return 1;
        }

        /**
         * Returns <code>true</code> if this set contains
         * the specified object.
         *
         * @param obj Object to test for membership in this set.
         * @return <code>true</code> if this set contains the
         * specified object.
         */
       @Override
       public boolean contains(Object obj) {
            return mMember.equals(obj);
        }

        /**
         * Returns the union of this set with the specified set,
         * without modifying either this set or the specified set.
         *
         * @param that Small set of elements to union with this set.
         * @return The small set consisting of all of the elements in
         * this set and the specified set.
         */
        @Override
        public SmallSet<F> union(SmallSet<? extends F> that) {
            switch (that.size()) {
            case 0: return this;
            case 1: return SmallSet.<F>create(mMember,that.iterator().next());
            case 2: return SmallSet.<F>create(mMember,that);
            default: return SmallSet.<F>create(this,that);
            }
        }

    }

    /**
     * A <code>PairSet</code> is used for small sets consisting
     * of exactly two members.
     *
     * @param <F> the type of object stored in the set
     */
    private static class PairSet<F> extends SmallSet<F> {

        /**
         * First member in this set.
         */
        private final F mMember1;

        /**
         * Second member in this set.
         */
        private final F mMember2;

        /**
         * Returns a small set consisting of the specified members.
         * Members should not be identical to each other, but
         * no checking is done in the pair set itself.
         *
         * @param member1 First member of the set.
         * @param member2 Second member of the set.
         */
        public PairSet(F member1, F member2) {
            mMember1 = member1;
            mMember2 = member2;
        }

        /**
         * Returns the size of this set, <code>2</code>.
         *
         * @return The size of this set, <code>2</code>.
         */
        @Override
        public int size() {
            return 2;
        }

        /**
         * Returns <code>true</code> if this set contains
         * the specified object.
         *
         * @param obj Object to test for membership in this set.
         * @return <code>true</code> if this set contains the
         * specified object.
         */
        @Override
        public boolean contains(Object obj) {
            return obj.equals(mMember1)
                || obj.equals(mMember2);
        }

        /**
         * Returns an iterator over this pair set.
         *
         * @return Iterator over this pair set.
         */
        @Override
        public Iterator<F> iterator() {
            return Iterators.<F>pair(mMember1,mMember2);
        }

        /**
         * Returns the union of this set with the specified set,
         * without modifying either this set or the specified set.
         *
         * @param that Small set of elements to union with this set.
         * @return The small set consisting of all of the elements in
         * this set and the specified set.
         */
        @Override
        public SmallSet<F> union(SmallSet<? extends F> that) {
            switch (that.size()) {
            case 0:
                return this;
            case 1:
                Object member = that.iterator().next();
                if (contains(member)) return this;
                return SmallSet.<F>create(this,that);
            default:
                return SmallSet.<F>create(this,that);
            }
        }
    }

    /**
     * @param <F> the type of object stored in the set
     */
    private static class ListSet<F> extends SmallSet<F> {

        private final F[] mMembers;

        ListSet(Set<? extends F> set) {
            // required for array
            @SuppressWarnings("unchecked")
            F[] tempMembers = (F[]) new Object[set.size()];
            mMembers = set.<F>toArray(tempMembers);
        }
        ListSet(F x, Set<? extends F> set) {
            // required for array
            @SuppressWarnings("unchecked")
            F[] tempMembers = (F[]) new Object[set.size()+1];
            mMembers = set.<F>toArray(tempMembers);
            mMembers[mMembers.length-1] = x;
        }


        @Override
        public SmallSet<F> union(SmallSet<? extends F> that) {
            switch (that.size()) {
            case 0:
                return this;
            case 1:
                F next = that.iterator().next();
                return contains(next)
                    ? this
                    : new ListSet<F>(next,this);
            default:
                HashSet<F> union = new HashSet<F>(size() + that.size());
                for (F member: mMembers)
                    union.add(member);
                union.addAll(that);
                return new ListSet<F>(union);
            }
        }

        @Override
        public Iterator<F> iterator() {
            return Iterators.<F>array(mMembers);
        }

        @Override
        public int size() {
            return mMembers.length;
        }

        @Override
        public boolean contains(Object obj) {
            for (int i = 0; i <mMembers.length; ++i)
                if (obj.equals(mMembers[i]))
                    return true;
            return false;
        }


    }

}
