/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.ls.LSInput;

// Third party packages
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * Test suite for {@link JarLSResourceResolver}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class JarLSResourceResolverUnitTest {

    /**
     * Test {@link JarLSResourceResolver#resolveResource} on <code>DC.xsd</code>.
     * This is an XSD file without imports of includes.
     */ 
    @Test public final void testResolveResourceDC() throws Exception {
        LSInput in = new JarLSResourceResolver().resolveResource(null, null, null, "DC.xsd", null);

        // Try parsing the stream as an XSD
        SchemaFactory schemaFactory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
        schemaFactory.newSchema(new StreamSource(in.getCharacterStream()));
    }

    /**
     * Test {@link JarLSResourceResolver#resolveResource} on <code>DI.xsd</code>.
     * This is an XSD file which includes DC.xsd, so it demonstrates that {@link SchemaFactory#setResourceResolver} is working.
     */ 
    @Test public final void testResolveResourceDI() throws Exception {
        JarLSResourceResolver resolver = new JarLSResourceResolver();
        LSInput in = resolver.resolveResource(null, null, null, "DI.xsd", null);

        // Try parsing the stream as an XSD
        SchemaFactory schemaFactory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver(resolver);
        schemaFactory.newSchema(new StreamSource(in.getCharacterStream()));
    }
}
