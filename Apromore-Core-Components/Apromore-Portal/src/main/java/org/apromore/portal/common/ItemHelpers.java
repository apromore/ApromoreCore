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

package org.apromore.portal.common;

import org.apromore.dao.model.Group;
import org.apromore.dao.model.User;
import org.apromore.portal.exception.UnknownItemTypeException;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummaryType;
import org.apromore.service.AuthorizationService;
import org.apromore.service.WorkspaceService;
import org.apromore.util.AccessType;
import org.zkoss.zkplus.spring.SpringUtil;

import java.util.Map;

/**
 * Intermediate helper until all these summaryTypes and AuthorizationService service to be made more object oriented
 *
 */
public final class ItemHelpers {

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

        if ((id = ItemHelpers.getLogId(item)) != null) {
            groupAccessMap = authorizationService.getLogAccessType(id);
        } else if ((id = ItemHelpers.getModelId(item)) != null) {
            groupAccessMap = authorizationService.getProcessAccessType(id);
        } else if ((id = ItemHelpers.getFolderId(item)) != null) {
            groupAccessMap = authorizationService.getFolderAccessType(id);
        } else {
            throw new UnknownItemTypeException("Invalid item type");
        }
        return groupAccessMap;
    }

    private static final AccessType getEffectiveAccessType(Object item, User user) throws Exception {
        Integer id;
        AccessType accessType = AccessType.RESTRICTED;

        if ((id = ItemHelpers.getLogId(item)) != null) {
            accessType = authorizationService.getLogAccessTypeByUser(id, user);
        } else if ((id = ItemHelpers.getModelId(item)) != null) {
            accessType = authorizationService.getProcessAccessTypeByUser(id, user);
        } else if ((id = ItemHelpers.getFolderId(item)) != null) {
            if (id == 0) { // everyone is the owner of root
                accessType = AccessType.OWNER;
            } else {
                accessType = authorizationService.getFolderAccessTypeByUser(id, user);
            }
        } else {
            throw new UnknownItemTypeException("Invalid item type");
        }
        return accessType;
    }

    private static final AccessType getLogEffectiveAccessType(Integer logId, User user) {
        return authorizationService.getLogAccessTypeByUser(logId, user);
    }

    public static final boolean isOwner(User user, Object item) throws Exception {
        AccessType accessType = ItemHelpers.getEffectiveAccessType(item, user);
        return AccessType.OWNER.equals(accessType);
    }

    public static final boolean canModifyCalendar(User user, Integer logId) {
        AccessType accessType = ItemHelpers.getLogEffectiveAccessType(logId, user);
        return AccessType.OWNER.equals(accessType) || AccessType.EDITOR.equals(accessType);
    }

    public static final boolean canDeleteCalendar(User user, Integer logId) {
        AccessType accessType = ItemHelpers.getLogEffectiveAccessType(logId, user);
        return AccessType.OWNER.equals(accessType);
    }

    public static final boolean canModify(User user, Object item) throws Exception {
        AccessType accessType = ItemHelpers.getEffectiveAccessType(item, user);
        return AccessType.OWNER.equals(accessType) || AccessType.EDITOR.equals(accessType);
    }

    public static final boolean canShare(User user, Object item) throws Exception {
        return ItemHelpers.isOwner(user, item);
    }

    public static final boolean canAddIn(User user, Object targetFolder) throws Exception {
        return ItemHelpers.isOwner(user, targetFolder);
    }

    public static final boolean canDelete(User user, Object item) throws Exception {
        return ItemHelpers.isOwner(user, item);
    }

    public static final boolean canRename(User user, Object item) throws Exception {
        return ItemHelpers.canModify(user, item);
    }

}
