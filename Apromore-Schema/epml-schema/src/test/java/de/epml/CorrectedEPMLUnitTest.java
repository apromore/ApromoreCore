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

package de.epml;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import static org.junit.Assert.assertEquals;

/**
 * Test suite for {@link CorrectedEPML}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CorrectedEPMLUnitTest {

    // Utilities for writing test cases

    /**
     * Fetch test data from the classpath.
     *
     * @param fileName  the filename of the test data
     * @return the test data
     */
    private InputStream getResourceAsStream(String fileName) {
        InputStream in = getClass().getClassLoader().getResourceAsStream(fileName);
        if (in == null) {
             throw new Error("Missing test data: " + fileName);
        }
        return in;
    }

    /**
     * Fetch test data from the classpath.
     *
     * @param fileName  the filename of the test data
     * @return the test data
     * @throws TransformerException if unable to buffer test data
     */
    private String getResourceAsString(String fileName) throws TransformerException {
        ByteArrayOutputStream expected = new ByteArrayOutputStream();
        TransformerFactory.newInstance().newTransformer().transform(
            new StreamSource(getResourceAsStream(fileName)),
            new StreamResult(expected)
        );
        return expected.toString();
    }

    /**
     * Correct an input EPML and check whether it matched the expected output.
     *
     * This also sanity checks that the <code>output</code> is schema-valid.
     *
     * @param input  the input EPML
     * @param output  the expected corrected EPML
     * @throws JAXBException if unable to unmarshal <code>output</code> as a JAXB object model
     * @throws SAXException if <code>output</code> is not well-formed XML
     * @throws TransformerException if unable to buffer test data
     */
    private void assertCorrected(final String output, final String input) throws IOException, SAXException, TransformerException {
        String expected = getResourceAsString(output);

        // Validate that the expected EPML is valid according to the (corrected) EPML 2.0 schema
        Validator validator = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI)
                                           .newSchema(new StreamSource(getResourceAsStream("xsd/EPML_2.0.xsd_corrected")))
                                           .newValidator();
        validator.setErrorHandler(new ErrorHandler() {
            public void error(SAXParseException e)      { throw new Error("Test data " + output + " invalid", e); }
            public void fatalError(SAXParseException e) { throw new Error("Test data " + output + " fatally invalid", e); }
            public void warning(SAXParseException e)    { /* ignore */ }
        });
        validator.validate(new StreamSource(new StringReader(expected)));

        // Perform the actual test assertion
        CorrectedEPML correctedEPML = new CorrectedEPML(new StreamSource(getResourceAsStream(input)));
        assertEquals(expected.toString(), correctedEPML.toString());

        // Correction is idempotent: once something is correct, correcting it again should cause no changes
        CorrectedEPML recorrectedEPML = new CorrectedEPML(new StreamSource(getResourceAsStream(output)));
        assertEquals(expected.toString(), recorrectedEPML.toString());
    }

    // Test cases

    /**
     * Correct EPML should be passed through untouched.
     *
     * Events without arcs.
     */
    @Test public final void test00correct() throws Exception {
        assertCorrected("00correct.epml", "00correct.epml");
    }

    /**
     * If the top-level <code>&lt;epml&gt;</code> element isn't namespaced, make it so.
     */
    @Test public final void test01noNamespace() throws Exception {
        assertCorrected("00correct.epml", "01no-namespace.epml");
    }

    /**
     * If <code>&lt;coordinates&gt;</code> are missing, add one.
     */
    @Test public final void test02noCoordinates() throws Exception {
        assertCorrected("02no-coordinates-corrected.epml", "02no-coordinates.epml");
    }

    /**
     * If EPCs occur outside any <code>&lt;directory&gt;</code>, add one.
     */
    @Test public final void test03noDirectory() throws Exception {
        assertCorrected("03no-directory-corrected.epml", "03no-directory.epml");
    }

    /**
     * Change <code>epc/@id</code> to <code>epc/@epcId</code>.
     */
    @Test public final void test04epcWithoutEpcId() throws Exception {
        assertCorrected("00correct.epml", "04epcWithoutEpcId.epml");
    }

    /**
     * Correct EPML should be passed through untouched.
     *
     * Technically this model is incorrect since the events don't alternate with functions, but
     * the schema isn't expected to police that.
     */
    @Test public final void test10correct() throws Exception {
        assertCorrected("10correct.epml", "10correct.epml");
    }

    /**
     * Generate new <code>arc/@id</code> in case of clashes.
     */
    @Test public final void test11arcIdClashes() throws Exception {
        assertCorrected("11arc-id-clashes-corrected.epml", "11arc-id-clashes.epml");
    }

    /**
     * Zero <code>epc/@epcId</code> needs renumbering.
     */
    @Test public final void test12zeroEpcId() throws Exception {
        assertCorrected("12zero-epcId-corrected.epml", "12zero-epcId.epml");
    }

    /**
     * Zero <code>event/@id</code> needs renumbering.
     */
    @Test public final void test13zeroEventId() throws Exception {
        assertCorrected("13zero-event-id-corrected.epml", "13zero-event-id.epml");
    }

    /**
     * Zero <code>arc/@id</code> needs renumbering.
     */
    @Test public final void test14zeroArcId() throws Exception {
        assertCorrected("14zero-arc-id-corrected.epml", "14zero-arc-id.epml");
    }

    /**
     * Correct EPML should be passed through untouched.
     *
     * Empty EPC.
     */
    @Test public final void test20correct() throws Exception {
        assertCorrected("20correct.epml", "20correct.epml");
    }

    /**
     * Interacting renumberings of epc, event and arc identifiers.
     */
    @Test public final void test21interactingIdRenumberings() throws Exception {
        assertCorrected("21interacting-id-renumberings-corrected.epml", "21interacting-id-renumberings.epml");
    }
}
