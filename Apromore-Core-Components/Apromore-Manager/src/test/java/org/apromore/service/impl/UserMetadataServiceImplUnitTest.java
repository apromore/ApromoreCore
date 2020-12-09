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

import static org.easymock.EasyMock.expect;
import static org.hamcrest.Matchers.equalTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verify;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apromore.builder.UserManagementBuilder;
import org.apromore.dao.GroupLogRepository;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.GroupUsermetadataRepository;
import org.apromore.dao.LogRepository;
import org.apromore.dao.UsermetadataLogRepository;
import org.apromore.dao.UsermetadataProcessRepository;
import org.apromore.dao.UsermetadataRepository;
import org.apromore.dao.UsermetadataTypeRepository;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.GroupUsermetadata;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.User;
import org.apromore.dao.model.Usermetadata;
import org.apromore.dao.model.UsermetadataLog;
import org.apromore.dao.model.UsermetadataProcess;
import org.apromore.dao.model.UsermetadataType;
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

@RunWith(MockitoJUnitRunner.class)
public class UserMetadataServiceImplUnitTest {

    // TODO: Rewrite unit test using UserMetadataBuilder and BaseTestClass

    private static final Logger LOGGER = LoggerFactory.getLogger(UserMetadataServiceImplUnitTest.class);

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

    UserManagementBuilder userBuilder;

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
	Log log2 = new Log(2);

	List<UsermetadataLog> usermetadataLogSet = userBuilder
		.withNewUserMetaDataLogList()
		.withUserMetaDataLog(um1, log1)
		.withUserMetaDataLog(um1, log2)
		.getUserMetaDataLogList();

	um1.setUsermetadataLog(new HashSet<UsermetadataLog>(usermetadataLogSet));

	List<UsermetadataLog> usermetadataLogSet1 = userBuilder
		.withNewUserMetaDataLogList()
		.withUserMetaDataLog(um2, log1)
		.withUserMetaDataLog(um2, log2)
		.getUserMetaDataLogList();

	um2.setUsermetadataLog(new HashSet<UsermetadataLog>(usermetadataLogSet1));
	Set<Usermetadata> usermetadataSetExpect = new HashSet<>();
	usermetadataSetExpect.add(um1);
	usermetadataSetExpect.add(um2);

	when(logRepo.findUniqueByID(log1.getId())).thenReturn(log1);
	when(logRepo.findUniqueByID(log2.getId())).thenReturn(log2);
	when(usermetadataLogRepo.findByLog(log1)).thenReturn(usermetadataLogSet);
	when(usermetadataLogRepo.findByLog(log2)).thenReturn(usermetadataLogSet1);

	Set<Usermetadata> usermetadataSet = userMetadataService.getUserMetadataByLogs(Arrays.asList(log1.getId(), log2.getId()),
		UserMetadataTypeEnum.FILTER);

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

	boolean result = userMetadataService.canUserEditMetadata(user.getUsername(), 1);

	assertThat(result).isFalse();
    }

    @Test
    @Rollback
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

	boolean result = userMetadataService.canUserEditMetadata(user.getUsername(), 1);
	assertThat(result).isTrue();
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
	assertThat(usermetadataSet).containsAll(usermetadataSetExpect);

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
	assertThat(result).containsAll(expectedResult);
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
	assertThat(result).containsAll(expectedResult);
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
	assertThat(result).isEqualTo(expectedResult);
	assertThat(userMetadataService.getUserMetadataAccessType(g2, um)).isEqualTo(AccessType.EDITOR);
	assertThat(userMetadataService.getUserMetadataAccessType(g3, um)).isEqualTo(AccessType.VIEWER);

    }
}
