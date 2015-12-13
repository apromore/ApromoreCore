/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package com.processconfiguration.cmapper;

// Java 2 Standard Edition classes
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

// Local classes
import com.processconfiguration.cmap.CMAP;
import org.apromore.filestore.client.FileStoreService;

/**
 * Configuration mapping stored on the WebDAV service.
 */
public class DavCmap implements Cmap {

    private static final Logger LOGGER = Logger.getLogger(DavCmap.class.getCanonicalName());

    private CMAP             cmap;
    private FileStoreService service;
    private URI              uri;

    /**
     * Sole constructor.
     *
     * @param uri  the URI of the configuration mapping on the DAV repository
     * @param service  the proxy to the Apromore-FileStore
     */
    public DavCmap(URI uri, FileStoreService service) throws Exception {
        this.service = service;
        this.uri     = uri;
    }

    /**
     * {@inheritDoc}
     */
    public CMAP getCmap() throws Exception {
        JAXBContext jc = JAXBContext.newInstance("com.processconfiguration.cmap");
        Unmarshaller u = jc.createUnmarshaller();
        URI uri2 = service.getBaseURI().resolve(uri);
        LOGGER.info("Accessing URI " + uri + " resolved as " + uri2);

        this.cmap = (CMAP) u.unmarshal(service.getFile(uri2.toString()));

        return cmap;
    }

    /**
     * {@inheritDoc}
     *
     * You must explicitly call the {@link OutputStream#close} method on the returned output stream,
     * otherwise the content is not guaranteed to be written to the WebDAV service.
     *
     * @return an output stream which buffers its input and flushes it to WebDAV when it is closed
     */
    public OutputStream getOutputStream() throws Exception {
        return new ByteArrayOutputStream() {
            public void close() throws IOException {
                try {
                    URI uri2 = service.getBaseURI().resolve(uri);
                    LOGGER.info("Closing output stream to " + uri + " resolved as " + uri2);
                    service.put(uri2.toString(), toByteArray(), "application/xml");
                    LOGGER.info("Closed output stream to " + uri + " resolved as " + uri2);
                } catch (Exception e) {
                    throw new IOException("Unable to flush buffer to WebDAV service " + uri, e);
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public URI getURI() {
        return uri;
    }
}
