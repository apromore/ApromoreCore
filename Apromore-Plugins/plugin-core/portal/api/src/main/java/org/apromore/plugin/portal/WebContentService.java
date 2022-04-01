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
package org.apromore.plugin.portal;

import java.io.InputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * An OSGi service which selectively serves web content.
 */
public interface WebContentService {

    /**
     * MIME type for ZUML files.
     */
    public final String ZUML_MIME_TYPE = "text/vnd.potix-zuml+xml";

    /**
     * @param path  a candidate servlet path
     * @return whether the <var>path</var> is a resource this service handles
     */
    boolean hasResource(String path);

    /**
     * @param path  a servlet path that's been accepted by {@link #hasResource}
     * @return a stream containing the resource content
     */
    InputStream getResourceAsStream(String path);
}
