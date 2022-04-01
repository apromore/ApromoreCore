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
package org.apromore.dao.jpa.usermetadata;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import org.apromore.config.BaseTestClass;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.GroupUsermetadataRepository;
import org.apromore.dao.LogRepository;
import org.apromore.dao.RoleRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.UsermetadataLogRepository;
import org.apromore.dao.UsermetadataRepository;
import org.apromore.dao.UsermetadataTypeRepository;
import org.apromore.dao.jpa.usermanagement.UserManagementBuilder;
import org.apromore.dao.model.AccessRights;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.GroupUsermetadata;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.Usermetadata;
import org.apromore.dao.model.UsermetadataLog;
import org.apromore.dao.model.UsermetadataType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserMetadataServiceUnitTest extends BaseTestClass {

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    UserManagementBuilder builder;

    @Autowired
    UsermetadataRepository usermetadataRepository;

    @Autowired
    UsermetadataTypeRepository usermetadataTypeRepository;

    @Autowired
    LogRepository logRepository;

    @Autowired
    UsermetadataLogRepository usermetadataLogRepository;

    @Autowired
    GroupUsermetadataRepository groupUsermetadataRepository;

    @BeforeEach
    void Setup() {
        builder = new UserManagementBuilder();
    }

    @Test
    void testSaveSingletonGroup() {
        // given
        Group testUser = builder.withGroup("testUser2", "USER").buildGroup();

        // when
        Group savedTestUser = groupRepository.saveAndFlush(testUser);

        // then
        assertThat(savedTestUser.getId()).isNotNull();
        assertThat(savedTestUser.getName()).isEqualTo(testUser.getName());
        assertThat(savedTestUser.getType()).isEqualTo(testUser.getType());
    }

    @Test
    void testSaveGroup() {
        // given
        Group userGroup = builder.withGroup("testGroup1", "GROUP").buildGroup();

        // when
        Group savedUserGroup = groupRepository.saveAndFlush(userGroup);
        // then
        assertThat(savedUserGroup.getId()).isNotNull();
        assertThat(savedUserGroup.getName()).isEqualTo(userGroup.getName());
        assertThat(savedUserGroup.getType()).isEqualTo(userGroup.getType());

    }

    @Test
    void insertUsermetadataTest() {
        // Given
        Usermetadata um = builder.withUserMetadataType("test Type", 1).withUserMetaData("Test", "test")
                .buildUserMetaData();

        UsermetadataType type = usermetadataTypeRepository.save(um.getUsermetadataType());
        um.setUsermetadataType(type);

        // When
        um = usermetadataRepository.saveAndFlush(um);
        Usermetadata umExpected = usermetadataRepository.findById(um.getId()).get();

        // Then
        assertThat(um.getId()).isNotNull();
        assertThat(umExpected.getId()).isNotNull();
        assertThat(umExpected.getCreatedTime()).isEqualTo(um.getCreatedTime());

    }

    @Test
    void insertUsermetadataLogTest() {

        // Given
        Usermetadata um = builder.withUserMetadataType("test Type2", 1).withUserMetaData("Test2", "test2")
                .buildUserMetaData();

        UsermetadataType type = usermetadataTypeRepository.save(um.getUsermetadataType());
        um.setUsermetadataType(type);
        um = usermetadataRepository.saveAndFlush(um);

        Log logs = new Log();
        logs.setName("Test Log");
        logs.setFilePath("Test Log");
        logs.setDomain("Test Log");
        logs.setRanking("1");
        logs.setCreateDate(new Date().toString());
        logs = logRepository.saveAndFlush(logs);

        // when
        UsermetadataLog ul = new UsermetadataLog();
        ul.setLog(logs);
        ul.setUsermetadata(um);

        ul = usermetadataLogRepository.saveAndFlush(ul);

        Group testUser1 = groupRepository.saveAndFlush(builder.withGroup("testUser1", "USER").buildGroup());

        GroupUsermetadata gu = new GroupUsermetadata();
        gu.setGroup(testUser1);
        gu.setUsermetadata(um);
        gu.setAccessRights(new AccessRights(true, true, true));

        gu = groupUsermetadataRepository.saveAndFlush(gu);

        // Then
        assertThat(ul.getId()).isNotNull();
        assertThat(gu.getId()).isNotNull();

    }

}
