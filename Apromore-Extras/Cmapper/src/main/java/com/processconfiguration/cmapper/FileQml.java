package com.processconfiguration.cmapper;

// Java 2 Standard Edition classes
import java.io.File;
import java.net.URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

// Local classes
import com.processconfiguration.qml.QMLType;

/**
 * QML questionnaire stored on the local filesystem.
 */
public class FileQml implements Qml {

    private QMLType qml;
    private URI     uri;

    /**
     * Sole constructor.
     */
    public FileQml(File file) throws Exception {
        this.uri = file.toURI();

        JAXBContext jc = JAXBContext.newInstance("com.processconfiguration.qml");
        Unmarshaller u = jc.createUnmarshaller();
        JAXBElement qmlElement = (JAXBElement) u.unmarshal(file);
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

