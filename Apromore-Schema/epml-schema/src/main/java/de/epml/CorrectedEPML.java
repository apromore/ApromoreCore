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

package de.epml;

import java.io.ByteArrayOutputStream;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Correct common EPML errors.
 *
 * Because the EPML 2.0 XML schema has a bug which prevents validation, EPML documents in practice have
 * a range of common deviations from the schema.  As long as an EPML document is at least well-formed XML,
 * the XSLT transform <code>preprocess-epml.xsl</code> corrects the following errors:
 *
 * <ul>
 * <li>If the <code>&lt;epml&gt;</code> element is not namespaced, a namespace declaration is added.</li> 
 * <li>If no <code>&lt;coordinates&gt;</code> element is present, one is added.</li>
 * <li>If EPCs appear outside a <code>&lt;directory&gt;</code> element, a top-level directory is added.</li>
 * <li>An <code>id</code> attribute occurring on an <code>&lt;epc&gt;</code> element is changed to an
 *     <code>epcId</code>.</li>
 * <li>If an <code>&lt;arc&gt;</code> has the same <code>id</code> attribute as another EPC component, it
 *     is assigned a new unique </code>id</code> number.  The assumption is that arcs are never referenced
 *     by other components.</li>
 * </ul>
 * 
 * The corrected EPML is intended to be schema valid with respect to the (corrected) EPML 2.0 XML schema,
 * but this is neither guaranteed nor confirmed by this class.  Validation may be performed separately
 * upon the corrected document.
 *
 * Note that even if an EPML document is not schema-valid, it may still be acceptable input to JAXB.
 */
public class CorrectedEPML {

    /**
     * Compiled version of <code>xsd/preprocess-epml.xsl</code> which implements the various corrections.
     */
    private Transformer transformer = null;

    /**
     * Buffer containing the transformed EPML document.
     */
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    /**
     * @param epmlSource  an EPML-formatted stream
     * @param TransformerException if unable to transform the <var>epmlSource</var>
     */
    public CorrectedEPML(Source epmlSource) throws TransformerException {

        // Lazily obtain the transformer for preprocessing EPML
        if (transformer == null) {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer(
                new StreamSource(getClass().getClassLoader().getResourceAsStream("xsd/preprocess-epml.xsl"))
            );
        }
        assert transformer != null;

        // Preprocess the native EPML input stream
        transformer.transform(epmlSource, new StreamResult(buffer));
    }

    /**
     * @return the corrected EPML document as a byte array.
     */
    public byte[] toByteArray() {
        return buffer.toByteArray();
    }

    /**
     * @return the corrected EPML document as a {@link String}.
     */
    public String toString() {
        return buffer.toString();
    }
}
