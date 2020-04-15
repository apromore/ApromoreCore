/*
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2018-2020 The University of Melbourne.
 *
 * "Apromore Core" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore Core" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.processdiscoverer.data;

import org.apromore.model.LogSummaryType;
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
    private LogSummaryType logSummary;
    private boolean firstTimeLoadingFinished = false;
    
    public ContextData(PortalContext portalContext, int containingFolderId, String containingFolderName, 
                    LogSummaryType logSummary) {
        this.containingFolderId = containingFolderId;
        this.containingFolderName = containingFolderName;
        this.portalContext = portalContext;
        this.logSummary = logSummary;
        this.firstTimeLoadingFinished = false;
    }
    
    public PortalContext  getPortalContext() {
        return this.portalContext;
    }
    
    public String getLogName() {
        return logSummary.getName();
    }
    
    public Integer getLogId() {
        return logSummary.getId();
    }
    
    public String getDomain() {
        return logSummary.getDomain();
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
            
}
