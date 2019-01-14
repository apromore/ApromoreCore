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

import java.io.IOException;

/**
 * Static utility methods for handling exceptions.
 *
 * @author Bob Carpenter
 * @version 3.8.1
 * @since   LingPipe2.0
 */
public class Exceptions {

    // disallow instances
    private Exceptions() {
        /* no instances */
    }

    /**
     * Convert the specified throwable to an I/O exception.  The
     * returned exception's message consists of the specified
     * message and the throwable's message and class name.
     * The stack trace of the returned exception will be identical
     * to that of the specified throwable.
     *
     * @param msg Message to include in result's message.
     * @param t Throwable to use for message and stack trace.
     * @return Exception constructed from inputs.
     */
    public static IOException toIO(String msg, Throwable t) {

        IOException e = new IOException(message(msg,t));
        copyStack(t,e);
        return e;
    }

    /**
     * Convert the specified throwable to an illegal argument
     * exception.  The returned exception's message consists of the
     * specified message and the throwable's message and class name.
     * The stack trace of the returned exception will be identical to
     * that of the specified throwable.
     *
     * @param msg Message to include in result's message.
     * @param t Throwable to use for message and stack trace.
     * @return Exception constructed from inputs.
     */
    public static IllegalArgumentException 
        toIllegalArgument(String msg, Throwable t) {

        IllegalArgumentException e
            = new IllegalArgumentException(message(msg,t));
        copyStack(t,e);
        return e;
    }

    /**
     * Throws an illegal argument exception if the specified value is
     * not finite and non-negative, using the specified variable name
     * in the report.  Note that {@code Double.NaN} is not considered
     * finite.
     *
     * @param name Name of variable to report.
     * @param value Value to test.
     * @throws IllegalArgumentException If the value is not finite and
     * non-negative.
     */
    public static void finiteNonNegative(String name, double value) {
        if (Double.isNaN(value)
            || Double.isInfinite(value)
            || value < 0.0) {
            String msg = name + " must be finite and non-negative."
                + " Found " + name + "=" + value;
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Throws an illegal argument exception if the specified value is
     * not finite.  Note that {@code Double.NaN} is not considered
     * finite.
     *
     * @param name Name of variable to report.
     * @param value Value to test.
     * @throws IllegalArgumentException If the value is not finite.
     */
    public static void finite(String name, double value) {
        if (Double.isNaN(value)
            || Double.isInfinite(value)) {
            String msg = name + " must be finite."
                + " Found " + name + "=" + value;
            throw new IllegalArgumentException(msg);
        }
    }

    private static String message(String msg, Throwable t) {
        return msg
            + " Rethrown."
            + " Original throwable class=" + t.getClass().toString()
            + " Original message=" + t.getMessage();
    }

    private static void copyStack(Throwable from, Throwable to) {
        to.setStackTrace(from.getStackTrace());
    }

}
