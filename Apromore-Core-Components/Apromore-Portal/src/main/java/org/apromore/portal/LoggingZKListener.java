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

import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.model.UserType;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.util.SessionCleanup;

/**
 * Log ZK lifecycle events.
 */
public class LoggingZKListener implements SessionCleanup {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(LoggingZKListener.class);

    /**
     * {@inheritDoc}
     *
     * This implementation logs when a user's login session times out.
     */
    @Override
    public void cleanup(final Session session) {
        UserType user = (UserType) session.getAttribute(UserSessionManager.USER);
        if (user == null) {
            LOGGER.debug("Unauthenticated user session expired");

        } else if (MDC.get(PortalLoggerFactory.MDC_APROMORE_USER_KEY) != null) {
            LOGGER.info("User \"{}\" logout", user.getUsername());

        } else {
            LOGGER.debug("User \"{}\" session expired", user.getUsername());
        }
    }
}
