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
package org.apromore.portal;

import org.apromore.plugin.portal.PortalLoggerFactory;
import org.slf4j.Logger;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.ExecutionInit;

/**
 * Callbacks during the lifecycle of ZK scopes.
 */
public class ApromoreZKListener implements ExecutionInit {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(ApromoreZKListener.class);

    /**
     * {@inheritDoc}
     *
     * This implementation logs the user out if the security token has expired, or refreshes it
     * if it's still valid but close to expiry.
     */
    @Override
    public void init(Execution exec, Execution parent) {
        
        LOGGER.info("Initialize execution {} with parent {}", exec, parent);

        // If there's a parent execution, it will have already performed the required work
        if (parent != null) {
            return;
        }

        // TODO: check the JWT and expire it if required, e.g. with signOut(exec.getSession())
        // TODO: check the JWT and refresh it if required, e.g. with refreshSessionTimeout(exec)
    }

    /**
     * Issue a new JWT with an extended expiry.
     */
    private static void refreshSessionTimeout(final Execution exec) {
        // exec.addResponseHeader(...);  or maybe  ((HttpServletResponse) exec.getNativeResponse())...
    }

    /**
     * Broadcast that this session has been signed out.
     */
    private static void signOut(final Session session) {
        EventQueues.lookup("signOutQueue", EventQueues.APPLICATION, true)
                   .publish(new Event("onSignout", null, session));
    }
}
