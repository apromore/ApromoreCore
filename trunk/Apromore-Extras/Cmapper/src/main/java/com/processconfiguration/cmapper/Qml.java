package com.processconfiguration.cmapper;

// Java 2 Standard Edition classes
import java.net.URI;

// Local classes
import com.processconfiguration.qml.QMLType;

/**
 * QML questionnaire stored on the WebDAV service.
 */
interface Qml {

    /**
     * @return the QML questionnaire
     */
    public QMLType getQml() throws Exception;

    /**
     * @return the URI of the process model, <code>null</code> if the model isn't stored anywhere
     */
    public URI getURI();
}

