/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

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
