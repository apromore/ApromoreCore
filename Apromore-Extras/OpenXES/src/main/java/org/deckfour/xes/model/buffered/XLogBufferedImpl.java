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
