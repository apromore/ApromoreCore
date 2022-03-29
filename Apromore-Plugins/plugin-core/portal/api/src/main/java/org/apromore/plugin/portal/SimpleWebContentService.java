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
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.slf4j.Logger;

/**
 * A generic implementation of {@link WebContentService}.
 *
 * Serves any file in the component's src/main/resource directory as the corresponding URL.
 * E.g. src/main/resource/test.zul is accessible as http://localhost:9000/test.zul.
 * Resources ending in .class or within the META-INF or WEB-INF directories are excluded.
 *
 * A component can employ it by configuring a bean in its Spring context and exporting it as an OSGi service.
 * The bean must be passed an arbitrary object from one of the bundle's packages in order to set the classloader.
 * An example Spring configuration:
 * <pre>
 * <osgi:service interface="org.apromore.plugin.portal.WebContentService">
 *   <beans:bean class="org.apromore.plugin.portal.SimpleWebContentService">
 *     <beans:constructor-arg type="Object" ref="anyBeanFromAPackageDefinedByThisComponent"/>
 *   </beans:bean>
 * </osgi:service>
 * </pre>
 */
public class SimpleWebContentService implements WebContentService {

    private static Logger LOGGER = PortalLoggerFactory.getLogger(SimpleWebContentService.class);

    /**
     * A classloader to access the web content.
     */
    private final ClassLoader classLoader;

    private final String name;

    /**
     * Paths matching the given pattern will never be exposed by this service.
     */
    private Pattern excludePattern = Pattern.compile("WEB-INF/.*|.*\\.class|.*/");

    /**
     * @param object  an object from a package of the plugin bundle; must not be <code>null</code>
     */
    public SimpleWebContentService(Object object) {
        classLoader = object.getClass().getClassLoader();
        name = object.getClass().getName();
    }

    public String toString() {
        return super.toString() + "(" + name + ")";
    }

    // Implementation of RouteService

    @Override
    public boolean hasResource(String path) {
        return !excludePattern.matcher(path).matches() && classLoader.getResource(path) != null;
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        LOGGER.debug("Getting resource " + path);
        assert hasResource(path);
        return classLoader.getResourceAsStream(path);
    }
}
