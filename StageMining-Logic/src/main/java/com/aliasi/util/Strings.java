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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import java.text.DecimalFormat;

/**
 * Static utility methods for processing strings, characters and
 * string buffers.
 *
 * @author  Bob Carpenter
 * @version 4.0.1
 * @since   LingPipe1.0
 * @see     java.lang.Character
 * @see     java.lang.String
 * @see     java.lang.StringBuilder
 */
public class Strings {

    /**
     * Forbid instance construction.
     */
    private Strings() {
        /* no instances */
    }

    /**
     * String representing the UTF-8 encoding for characters.
     */
    public static String UTF8 = "UTF-8";

    /**
     * String representing the Latin1 encoding for characters.
     */
    public static String Latin1 = "ISO-8859-1";

    /**
     * String representing the ASCII encoding for characters.
     */
    public static String ASCII = "ASCII";

    /**
     * Return the string that is the reverse of the specified
     * character sequence.
     */
    public static String reverse(CharSequence cs) {
        StringBuilder sb = new StringBuilder(cs.length());
        for (int i = cs.length(); --i >= 0; )
            sb.append(cs.charAt(i));
        return sb.toString();
    }

    /**
     * Returns <code>true</code> if the specified string contains
     * an instance of the specified character.
     *
     * @param s String to check for character.
     * @param c Character.
     * @return <code>true</code> if specified character occurs in
     * specified string.
     */
    public static boolean containsChar(String s, char c) {
        return s.indexOf(c) >= 0;
    }

    /**
     * Returns <code>true</code> if the specified buffer contains
     * only whitespace characters.
     *
     * @param sb String buffer to test for whitespace.
     * @return <code>true</code> if the specified buffer contains only
     * whitespace characters.
     */
    public static boolean allWhitespace(StringBuilder sb) {
        return allWhitespace(sb.toString());
    }


    /**
     * Returns <code>true</code> if the specified string contains
     * only whitespace characters.
     *
     * @param s Stirng to test for whitespace.
     * @return <code>true</code> if the specified string contains only
     * whitespace characters.
     */
    public static boolean allWhitespace(String s) {
        return allWhitespace(s.toCharArray(),0,s.length());
    }

    /**
     * Returns <code>true</code> if the specified range of the
     * specified character array only whitespace characters, as defined for
     * characters by {@link #isWhitespace(char c)}.
     *
     * @param ch Character array to test for whitespace characters in range.
     * @param start Beginning of range to test.
     * @param length Number of characters to test.
     * @return <code>true</code> if the specified string contains only
     * whitespace characters.
     */
    public static boolean allWhitespace(char[] ch, int start, int length) {
        for (int i = start; i < start+length; ++i)
            if (!isWhitespace(ch[i])) return false;
        return true;
    }

    /**
     * Returns true if specified character is a whitespace character.
     * The definition in {@link
     * java.lang.Character#isWhitespace(char)} is extended to include
     * the unicode non-breakable space character (unicode 160).
     *
     * @param c Character to test.
     * @return <code>true</code> if specified character is a
     * whitespace.
     * @see java.lang.Character#isWhitespace(char)
     */
    public static boolean isWhitespace(char c) {
        return Character.isWhitespace(c) || c == NBSP_CHAR;
    }

    /**
     * Appends a whitespace-normalized form of the specified character
     * sequence into the specified string buffer.  Initial and final
     * whitespaces are not appended, and every other maximal sequence
     * of contiguous whitespace is replaced with a single whitespace
     * character.  For instance, <code>&quot; a\tb\n&quot;</code>
     * would append the following characters to <code>&quot;a
     * b&quot;</code>.
     *
     * <P>This command is useful for text inputs for web or GUI
     * applications.
     *
     * @param cs Character sequence whose normalization is appended to
     * the buffer.
     * @param sb String buffer to which the normalized character
     * sequence is appended.
     */
    public static void normalizeWhitespace(CharSequence cs, StringBuilder sb) {
        int i = 0;
        int length = cs.length();
        while (length > 0 && isWhitespace(cs.charAt(length-1)))
            --length;
        while (i < length && isWhitespace(cs.charAt(i)))
            ++i;
        boolean inWhiteSpace = false;
        for ( ; i < length; ++i) {
            char nextChar = cs.charAt(i);
            if (isWhitespace(nextChar)) {
                if (!inWhiteSpace) {
                    sb.append(' ');
                    inWhiteSpace = true;
                }
            } else {
                inWhiteSpace = false;
                sb.append(nextChar);
            }
        }
    }

    /**
     * Returns a whitespace-normalized version of the specified
     * character sequence.  See {@link
     * #normalizeWhitespace(CharSequence,StringBuilder)} for
     * information on the normalization procedure.
     *
     * @param cs Character sequence to normalize.
     * @return Normalized version of character sequence.
     */
    public static String normalizeWhitespace(CharSequence cs) {
        StringBuilder sb = new StringBuilder();
        normalizeWhitespace(cs,sb);
        return sb.toString();
    }

    /**
     * Returns <code>true</code> if all of the characters
     * making up the specified string are digits.
     *
     * @param s String to test.
     * @return <code>true</code> if all of the characters making up
     * the specified string are digits.
     */
    public static boolean allDigits(String s) {
        return allDigits(s.toCharArray(),0,s.length());
    }

    /**
     * Returns <code>true</code> if all of the characters
     * in the specified range are digits.
     *
     * @param cs Underlying characters to test.
     * @param start Index of first character to test.
     * @param length Number of characters to test.
     * @return <code>true</code> if all of the characters making up
     * the specified string are digits.
     */
    public static boolean allDigits(char[] cs, int start, int length) {
        for (int i = 0; i < length; ++i)
            if (!Character.isDigit(cs[i+start])) return false;
        return true;
    }

    /**
     * Returns true if specified character is a punctuation character.
     * Punctuation includes comma, period, exclamation point, question
     * mark, colon and semicolon.  Note that quotes and apostrophes
     * are not considered punctuation by this method.
     *
     * @param c Character to test.
     * @return <code>true</code> if specified character is a
     * whitespace.
     * @see java.lang.Character
     */
    public static boolean isPunctuation(char c) {
        return c == ','
            || c == '.'
            || c == '!'
            || c == '?'
            || c == ':'
            || c == ';'
            ;
    }

    /**
     * Returns the result of concatenating the specified number of
     * copies of the specified string.  Note that there are no spaces
     * inserted between the specified strings in the output.
     *
     * @param s String to concatenate.
     * @param count Number of copies of string to concatenate.
     * @return Specified string concatenated with itself the specified
     * number of times.
     */
    public static String power(String s, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; ++i)
            sb.append(s);
        return sb.toString();
    }

    /**
     * Concatenate the elements of the specified array as strings,
     * separating with the default separator {@link
     * #DEFAULT_SEPARATOR_STRING}.
     *
     * @param xs Array of objects whose string representations are
     * concatenated.
     * @return Concatenation of string representations of specified
     * objects separated by the default separator.
     */
    public static String concatenate(Object[] xs) {
        return concatenate(xs,DEFAULT_SEPARATOR_STRING);
    }

    /**
     * Concatenate the elements of the specified array as strings,
     * separating with the specified string spacer.
     *
     * @param xs Array of objects whose string representations are
     * concatenated.
     * @param spacer String to insert between the string
     * representations.
     * @return Concatenation of string representations of specified
     * objects separated by the specified spacer.
     */
    public static String concatenate(Object[] xs, String spacer) {
        return concatenate(xs,0,spacer);
    }

    /**
     * Concatenate the elements of the specified array as strings,
     * starting at the object at the specified index and continuing
     * through the rest of the string, separating with the specified
     * string spacer.
     *
     * @param xs Array of objects whose string representations are
     * concatenated.
     * @param start Index of first object to include.
     * @param spacer String to insert between the string
     v     * representations.
     * @return Concatenation of string representations of specified
     * objects separated by the specified spacer.
     */
    public static String concatenate(Object[] xs, int start,
                                     String spacer) {
        return concatenate(xs,start,xs.length,spacer);
    }

    /**
     * Concatenate the elements of the specified array as strings,
     * starting at the object at the specified index and continuing
     * through one element before the specified end index, separating
     * with the default spacer {@link #DEFAULT_SEPARATOR_STRING}.
     *
     * @param xs Array of objects whose string representations are
     * concatenated.
     * @param start Index of first object to include.
     * @param end The index of the last element to include plus
     * <code>1</code>.
     * @return Concatenation of string representations of specified
     * objects separated by the specified spacer.
     */
    public static String concatenate(Object[] xs, int start, int end) {
        return concatenate(xs,start,end,DEFAULT_SEPARATOR_STRING);
    }

    /**
     * Concatenate the elements of the specified array as strings,
     * starting at the object at the specified index and continuing
     * through one element before the specified end index, separating
     * with the specified spacer.
     *
     * @param xs Array of objects whose string representations are
     * concatenated.
     * @param start Index of first object to include.
     * @param end The index of the last element to include plus
     * <code>1</code>.
     * @param spacer String to insert between the string
     * representations.
     * @return Concatenation of string representations of specified
     * objects separated by the specified spacer.
     */
    public static String concatenate(Object[] xs, int start, int end,
                                     String spacer) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; ++i) {
            if (i > start) sb.append(spacer);
            sb.append(xs[i]);
        }
        sb.setLength(sb.length());
        return sb.toString();
    }

    /**
     * Appends an ``indentation'' to the specified string buffer,
     * consisting of a newline character and the specified number of
     * space characters to the specified string buffer.
     *
     * @param sb String buffer to indent.
     * @param length Number of spaces to append after a newline to the
     * specified string buffer.
     */
    public static void indent(StringBuilder sb, int length) {
        sb.append(NEWLINE_CHAR);
        padding(sb,length);
    }

    /**
     * Returns a string consisting of the specified number of default
     * separator characters {@link #DEFAULT_SEPARATOR_CHAR}.
     *
     * @param length Number of separator characters in returned
     * string.
     * @return String of specified number of default separator
     * characters.
     */
    public static String padding(int length) {
        StringBuilder sb = new StringBuilder();
        padding(sb,length);
        return sb.toString();
    }

    /**
     * Append the specified number of default separator characters
     * {@link #DEFAULT_SEPARATOR_CHAR} to the specified string buffer.
     *
     * @param sb String buffer to which to append specified number of
     * default separator characters.
     * @param length Number of separator characters to append.
     */
    public static void padding(StringBuilder sb, int length) {
        for (int i = 0; i < length; ++i) sb.append(DEFAULT_SEPARATOR_CHAR);
    }

    /**
     * Return a string representation of a function applied
     * to its arguments.  Arguments will be converted to
     * strings and separated with commas.
     *
     * @param functionName Name of function.
     * @param args Arguments to function.
     * @return String representation of specified function applied to
     * specified arguments.
     */
    public static String functionArgs(String functionName, Object[] args) {
        return functionName + functionArgsList(args);
    }

    /**
     * Returns a string representation of the specified array as a
     * function's argument list.  Each object is converted to a
     * string, and the list of objects is separated by commas, and the
     * whole is surrounded by round parentheses.
     *
     * @param args Objects to represent arguments.
     * @return String representation of argument list.
     */
    public static String functionArgsList(Object[] args) {
        return "(" + concatenate(args,",") + ")";
    }

    /**
     * Returns <code>true</code> if all of the characters in the
     * specified array are lower case letters.
     *
     * @param chars Array of characters to test.
     * @return <code>true</code> if all of the characters in the
     * specified array are lower case letters.
     */
    public static boolean allLowerCase(char[] chars) {
        for (int i = 0; i < chars.length; ++i)
            if (!Character.isLowerCase(chars[i]))
                return false;
        return true;
    }
    /**
     * Returns <code>true</code> if the specified character sequence
     * contains only lowercase letters.  The test is performed by
     * {@link Character#isLowerCase(char)}.  This is the same test as
     * performed by {@link #allLowerCase(char[])}.
     *
     * @param token Token to check.
     * @return <code>true</code> if token is all lower-case.
     */
    public static boolean allLowerCase(CharSequence token) {
        int len = token.length();
        for (int i=0; i < len; i++) {
            if (!Character.isLowerCase(token.charAt(i)))
                return false;
        }
        return true;
    }

    /**
     * Returns <code>true</code> if all of the characters in the
     * specified array are upper case letters.
     *
     * @param chars Array of characters to test.
     * @return <code>true</code> if all of the characters in the
     * specified array are upper case letters.
     */
    public static boolean allUpperCase(char[] chars) {
        for (int i = 0; i < chars.length; ++i)
            if (!Character.isUpperCase(chars[i]))
                return false;
        return true;
    }

    /**
     * Returns <code>true</code> if all of the characters in the
     * specified array are letters.
     *
     * @param chars Array of characters to test.
     * @return <code>true</code> if all of the characters in the
     * specified array are letters.
     */
    public static boolean allLetters(char[] chars) {
        for (int i = 0; i < chars.length; ++i)
            if (!Character.isLetter(chars[i]))
                return false;
        return true;
    }

    /**
     * Returns <code>true</code> if all of the characters in the
     * specified array are punctuation as specified by
     * {@link Strings#isPunctuation(char)}.
     *
     * @param chars Array of characters to test.
     * @return <code>true</code> if all of the characters in the
     * specified array are punctuation.
     */
    public static boolean allPunctuation(char[] chars) {
        for (int i = 0; i < chars.length; ++i)
            if (!Strings.isPunctuation(chars[i]))
                return false;
        return true;
    }

    /**
     * Returns <code>true</code> if all of the characters in the
     * specified string are punctuation as specified by
     * {@link Strings#isPunctuation(char)}.
     *
     * @param token Token string to test.
     * @return <code>true</code> if all of the characters in the
     * specified string are punctuation.
     */
    public static boolean allPunctuation(String token) {
        for (int i = token.length(); --i >= 0; )
            if (!Strings.isPunctuation(token.charAt(i)))
                return false;
        return true;
    }

    /**
     * Returns an array of substrings of the specified string,
     * in order, with divisions before and after any instance
     * of the specified character.  The returned array will always
     * have at least one element.  Elements in the returned array
     * may be empty.  The following examples illustrate this behavior:
     *
     * <br/><br/>
     * <table border="1" cellpadding="5">
     * <tr><td><b>Call</b></td><td><b>Result</b></td></tr>
     * <tr>
     *   <td><code>split("",' ')</code></td>
     *   <td><code>{ "" }</code></td>
     * </tr>
     * <tr>
     *   <td><code>split("a",' ')</code></td>
     *   <td><code>{ "a" }</code></td>
     * </tr>
     * <tr>
     *   <td><code>split("a b",' ')</code></td>
     *   <td><code>{ "a", "b" }</code></td>
     * </tr>
     * <tr>
     *   <td><code>split("aaa bb cccc",' ')</code></td>
     *   <td><code>{ "aaa", "bb", "cccc" }</code></td>
     * </tr>
     * <tr>
     *   <td><code>split(" a",' ')</code></td>
     *   <td><code>{ "", "a" }</code></td>
     * </tr>
     * <tr>
     *   <td><code>split("a ",' ')</code></td>
     *   <td><code>{ "a", "" }</code></td>
     * </tr>
     * <tr>
     *   <td><code>split(" a ",' ')</code></td>
     *   <td><code>{ "", "a", "" }</code></td>
     * </tr>
     * </table>
     *
     * @param s String to split.
     * @param c Character on which to split the string.
     * @return The array of substrings resulting from splitting the
     * specified string on the specified character.
     */
    public static String[] split(String s, char c) {
        char[] cs = s.toCharArray();
        int tokCount = 1;
        for (int i = 0; i < cs.length; ++i)
            if (cs[i] == c) ++tokCount;
        String[] result = new String[tokCount];
        int tokIndex = 0;
        int start = 0;
        for (int end = 0; end < cs.length; ++end) {
            if (cs[end] == c) {
                result[tokIndex] = new String(cs,start,end-start);
                ++tokIndex;
                start = end+1;
            }
        }
        result[tokIndex] = new String(cs,start,cs.length-start);
        return result;
    }


    /**
     * Returns <code>true</code> if none of the characters in the
     * specified array are letters or digits.
     *
     * @param cs Array of characters to test.
     * @return <code>true</code> if none of the characters in the
     * specified array are letters or digits.
     */
    public static boolean allSymbols(char[] cs) {
        for (int i = 0; i < cs.length; ++i)
            if (Character.isLetter(cs[i]) || Character.isDigit(cs[i]))
                return false;
        return true;
    }

    /**
     * Returns <code>true</code> if at least one of the characters in
     * the specified array is a digit.
     *
     * @param chars Array of characters to test.
     * @return <code>true</code> if at least one of the characters in
     * the specified array is a digit.
     */
    public static boolean containsDigits(char[] chars) {
        for (int i = 0; i < chars.length; ++i)
            if (Character.isDigit(chars[i]))
                return true;
        return false;
    }

    /**
     * Returns <code>true</code> if at least one of the characters in
     * the specified array is a letter.
     *
     * @param chars Array of characters to test.
     * @return <code>true</code> if at least one of the characters in
     * the specified array is a letter.
     */
    public static boolean containsLetter(char[] chars) {
        for (int i = 0; i < chars.length; ++i)
            if (Character.isLetter(chars[i]))
                return true;
        return false;
    }


    /**
     * Returns <code>true</code> if the first character in the
     * specified array is an upper case letter and all subsequent
     * characters are lower case letters.
     *
     * @param chars Array of characters to test.
     * @return <code>true</code> if all of the characters in the
     * specified array are lower case letters.
     */
    public static boolean capitalized(char[] chars) {
        if (chars.length == 0) return false;
        if (!Character.isUpperCase(chars[0])) return false;
        for (int i = 1; i < chars.length; ++i)
            if (!Character.isLowerCase(chars[i]))
                return false;
        return true;
    }

    /**
     * Returns a title-cased version of the specified word,
     * which involves capitalizing the first character in
     * the word if it is a letter.
     *
     * @param word The word to convert to title case.
     * @return Title cased version of specified word.
     */
    public static String titleCase(String word) {
        if (word.length() < 1) return word;
        if (!Character.isLetter(word.charAt(0))) return word;
        return Character.toUpperCase(word.charAt(0))
            + word.substring(1);
    }

    /**
     * Returns a hexadecimal string-based representation of the
     * specified byte array.  Each byte is converted using {@link
     * #byteToHex(byte)} and the results are concatenated into
     * the final string representation.  Letter-based digits are
     * lowercase.
     *
     * @param bytes Array of bytes to convert.
     * @return The hexadecimal string-based representation of the
     * specified bytes.
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i)
            sb.append(byteToHex(bytes[i]));
        return sb.toString();
    }

    static String byteToHex(byte b) {
        String result = Integer.toHexString(Math.byteAsUnsigned(b));
        switch (result.length()) {
        case 0: return "00";
        case 1: return "0" + result;
        case 2: return result;
        default: break;
        }
        String msg = "byteToHex(" + b + ")=" + result;
        throw new IllegalArgumentException(msg);
    }


    /**
     * Throws an exception if the start and end plus one indices are not
     * in the range for the specified array of characters.
     *
     * @param cs Array of characters.
     * @param start Index of first character.
     * @param end Index of one past last character.
     * @throws IndexOutOfBoundsException If the specified indices are out of
     * bounds of the specified character array.
     */
    public static void checkArgsStartEnd(char[] cs, int start, int end) {
        if (end < start) {
            String msg = "End must be >= start."
                + " Found start=" + start
                + " end=" + end;
            throw new IndexOutOfBoundsException(msg);
        }
        if (start >= 0 && end <= cs.length) return; // faster check
        if (start < 0 || start >= cs.length) {
            String msg = "Start must be greater than 0 and less than length of array."
                + " Found start=" + start
                + " Array length=" + cs.length;
            throw new IndexOutOfBoundsException(msg);

        }
        if (end < 0 || end > cs.length) {
            String msg = "End must be between 0 and  the length of the array."
                + " Found end=" + end
                + " Array length=" + cs.length;
            throw new IndexOutOfBoundsException(msg);
        }
    }

    /**
     * Returns an array of characters corresponding to the specified
     * character sequence.  The result is a copy, so modifying it will
     * not affect the argument sequence.
     *
     * @param cSeq Character sequence to convert.
     * @return Array of characters from the specified character sequence.
     */
    public static char[] toCharArray(CharSequence cSeq) {
        char[] cs = new char[cSeq.length()];
        for (int i = 0; i < cs.length; ++i)
            cs[i] = cSeq.charAt(i);
        return cs;
    }

    /**
     * Takes a time in nanoseconds and returns an hours, minutes and
     * seconds representation.  See {@link #msToString(long)} for more
     * information on output format.
     *
     * <p>Recall that 1 second = 1,000,000,000 nanoseconds.
     *
     * @param ns Amount of time in nanoseconds.
     */
    public static String nsToString(long ns) {
        return msToString(ns/1000000);
    }


    /**
     * Takes a time in milliseconds and returns an hours, minutes and
     * seconds representation.  Fractional ms times are rounded down.
     * Leading zeros and all-zero slots are removed.  A table of input
     * and output examples follows.
     *
     * <p>Recall that 1 second = 1000 milliseconds.
     *
     * <table border='1' cellpadding='5'>
     * <tr><td><i>Input ms</i></td><td><i>Output String</i></td></tr>
     * <tr><td>0</td><td><code>:00</code></td></tr>
     * <tr><td>999</td><td><code>:00</code></td></tr>
     * <tr><td>1001</td><td><code>:01</code></td></tr>
     * <tr><td>32,000</td><td><code>:32</code></td></tr>
     * <tr><td>61,000</td><td><code>1:01</code></td></tr>
     * <tr><td>11,523,000</td><td><code>3:12:03</code></td></tr>
     * </table>
     *
     * @param ms Time in milliseconds.
     * @return String-based representation of time in hours, minutes
     * and second format.
     */
    public static String msToString(long ms) {
        long totalSecs = ms/1000;
        long hours = (totalSecs / 3600);
        long mins = (totalSecs / 60) % 60;
        long secs = totalSecs % 60;
        String minsString = (mins == 0)
            ? "00"
            : ((mins < 10)
               ? "0" + mins
               : "" + mins);
        String secsString = (secs == 0)
            ? "00"
            : ((secs < 10)
               ? "0" + secs
               : "" + secs);
        if (hours > 0)
            return hours + ":" + minsString + ":" + secsString;
        else if (mins > 0)
            return mins + ":" + secsString;
        else return ":" + secsString;
    }

    /**
     * Return <code>true</code> if the two character sequences have
     * the same length and the same characters.  Recall that equality
     * is not refined in the specification of {@link CharSequence}, but
     * rather inherited from {@link Object#equals(Object)}.
     *
     * The related method {@link #hashCode(CharSequence)} returns
     * hash codes consistent with this notion of equality.
     *
     * @param cs1 First character sequence.
     * @param cs2 Second character sequence.
     * @return <code>true</code> if the character sequences yield
     * the same strings.
     */
    public static boolean equalCharSequence(CharSequence cs1,
                                            CharSequence cs2) {
        if (cs1 == cs2) return true;
        int len = cs1.length();
        if (len != cs2.length()) return false;
        for (int i = 0; i < len; ++i)
            if (cs1.charAt(i) != cs2.charAt(i)) return false;
        return true;
    }


    /**
     * Returns a hash code for a character sequence that is equivalent
     * to the hash code generated for a its string yield.  Recall that
     * the interface {@link CharSequence} does not refine the definition
     * of equality beyond that of {@link Object#equals(Object)}.
     *
     * <P>The return result is the same as would be produced by:
     *
     * <pre>
     *    hashCode(cSeq) = cSeq.toString().hashCode()</pre>
     *
     * Recall that the {@link CharSequence} interface requires its
     * {@link CharSequence#toString()} to return a string
     * corresponding to its characters as returned by
     * <code>charAt(0),...,charAt(length()-1)</code>.  This value
     * can be defined directly by inspecting the hash code for strings:
     *
     * <pre>
     *      int h = 0;
     *      for (int i = 0; i < cSeq.length(); ++i)
     *          h = 31*h + cSeq.charAt(i);
     *      return h;</pre>
     *
     * @param cSeq The character sequence.
     * @return The hash code for the specified character sequence.
     */
    public static int hashCode(CharSequence cSeq) {
        if (cSeq instanceof String) return cSeq.hashCode();
        int h = 0;
        for (int i = 0; i < cSeq.length(); ++i)
            h = 31*h + cSeq.charAt(i);
        return h;
    }

    /**
     * Returns the equivalent de-accented character for characters in
     * the Latin-1 (ISO-8859-1) range (0000-00FF).  Characters not in
     * the Latin-1 range are returned as-is.
     *
     * Note that Latin-1 is a superset of ASCII, and the unsigned byte
     * encoding of Latin-1 characters (ISO-8859-1) provides the same
     * code points as Unicode for characters.
     *
     * <p>The <code>unicode.org</code> site supplies a complete <a
     * href="http://unicode.org/charts/PDF/U0080.pdf">Latin-1
     * Supplement</code>, listing the code points for each character.
     *
     * @param c Character to de-accent.
     * @return Equivalent character without accent.
     */
    public static char deAccentLatin1(char c) {
        switch (c) {
        case '\u00C0': return 'A';
        case '\u00C1': return 'A';
        case '\u00C2': return 'A';
        case '\u00C3': return 'A';
        case '\u00C4': return 'A';
        case '\u00C5': return 'A';
        case '\u00C6': return 'A';  // capital AE ligature
        case '\u00C7': return 'C';
        case '\u00C8': return 'E';
        case '\u00C9': return 'E';
        case '\u00CA': return 'E';
        case '\u00CB': return 'E';
        case '\u00CC': return 'I';
        case '\u00CD': return 'I';
        case '\u00CE': return 'I';
        case '\u00CF': return 'I';

        case '\u00D0': return 'D';
        case '\u00D1': return 'N';
        case '\u00D2': return 'O';
        case '\u00D3': return 'O';
        case '\u00D4': return 'O';
        case '\u00D5': return 'O';
        case '\u00D6': return 'O';
        case '\u00D8': return 'O';
        case '\u00D9': return 'U';
        case '\u00DA': return 'U';
        case '\u00DB': return 'U';
        case '\u00DC': return 'U';
        case '\u00DD': return 'Y';
        case '\u00DE': return 'P'; // runic letter thorn
        case '\u00DF': return 's'; // upper case is SS

        case '\u00E0': return 'a';
        case '\u00E1': return 'a';
        case '\u00E2': return 'a';
        case '\u00E3': return 'a';
        case '\u00E4': return 'a';
        case '\u00E5': return 'a';
        case '\u00E6': return 'a'; // ae ligature
        case '\u00E7': return 'c';
        case '\u00E8': return 'e';
        case '\u00E9': return 'e';
        case '\u00EA': return 'e';
        case '\u00EB': return 'e';
        case '\u00EC': return 'i';
        case '\u00ED': return 'i';
        case '\u00EE': return 'i';
        case '\u00EF': return 'i';

        case '\u00F0': return 'd';
        case '\u00F1': return 'n';
        case '\u00F2': return 'o';
        case '\u00F3': return 'o';
        case '\u00F4': return 'o';
        case '\u00F5': return 'o';
        case '\u00F6': return 'o';
        case '\u00F8': return 'o';
        case '\u00F9': return 'u';
        case '\u00FA': return 'u';
        case '\u00FB': return 'u';
        case '\u00FC': return 'u';
        case '\u00FD': return 'y';
        case '\u00FE': return 'p';  // runic letter thorn
        case '\u00FF': return 'y';

        default: return c;
        }
    }

    /**
     * Returns the string constructed from the specified character
     * sequence by deaccenting each of its characters.  See {@link
     * #deAccentLatin1(char)} for details of the de-accenting.
     *
     * @param cSeq Character sequence to de accent.
     * @return De-accented version of input.
     */
    public static String deAccentLatin1(CharSequence cSeq) {
        char[] cs = new char[cSeq.length()];
        for (int i = 0; i < cs.length; ++i)
            cs[i] = deAccentLatin1(cSeq.charAt(i));
        return new String(cs);
    }

    /**
     * Returns the length of the longest shared prefix of the two
     * input strings.
     *
     * @param a First string.
     * @param b Second string.
     * @return The length of the longest shared prefix of the two
     * strings.
     */
    public static int sharedPrefixLength(String a, String b) {
        int end = java.lang.Math.min(a.length(),b.length());
        for (int i = 0; i < end; ++i) 
            if (a.charAt(i) != b.charAt(i))
                return i;
        return end;
    }

    /**
     * Returns {@code true} if the specified character sequence is a
     * valid sequence of UTF-16 {@code char} values.  A sequence is
     * legal if each high surrogate {@code char} value is followed by
     * a low surrogate value (as defined by {@link
     * Character#isHighSurrogate(char)} and {@link
     * Character#isLowSurrogate(char)}).
     *
     * <p>This method does <b>not</b> check to see if the sequence of
     * code points defined by the UTF-16 consists only of code points
     * defined in the latest Unicode standard.  The method only tests
     * the validity of the UTF-16 encoding sequence.
     * 
     * @param cs Character sequence to test.
     * @return {@code true} if the sequence of characters is
     * legal in UTF-16.
     */
    public static boolean isLegalUtf16(CharSequence cs) {
        for (int i = 0; i < cs.length(); ++i) {
            char high = cs.charAt(i);
            if (Character.isLowSurrogate(high))
                return false;
            if (!Character.isHighSurrogate(high))
                continue;
            ++i;
            if (i >= cs.length())
                return false;
            char low = cs.charAt(i);
            if (!Character.isLowSurrogate(low))
                return false;
            int codePoint = Character.toCodePoint(high,low);
            if (!Character.isValidCodePoint(codePoint))
                return false;
        }
        return true;
    }


    /**
     * Return a displayable version of the character sequence,
     * followed by integer positions at various powers of 10.
     * For instance, for the input string \code{John ran home.}, the
     * output is
     *
     * <blockquote><pre>
     * John ran home.
     * 01234567890123
     * 0         1
     * </pre></blockquote>
     *
     * This allows easy access to the index of positions in the
     * string.
     *
     * @param in Input sequence to annotate.
     * @return The input sequence followed by indexing positions.
     */
    public static String textPositions(CharSequence in) {
        StringBuilder sb = new StringBuilder();
        sb.append(in);
        for (int base = 1; base <= in.length(); base *= 10) {
            sb.append('\n');
            for (int i = 0; i < in.length(); ++i)
                sb.append(i % base == 0 
                          ? Integer.toString((i/base)%10)
                          : " ");
        }
        return sb.toString();
    }

    /**
     * The non-breakable space character.
     */
    public static char NBSP_CHAR = (char)160;

    /**
     * The newline character.
     */
    public static char NEWLINE_CHAR = '\n';

    /**
     * The default separator character, a single space.
     */
    public static char DEFAULT_SEPARATOR_CHAR = ' ';

    /**
     * The default separator string.  The string is length
     * <code>1</code>, consisting of the default separator character
     * {@link #DEFAULT_SEPARATOR_CHAR}.
     */
    public static String DEFAULT_SEPARATOR_STRING
        = String.valueOf(DEFAULT_SEPARATOR_CHAR);

    /**
     * A string consisting of a single space.
     */
    public static final String SINGLE_SPACE_STRING = " ";

    /**
     * The empty string.
     */
    public static final String EMPTY_STRING = "";

    /**
     * The zero-length character array.  
     */
    public static final char[] EMPTY_CHAR_ARRAY = new char[0];

    /**
     * The zero-length string array.
     */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];


    /**
     * The zero-length two-dimensional array of strings.
     */
    public static final String[][] EMPTY_STRING_2D_ARRAY = new String[0][];

}
