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

package org.apromore.canoniser.yawl.internal.utils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Used to add Namespace to
 * 
 * Implementation like here: http://stackoverflow.com/questions/277502/jaxb-how-to-ignore-namespace-during-unmarshalling-xml-document
 * 
 */
public class NamespaceFilter extends XMLFilterImpl {

    private String usedNamespaceUri;
    private final boolean addNamespace;

    // State variable
    private boolean addedNamespace = false;

    public NamespaceFilter(final String namespaceUri, final boolean addNamespace) {
        super();

        if (addNamespace) {
            this.usedNamespaceUri = namespaceUri;
        } else {
            this.usedNamespaceUri = "";
        }
        this.addNamespace = addNamespace;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.XMLFilterImpl#startDocument()
     */
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        if (addNamespace) {
            startControlledPrefixMapping();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.XMLFilterImpl#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String arg0, final String arg1, final String arg2, final Attributes arg3) throws SAXException {

        super.startElement(this.usedNamespaceUri, arg1, arg2, arg3);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.XMLFilterImpl#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(final String arg0, final String arg1, final String arg2) throws SAXException {

        super.endElement(this.usedNamespaceUri, arg1, arg2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.XMLFilterImpl#startPrefixMapping(java.lang.String, java.lang.String)
     */
    @Override
    public void startPrefixMapping(final String prefix, final String url) throws SAXException {

        if (addNamespace) {
            this.startControlledPrefixMapping();
        }
        // Else remove the namespace, i.e. don´t call startPrefixMapping for parent!
    }

    private void startControlledPrefixMapping() throws SAXException {

        if (this.addNamespace && !this.addedNamespace) {
            // We should add namespace since it is set and has not yet been done.
            super.startPrefixMapping("", this.usedNamespaceUri);

            // Make sure we dont do it twice
            this.addedNamespace = true;
        }
    }

}
