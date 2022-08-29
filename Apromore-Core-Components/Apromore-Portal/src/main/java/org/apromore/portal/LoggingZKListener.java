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
package org.apromore.portal;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.model.UserType;
import org.slf4j.MDC;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.ExecutionCleanup;
import org.zkoss.zk.ui.util.ExecutionInit;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.util.SessionCleanup;

/**
 * Log ZK lifecycle events.
 */
@Slf4j
public class LoggingZKListener implements ExecutionCleanup, ExecutionInit, SessionCleanup {

    /**
     * This MDC key will be set with the username of the current user.
     */
    public static final String MDC_APROMORE_USER_KEY = "apromore.user";

    // Execution listener

    @Override
    public void init(final Execution execution, final Execution parent) {
        UserType user = UserSessionManager.getCurrentUser();
        MDC.put(MDC_APROMORE_USER_KEY, user == null ? "ZK" : user.getUsername());
    }

    @Override
    public void cleanup(final Execution execution, final Execution parent, List<Throwable> errs) {
        MDC.remove(MDC_APROMORE_USER_KEY);
    }

    // Session listener

    /**
     * {@inheritDoc}
     *
     * This implementation logs when a user's login session times out.
     */
    @Override
    public void cleanup(final Session session) {
        UserType user = (UserType) session.getAttribute(UserSessionManager.USER);
        if (user == null) {
            log.debug("Unauthenticated user session expired");

        } else if (MDC.get(MDC_APROMORE_USER_KEY) != null) {
            log.info("User \"{}\" logout", user.getUsername());

        } else {
            log.debug("User \"{}\" session expired", user.getUsername());
        }
    }
}
