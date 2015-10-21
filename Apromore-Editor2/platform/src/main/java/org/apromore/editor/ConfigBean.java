package org.apromore.editor;

// Third party packages
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean for the subset of entries in the <code>site.properties</code> configuration artifact which are relevant to the editor.
 *
 * @author @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class ConfigBean {

    String editorDir;
    String externalHost;
    int externalPort;

    public ConfigBean(String editorDir, String externalHost, int externalPort) {

        LoggerFactory.getLogger(ConfigBean.class.getCanonicalName()).info("Editor configured with:" +
            " editor.dir=" + editorDir +
            " site.externalHost=" + externalHost +
            " site.externalPort=" + externalPort);

        this.editorDir = editorDir;
        this.externalHost = externalHost;
        this.externalPort = externalPort;
    }

    public String getEditorDir() { return editorDir;  }
    public String getExternalHost() { return externalHost; }
    public int getExternalPort() { return externalPort; }
}
