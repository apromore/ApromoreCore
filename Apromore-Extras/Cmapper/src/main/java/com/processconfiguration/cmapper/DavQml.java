/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package com.processconfiguration.cmapper;

// Java 2 Standard Edition classes
import java.net.URI;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

// Local classes
import com.processconfiguration.qml.QMLType;
import org.apromore.filestore.client.FileStoreService;

/**
 * QML questionnaire stored on the WebDAV service.
 */
public class DavQml implements Qml {

    private static final Logger LOGGER = Logger.getLogger(DavQml.class.getCanonicalName());

    private QMLType          qml;
    private FileStoreService service;
    private URI              uri;

    /**
     * Sole constructor.
     *
     * @param uri  the URI of the QML file on the DAV repository; relative URIs are resolved against the base URI of the </var>service</var>
     * @param service  the proxy to the Apromore-FileStore
     */
    public DavQml(URI uri, FileStoreService service) throws Exception {
        this.service = service;
        this.uri     = uri;

        URI uri2 = service.getBaseURI().resolve(uri);
        LOGGER.info("Accessing URI " + uri + " resolved to " + uri2 + " against " + service.getBaseURI());
        JAXBElement qmlElement = (JAXBElement) JAXBContext.newInstance("com.processconfiguration.qml")
                                                          .createUnmarshaller()
                                                          .unmarshal(service.getFile(uri2.toString()));
        this.qml = (QMLType) qmlElement.getValue();
    }

    /**
     * {@inheritDoc}
     */
    public QMLType getQml() throws Exception {
        return qml;
    }

    /**
     * {@inheritDoc}
     */
    public URI getURI() {
        return uri;
    }
}

