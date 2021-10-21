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

package org.apromore.plugin.portal.processdiscoverer.data;

/**
 * ContextData contains contextual data of this plugin
 * Many data items are about the calling plugin or the portal
 * 
 * @author Bruce Nguyen
 *
 */
public class ContextData {
    private final String userName;
    private final int containingFolderId;
    private final String containingFolderName;
    private final String domain;
    private final String logName;
    private final int logId;
    private final boolean isCalendarEnabled;
    
    private ContextData (String domain,
                        String userName,
                        int logId, String logName,
                        int containingFolderId, String containingFolderName,
                         boolean isCalendarEnabled) {
        this.userName = userName;
        this.containingFolderId = containingFolderId;
        this.containingFolderName = containingFolderName;
        this.domain = domain;
        this.logId = logId;
        this.logName = logName;
        this.isCalendarEnabled = isCalendarEnabled;
    }
    
    public static ContextData valueOf (String domain,
                        String userName,
                        int logId, String logName,
                        int containingFolderId, String containingFolderName, boolean isCalendarEnabled) {
        return new ContextData(domain, userName, logId, logName, containingFolderId, containingFolderName, isCalendarEnabled);
    }
    
    public String getUsername() {
        return this.userName;
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

    public boolean isCalendarEnabled() {
        return this.isCalendarEnabled;
    }
    
}
