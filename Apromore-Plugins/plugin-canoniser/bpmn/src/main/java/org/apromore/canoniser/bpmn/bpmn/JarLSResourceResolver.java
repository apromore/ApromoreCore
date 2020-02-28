/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

// Third party packages
import org.apache.commons.io.input.BOMInputStream;

/**
 * During XML schema parsing, resolve imports and includes by loading resources in the Java class path, rather than loading them from URLs.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
class JarLSResourceResolver implements LSResourceResolver {

    /** {@inheritDoc} */
    @Override
    public LSInput resolveResource(final String type, final String namespaceURI, final String publicId, final String systemId, final String baseURI) {
        return new LSInput() {

            /** @return a byte stream from the <code>xsd/</code> directory within the classpath */
            @Override
            public InputStream getByteStream() {
                return getClass().getClassLoader().getResourceAsStream("xsd/" + systemId);
            }

            /** @return a character stream in the default platform encoding, with any byte-order-mark stripped. */
            @Override
            public Reader getCharacterStream() {
                return new InputStreamReader(new BOMInputStream(getByteStream(), false));
            }

            // Cursory implementations of the remainder of the LSInput interface

            @Override public String getBaseURI() { return null; }
            @Override public boolean getCertifiedText() { return false; }
            @Override public String getEncoding() { return null; }
            @Override public String getPublicId() { return null; }
            @Override public String getStringData() { return null;  /* throw new UnsupportedOperationException(systemId); */ }
            @Override public String getSystemId() { return null;  /* return systemId; */ }
            @Override public void setBaseURI(final String baseURI) { throw new UnsupportedOperationException(baseURI); }
            @Override public void setByteStream(final InputStream in) { throw new UnsupportedOperationException(); }
            @Override public void setCertifiedText(final boolean certifiedText) { throw new UnsupportedOperationException(); }
            @Override public void setCharacterStream(final Reader reader) { throw new UnsupportedOperationException(); }
            @Override public void setEncoding(final String encoding) { throw new UnsupportedOperationException(encoding); }
            @Override public void setPublicId(final String publicId) { throw new UnsupportedOperationException(publicId); }
            @Override public void setStringData(final String stringData) { throw new UnsupportedOperationException(stringData); }
            @Override public void setSystemId(final String systemId) { throw new UnsupportedOperationException(systemId); }
        };
    }
}
