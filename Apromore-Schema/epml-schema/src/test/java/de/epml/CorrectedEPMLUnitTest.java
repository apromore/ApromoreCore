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

    // Test cases

    /**
     * Test that correct EPML passes through untouched.
     */
    @Ignore
    @Test public final void test1() throws TransformerException {

        CorrectedEPML correctedEPML = new CorrectedEPML(new StreamSource(getResourceAsStream("test1.epml")));
        String expected = getResourceAsString("test1-expected.epml");

        assertEquals(expected.toString(), correctedEPML.toString());
    }
}
