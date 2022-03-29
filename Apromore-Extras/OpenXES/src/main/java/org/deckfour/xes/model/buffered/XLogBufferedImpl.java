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
package org.deckfour.xes.model.buffered;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.nikefs2.NikeFS2RandomAccessStorage;
import org.deckfour.xes.nikefs2.NikeFS2StorageProvider;

import java.lang.ref.WeakReference;

public class XLogBufferedImpl {

    /**
     * The number of attributes contained in a buffer.
     */
    private int size = 0;
    /**
     * The random access storage to back the buffer
     * of attributes.
     */
    private NikeFS2RandomAccessStorage storage = null;
    /**
     * Storage provider which is used to allocate new buffer storages.
     */
    private NikeFS2StorageProvider provider = null;

    /**
     * Weak reference to cache in-heap attribute map.
     */
    private WeakReference<XLog> cacheLog = null;

}
