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
