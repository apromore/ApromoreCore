package de.epml;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.junit.Ignore;
import org.junit.Test;
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
     * @param input  the input EPML
     * @param output  the expected corrected EPML
     * @throws TransformerException if unable to buffer test data
     */
    private void assertCorrected(String output, String input) throws TransformerException {

        CorrectedEPML correctedEPML = new CorrectedEPML(new StreamSource(getResourceAsStream(input)));
        String expected = getResourceAsString(output);

        assertEquals(expected.toString(), correctedEPML.toString());
    }

    // Test cases

    /**
     * Correct EPML should be passed through untouched.
     */
    @Test public final void test00correct() throws TransformerException {
        assertCorrected("00correct.epml", "00correct.epml");
    }

    /**
     * If the top-level <code>&lt;epml&gt;</code> element isn't namespaced, make it so.
     */
    @Test public final void test01noNamespace() throws TransformerException {
        assertCorrected("00correct.epml", "01no-namespace.epml");
    }

    /**
     * If <code>&lt;coordinates&gt;</code> are missing, add one.
     */
    @Test public final void test02noCoordinates() throws TransformerException {
        assertCorrected("02no-coordinates-corrected.epml", "02no-coordinates.epml");
    }

    /**
     * If EPCs occur outside any <code>&lt;directory&gt;</code>, add one.
     */
    @Test public final void test03noDirectory() throws TransformerException {
        assertCorrected("03no-directory-corrected.epml", "03no-directory.epml");
    }

    /**
     * Change <code>epc/@id</code> to <code>epc/@epcId</code>.
     */
    @Test public final void test04epcWithoutEpcId() throws TransformerException {
        assertCorrected("00correct.epml", "04epcWithoutEpcId.epml");
    }
}
