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

import java.util.Set;
import java.util.HashSet;

/**
 * The {@code CollectionUtils} class provides static utility methods
 * for dealing with Java collections.
 *
 * @author Bob Carpenter
 * @version 4.0.1
 * @since LingPipe4.0.1
 */
public class CollectionUtils {

    private CollectionUtils() {
        /* no instances */
    }

    /**
     * Return the hash set consisting of adding the specified argument
     * to a newly constructed set.
     *
     * @param es Array of objects to add.
     * @return Hash set containing the argument elements.
     */
    public static <E> HashSet<E> asSet(E... es) {
        HashSet<E> result = new HashSet<E>();
        for (E e : es)
            result.add(e);
        return result;
    }

    

}