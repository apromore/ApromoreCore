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
package org.apromore.service.impl;

import org.apromore.builder.UserManagementBuilder;
import org.apromore.dao.*;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.*;
import org.apromore.exception.UserNotFoundException;
import org.apromore.service.UserService;
import org.apromore.util.AccessType;
import org.apromore.util.UserMetadataTypeEnum;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.Rollback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserMetadataServiceImplUnitTest {

    // TODO: Rewrite unit test using UserMetadataBuilder and BaseTestClass

    private static final Logger LOGGER = LoggerFactory.getLogger(UserMetadataServiceImplUnitTest.class);
    UserManagementBuilder userBuilder;
    @Mock
    private LogRepository logRepo;
    @Mock
    private GroupLogRepository groupLogRepo;
    @Mock
    private UserService userSrv;
    @Mock
    private GroupUsermetadataRepository groupUsermetadataRepo;
    @Mock
    private UsermetadataRepository userMetadataRepo;
    @Mock
    private UsermetadataTypeRepository usermetadataTypeRepo;
    @Mock
    private UsermetadataLogRepository usermetadataLogRepo;
    @Mock
    private UsermetadataProcessRepository usermetadataProcessRepo;
    @Mock
    private GroupRepository groupRepo;
    @InjectMocks
    private UserMetadataServiceImpl userMetadataService;
    private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private String now = dateFormat.format(new Date());

    @Before
    public final void setUp() {
        userBuilder = new UserManagementBuilder();
    }

    @Test
    public void testGetUserMetadataByUser() throws UserNotFoundException {
// Given
        User user = userBuilder.withUser("test_username", "RowGuid")
                .withDummyUserGroup()
                .buildUser();

        UsermetadataType usermetadataType1 = userBuilder
                .withUserMetaDataType("FILTER", 1, true, 1)
                .getUserMetaDataType();

        Usermetadata um = userBuilder
                .withUserMetaData("test content", "user_UUID", true, 1, usermetadataType1)
                .getUserMetaData();

        Usermetadata um1 = userBuilder
                .withUserMetaData("test content", "user_UUID", true, 1, usermetadataType1)
                .getUserMetaData();

        Usermetadata um2 = userBuilder
                .withUserMetaData("test content", "user_UUID", true, 1, usermetadataType1)
                .getUserMetaData();

        List<GroupUsermetadata> groupUsermetadataList = userBuilder
                .withGroupUserMetaData(user.getGroups().iterator().next(), um, true, false, false)
                .withGroupUserMetaData(user.getGroups().iterator().next(), um, true,
                        true, true).getGroupUserMetaDataList();

        Set<Usermetadata> usermetadataSetExpect = new HashSet<>();
        usermetadataSetExpect.add(um);

        when(userSrv.findUserByLogin(user.getUsername())).thenReturn(user);
        when(groupUsermetadataRepo.findByGroup(user.getGroups().iterator().next())).thenReturn(groupUsermetadataList);

//	When
        Set<Usermetadata> usermetadataSet = userMetadataService.getUserMetadataByUser(user.getUsername(),
                UserMetadataTypeEnum.FILTER);

//	Then
        assertThat(usermetadataSet).containsAll(usermetadataSetExpect);
        verify(userSrv, times(1)).findUserByLogin(user.getUsername());
        verify(groupUsermetadataRepo, times(1)).findByGroup(user.getGroups().iterator().next());

    }

    @Test
    public void testGetUserMetadataByLogs() {

        // Given
        User user = userBuilder.withUser("test_username", "RowGuid")
                .withDummyUserGroup()
                .buildUser();

        UsermetadataType usermetadataType1 = userBuilder
                .withUserMetaDataType("FILTER", 1, true, 1)
                .getUserMetaDataType();

        Usermetadata um = userBuilder
                .withUserMetaData("test content", "user_UUID", true, 1, usermetadataType1)
                .getUserMetaData();

        Usermetadata um1 = userBuilder
                .withUserMetaData("test content", "user_UUID", true, 1, usermetadataType1)
                .getUserMetaData();

        Usermetadata um2 = userBuilder
                .withUserMetaData("test content", "user_UUID", true, 1, usermetadataType1)
                .getUserMetaData();

        List<GroupUsermetadata> groupUsermetadataList = userBuilder
                .withGroupUserMetaData(user.getGroups().iterator().next(), um, true, false, false)
                .withGroupUserMetaData(user.getGroups().iterator().next(), um, true, true, true)
                .getGroupUserMetaDataList();

        Log log1 = new Log(1);
        Set<Usermetadata> usermetadataSet1 = new HashSet<>();
        usermetadataSet1.add(um1);
        log1.setUsermetadataSet(usermetadataSet1);
        Log log2 = new Log(2);
        Set<Usermetadata> usermetadataSet2 = new HashSet<>();
        usermetadataSet2.add(um2);
        log1.setUsermetadataSet(usermetadataSet2);

        Set<Log> logs = new HashSet<>();
        logs.add(log1);
        logs.add(log2);

        um1.setLogs(logs);
        um2.setLogs(logs);

        Set<Usermetadata> usermetadataSetExpect = new HashSet<>();
        usermetadataSetExpect.add(um1);
        usermetadataSetExpect.add(um2);

        when(logRepo.findUniqueByID(log1.getId())).thenReturn(log1);
        when(logRepo.findUniqueByID(log2.getId())).thenReturn(log2);

//	When
        Set<Usermetadata> usermetadataSet = userMetadataService.getUserMetadataByLogs(Arrays.asList(log1.getId(),
                log2.getId()),
                UserMetadataTypeEnum.FILTER);
//	Then
        assertThat(usermetadataSet).containsAll(usermetadataSetExpect);

    }

    @Test
    public void testGetUserMetadataByLogsReturnEmptySet() {

        List<Integer> logIds = new ArrayList<>();
        Set<Usermetadata> usermetadataSetExpect = new HashSet<>();

        Set<Usermetadata> usermetadataSet = userMetadataService.getUserMetadataByLogs(logIds,
                UserMetadataTypeEnum.FILTER);

        assertThat(usermetadataSet).isEmpty();
    }

    @Test
    public void testCanUserEditMetadata_returnFalse() throws UserNotFoundException {

        // Given
        User user = userBuilder.withUser("test_username", "RowGuid")
                .withDummyUserGroup()
                .buildUser();

        UsermetadataType usermetadataType1 = userBuilder
                .withUserMetaDataType("FILTER", 1, true, 1)
                .getUserMetaDataType();

        Usermetadata um = userBuilder
                .withUserMetaData("test content", "user_UUID", true, 1, usermetadataType1)
                .getUserMetaData();

        Usermetadata um1 = userBuilder
                .withUserMetaData("test content", "user_UUID", true, 1, usermetadataType1)
                .getUserMetaData();

        List<GroupUsermetadata> groupUsermetadataList = userBuilder
                .withGroupUserMetaData(user.getGroups().iterator().next(), um, true, false, false)
                .withGroupUserMetaData(new Group(3), um, true, true, false)
                .getGroupUserMetaDataList();

        Integer usermetadataId = 1;

        when(userSrv.findUserByLogin(user.getUsername())).thenReturn(user);
        when(groupUsermetadataRepo.findByLogAndUser(usermetadataId, user.getRowGuid())).thenReturn(groupUsermetadataList);

//	When
        boolean result = userMetadataService.canUserEditMetadata(user.getUsername(), 1);
//	Then
        assertThat(result).isFalse();
    }

    @Test
    public void testCanUserEditMetadata_returnTrue() throws UserNotFoundException {

        // Given
        User user = userBuilder.withUser("test_username", "RowGuid")
                .withDummyUserGroup()
                .buildUser();

        UsermetadataType usermetadataType1 = userBuilder
                .withUserMetaDataType("FILTER", 1, true, 1)
                .getUserMetaDataType();

        Usermetadata um = userBuilder
                .withUserMetaData("test content", "user_UUID", true, 1, usermetadataType1)
                .getUserMetaData();

        Usermetadata um1 = userBuilder
                .withUserMetaData("test content", "user_UUID", true, 1, usermetadataType1)
                .getUserMetaData();

        List<GroupUsermetadata> groupUsermetadataList = userBuilder
                .withGroupUserMetaData(user.getGroups().iterator().next(), um, true, false, false)
                .withGroupUserMetaData(new Group(3), um, true, true, true)
                .getGroupUserMetaDataList();

        Integer usermetadataId = 1;

        when(userSrv.findUserByLogin(user.getUsername())).thenReturn(user);
        when(groupUsermetadataRepo.findByLogAndUser(usermetadataId, user.getRowGuid())).thenReturn(groupUsermetadataList);

//	When
        boolean result = userMetadataService.canUserEditMetadata(user.getUsername(), 1);
//	Then
        assertThat(result).isTrue();
    }

    @Test
    @Rollback
    public void testGetUserMetadataWithoutLog() throws UserNotFoundException {

        // Given
        User user = userBuilder.withUser("test_username", "RowGuid")
                .withDummyUserGroup()
                .buildUser();

        Group group1 = new Group(1);
        Group group2 = new Group(2);

        user.getGroups().add(group1);
        user.getGroups().add(group2);

        Log log1 = new Log(1);
        Log log2 = new Log(2);

        GroupLog gl1 = new GroupLog(group1, log1, true, false, false);
        GroupLog gl2 = new GroupLog(group2, log2, true, true, false);

        log1.getGroupLogs().add(gl1);
        log2.getGroupLogs().add(gl2);

        UsermetadataType usermetadataType1 = userBuilder
                .withUserMetaDataType("FILTER", 1, true, 1)
                .getUserMetaDataType();

        UsermetadataType usermetadataType2 = userBuilder
                .withUserMetaDataType("DASHBOARD", 1, true, 2)
                .getUserMetaDataType();

        Usermetadata um = userBuilder
                .withUserMetaData("test content", "user_UUID", true, 1, usermetadataType1)
                .getUserMetaData();

        Usermetadata um1 = userBuilder
                .withUserMetaData("test content", "user_UUID", true, 1, usermetadataType2)
                .getUserMetaData();

        List<GroupUsermetadata> groupUsermetadataList = userBuilder
                .withGroupUserMetaData(user.getGroups().iterator().next(), um, true, false, false)
                .withGroupUserMetaData(user.getGroups().iterator().next(), um, true, true, true)
                .getGroupUserMetaDataList();

        log1.getUsermetadataSet().add(um);
        log2.getUsermetadataSet().add(um1);

        List<GroupLog> groupLogList = new ArrayList<>();
        groupLogList.add(gl1);
        groupLogList.add(gl2);

        Set<Usermetadata> usermetadataSetExpect = new HashSet<>();
        usermetadataSetExpect.add(um);

        when(userSrv.findUserByLogin(user.getUsername())).thenReturn(user);
        when(groupLogRepo.findLogsByUser(user.getRowGuid())).thenReturn(groupLogList);
        when(logRepo.findUniqueByID(1)).thenReturn(log1);
        when(logRepo.findUniqueByID(2)).thenReturn(log2);

        //	When
        Set<Usermetadata> usermetadataSet = userMetadataService.getUserMetadataWithoutLog(UserMetadataTypeEnum.FILTER
                , user.getUsername());

        //	Then
        assertThat(usermetadataSet).containsAll(usermetadataSetExpect);

    }

    @Test
    public void testGetDependentLog() {

        // Given
        User user = userBuilder.withUser("test_username", "RowGuid")
                .withDummyUserGroup()
                .buildUser();

        UsermetadataType usermetadataType1 = userBuilder
                .withUserMetaDataType("FILTER", 1, true, 1)
                .getUserMetaDataType();

        UsermetadataType usermetadataType2 = userBuilder
                .withUserMetaDataType("DASHBOARD", 1, true, 2)
                .getUserMetaDataType();

        Usermetadata um = userBuilder
                .withUserMetaData("test content", "user_UUID", true, 1, usermetadataType1)
                .getUserMetaData();

        Usermetadata um1 = userBuilder
                .withUserMetaData("test content", "user_UUID", true, 1, usermetadataType1)
                .getUserMetaData();

        Usermetadata um2 = userBuilder
                .withUserMetaData("test content", "user_UUID", true, 1, usermetadataType1)
                .getUserMetaData();

        List<GroupUsermetadata> groupUsermetadataList = userBuilder
                .withGroupUserMetaData(user.getGroups().iterator().next(), um, true, false, false)
                .withGroupUserMetaData(user.getGroups().iterator().next(), um, true, true, true)
                .getGroupUserMetaDataList();

        Log log1 = new Log(1);
        Set<Usermetadata> usermetadataSet1 = new HashSet<>();
        usermetadataSet1.add(um1);
        log1.setUsermetadataSet(usermetadataSet1);
        Log log2 = new Log(2);
        Set<Usermetadata> usermetadataSet2 = new HashSet<>();
        usermetadataSet2.add(um2);
        log1.setUsermetadataSet(usermetadataSet2);

        Set<Log> logs = new HashSet<>();
        logs.add(log1);
        logs.add(log2);

        List<UsermetadataLog> usermetadataLogSet = userBuilder
                .withNewUserMetaDataLogList()
                .withUserMetaDataLog(um1, log1)
                .withUserMetaDataLog(um1, log2)
                .getUserMetaDataLogList();

//	um1.setUsermetadataLog(new HashSet<UsermetadataLog>(usermetadataLogSet));
        um1.setLogs(logs);

        List<UsermetadataLog> usermetadataLogSet1 = userBuilder
                .withNewUserMetaDataLogList()
                .withUserMetaDataLog(um2, log1)
                .withUserMetaDataLog(um2, log2)
                .getUserMetaDataLogList();

//	um2.setUsermetadataLog(new HashSet<UsermetadataLog>(usermetadataLogSet1));
        um2.setLogs(logs);

        List<Log> expectedResult = new ArrayList<>();
        expectedResult.add(log1);
        expectedResult.add(log2);

//	When
        List<Log> result = userMetadataService.getDependentLog(um1);
//	Then
        assertThat(result).containsAll(expectedResult);
    }

    @Test
    public void testGetDependentProcess() {

        // Given
        User user = userBuilder.withUser("test_username", "RowGuid")
                .withDummyUserGroup()
                .buildUser();

        UsermetadataType usermetadataType1 = userBuilder
                .withUserMetaDataType("FILTER", 1, true, 1)
                .getUserMetaDataType();

        UsermetadataType usermetadataType2 = userBuilder
                .withUserMetaDataType("DASHBOARD", 1, true, 2)
                .getUserMetaDataType();

        Usermetadata um = userBuilder
                .withUserMetaData("test content", "user_UUID", true, 1, usermetadataType1)
                .getUserMetaData();

        Usermetadata um1 = userBuilder
                .withUserMetaData("test content", "user_UUID", true, 1, usermetadataType1)
                .getUserMetaData();

        Set<UsermetadataProcess> usermetadataProcessSet = new HashSet<>();

        Process p1 = new Process(1);
        Process p2 = new Process(2);
        UsermetadataProcess up1 = new UsermetadataProcess(um1, p1);
        UsermetadataProcess up2 = new UsermetadataProcess(um1, p2);

        usermetadataProcessSet.add(up1);
        usermetadataProcessSet.add(up2);

        um1.setUsermetadataProcess(usermetadataProcessSet);

        List<Process> expectedResult = new ArrayList<>();
        expectedResult.add(p1);
        expectedResult.add(p2);

        List<Process> result = userMetadataService.getDependentProcess(um1);
        assertThat(result).containsAll(expectedResult);
    }

    @Test
    public void testGetUserMetadataAccessType() {

        Group g1 = new Group(1);
        Group g2 = new Group(2);
        Group g3 = new Group(3);

        UsermetadataType usermetadataType1 = userBuilder
                .withUserMetaDataType("FILTER", 1, true, 1)
                .getUserMetaDataType();

        Usermetadata um = userBuilder
                .withUserMetaData("test content", "user_UUID", true, 1, usermetadataType1)
                .getUserMetaData();

        GroupUsermetadata gu = new GroupUsermetadata(g1, um, true, true, true);
        GroupUsermetadata gu2 = new GroupUsermetadata(g2, um, true, true, false);
        GroupUsermetadata gu3 = new GroupUsermetadata(g3, um, true, false, false);
        Set<GroupUsermetadata> groupUsermetadataSet = new HashSet<>();
        groupUsermetadataSet.add(gu);
        groupUsermetadataSet.add(gu2);
        groupUsermetadataSet.add(gu3);

        um.setGroupUserMetadata(groupUsermetadataSet);

        AccessType expectedResult = AccessType.OWNER;

        when(groupUsermetadataRepo.findByGroupAndUsermetadata(g1, um)).thenReturn(gu);
        when(groupUsermetadataRepo.findByGroupAndUsermetadata(g2, um)).thenReturn(gu2);
        when(groupUsermetadataRepo.findByGroupAndUsermetadata(g3, um)).thenReturn(gu3);


        AccessType result = userMetadataService.getUserMetadataAccessType(g1, um);
        assertThat(result).isEqualTo(expectedResult);
        assertThat(userMetadataService.getUserMetadataAccessType(g2, um)).isEqualTo(AccessType.EDITOR);
        assertThat(userMetadataService.getUserMetadataAccessType(g3, um)).isEqualTo(AccessType.VIEWER);

    }
}
