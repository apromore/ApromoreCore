/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.plugin.portal.bpmneditor;

import java.io.InputStream;
import org.apromore.plugin.portal.WebContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BPMNEditorWebContentService implements WebContentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BPMNEditorWebContentService.class);

    private final ClassLoader classLoader = BPMNEditorWebContentService.class.getClassLoader();

    @Override
    public boolean hasResource(String path) {
        return path.startsWith("/bpmneditor/")
            && !path.startsWith("/bpmneditor/WEB-INF")
            && classLoader.getResource(path) != null;
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        LOGGER.debug(String.format("Getting resource %s", path));
        if (hasResource(path)) {
            throw new IllegalArgumentException("Empty path");
        }
        return classLoader.getResourceAsStream(path);
    }
}
