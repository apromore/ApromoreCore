/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.plugin.portal;

import org.zkoss.zk.ui.Component;

import java.io.IOException;
import java.util.Map;

/**
 * Provides access to the UI of the portal. This class MUST be used by plug-ins to create UI elements to ensure proper class loading.
 * So far, it only allows to create generic ZK components.
 * In the future it may be extended with functionality that allows to mutate certain parts of the portal.
 */
public interface PortalUI {

    /**
     * Creates a ZK component using the provided ClassLoader to lookup the ZUL file specified as URI.
     *
     * @param bundleClassLoader provide the ClassLoader of a class within your plug-in bundle (e.g., plugin.getClass().getClassLoader())
     * @param uri the path to the ZUL file(e.g., test.zul for a file test.zul in the main/resources dir)
     * @param parent optional parent of the component, if NULL then it will be a top-level component
     * @param arguments optional arguments
     * @return the corresponding ZK component
     * @throws IOException in case loading the ZUL file failed
     */
    Component createComponent(ClassLoader bundleClassLoader, String uri, Component parent, Map<?, ?> arguments) throws IOException;

}
