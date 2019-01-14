/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

package org.apromore.filestore.webdav.fromcatalina;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

/**
 * XMLWriter helper class.
 * 
 * @author <a href="mailto:remm@apache.org">Remy Maucherat</a>
 */
public class XMLWriter {

    // -------------------------------------------------------------- Constants

    /**
     * Opening tag.
     */
    public static final int OPENING = 0;

    /**
     * Closing tag.
     */
    public static final int CLOSING = 1;

    /**
     * Element with no content.
     */
    public static final int NO_CONTENT = 2;

    // ----------------------------------------------------- Instance Variables

    /**
     * Buffer.
     */
    protected StringBuffer _buffer = new StringBuffer();

    /**
     * Writer.
     */
    protected Writer _writer = null;

    /**
     * Namespaces to be declared in the root element
     */
    protected Map<String, String> _namespaces;

    /**
     * Is true until the root element is written
     */
    protected boolean _isRootElement = true;

    // ----------------------------------------------------------- Constructors

    /**
     * Constructor.
     */
    public XMLWriter(Map<String, String> namespaces) {
        _namespaces = namespaces;
    }

    /**
     * Constructor.
     */
    public XMLWriter(Writer writer, Map<String, String> namespaces) {
        _writer = writer;
        _namespaces = namespaces;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Retrieve generated XML.
     * 
     * @return String containing the generated XML
     */
    @Override
    public String toString() {
        return _buffer.toString();
    }

    /**
     * Write property to the XML.
     * 
     * @param name
     *      Property name
     * @param value
     *      Property value
     */
    public void writeProperty(String name, String value) {
        writeElement(name, OPENING);
        _buffer.append(value);
        writeElement(name, CLOSING);
    }

    /**
     * Write property to the XML.
     * 
     * @param name
     *      Property name
     */
    public void writeProperty(String name) {
        writeElement(name, NO_CONTENT);
    }

    /**
     * Write an element.
     * 
     * @param name
     *      Element name
     * @param type
     *      Element type
     */
    public void writeElement(String name, int type) {
        StringBuffer nsdecl = new StringBuffer();

        if (_isRootElement) {
            for (Iterator<String> iter = _namespaces.keySet().iterator(); iter
                    .hasNext();) {
                String fullName = (String) iter.next();
                String abbrev = (String) _namespaces.get(fullName);
                nsdecl.append(" xmlns:").append(abbrev).append("=\"").append(
                        fullName).append("\"");
            }
            _isRootElement = false;
        }

        int pos = name.lastIndexOf(':');
        if (pos >= 0) {
            // lookup prefix for namespace
            String fullns = name.substring(0, pos);
            String prefix = (String) _namespaces.get(fullns);
            if (prefix == null) {
                // there is no prefix for this namespace
                name = name.substring(pos + 1);
                nsdecl.append(" xmlns=\"").append(fullns).append("\"");
            } else {
                // there is a prefix
                name = prefix + ":" + name.substring(pos + 1);
            }
        } else {
            throw new IllegalArgumentException(
                    "All XML elements must have a namespace");
        }

        switch (type) {
        case OPENING:
            _buffer.append("<");
            _buffer.append(name);
            _buffer.append( nsdecl);
            _buffer.append( ">");
            break;
        case CLOSING:
            _buffer.append("</");
            _buffer.append( name);
            _buffer.append( ">\n");
            break;
        case NO_CONTENT:
        default:
            _buffer.append("<");
            _buffer.append( name);
            _buffer.append( nsdecl);
            _buffer.append( "/>");
            break;
        }
    }

    /**
     * Write text.
     * 
     * @param text
     *      Text to append
     */
    public void writeText(String text) {
        _buffer.append(text);
    }

    /**
     * Write data.
     * 
     * @param data
     *      Data to append
     */
    public void writeData(String data) {
        _buffer.append("<![CDATA[");
        _buffer.append( data);
        _buffer.append( "]]>");
    }

    /**
     * Write XML Header.
     */
    public void writeXMLHeader() {
        _buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
    }

    /**
     * Send data and reinitializes buffer.
     */
    public void sendData() throws IOException {
        if (_writer != null) {
            _writer.write(_buffer.toString());
            _writer.flush();
            _buffer = new StringBuffer();
        }
    }

}
