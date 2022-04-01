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
package org.apromore.dao.jpa.grouplog;

import static org.assertj.core.api.Assertions.assertThat;

import org.apromore.config.BaseTestClass;
import org.apromore.dao.GroupLogRepository;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.LogRepository;
import org.apromore.dao.jpa.usermanagement.UserManagementBuilder;
import org.apromore.dao.model.AccessRights;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.GroupLog;
import org.apromore.dao.model.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GroupLogUnitTest extends BaseTestClass {


    private UserManagementBuilder userManagementBuilder;

    @Autowired
    private GroupLogRepository groupLogRepo;

    @Autowired
    private GroupRepository groupRepo;

    @Autowired
    private LogRepository logRepo;

    @BeforeEach
    void before() {
        userManagementBuilder = new UserManagementBuilder();
    }

    @Test
    void testCreateGroupLog() {

        // Given
        AccessRights accessRights = new AccessRights(true, false, true);
        Group group = userManagementBuilder.withGroup("test GroupLog2", "GROUP").buildGroup();
        Log log = new Log();
        log.setName("Test Log");
        log.setFilePath("filePath");
        log = logRepo.saveAndFlush(log);
        group = groupRepo.saveAndFlush(group);
        GroupLog groupLog = new GroupLog(group, log, accessRights);

        // When
        groupLog = groupLogRepo.saveAndFlush(groupLog);

        // Then
        assertThat(groupLog.getId()).isNotNull();
        assertThat(groupLog.getAccessRights().isReadOnly()).isTrue();
        assertThat(groupLog.getAccessRights().isWriteOnly()).isFalse();
        assertThat(groupLog.getAccessRights().isOwnerShip()).isTrue();
    }
}
