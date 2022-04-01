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
package org.apromore.portal.plugincontrol;

import java.util.UUID;

import org.zkoss.zk.ui.Session;

/**
 * PluginExecutionManager is a singleton used to manage all running plugin instances in the portal.
 * Plugin instance is the next level of isolation in Apromore under user session.
 * That means each user session can run different plugin instances. The plugin instances in one user
 * session must be isolated, obviously the plugin instances in two user sessions must be also isolated.
 * 
 * This implementation uses ZK's Session to store these instances. As ZK will destroy a user session when
 * it times out, the instances under the session will be also destroyed automatically.
 * 
 * @author Bruce Nguyen
 *
 */
public abstract class PluginExecutionManager {
    
    public static PluginExecution getPluginExecution(String executionId, Session session) throws PluginExecutionException {
        PluginExecution pluginExec = (PluginExecution)session.getAttribute(executionId);
        if (pluginExec == null) {
            throw new PluginExecutionException("Not found plugin execution with id = " + executionId);
        }
        return pluginExec;
    }
    
    public static String registerPluginExecution(PluginExecution pluginExecution, Session session) {
        String executionId = UUID.randomUUID().toString();
        session.setAttribute(executionId, pluginExecution);
        return executionId;
    }
}
