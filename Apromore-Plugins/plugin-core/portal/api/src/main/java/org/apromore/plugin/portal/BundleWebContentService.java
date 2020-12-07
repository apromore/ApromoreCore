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

package org.apromore.plugin.portal;

import java.io.InputStream;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link WebContentService} serving resources from a specified {@link BundleContext}.
 *
 * Serves files in the component's src/main/resource directory as the corresponding URL.
 * E.g. src/main/resource/test.zul is accessible as http://localhost:9000/test.zul.
 * Regular expressions can be configured to include or exclude particular paths.
 *
 * A component can employ it by configuring a bean in its Spring context and exporting it as an OSGi service.
 * The bean must be passed a {@link BundleContext} constructor argument specifying the source bundle.
 * The bean has an "exclude" property whose value is regular expression in the format defined
 * by {@link Pattern}.  The default is <code>WEB-INF/.*|.*\.class|.*&#47;</code>
 * which excludes web configuration, Java classes, and directories.
 * There is an analogous "include" pattern whose default pattern is <code>.*</code>.
 * It is good practice to use this to assign a unique prefix to each plugin's resources to
 * prevent URL contention between plugins.
 *
 * An example Spring configuration:
 * <pre>
 * &lt;osgi:service interface="org.apromore.plugin.portal.WebContentService"&gt;
 *   &lt;beans:bean class="org.apromore.plugin.portal.BundleWebContentService"&gt;
 *     &lt;beans:constructor-arg ref="bundleContext"/&gt;
 *     &lt;beans:property name="include" value="/fancyviewer/.*\.(gif|jpg|png|svg)"/&gt;
 *     &lt;beans:property name="exclude" value="WEB-INF/.*|.*\.class|.*&#47;"/&gt;
 *   &lt;/beans:bean&gt;
 * &lt;/osgi:service&gt;
 * </pre>
 */
public class BundleWebContentService implements WebContentService {

    private static Logger LOGGER = LoggerFactory.getLogger(BundleWebContentService.class);

    /**
     * The bundle containing the resources.
     */
    private final Bundle bundle;

    /**
     * Paths matching the given pattern will never be exposed by this service.
     */
    private Pattern excludePattern = Pattern.compile("WEB-INF/.*|.*\\.class|.*/");

    /**
     * Paths which don't match the given pattern will never be exposed by this service.
     */
    private Pattern includePattern = Pattern.compile(".*");

    /**
     * @param bundleContext  the context whose resources need to be exposed; must not be <code>null</code>
     */
    public BundleWebContentService(BundleContext bundleContext) {
        bundle = bundleContext.getBundle();
    }

    public String toString() {
        return super.toString() + "(" + bundle.getClass().getName() + ")";
    }

    // Bean properties

    /**
     * @return a regular expression in the format defined by {@link Pattern} which is used to filter
     *     which resource paths are exposed as web content
     */
    public String getExclude() {
        return excludePattern.pattern();
    }

    /**
     * @param pattern  a regular expression in the format defined by {@link Pattern} which will be used to filter
     *     which resource paths are exposed as web content
     */
    public void setExclude(String pattern) {
        excludePattern = Pattern.compile(pattern);
    }

    /**
     * @return a regular expression in the format defined by {@link Pattern} which is used to filter
     *     which resource paths are exposed as web content
     */
    public String getInclude() {
        return includePattern.pattern();
    }

    /**
     * @param pattern  a regular expression in the format defined by {@link Pattern} which will be used to filter
     *     which resource paths are exposed as web content
     */
    public void setInclude(String pattern) {
        includePattern = Pattern.compile(pattern);
    }

    // Implementation of WebContentService

    @Override
    public boolean hasResource(String path) {
        return !excludePattern.matcher(path).matches() &&
            includePattern.matcher(path).matches() &&
            bundle.getResource(path) != null;
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        assert hasResource(path);
        try {
            return bundle.getResource(path).openStream();

        } catch (IOException e) {
            throw new Error("Unable to read " + path, e);
        }
    }
}
