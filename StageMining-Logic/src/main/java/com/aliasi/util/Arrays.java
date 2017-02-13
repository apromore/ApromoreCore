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
import java.util.List;
import java.util.Random;

/**
 * Static utility methods for processing arrays.
 *
 * @author  Bob Carpenter
 * @version 4.0.0
 * @since   LingPipe1.0
 */
public class Arrays {

    /**
     * Forbid instance construction.
     */
    private Arrays() {
        /* no instances */
    }

    /**
     * Returns a copy of the specified array of objects of the
     * specified size.  The runtime type of the array returned will be
     * that of the specified input array.
     *
     * <p>As many of the original elements as will fit
     * in the new array are copied into the returned array.  If the
     * new size is longer, the remaining elements will be null.
     *
     * @param xs Original array.
     * @param newSize Size of returned array.
     * @return New array of specified length with as many elements copied
     * from the original array as will fit.
     * @param <E> type of objects in array.
     */
    public static <E> E[] reallocate(E[] xs, int newSize) {
        @SuppressWarnings("unchecked") // must work because of type system and reflect.Array
        E[] ys 
            = (E[])
            java.lang.reflect.Array
            .newInstance(xs.getClass().getComponentType(), newSize);
        int end = java.lang.Math.min(xs.length,newSize);
        for (int i = 0; i < end; ++i)
            ys[i] = xs[i];
        return ys;
    }

    /**
     * Returns a copy of the specified array of integers of the
     * specified size.  As many of the original elements as will
     * fit in the new array are copied.  If the new size is longer, the
     * remaining elements will be 0.
     *
     * @param xs Original array.
     * @param newSize Length of returned array.
     * @return New array of specified length with as many elements
     * copied from the original array as will fit.
     */
    public static int[] reallocate(int[] xs, int newSize) {
        int[] ys = new int[newSize];
        int end = java.lang.Math.min(xs.length,newSize);
        for (int i = 0; i < end; ++i)
            ys[i] = xs[i];
        return ys;
    }

    /**
     * Reallocates the specified integer array to be 50 percent
     * longer, with a minimum growth in length of one element.  All of
     * the elements of the specified array will be copied into the
     * resulting array and the remaining elements initialized to zero
     * (<code>0</code>).
     *
     * @param xs Array to reallocate.
     * @return Result of reallocation.
     */
    public static int[] reallocate(int[] xs) {
        int len = (xs.length * 3) / 2;
        return reallocate(xs,len == xs.length ? xs.length + 1 : len);
    }

    /**
     * Return the result of adding the specified character to the
     * specified sorted character array.  The original array will
     * be returned if the character is in the array, otherwise a
     * new array will be constructed and returned.
     *
     * <p><i>Warning:</i> No check is done that the incoming character
     * array is in order.
     *
     * @param c Character to add.
     * @param cs Array of characters in sorted order.
     * @return The result of adding the character to the array in the
     * correct order, returning a larger array if necessary.
     */
    public static char[] add(char c, char[] cs) {
        if (java.util.Arrays.binarySearch(cs,c) >= 0)
            return cs;
        char[] result = new char[cs.length+1];
        int i = 0;
        while (i < cs.length && c > cs[i]) {
            result[i] = cs[i];
            ++i;
        }
        result[i] = c;
        ++i;
        while (i < result.length) {
            result[i] = cs[i-1];
            ++i;
        }
        return result;
    }

    /**
     * Return a shallow copy of the specified array that
     * contains the same elements as the specified array.  If
     * the input is <code>null</code>, then <code>null</code>
     * is returned.
     *
     * @param cs Array to copy.
     * @return Shallow copy of array.
     */
    public static char[] copy(char[] cs) {
        if (cs == null) return null;
        char[] cs2 = new char[cs.length];
        for (int i = 0; i < cs.length; ++i)
            cs2[i] = cs[i];
        return cs2;
    }


    /**
     * Converts the specified character sequence to an array
     * of characters.
     *
     * @param cSeq Character sequence to convert.
     * @return Array of characters in sequence.
     */
    public static char[] toArray(CharSequence cSeq) {
        // return cSeq.toString().toCharArray();
        char[] cs = new char[cSeq.length()];
        for (int i = 0; i < cs.length; ++i)
            cs[i] = cSeq.charAt(i);
        return cs;
    }


    /**
     * Returns true if the specified object is an element of the
     * specified array.  Returns <code>false</code> if the specified
     * array is null.
     *
     * @param x Object to test for membership.
     * @param xs Array to test for object.
     * @return <code>true</code> if the specified object is an element
     * of the specified array.
     */
    public static boolean member(Object x, Object[] xs) {
        if (xs == null) return false;
        for (int i = xs.length; --i >= 0; ) {
            if (xs[i] == null) continue;
            if (xs[i].equals(x)) return true;
        }
        return false;
    }

    /**
     * Returns true if the specified character is a member of the
     * specified array.  Returns <code>false</code> if the specified
     * array is null.
     *
     * @param c Character to test for membership.
     * @param cs Array to test for character.
     * @return <code>true</code> if the specified character is an
     * element of the specified array.
     */
    public static boolean member(char c, char[] cs) {
        if (cs == null) return false;
        for (int i = 0; i < cs.length; ++i) {
            if (cs[i] == c) return true;
        }
        return false;
    }

    /**
     * Returns the concatenation of the string representations of the
     * specified objects separated by commas, with the whole
     * surrounded by square brackets and separated by a comma.
     *
     * @param xs Array of which to return a string representation.
     * @return String representation of the specified array.
     */
    public static String arrayToString(Object[] xs) {
        StringBuilder sb = new StringBuilder();
        arrayToStringBuilder(sb,xs);
        return sb.toString();
    }

    /**
     * Appends to the string buffer the concatenation of the string
     * representations of the specified objects separated by commas,
     * with the whole surrounded by square brackets and separated by a
     * comma.
     *
     * @param sb String buffer to which string representation is
     * appended.
     * @param xs Array of which to return a string representation.
     */
    public static void arrayToStringBuilder(StringBuilder sb, Object[] xs) {
        sb.append('[');
        for (int i = 0; i < xs.length; ++i) {
            if (i > 0) sb.append(',');
            sb.append(xs[i]);
        }
        sb.append(']');
    }

    /**
     * Returns the array of characters consisting of the members of
     * the first specified array followed by the specified character.
     *
     * @param cs Characters to start resulting array.
     * @param c Last character in resulting array.
     * @return Array of characters consisting of the characters in the
     * first array followed by the last character.
     * @throws NullPointerException If the array of characters is
     * null.
     */
    public static char[] concatenate(char[] cs, char c) {
        char[] result = new char[cs.length+1];
        for (int i = 0; i < cs.length; ++i)
            result[i] = cs[i];
        result[result.length-1] = c;
        return result;
    }



    /**
     * Returns a new array of strings containing the elements of the
     * first array of strings specified followed by the elements of
     * the second array of strings specified.
     *
     * @param xs First array of strings.
     * @param ys Second array of strings.
     * @return Concatenation of first array of strings followed by the
     * second array of strings.
     */
    public static String[] concatenate(String[] xs, String[] ys) {
        String[] result = new String[xs.length + ys.length];
        System.arraycopy(xs,0,result,0,xs.length);
        System.arraycopy(ys,0,result,xs.length,ys.length);
        return result;
    }



    /**
     * Return <code>true</code> if the specified arrays are
     * the same length and contain the same elements.
     *
     * @param xs First array.
     * @param ys Second array.
     * @return <code>true</code> if the specified arrays are the same
     * length and contain the same elements.
     */
    public static boolean equals(Object[] xs, Object[] ys) {
        if (xs.length != ys.length) return false;
        for (int i = 0; i < xs.length; ++i)
            if (!xs[i].equals(ys[i])) return false;
        return true;
    }

    /**
     * Randomly permutes the elements of the specified array using
     * a freshly generated randomizer.  The
     * resulting array will have the same elements, but arranged into
     * a (possibly) different order.
     *
     * @param xs Array to permute.
     * @param <E> the type of objects in the array being permuted
     */
    public static <E> void permute(E[] xs) {
        permute(xs,new Random());
    }

    /**
     * Randomly permutes the elements of the specified array using the
     * specified randomizer.  The resulting array will have the same
     * elements, but arranged into a (possibly) different order.
     *
     * @param xs Array to permute.
     * @param random Randomizer to use for permuation.
     * @param <E> the type of objects in the array being permuted
     */
    public static <E> void permute(E[] xs, Random random) {
        for (int i = xs.length; --i > 0; ) {
            int pos = random.nextInt(i);
            E temp = xs[pos];
            xs[pos] = xs[i];
            xs[i] = temp;
        }
    }

    /**
     * Randomly permutes the elements of the specified integer array
     * using a newly created randomizer.  The resulting array will
     * have the same elements, but arranged into a (possibly)
     * different order.  The randomizer is created with a call to
     * the nullary constructor {@link java.util.Random#Random()}.
     *
     * @param xs Array to permute.
     */
    public static void permute(int[] xs) {
        permute(xs, new Random());
    }

    /**
     * Randomly permutes the elements of the specified integer
     * array using the specified randomizer.
     *
     * @param xs Array to permute.
     * @param random Randomizer to use for permutations.
     */
    public static void permute(int[] xs, Random random) {
        for (int i = xs.length; --i > 0; ) {
            int pos = random.nextInt(i);
            int temp = xs[pos];
            xs[pos] = xs[i];
            xs[i] = temp;
        }
    }


    /**
     * A length <code>0</code> array of integers.
     */
    public static final int[] EMPTY_INT_ARRAY = new int[] { };


}
