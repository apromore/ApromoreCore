/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

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
