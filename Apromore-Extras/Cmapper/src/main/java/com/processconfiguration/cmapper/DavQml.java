package com.processconfiguration.cmapper;

// Java 2 Standard Edition classes
import java.net.URI;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

// Local classes
import com.processconfiguration.qml.QMLType;
import org.apromore.filestore.client.FileStoreService;

/**
 * QML questionnaire stored on the WebDAV service.
 */
public class DavQml implements Qml {

    private static final Logger LOGGER = Logger.getLogger(DavQml.class.getCanonicalName());

    private QMLType          qml;
    private FileStoreService service;
    private URI              uri;

    /**
     * Sole constructor.
     *
     * @param uri  the URI of the QML file on the DAV repository
     * @param service  the proxy to the Apromore-FileStore
     */
    public DavQml(URI uri, FileStoreService service) throws Exception {
        this.service = service;
        this.uri     = uri;

        URI davURI = new URI("http://admin:password@localhost:9000");

        JAXBContext jc = JAXBContext.newInstance("com.processconfiguration.qml");
        Unmarshaller u = jc.createUnmarshaller();
        URI uri2 = davURI.resolve(uri);
        URI uri3 = new URI(uri2.getScheme(),
                           "admin:password",
                           uri2.getHost(),
                           uri2.getPort(),
                           uri2.getPath(),
                           uri2.getQuery(),
                           uri2.getFragment());
        String cooked = uri3.toString();
        LOGGER.info("Accessing URI " + uri + " cooked " + cooked);
        JAXBElement qmlElement = (JAXBElement) u.unmarshal(service.getFile(cooked));
        this.qml = (QMLType) qmlElement.getValue();
    }

    /**
     * {@inheritDoc}
     */
    public QMLType getQml() throws Exception {
        return qml;
    }

    /**
     * {@inheritDoc}
     */
    public URI getURI() {
        return uri;
    }
}

