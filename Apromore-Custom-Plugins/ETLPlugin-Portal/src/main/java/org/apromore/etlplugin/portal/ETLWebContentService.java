/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.etlplugin.portal;

import org.apromore.plugin.portal.WebContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Serve the custom web content for ETL.
 */
public class ETLWebContentService implements WebContentService {

    private static Logger LOGGER = LoggerFactory.getLogger(ETLWebContentService.class);
    private final ClassLoader classLoader = ETLWebContentService.class.getClassLoader();

    /**
     * Checks for the resource.
     *
     * @param path  a candidate servlet path
     * @return the resource.
     */
    @Override
    public boolean hasResource(String path) {
        return path.startsWith("/etlplugin/")
                && !path.startsWith("/etlplugin/WEB-INF")
                && classLoader.getResource(path) != null;
    }

    /**
     * Get Resource as stream.
     *
     * @param path  a servlet path that's been accepted by {@link #hasResource}
     * @return the Input stream.
     */
    @Override
    public InputStream getResourceAsStream(String path) {
        LOGGER.info("Getting resource " + path);
        if(hasResource(path)) {
            return classLoader.getResourceAsStream(path);
        } else {
            return null;
        }
    }
}
