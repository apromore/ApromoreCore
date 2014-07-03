package com.processconfiguration.cmapper;

// Java 2 Standard Edition classes
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

// Local classes
import com.processconfiguration.cmap.CMAP;
import org.apromore.filestore.client.FileStoreService;

/**
 * Configuration mapping stored on the WebDAV service.
 */
public class DavCmap implements Cmap {

    private static final Logger LOGGER = Logger.getLogger(DavCmap.class.getCanonicalName());

    private CMAP             cmap;
    private FileStoreService service;
    private URI              uri;

    /**
     * Sole constructor.
     *
     * @param uri  the URI of the configuration mapping on the DAV repository
     * @param service  the proxy to the Apromore-FileStore
     */
    public DavCmap(URI uri, FileStoreService service) throws Exception {
        this.service = service;
        this.uri     = uri;
    }

    /**
     * {@inheritDoc}
     */
    public CMAP getCmap() throws Exception {
        JAXBContext jc = JAXBContext.newInstance("com.processconfiguration.cmap");
        Unmarshaller u = jc.createUnmarshaller();
        URI uri2 = service.getBaseURI().resolve(uri);
        LOGGER.info("Accessing URI " + uri + " resolved as " + uri2);

        this.cmap = (CMAP) u.unmarshal(service.getFile(uri2.toString()));

        return cmap;
    }

    /**
     * {@inheritDoc}
     *
     * You must explicitly call the {@link OutputStream#close} method on the returned output stream,
     * otherwise the content is not guaranteed to be written to the WebDAV service.
     *
     * @return an output stream which buffers its input and flushes it to WebDAV when it is closed
     */
    public OutputStream getOutputStream() throws Exception {
        return new ByteArrayOutputStream() {
            public void close() throws IOException {
                try {
                    URI uri2 = service.getBaseURI().resolve(uri);
                    LOGGER.info("Closing output stream to " + uri + " resolved as " + uri2);
                    service.put(uri2.toString(), toByteArray(), "application/xml");
                    LOGGER.info("Closed output stream to " + uri + " resolved as " + uri2);
                } catch (Exception e) {
                    throw new IOException("Unable to flush buffer to WebDAV service " + uri, e);
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public URI getURI() {
        return uri;
    }
}
