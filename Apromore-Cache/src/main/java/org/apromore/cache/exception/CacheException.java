/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.cache.exception;

/**
 * Root exception for all Apromore runtime exceptions.  This class is used as the root instead
 * of {@link java.lang.SecurityException} to remove the potential for conflicts;  many other
 * frameworks and products (such as J2EE containers) perform special operations when
 * encountering {@link java.lang.SecurityException}.
 *
 * @since 7.10
 */
public class CacheException extends RuntimeException {

    /**
     * Creates a new <code>CacheException</code>.
     */
    public CacheException() {
        super();
    }

    /**
     * Creates a new <code>CacheException</code>.
     *
     * @param message the reason for the exception.
     */
    public CacheException(String message) {
        super(message);
    }

    /**
     * Creates a new <code>CacheException</code>.
     *
     * @param cause the underlying cause of the exception.
     */
    public CacheException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new <code>CacheException</code>.
     *
     * @param message the reason for the exception.
     * @param cause   the underlying cause of the exception.
     */
    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }
}
