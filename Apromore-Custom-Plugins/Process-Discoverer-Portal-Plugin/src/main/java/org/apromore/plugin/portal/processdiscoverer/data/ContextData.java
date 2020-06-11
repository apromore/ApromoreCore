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

package org.apromore.plugin.portal.processdiscoverer.data;

import org.apromore.plugin.portal.PortalContext;

/**
 * ContextData contains contextual data of this plugin
 * Many data items are about the calling plugin or the portal
 * 
 * @author Bruce Nguyen
 *
 */
public class ContextData {
    private int containingFolderId = 0;
    private String containingFolderName = "";  
    private PortalContext portalContext;
    private String domain;
    private String logName;
    private int logId;
    private boolean firstTimeLoadingFinished = false;
    private ConfigData configData;
    
    public ContextData(PortalContext portalContext, 
                        String domain,
                        int logId, String logName,
                        int containingFolderId, String containingFolderName, 
                        ConfigData configData) {
        this.containingFolderId = containingFolderId;
        this.containingFolderName = containingFolderName;
        this.portalContext = portalContext;
        this.domain = domain;
        this.logId = logId;
        this.logName = logName;
        this.firstTimeLoadingFinished = false;
        this.configData = configData;
    }
    
    public PortalContext  getPortalContext() {
        return this.portalContext;
    }
    
    public String getLogName() {
        return logName;
    }
    
    public int getLogId() {
        return logId;
    }
    
    public String getDomain() {
        return domain;
    }
    
    public int getFolderId() {
        return this.containingFolderId;
    }
    
    public String getFolderName() {
        return this.containingFolderName;
    }
    
    public boolean getFirstTimeLoadingFinished() {
        return this.firstTimeLoadingFinished;
    }
    
    public void setFirstTimeLoadingFinished(boolean value) {
        this.firstTimeLoadingFinished = value;
    }
    
    public ConfigData getConfigData() {
        return this.configData;
    }
            
}
