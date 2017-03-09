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

import java.util.Comparator;

/**
 * The <code>Scored</code> interface should be implemented by objects
 * that return a double-valued score.  There is a simple score
 * comparator, and scored objects are the natural wrappers of other
 * objects in priority queues.
 *
 * @author  Bob Carpenter
 * @version 4.0.0
 * @since   LingPipe2.0
 */
public interface Scored {

    /**
     * Returns the score for this object.
     *
     * @return The score for this object.
     */
    public double score();



}
