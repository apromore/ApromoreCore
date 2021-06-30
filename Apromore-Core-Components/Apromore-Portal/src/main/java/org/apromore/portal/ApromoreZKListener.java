package org.apromore.portal;

/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import java.util.Map;
import javax.servlet.SessionCookieConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.slf4j.Logger;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppInit;

/**
 * Callbacks during the lifecycle of ZK scopes.
 */
public class ApromoreZKListener implements WebAppInit {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(ApromoreZKListener.class);

    /**
     * If this property key appears in <code>site.cfg</code>, it will be parsed as a boolean and
     * determines whether session cookie "HttpOnly" flags will be set.
     */
    public static final String COOKIE_HTTP_ONLY = "site.cookie.httpOnly";

    /**
     * If this property key appears in <code>site.cfg</code>, it will be parsed as a boolean and
     * determines whether session cookie "Secure" flags will be set.
     */
    public static final String COOKIE_SECURE = "site.cookie.secure";

    /**
     * {@inheritDoc}
     *
     * This implementation allows the cookie configuration to be set from the central application
     * configuration file <code>site.cfg</code>, overriding the Portal's <code>web.xml</code>.
     */
    @Override
    public void init(final WebApp webApp) {
        LOGGER.trace("Initialize web app {}", webApp);
        try {
            Map<String, Object> siteConfiguration = OSGi.getConfiguration("site", webApp.getServletContext());
            LOGGER.trace("Site configuration {}", siteConfiguration);

            // Allow override of the "HttpOnly" cookie flag
            if (siteConfiguration.containsKey(COOKIE_HTTP_ONLY)) {
                boolean isHttpOnly = Boolean.parseBoolean((String) siteConfiguration.get(COOKIE_HTTP_ONLY));
                LOGGER.info("Overriding session cookie configuration: HttpOnly is now {}", isHttpOnly);
                webApp.getServletContext().getSessionCookieConfig().setHttpOnly(isHttpOnly);
            }

            // Allow override of the "Secure" cookie flag
            if (siteConfiguration.containsKey(COOKIE_SECURE)) {
                boolean isSecure = Boolean.parseBoolean((String) siteConfiguration.get(COOKIE_SECURE));
                LOGGER.info("Overriding session cookie configuration: Secure is now {}", isSecure);
                webApp.getServletContext().getSessionCookieConfig().setSecure(isSecure);
            }

        } catch (Exception e) {
            LOGGER.error("Failed to initialize web app", e);
        }
    }
}
