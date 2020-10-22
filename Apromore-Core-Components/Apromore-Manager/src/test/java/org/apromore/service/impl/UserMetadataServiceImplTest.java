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

import org.apromore.common.ConfigBean;
import org.apromore.dao.*;
import org.apromore.dao.model.*;
import org.apromore.dao.model.Process;
import org.apromore.exception.UserNotFoundException;
import org.apromore.service.UserMetadataService;
import org.apromore.service.UserService;
import org.apromore.util.AccessType;
import org.apromore.util.UserMetadataTypeEnum;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.powermock.api.easymock.PowerMock.*;

public class UserMetadataServiceImplTest {

    //TODO: Rewrite unit test using UserMetadataBuilder and BaseTestClass

    private static final Logger LOGGER = LoggerFactory.getLogger(UserMetadataServiceImplTest.class);
    // inject EntityManager for simple test
    private static EntityManagerFactory emf;
    private EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("TESTApromore");
        }
        return emf;
    }

    private LogRepository logRepo;
    private GroupLogRepository groupLogRepo;
    private UserService userSrv;
    private GroupUsermetadataRepository groupUsermetadataRepo;
    private UsermetadataRepository userMetadataRepo;
    private UsermetadataTypeRepository usermetadataTypeRepo;
    private UsermetadataLogRepository usermetadataLogRepo;
    private GroupRepository groupRepo;
    private ConfigBean config;
    private UserMetadataService userMetadataService;

    private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private String now = dateFormat.format(new Date());

    @Before
    public final void setUp() {
        groupUsermetadataRepo = createMock(GroupUsermetadataRepository.class);
        logRepo = createMock(LogRepository.class);
        groupLogRepo = createMock(GroupLogRepository.class);
        userSrv = createMock(UserService.class);
        userMetadataRepo = createMock(UsermetadataRepository.class);
        usermetadataTypeRepo = createMock(UsermetadataTypeRepository.class);
        usermetadataLogRepo = createMock(UsermetadataLogRepository.class);
        groupRepo = createMock(GroupRepository.class);

        config = new ConfigBean();

        userMetadataService = new UserMetadataServiceImpl(logRepo, groupLogRepo, userSrv,
                groupUsermetadataRepo,
                userMetadataRepo, usermetadataTypeRepo, usermetadataLogRepo, groupRepo);
    }

    @Test
    public void testGetUserMetadataByUser() throws UserNotFoundException {

        String username = "test_username";

        User user = new User();
        String rowGuid = "RowGuid";
        user.setRowGuid(rowGuid);
        Set<Group> groupSet = new HashSet<>();
        Group g1 = new Group(1);
        groupSet.add(g1);
        user.setGroups(groupSet);

        List<Integer> logIds = new ArrayList<>();
        logIds.add(1);
        logIds.add(2);

        Log log1 = new Log(1);
        Log log2 = new Log(2);

        UsermetadataType usermetadataType1 = new UsermetadataType();
        usermetadataType1.setIsValid(true);
        usermetadataType1.setType("FILTER");
        usermetadataType1.setVersion(1);
        usermetadataType1.setId(1);

        UsermetadataType usermetadataType2 = new UsermetadataType();
        usermetadataType2.setIsValid(true);
        usermetadataType2.setType("DASHBOARD");
        usermetadataType2.setVersion(1);
        usermetadataType2.setId(2);

        Usermetadata um = new Usermetadata();
        um.setId(1);
        um.setContent("test content");
        um.setCreatedBy("user_UUID");
        um.setCreatedTime(now);
        um.setIsValid(true);
        um.setRevision(1);
        um.setUsermetadataType(usermetadataType1);
        um.setUpdatedBy("user_UUID");
        um.setUpdatedTime(now);

        Usermetadata um1 = new Usermetadata();
        um1.setId(2);
        um1.setContent("test content");
        um1.setCreatedBy("user_UUID");
        um1.setCreatedTime(now);
        um1.setIsValid(true);
        um1.setRevision(1);
        um1.setUsermetadataType(usermetadataType1);
        um1.setUpdatedBy("user_UUID");
        um1.setUpdatedTime(now);

        Usermetadata um2 = new Usermetadata();
        um2.setId(3);
        um2.setContent("test content");
        um2.setCreatedBy("user_UUID");
        um2.setCreatedTime(now);
        um2.setIsValid(true);
        um2.setRevision(1);
        um2.setUsermetadataType(usermetadataType1);
        um2.setUpdatedBy("user_UUID");
        um2.setUpdatedTime(now);

        List<GroupUsermetadata> groupUsermetadataList = new ArrayList<>();
        groupUsermetadataList.add(new GroupUsermetadata(g1, um, true, false, false));
        groupUsermetadataList.add(new GroupUsermetadata(g1, um, true, true, true));

        Set<UsermetadataLog> usermetadataLogSet = new HashSet<>();
        Set<UsermetadataLog> usermetadataLogSet1 = new HashSet<>();
        UsermetadataLog ul1 = new UsermetadataLog(um1, log1);
        UsermetadataLog ul2 = new UsermetadataLog(um1, log2);
        UsermetadataLog ul3 = new UsermetadataLog(um2, log1);
        UsermetadataLog ul4 = new UsermetadataLog(um2, log2);
        usermetadataLogSet.add(ul1);
        usermetadataLogSet.add(ul2);
        um1.setUsermetadataLog(usermetadataLogSet);
        usermetadataLogSet1.add(ul3);
        usermetadataLogSet1.add(ul4);
        um2.setUsermetadataLog(usermetadataLogSet1);

        List<UsermetadataLog> usermetadataLogList1 = new ArrayList<>();
        usermetadataLogList1.add(ul1);
        usermetadataLogList1.add(ul3);

        List<UsermetadataLog> usermetadataLogList2 = new ArrayList<>();
        usermetadataLogList2.add(ul2);
        usermetadataLogList2.add(ul4);

        Set<Usermetadata> usermetadataSetExpect = new HashSet<>();
        usermetadataSetExpect.add(um);

        expect(userSrv.findUserByLogin(username)).andReturn(user);
        expect(groupUsermetadataRepo.findByGroup(g1)).andReturn(groupUsermetadataList);
//        expect(logRepo.findUniqueByID(logIds.get(0))).andReturn(log1);
//        expect(logRepo.findUniqueByID(logIds.get(1))).andReturn(log2);
//        expect(usermetadataLogRepo.findByLog(log1)).andReturn(usermetadataLogList1);
//        expect(usermetadataLogRepo.findByLog(log2)).andReturn(usermetadataLogList2);
        replayAll();

        Set<Usermetadata> usermetadataSet = userMetadataService.getUserMetadataByUser(username,
                UserMetadataTypeEnum.FILTER);
        verifyAll();
        assertThat(usermetadataSet, equalTo(usermetadataSetExpect));

    }

    @Test
    public void testGetUserMetadataByLogs() {

        String username = "test_username";

        User user = new User();
        String rowGuid = "RowGuid";
        user.setRowGuid(rowGuid);
        Set<Group> groupSet = new HashSet<>();
        Group g1 = new Group(1);
        groupSet.add(g1);
        user.setGroups(groupSet);

        List<Integer> logIds = new ArrayList<>();
        logIds.add(1);
        logIds.add(2);

        Log log1 = new Log(1);
        Log log2 = new Log(2);

        UsermetadataType usermetadataType1 = new UsermetadataType();
        usermetadataType1.setIsValid(true);
        usermetadataType1.setType("FILTER");
        usermetadataType1.setVersion(1);
        usermetadataType1.setId(1);

        UsermetadataType usermetadataType2 = new UsermetadataType();
        usermetadataType2.setIsValid(true);
        usermetadataType2.setType("DASHBOARD");
        usermetadataType2.setVersion(1);
        usermetadataType2.setId(2);

        Usermetadata um = new Usermetadata();
        um.setId(1);
        um.setContent("test content");
        um.setCreatedBy("user_UUID");
        um.setCreatedTime(now);
        um.setIsValid(true);
        um.setRevision(1);
        um.setUsermetadataType(usermetadataType1);
        um.setUpdatedBy("user_UUID");
        um.setUpdatedTime(now);

        Usermetadata um1 = new Usermetadata();
        um1.setId(2);
        um1.setContent("test content");
        um1.setCreatedBy("user_UUID");
        um1.setCreatedTime(now);
        um1.setIsValid(true);
        um1.setRevision(1);
        um1.setUsermetadataType(usermetadataType1);
        um1.setUpdatedBy("user_UUID");
        um1.setUpdatedTime(now);

        Usermetadata um2 = new Usermetadata();
        um2.setId(3);
        um2.setContent("test content");
        um2.setCreatedBy("user_UUID");
        um2.setCreatedTime(now);
        um2.setIsValid(true);
        um2.setRevision(1);
        um2.setUsermetadataType(usermetadataType1);
        um2.setUpdatedBy("user_UUID");
        um2.setUpdatedTime(now);

        List<GroupUsermetadata> groupUsermetadataList = new ArrayList<>();
        groupUsermetadataList.add(new GroupUsermetadata(g1, um, true, false, false));
        groupUsermetadataList.add(new GroupUsermetadata(g1, um, true, true, true));

        Set<UsermetadataLog> usermetadataLogSet = new HashSet<>();
        Set<UsermetadataLog> usermetadataLogSet1 = new HashSet<>();
        UsermetadataLog ul1 = new UsermetadataLog(um1, log1);
        UsermetadataLog ul2 = new UsermetadataLog(um1, log2);
        UsermetadataLog ul3 = new UsermetadataLog(um2, log1);
        UsermetadataLog ul4 = new UsermetadataLog(um2, log2);
        usermetadataLogSet.add(ul1);
        usermetadataLogSet.add(ul2);
        um1.setUsermetadataLog(usermetadataLogSet);
        usermetadataLogSet1.add(ul3);
        usermetadataLogSet1.add(ul4);
        um2.setUsermetadataLog(usermetadataLogSet1);

        List<UsermetadataLog> usermetadataLogList1 = new ArrayList<>();
        usermetadataLogList1.add(ul1);
        usermetadataLogList1.add(ul3);

        List<UsermetadataLog> usermetadataLogList2 = new ArrayList<>();
        usermetadataLogList2.add(ul2);
        usermetadataLogList2.add(ul4);

        Set<Usermetadata> usermetadataSetExpect = new HashSet<>();
        usermetadataSetExpect.add(um1);
        usermetadataSetExpect.add(um2);

//        expect(userSrv.findUserByLogin(username)).andReturn(user);
//        expect(groupUsermetadataRepo.findByGroup(g1)).andReturn(groupUsermetadataList);
        expect(logRepo.findUniqueByID(logIds.get(0))).andReturn(log1);
        expect(logRepo.findUniqueByID(logIds.get(1))).andReturn(log2);
        expect(usermetadataLogRepo.findByLog(log1)).andReturn(usermetadataLogList1);
        expect(usermetadataLogRepo.findByLog(log2)).andReturn(usermetadataLogList2);
        replayAll();

        Set<Usermetadata> usermetadataSet = userMetadataService.getUserMetadataByLogs(logIds,
                UserMetadataTypeEnum.FILTER);
        verifyAll();
        assertThat(usermetadataSet, equalTo(usermetadataSetExpect));

    }

    @Test
    public void testGetUserMetadataByLogsReturnEmptySet() {

        List<Integer> logIds = new ArrayList<>();
        Set<Usermetadata> usermetadataSetExpect = new HashSet<>();

        Set<Usermetadata> usermetadataSet = userMetadataService.getUserMetadataByLogs(logIds,
                UserMetadataTypeEnum.FILTER);

        assertThat(usermetadataSet, equalTo(usermetadataSetExpect));
    }

    private Usermetadata generateUsermetadata () {

        Usermetadata um = new Usermetadata();
        um.setId(1);
        um.setContent("test content");
        um.setCreatedBy("user_UUID");
        um.setCreatedTime(now);
        um.setIsValid(true);
        um.setRevision(1);
        um.setUsermetadataType(generateUsermetadataType(1, "FILTER"));
        um.setUpdatedBy("user_UUID");
        um.setUpdatedTime(now);

        return um;
    }

    private UsermetadataType generateUsermetadataType (Integer id, String type) {

        UsermetadataType usermetadataType = new UsermetadataType();
        usermetadataType.setIsValid(true);
        usermetadataType.setType(type);
        usermetadataType.setVersion(1);
        usermetadataType.setId(id);

        return usermetadataType;
    }

    @Test
    @Rollback
    public void testCanUserEditMetadata_returnFalse() throws UserNotFoundException {

        String username = "test_username";
        Integer usermetadataId = 1;

        User user = new User();
        String rowGuid = "RowGuid";
        user.setRowGuid(rowGuid);

        UsermetadataType usermetadataType = new UsermetadataType();
        usermetadataType.setIsValid(true);
        usermetadataType.setType("Test Type");
        usermetadataType.setVersion(1);

        Usermetadata um = new Usermetadata();
        um.setId(1);
        um.setContent("test content");
        um.setCreatedBy("user_UUID");
        um.setCreatedTime(now);
        um.setIsValid(true);
        um.setRevision(1);
        um.setUsermetadataType(usermetadataType);
        um.setUpdatedBy("user_UUID");
        um.setUpdatedTime(now);

        Usermetadata um1 = new Usermetadata();
        um1.setId(2);
        um1.setContent("test content");
        um1.setCreatedBy("user_UUID");
        um1.setCreatedTime(now);
        um1.setIsValid(true);
        um1.setRevision(1);
        um1.setUsermetadataType(usermetadataType);
        um1.setUpdatedBy("user_UUID");
        um1.setUpdatedTime(now);

        List<GroupUsermetadata> groupUsermetadataList = new ArrayList<>();
        groupUsermetadataList.add(new GroupUsermetadata(new Group(1), um, true, false, false));
        groupUsermetadataList.add(new GroupUsermetadata(new Group(2), um, true, true, false));


        expect(userSrv.findUserByLogin(username)).andReturn(user);
        expect(groupUsermetadataRepo.findByLogAndUser(usermetadataId, rowGuid)).andReturn(groupUsermetadataList);


        replay(groupUsermetadataRepo, userSrv);
        boolean result = userMetadataService.canUserEditMetadata(username, 1);
        verify(groupUsermetadataRepo, userSrv);
        assertThat(result, equalTo(false));
    }

    @Test
    @Rollback
    public void testCanUserEditMetadata_returnTrue() throws UserNotFoundException {

        String username = "test_username";
        Integer usermetadataId = 1;

        User user = new User();
        String rowGuid = "RowGuid";
        user.setRowGuid(rowGuid);

        UsermetadataType usermetadataType = new UsermetadataType();
        usermetadataType.setIsValid(true);
        usermetadataType.setType("Test Type");
        usermetadataType.setVersion(1);

        Usermetadata um = new Usermetadata();
        um.setId(1);
        um.setContent("test content");
        um.setCreatedBy("user_UUID");
        um.setCreatedTime(now);
        um.setIsValid(true);
        um.setRevision(1);
        um.setUsermetadataType(usermetadataType);
        um.setUpdatedBy("user_UUID");
        um.setUpdatedTime(now);

        Usermetadata um1 = new Usermetadata();
        um1.setId(2);
        um1.setContent("test content");
        um1.setCreatedBy("user_UUID");
        um1.setCreatedTime(now);
        um1.setIsValid(true);
        um1.setRevision(1);
        um1.setUsermetadataType(usermetadataType);
        um1.setUpdatedBy("user_UUID");
        um1.setUpdatedTime(now);

        List<GroupUsermetadata> groupUsermetadataList = new ArrayList<>();
        groupUsermetadataList.add(new GroupUsermetadata(new Group(1), um, true, false, false));
        groupUsermetadataList.add(new GroupUsermetadata(new Group(2), um, true, true, true));


        expect(userSrv.findUserByLogin(username)).andReturn(user);
        expect(groupUsermetadataRepo.findByLogAndUser(usermetadataId, rowGuid)).andReturn(groupUsermetadataList);
        replayAll();

        boolean result = userMetadataService.canUserEditMetadata(username, 1);
        verifyAll();
        assertThat(result, equalTo(true));
    }

    @Test
    @Rollback
    public void testGetUserMetadataWithoutLog() throws UserNotFoundException {

        String username = "test_username";

        User user = new User();
        String rowGuid = "RowGuid";
        user.setRowGuid(rowGuid);
        Set<Group> groupSet = new HashSet<>();
        Group g1 = new Group(1);
        groupSet.add(g1);
        user.setGroups(groupSet);

        UsermetadataType usermetadataType1 = new UsermetadataType();
        usermetadataType1.setIsValid(true);
        usermetadataType1.setType("FILTER");
        usermetadataType1.setVersion(1);
        usermetadataType1.setId(1);

        UsermetadataType usermetadataType2 = new UsermetadataType();
        usermetadataType2.setIsValid(true);
        usermetadataType2.setType("DASHBOARD");
        usermetadataType2.setVersion(1);
        usermetadataType2.setId(2);

        Usermetadata um = new Usermetadata();
        um.setId(1);
        um.setContent("test content");
        um.setCreatedBy("user_UUID");
        um.setCreatedTime(now);
        um.setIsValid(true);
        um.setRevision(1);
        um.setUsermetadataType(usermetadataType1);
        um.setUpdatedBy("user_UUID");
        um.setUpdatedTime(now);

        Usermetadata um1 = new Usermetadata();
        um1.setId(2);
        um1.setContent("test content");
        um1.setCreatedBy("user_UUID");
        um1.setCreatedTime(now);
        um1.setIsValid(true);
        um1.setRevision(1);
        um1.setUsermetadataType(usermetadataType2);
        um1.setUpdatedBy("user_UUID");
        um1.setUpdatedTime(now);

        List<GroupUsermetadata> groupUsermetadataList = new ArrayList<>();
        groupUsermetadataList.add(new GroupUsermetadata(g1, um, true, false, false));
        groupUsermetadataList.add(new GroupUsermetadata(g1, um, true, true, true));

        Set<Usermetadata> usermetadataSetExpect = new HashSet<>();
        usermetadataSetExpect.add(um);

        expect(userSrv.findUserByLogin(username)).andReturn(user);
        expect(groupUsermetadataRepo.findByGroup(g1)).andReturn(groupUsermetadataList);
        replayAll();

        Set<Usermetadata> usermetadataSet = userMetadataService.getUserMetadataWithoutLog(UserMetadataTypeEnum.FILTER,
                username);
        verifyAll();
        assertThat(usermetadataSet, equalTo(usermetadataSetExpect));


    }

    @Test
    public void testGetDependentLog() {

        String username = "test_username";

        User user = new User();
        String rowGuid = "RowGuid";
        user.setRowGuid(rowGuid);
        Set<Group> groupSet = new HashSet<>();
        Group g1 = new Group(1);
        groupSet.add(g1);
        user.setGroups(groupSet);

        List<Integer> logIds = new ArrayList<>();
        logIds.add(1);
        logIds.add(2);

        Log log1 = new Log(1);
        Log log2 = new Log(2);

        UsermetadataType usermetadataType1 = new UsermetadataType();
        usermetadataType1.setIsValid(true);
        usermetadataType1.setType("FILTER");
        usermetadataType1.setVersion(1);
        usermetadataType1.setId(1);

        UsermetadataType usermetadataType2 = new UsermetadataType();
        usermetadataType2.setIsValid(true);
        usermetadataType2.setType("DASHBOARD");
        usermetadataType2.setVersion(1);
        usermetadataType2.setId(2);

        Usermetadata um = new Usermetadata();
        um.setId(1);
        um.setContent("test content");
        um.setCreatedBy("user_UUID");
        um.setCreatedTime(now);
        um.setIsValid(true);
        um.setRevision(1);
        um.setUsermetadataType(usermetadataType1);
        um.setUpdatedBy("user_UUID");
        um.setUpdatedTime(now);

        Usermetadata um1 = new Usermetadata();
        um1.setId(2);
        um1.setContent("test content");
        um1.setCreatedBy("user_UUID");
        um1.setCreatedTime(now);
        um1.setIsValid(true);
        um1.setRevision(1);
        um1.setUsermetadataType(usermetadataType1);
        um1.setUpdatedBy("user_UUID");
        um1.setUpdatedTime(now);

        Usermetadata um2 = new Usermetadata();
        um2.setId(3);
        um2.setContent("test content");
        um2.setCreatedBy("user_UUID");
        um2.setCreatedTime(now);
        um2.setIsValid(true);
        um2.setRevision(1);
        um2.setUsermetadataType(usermetadataType1);
        um2.setUpdatedBy("user_UUID");
        um2.setUpdatedTime(now);

        List<GroupUsermetadata> groupUsermetadataList = new ArrayList<>();
        groupUsermetadataList.add(new GroupUsermetadata(g1, um, true, false, false));
        groupUsermetadataList.add(new GroupUsermetadata(g1, um, true, true, true));

        Set<UsermetadataLog> usermetadataLogSet = new HashSet<>();
        Set<UsermetadataLog> usermetadataLogSet1 = new HashSet<>();
        UsermetadataLog ul1 = new UsermetadataLog(um1, log1);
        UsermetadataLog ul2 = new UsermetadataLog(um1, log2);
        UsermetadataLog ul3 = new UsermetadataLog(um2, log1);
        UsermetadataLog ul4 = new UsermetadataLog(um2, log2);
        usermetadataLogSet.add(ul1);
        usermetadataLogSet.add(ul2);
        um1.setUsermetadataLog(usermetadataLogSet);
        usermetadataLogSet1.add(ul3);
        usermetadataLogSet1.add(ul4);
        um2.setUsermetadataLog(usermetadataLogSet1);

        List<UsermetadataLog> usermetadataLogList1 = new ArrayList<>();
        usermetadataLogList1.add(ul1);
        usermetadataLogList1.add(ul3);

        List<UsermetadataLog> usermetadataLogList2 = new ArrayList<>();
        usermetadataLogList2.add(ul2);
        usermetadataLogList2.add(ul4);

        List<Log> expectedResult = new ArrayList<>();
        expectedResult.add(log1);
        expectedResult.add(log2);

        List<Log> result = userMetadataService.getDependentLog(um1);
        assertThat(result, equalTo(expectedResult));
    }

    @Test
    public void testGetDependentProcess() {

        String username = "test_username";

        User user = new User();
        String rowGuid = "RowGuid";
        user.setRowGuid(rowGuid);
        Set<Group> groupSet = new HashSet<>();
        Group g1 = new Group(1);
        groupSet.add(g1);
        user.setGroups(groupSet);

        List<Integer> logIds = new ArrayList<>();
        logIds.add(1);
        logIds.add(2);

        Log log1 = new Log(1);
        Log log2 = new Log(2);

        UsermetadataType usermetadataType1 = new UsermetadataType();
        usermetadataType1.setIsValid(true);
        usermetadataType1.setType("FILTER");
        usermetadataType1.setVersion(1);
        usermetadataType1.setId(1);

        UsermetadataType usermetadataType2 = new UsermetadataType();
        usermetadataType2.setIsValid(true);
        usermetadataType2.setType("DASHBOARD");
        usermetadataType2.setVersion(1);
        usermetadataType2.setId(2);

        Usermetadata um = new Usermetadata();
        um.setId(1);
        um.setContent("test content");
        um.setCreatedBy("user_UUID");
        um.setCreatedTime(now);
        um.setIsValid(true);
        um.setRevision(1);
        um.setUsermetadataType(usermetadataType1);
        um.setUpdatedBy("user_UUID");
        um.setUpdatedTime(now);

        Usermetadata um1 = new Usermetadata();
        um1.setId(2);
        um1.setContent("test content");
        um1.setCreatedBy("user_UUID");
        um1.setCreatedTime(now);
        um1.setIsValid(true);
        um1.setRevision(1);
        um1.setUsermetadataType(usermetadataType1);
        um1.setUpdatedBy("user_UUID");
        um1.setUpdatedTime(now);

        Usermetadata um2 = new Usermetadata();
        um2.setId(3);
        um2.setContent("test content");
        um2.setCreatedBy("user_UUID");
        um2.setCreatedTime(now);
        um2.setIsValid(true);
        um2.setRevision(1);
        um2.setUsermetadataType(usermetadataType1);
        um2.setUpdatedBy("user_UUID");
        um2.setUpdatedTime(now);

        List<GroupUsermetadata> groupUsermetadataList = new ArrayList<>();
        groupUsermetadataList.add(new GroupUsermetadata(g1, um, true, false, false));
        groupUsermetadataList.add(new GroupUsermetadata(g1, um, true, true, true));

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
        assertThat(result, equalTo(expectedResult));
    }

    @Test
    @Rollback
    @Ignore("For POJO testing only")
    public void insertUsermetadataTest() {

        EntityManager em = getEntityManagerFactory().createEntityManager();
        assert em != null;

        UsermetadataType usermetadataType = new UsermetadataType();
        usermetadataType.setIsValid(true);
        usermetadataType.setType("Test Type");
        usermetadataType.setVersion(1);

        Usermetadata um = new Usermetadata();
        um.setId(999);
        um.setContent("test content");
        um.setCreatedBy("user_UUID");
        um.setCreatedTime(now);
        um.setIsValid(true);
        um.setRevision(1);
        um.setUsermetadataType(usermetadataType);
        um.setUpdatedBy("user_UUID");
        um.setUpdatedTime(now);

        em.persist(um);

        Query query = em.createQuery("SELECT s FROM Usermetadata s WHERE s.id=:param1")
                .setParameter("param1", 999);
        List<Usermetadata> usermetadatas = query.getResultList();

        for (Usermetadata u : usermetadatas) {
            LOGGER.info("RESULT: " + u.getCreatedTime());
            Assert.assertEquals(u.getCreatedTime(), um.getCreatedTime());
        }
        em.close();
    }

    @Test
    @Rollback
    @Ignore("For POJO testing only")
    public void insertUsermetadataLogTest() {

        EntityManager em = getEntityManagerFactory().createEntityManager();
        assert em != null;

        UsermetadataType usermetadataType = new UsermetadataType();
        usermetadataType.setIsValid(true);
        usermetadataType.setType("Test Type");
        usermetadataType.setVersion(1);

        Usermetadata um = new Usermetadata();
        um.setContent("test content");
        um.setCreatedBy("user_UUID");
        um.setCreatedTime(now);
        um.setIsValid(true);
        um.setRevision(1);
        um.setUsermetadataType(usermetadataType);
        um.setUpdatedBy("user_UUID");
        um.setUpdatedTime(now);

        Query query = em.createQuery("SELECT s FROM Log s WHERE s.id=:param1")
                .setParameter("param1", 144);
        List<Log> logs = query.getResultList();

        Log log = logs.get(0);
        log.setCreateDate(now);

        UsermetadataLog ul = new UsermetadataLog();
        ul.setLog(log);
        ul.setUsermetadata(um);

        em.getTransaction().begin();

        em.persist(ul);

        em.getTransaction().commit();
        em.close();
    }

    @Test
    @Rollback
    @Ignore("For POJO testing only")
    public void insertGroupUsermetadataTest() {

        EntityManager em = getEntityManagerFactory().createEntityManager();
        assert em != null;

        Query query1 = em.createQuery("SELECT s FROM Usermetadata s WHERE s.id=:param1")
                .setParameter("param1", 10);
        List<Usermetadata> userMetadata = query1.getResultList();

        Usermetadata um = userMetadata.get(0);

        Query query = em.createQuery("SELECT s FROM Group s WHERE s.id=:param1")
                .setParameter("param1", 8);
        List<Group> groups = query.getResultList();

        Group group = groups.get(0);

        GroupUsermetadata gu = new GroupUsermetadata();
        gu.setGroup(group);
        gu.setUsermetadata(um);
        gu.setAccessRights(new AccessRights(true, true, true));

        em.getTransaction().begin();

        em.persist(gu);

        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void getUserMetadataAccessType() {

        Group g1 = new Group(1);
        Group g2 = new Group(2);
        Group g3 = new Group(3);

        UsermetadataType usermetadataType1 = new UsermetadataType();
        usermetadataType1.setIsValid(true);
        usermetadataType1.setType("FILTER");
        usermetadataType1.setVersion(1);
        usermetadataType1.setId(1);

        Usermetadata um = new Usermetadata();
        um.setId(1);
        um.setContent("test content");
        um.setCreatedBy("user_UUID");
        um.setCreatedTime(now);
        um.setIsValid(true);
        um.setRevision(1);
        um.setUsermetadataType(usermetadataType1);
        um.setUpdatedBy("user_UUID");
        um.setUpdatedTime(now);

        GroupUsermetadata gu = new GroupUsermetadata(g1, um, true, true, true);
        GroupUsermetadata gu2 = new GroupUsermetadata(g2, um, true, true, false);
        GroupUsermetadata gu3 = new GroupUsermetadata(g3, um, true, false, false);
        Set<GroupUsermetadata> groupUsermetadataSet = new HashSet<>();
        groupUsermetadataSet.add(gu);
        groupUsermetadataSet.add(gu2);
        groupUsermetadataSet.add(gu3);

        um.setGroupUserMetadata(groupUsermetadataSet);

        AccessType expectedResult = AccessType.OWNER;

        expect(groupUsermetadataRepo.findByGroupAndUsermetadata(g1, um)).andReturn(gu);
        expect(groupUsermetadataRepo.findByGroupAndUsermetadata(g2, um)).andReturn(gu2);
        expect(groupUsermetadataRepo.findByGroupAndUsermetadata(g3, um)).andReturn(gu3);
        replayAll();

        AccessType result = userMetadataService.getUserMetadataAccessType(g1, um);
        assertThat(result, equalTo(expectedResult));
        assertThat(userMetadataService.getUserMetadataAccessType(g2, um), equalTo(AccessType.EDITOR));
        assertThat(userMetadataService.getUserMetadataAccessType(g3, um), equalTo(AccessType.VIEWER));

    }
}
