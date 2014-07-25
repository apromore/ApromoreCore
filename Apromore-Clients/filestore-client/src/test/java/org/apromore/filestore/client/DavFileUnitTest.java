package org.apromore.filestore.client;

// Java 2 Standard Edition classes
import java.io.File;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

// Third party classes
import com.github.sardine.DavResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test suite for {@link DavFile}.
 *
 * This should confirm that all native behavior from {@link File} has been overridden.
 *
 * @author <a href=mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class DavFileUnitTest {

    private static Logger LOGGER = Logger.getLogger(DavFileUnitTest.class.getCanonicalName());

    /**
     * The test object.
     */
    private DavFile file;

    static final Map<QName,String> customProps = new HashMap<>();
    static class TestDavResource extends DavResource {
        public TestDavResource() throws URISyntaxException {
            super(
                "http://localhost:9000/filestore/dav",
                new Date(1000000),  // creation
                new Date(2000000),  // modified
                DavResource.HTTPD_UNIX_DIRECTORY_CONTENT_TYPE,
                0L,  // content length
                "etag",  // etag
                "foo",  // display name
                "en",  // content language
                customProps
            );
        }
    }

    /**
     * Create the test object.
     */
    @Before
    public void createTestObject() throws Exception {

        file = new DavFile(new TestDavResource());
    }

    @Test
    public void testStaticFields() {
        assertEquals(":", DavFile.pathSeparator);
        assertEquals(':', DavFile.pathSeparatorChar);
        assertEquals("/", DavFile.separator);
        assertEquals('/', DavFile.separatorChar);
    }
}
