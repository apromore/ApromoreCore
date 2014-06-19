package com.processconfiguration.cmapper;

// Java 2 Standard Edition classes
import java.io.OutputStream;
import java.net.URI;

// Local classes
import com.processconfiguration.cmap.CMAP;

/**
 * Cmap configuration mapping on the WebDAV service.
 */
interface Cmap {

    /**
     * @return the configuration mapping
     */
    public CMAP getCmap() throws Exception;

    /**
     * @return stream for writing or overwriting this configuration mapping
     */
    public OutputStream getOutputStream() throws Exception;

    /**
     * @return the URI of the configuration mapping, <code>null</code> if the document isn't stored anywhere
     */
    public URI getURI();
}

