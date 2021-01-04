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
package org.apromore.portal.plugincontrol;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is to manage all running plugins (PluginExecution) in the portal.
 * 
 * @author Bruce Nguyen
 *
 */
public class PluginExecutionManager {
    private Map<String,PluginExecution> pluginExecutions = new HashMap<>(); // plugin instance ID => PortalPlugin
    
    public PluginExecutionManager() {
        
    }
    
    public PluginExecution getPluginExecution(String executionId) {
        return pluginExecutions.get(executionId);
    }
    
    public String registerPluginExecution(PluginExecution pluginExecution) {
        String executionId = UUID.randomUUID().toString();
        pluginExecutions.put(executionId, pluginExecution);
        return executionId;
    }
}
