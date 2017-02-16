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

/**
 * A <code>Factory</code> provides a generic interface for object
 * creation of a specified type.  The interface specifies a single
 * method, {@link #create()}, which returns an object of the factory's
 * type.
 *
 * @author  Bob Carpenter
 * @version 3.8
 * @since   LingPipe2.0
 * @param <E> the type of objects produced by the factory
 */
public interface Factory<E> {

    /**
     * Return an instance created by this factory.
     *
     * @return An instance.
     */
    public E create();

}
