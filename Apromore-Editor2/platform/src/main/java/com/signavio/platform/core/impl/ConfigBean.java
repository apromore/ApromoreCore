package com.signavio.platform.core.impl;

// Third party packages
import org.apache.log4j.Logger;

public class ConfigBean {

    String editorDir;
    String externalHost;
    int externalPort;

    public ConfigBean(String editorDir, String externalHost, int externalPort) {

        Logger.getLogger(ConfigBean.class).info("Editor configured with:" +
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
