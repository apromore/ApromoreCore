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

package org.apromore.portal.access;

import java.util.Map;

import org.apromore.service.SecurityService;
import org.apromore.service.AuthorizationService;
import org.apromore.service.WorkspaceService;
import org.apromore.util.AccessType;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummariesType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.UserType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.portal.common.Constants;
import org.apromore.dao.model.User;
import org.apromore.dao.model.Group;
import org.apromore.portal.exception.UnknownItemTypeException;
import org.zkoss.spring.SpringUtil;

/**
 * Intermediate helper until all these summaryTypes and AuthorizationService service to be made more object oriented
 *
 */
public final class Helpers {

    private static final AuthorizationService authorizationService = (AuthorizationService) SpringUtil.getBean(Constants.AUTH_SERVICE);
    private static final WorkspaceService workspaceService = (WorkspaceService) SpringUtil.getBean(Constants.WORKSPACE_SERVICE);

    public static final Integer getLogId(Object item) {
        return item.getClass().equals(LogSummaryType.class) ? ((SummaryType)item).getId() : null;
    }

    public static final Integer getModelId(Object item) {
        return item.getClass().equals(ProcessSummaryType.class) ? ((SummaryType)item).getId() : null;
    }

    public static final Integer getFolderId(Object item) {
        return item.getClass().equals(FolderType.class) ? ((FolderType)item).getId() : null;
    }

    public static final Map<Group, AccessType> getGroupAccessFromSummaryType(Object item) throws Exception {
        Map<Group, AccessType> groupAccessMap;
        Integer id;

        if ((id = Helpers.getLogId(item)) != null) {
            groupAccessMap = authorizationService.getLogAccessType(id);
        } else if ((id = Helpers.getModelId(item)) != null) {
            groupAccessMap = authorizationService.getProcessAccessType(id);
        } else if ((id = Helpers.getFolderId(item)) != null) {
            groupAccessMap = authorizationService.getFolderAccessType(id);
        } else {
            throw new UnknownItemTypeException("Invalid item type");
        }
        return groupAccessMap;
    }

    private static final AccessType getEffectiveAccessType(Object item, User user) throws Exception {
        Integer id;
        AccessType accessType = AccessType.NONE;

        if ((id = Helpers.getLogId(item)) != null) {
            accessType = authorizationService.getLogAccessTypeByUser(id, user);
        } else if ((id = Helpers.getModelId(item)) != null) {
            accessType = authorizationService.getProcessAccessTypeByUser(id, user);
        } else if ((id = Helpers.getFolderId(item)) != null) {
            accessType = authorizationService.getFolderAccessTypeByUser(id, user);
        } else {
            throw new UnknownItemTypeException("Invalid item type");
        }
        return accessType;
    }

    public static final boolean isShareable(Object item, User user) throws Exception {
        AccessType accessType = Helpers.getEffectiveAccessType(item, user);
        return AccessType.OWNER.equals(accessType);
    }

    public static final boolean isChangeable(Object item, User user) throws Exception {
        AccessType accessType = Helpers.getEffectiveAccessType(item, user);
        return AccessType.OWNER.equals(accessType) || AccessType.EDITOR.equals(accessType);
    }
}
