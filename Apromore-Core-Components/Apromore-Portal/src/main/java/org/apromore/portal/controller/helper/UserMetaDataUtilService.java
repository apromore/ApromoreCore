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
package org.apromore.portal.controller.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apromore.dao.model.User;
import org.apromore.dao.model.Usermetadata;
import org.apromore.exception.UserNotFoundException;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.model.UserMetadataSummaryType;
import org.apromore.service.UserMetadataService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.util.AccessType;
import org.apromore.util.UserMetadataTypeEnum;
import org.slf4j.Logger;
import org.zkoss.zkplus.spring.SpringUtil;

public class UserMetaDataUtilService {
    private User user;
    private UserMetadataService userMetadataService;
    private UserInterfaceHelper userInterfaceHelper;
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(UserMetaDataUtilService.class);

    public UserMetaDataUtilService() {
        this.userMetadataService = (UserMetadataService) SpringUtil.getBean("userMetadataService");
        this.userInterfaceHelper = (UserInterfaceHelper) SpringUtil.getBean("uiHelper");
        try {
            this.user = userMetadataService.findUserByRowGuid(UserSessionManager.getCurrentUser().getId());
        } catch (UserNotFoundException e) {
            LOGGER.error("Error in retrieving user,",e);
        }
    }

    public List<UserMetadataSummaryType> getUserMetadataSummariesForFilter(Integer logId) {
        return getUserMetadataSummaries(logId, UserMetadataTypeEnum.FILTER);
    }

    public List<UserMetadataSummaryType> getUserMetadataSummariesForDashboard(Integer logId) {
        return getUserMetadataSummaries(logId, UserMetadataTypeEnum.DASHBOARD);
    }

    private List<UserMetadataSummaryType> getUserMetadataSummaries(Integer logId,
                                                                   UserMetadataTypeEnum userMetadataTypeEnum) {
        Set<Usermetadata> filterUmSet = getUserMetadata(logId, userMetadataTypeEnum);
        if (filterUmSet != null) {
            List<UserMetadataSummaryType> umSummaries = new ArrayList<>();
            try {
                for (Usermetadata u : filterUmSet) {
                    AccessType accessType = userMetadataService.getUserMetadataAccessTypeByUser(u.getId(), user);
                    if (accessType != null) {
                        getSummary(umSummaries, u, accessType);
                    }
                }
                return umSummaries;

            } catch (UserNotFoundException e) {
                LOGGER.error("Error in retrieving UserMetadata Summary Information", e);
            }
        }

        return Collections.emptyList();
    }

    private void getSummary(List<UserMetadataSummaryType> umSummaries, Usermetadata u, AccessType accessType) {
        try {
            umSummaries.add(
                userInterfaceHelper.buildUserMetadataSummary(u.getCreatedBy(), u, accessType));
        } catch (UserNotFoundException e) {
            LOGGER.error("Error in retrieving user,",e);
        }
    }

    private Set<Usermetadata> getUserMetadata(Integer logId, UserMetadataTypeEnum userMetadataTypeEnum) {
        try {
            return userMetadataService.getUserMetadata(user.getUsername(), Collections.singletonList(logId),
                    userMetadataTypeEnum);
        } catch (Exception ex) {
            LOGGER.error("Error in retrieving UserMetadata", ex);
        }
        return Collections.emptySet();
    }

    public Usermetadata getUserMetaDataById(int umId) {
        return userMetadataService.findById(umId);
    }
}
