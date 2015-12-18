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

