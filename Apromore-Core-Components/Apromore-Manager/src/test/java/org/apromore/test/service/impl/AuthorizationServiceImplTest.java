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
package org.apromore.test.service.impl;

import junit.framework.Assert;
import org.apromore.AbstractTest;
import org.apromore.builder.UserManagementBuilder;
import org.apromore.dao.*;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.*;
import org.apromore.service.FolderService;
import org.apromore.service.WorkspaceService;
import org.apromore.service.impl.AuthorizationServiceImpl;
import org.apromore.util.AccessType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;

import java.util.*;

import static org.easymock.EasyMock.expect;

public class AuthorizationServiceImplTest extends AbstractTest {

    private AuthorizationServiceImpl authorizationService;
    private WorkspaceService workspaceService;
    private GroupUsermetadataRepository groupUsermetadataRepository;
    private GroupLogRepository groupLogRepository;
    private UsermetadataRepository usermetadataRepository;
    private LogRepository logRepository;
    private FolderService folderService;
    private ProcessRepository processRepository;
    private FolderRepository folderRepository;
    private GroupRepository groupRepository;
    private GroupProcessRepository groupProcessRepository;
    private GroupFolderRepository groupFolderRepository;

    private Group group1;
    private Group group2;
    private Group group3;
    private Group group4;
    private Role role;
    private User user;
    private Workspace wp;
    private NativeType nativeType;

    @Before
    public void setUp() {

        groupUsermetadataRepository = createMock(GroupUsermetadataRepository.class);
        groupLogRepository = createMock(GroupLogRepository.class);
        usermetadataRepository = createMock(UsermetadataRepository.class);
        logRepository = createMock(LogRepository.class);
        folderService = createMock(FolderService.class);
        processRepository = createMock(ProcessRepository.class);
        folderRepository = createMock(FolderRepository.class);
        groupRepository = createMock(GroupRepository.class);
        groupProcessRepository = createMock(GroupProcessRepository.class);
        groupFolderRepository = createMock(GroupFolderRepository.class);

        authorizationService = new AuthorizationServiceImpl(groupUsermetadataRepository,
                usermetadataRepository, logRepository, folderService, processRepository,
                folderRepository, groupRepository, groupLogRepository, groupProcessRepository, groupFolderRepository);

        // Set up test data
        group1 = createGroup(1, Group.Type.GROUP);
        group2 = createGroup(2, Group.Type.GROUP);
        group3 = createGroup(3, Group.Type.USER);
        group4 = createGroup(4, Group.Type.USER);

        role = createRole(createSet(createPermission()));
        user = createUser("userName1", group1, createSet(group1, group3), createSet(role));

        wp = createWorkspace(user);

        nativeType = createNativeType();

    }

    @Test
    @Rollback
    public void testGetLogAccessType() {

        // Set up test data
        Log log = createLog(user, createFolder("testFolder", null, wp));

        List<GroupLog> groupLogs = new ArrayList<>();
        groupLogs.add(new GroupLog(group1, log, true, true, true));
        groupLogs.add(new GroupLog(group2, log, true, true, false));
        groupLogs.add(new GroupLog(group3, log, true, false, false));
        groupLogs.add(new GroupLog(group4, log, false, false, false));

        // Mock recording
        expect(authorizationService.getGroupLogs(123)).andReturn(groupLogs);
        replayAll();

        // Mock call
        Map<Group, AccessType> groupAccessTypeMap = authorizationService.getLogAccessType(123);

        // Verify Mock and result
        verifyAll();
        Map<Group, AccessType> result = new HashMap<>();
        result.put(group1, AccessType.OWNER);
        result.put(group2, AccessType.EDITOR);
        result.put(group3, AccessType.VIEWER);
        result.put(group4, AccessType.RESTRICTED);
        Assert.assertEquals(groupAccessTypeMap, result);

    }

    @Test
    @Rollback
    public void testGetProcessAccessType() {

        // Set up test data
        Folder folder = createFolder("sourceFolder", null, wp);
        Process process = createProcess(user, nativeType, folder);

        List<GroupProcess> groupProcesses = new ArrayList<>();
        groupProcesses.add(new GroupProcess(process, group1, new AccessRights(true, true, true)));
        groupProcesses.add(new GroupProcess(process, group2, true, true, false));
        groupProcesses.add(new GroupProcess(process, group3, true, false, false));
        groupProcesses.add(new GroupProcess(process, group4, false, false, false));

        // Mock recording
        expect(authorizationService.getGroupProcesses(123)).andReturn(groupProcesses);
        replayAll();

        // Mock call
        Map<Group, AccessType> groupAccessTypeMap = authorizationService.getProcessAccessType(123);

        // Verify Mock and result
        verifyAll();
        Map<Group, AccessType> result = new HashMap<>();
        result.put(group1, AccessType.OWNER);
        result.put(group2, AccessType.EDITOR);
        result.put(group3, AccessType.VIEWER);
        result.put(group4, AccessType.RESTRICTED);
        Assert.assertEquals(groupAccessTypeMap, result);
    }

    @Test
    @Rollback
    public void testGetFolderAccessType() {

        // Set up test data
        Folder folder = createFolder("movedFolder", null, wp);

        List<GroupFolder> groupFolders = new ArrayList<>();
        groupFolders.add(new GroupFolder(group1, folder, true, true, true));
        groupFolders.add(new GroupFolder(group2, folder, true, true, false));
        groupFolders.add(new GroupFolder(group3, folder, true, false, false));
        groupFolders.add(new GroupFolder(group4, folder, false, false, false));

        // Mock recording
        expect(authorizationService.getGroupFolders(123)).andReturn(groupFolders);
        replayAll();

        // Mock call
        Map<Group, AccessType> groupAccessTypeMap = authorizationService.getFolderAccessType(123);

        // Verify Mock and result
        verifyAll();
        Map<Group, AccessType> result = new HashMap<>();
        result.put(group1, AccessType.OWNER);
        result.put(group2, AccessType.EDITOR);
        result.put(group3, AccessType.VIEWER);
        result.put(group4, AccessType.RESTRICTED);
        Assert.assertEquals(groupAccessTypeMap, result);
    }

    @Test
    @Rollback
    public void testGetLeastRestrictiveAccessType() {
        List<AccessType> accessTypes = new ArrayList<>();
        accessTypes.add(AccessType.RESTRICTED);
        accessTypes.add(AccessType.VIEWER);
        accessTypes.add(AccessType.EDITOR);
        accessTypes.add(AccessType.OWNER);

        Assert.assertEquals(authorizationService.getLeastRestrictiveAccessType(accessTypes), AccessType.OWNER);
    }

    @Test
    @Rollback
    public void testGetLeastRestrictiveAccessTypeAndGroup() {
        Map<Group, AccessType> accessTypes = new HashMap<>();
        Map<Group, AccessType> result = new HashMap<>();

        accessTypes.put(group1, AccessType.RESTRICTED);
        accessTypes.put(group2, AccessType.VIEWER);
        accessTypes.put(group3, AccessType.EDITOR);
        accessTypes.put(group4, AccessType.OWNER);

        result.put(group4, AccessType.OWNER);

        Assert.assertEquals(authorizationService.getLeastRestrictiveAccessTypeAndGroup(accessTypes), result);
    }

    @Test
    @Rollback
    public void testGetLeastRestrictiveAccessTypeAndGroupReturnMultiResult() {
        Map<Group, AccessType> accessTypes = new HashMap<>();
        Map<Group, AccessType> result = new HashMap<>();

        accessTypes.put(group1, AccessType.RESTRICTED);
        accessTypes.put(group2, AccessType.RESTRICTED);

        result.put(group1, AccessType.RESTRICTED);
        result.put(group2, AccessType.RESTRICTED);

        Assert.assertEquals(authorizationService.getLeastRestrictiveAccessTypeAndGroup(accessTypes), result);
    }

    @Test
    @Rollback
    public void testGetLeastRestrictiveAccessType_False() {
        List<AccessType> accessTypes = new ArrayList<>();
        accessTypes.add(AccessType.VIEWER);
        accessTypes.add(AccessType.EDITOR);
        accessTypes.add(AccessType.OWNER);

        Assert.assertNotSame(authorizationService.getLeastRestrictiveAccessType(accessTypes), AccessType.RESTRICTED);
    }

    @Test
    @Rollback
    public void testGetMostRestrictiveAccessType() {
        List<AccessType> accessTypes = new ArrayList<>();
        accessTypes.add(AccessType.VIEWER);
        accessTypes.add(AccessType.EDITOR);
        accessTypes.add(AccessType.OWNER);

        Assert.assertEquals(authorizationService.getMostRestrictiveAccessType(accessTypes), AccessType.VIEWER);
    }

    @Test
    @Rollback
    public void testGetMostRestrictiveAccessTypeReturnRestrictedViewer() {
        List<AccessType> accessTypes = new ArrayList<>();
        accessTypes.add(AccessType.RESTRICTED);
        accessTypes.add(AccessType.VIEWER);
        accessTypes.add(AccessType.EDITOR);
        accessTypes.add(AccessType.OWNER);

        Assert.assertEquals(authorizationService.getMostRestrictiveAccessType(accessTypes), AccessType.RESTRICTED);
    }

    @Test
    @Rollback
    public void testGetMostRestrictiveAccessTypeAndGroup() {
        Map<Group, AccessType> accessTypes = new HashMap<>();
        Map<Group, AccessType> result = new HashMap<>();

        accessTypes.put(group1, AccessType.RESTRICTED);
        accessTypes.put(group2, AccessType.VIEWER);
        accessTypes.put(group3, AccessType.EDITOR);
        accessTypes.put(group4, AccessType.OWNER);

        result.put(group1, AccessType.RESTRICTED);

        Assert.assertEquals(authorizationService.getMostRestrictiveAccessTypeAndGroup(accessTypes), result);
    }

    @Test
    @Rollback
    public void testGetMostRestrictiveAccessTypeAndGroupReturnMultiResult() {
        Map<Group, AccessType> accessTypes = new HashMap<>();
        Map<Group, AccessType> result = new HashMap<>();

        accessTypes.put(group1, AccessType.OWNER);
        accessTypes.put(group2, AccessType.OWNER);

        result.put(group1, AccessType.OWNER);
        result.put(group2, AccessType.OWNER);

        Assert.assertEquals(authorizationService.getMostRestrictiveAccessTypeAndGroup(accessTypes), result);
    }

    @Test
    @Rollback
    public void testGetLogAccessTypeByUser() {

        UserManagementBuilder builder = new UserManagementBuilder();

        // given
        Group userGroup = builder.withGroup("testGroup", "USER").buildGroup();

        // Set up test data
        Log log = createLog(user, createFolder("testFolder", null, wp));

        List<GroupLog> groupLogs = new ArrayList<>();
        groupLogs.add(new GroupLog(group1, log, true, true, true));
        groupLogs.add(new GroupLog(group2, log, true, true, false));
        groupLogs.add(new GroupLog(group3, log, true, false, false));
        groupLogs.add(new GroupLog(group4, log, false, false, false));

        Map<Group, AccessType> accessTypes = new HashMap<>();
        accessTypes.put(group1, AccessType.OWNER);
        accessTypes.put(group2, AccessType.EDITOR);
        accessTypes.put(group3, AccessType.VIEWER);
        accessTypes.put(group4, AccessType.RESTRICTED);


        // Mock recording
        expect(authorizationService.getGroupLogs(123)).andReturn(groupLogs);
        replayAll();

        // Mock call
        AccessType accessType = authorizationService.getLogAccessTypeByUser(123, user);

        // Verify Mock and result
        verifyAll();
        Assert.assertEquals(accessType, AccessType.OWNER);
    }

    @Test
    @Rollback
    public void testGetLogsAccessTypeByUser() {

        // Set up test data
        Log log1 = createLogWithId(1, user, createFolder("testFolder", null, wp));
        Log log2 = createLogWithId(2, user, createFolder("testFolder", null, wp));
        Set<Log> logs = new HashSet<>();
        logs.add(log1);
        logs.add(log2);

        List<GroupLog> groupLogs1 = new ArrayList<>();
        groupLogs1.add(new GroupLog(group1, log1, true, true, true));
        groupLogs1.add(new GroupLog(group2, log1, true, true, false));
        groupLogs1.add(new GroupLog(group3, log1, true, false, false));
        groupLogs1.add(new GroupLog(group4, log1, false, false, false));

        List<GroupLog> groupLogs2 = new ArrayList<>();
        groupLogs2.add(new GroupLog(group1, log2, true, true, true));
        groupLogs2.add(new GroupLog(group2, log2, true, true, false));
        groupLogs2.add(new GroupLog(group3, log2, true, false, false));
        groupLogs2.add(new GroupLog(group4, log2, false, false, false));

        Map<Group, AccessType> accessTypes = new HashMap<>();
        accessTypes.put(group1, AccessType.OWNER);
        accessTypes.put(group2, AccessType.EDITOR);
        accessTypes.put(group3, AccessType.VIEWER);
        accessTypes.put(group4, AccessType.RESTRICTED);


        // Mock recording
        expect(authorizationService.getGroupLogs(1)).andReturn(groupLogs1);
        expect(authorizationService.getGroupLogs(2)).andReturn(groupLogs2);
        replayAll();

        // Mock call
        AccessType accessType = authorizationService.getLogsAccessTypeByUser(logs, user);

        // Verify Mock and result
        verifyAll();
        Assert.assertEquals(accessType, AccessType.OWNER);
    }

    @Test
    @Rollback
    @Ignore
    public void testGetLogsAccessTypeByUser_ReturnRESTRICTED() {

        // Set up test data
        Log log1 = createLogWithId(1, user, createFolder("testFolder", null, wp));
        Log log2 = createLogWithId(2, user, createFolder("testFolder", null, wp));
        Set<Log> logs = new HashSet<>();
        logs.add(log1);
        logs.add(log2);

        List<GroupLog> groupLogs1 = new ArrayList<>();
        groupLogs1.add(new GroupLog(group4, log1, false, false, false));

        List<GroupLog> groupLogs2 = new ArrayList<>();
        groupLogs2.add(new GroupLog(group1, log2, true, true, true));
        groupLogs2.add(new GroupLog(group2, log2, true, true, false));
        groupLogs2.add(new GroupLog(group3, log2, true, false, false));
        groupLogs2.add(new GroupLog(group4, log2, false, false, false));

        Map<Group, AccessType> accessTypes = new HashMap<>();
        accessTypes.put(group1, AccessType.OWNER);
        accessTypes.put(group2, AccessType.EDITOR);
        accessTypes.put(group3, AccessType.VIEWER);
        accessTypes.put(group4, AccessType.RESTRICTED);


        // Mock recording
        expect(authorizationService.getGroupLogs(1)).andReturn(groupLogs1);
        replayAll();

        // Mock call
        AccessType accessType = authorizationService.getLogsAccessTypeByUser(logs, user);

        // Verify Mock and result
        verifyAll();
        Assert.assertEquals(accessType, AccessType.RESTRICTED);
    }

    @Test
    @Rollback
    public void testGetLogsAccessTypeByUser_ReturnViewer() {

        // Set up test data
        Log log1 = createLogWithId(1, user, createFolder("testFolder", null, wp));
        Log log2 = createLogWithId(2, user, createFolder("testFolder", null, wp));
        Set<Log> logs = new HashSet<>();
        logs.add(log1);
        logs.add(log2);

        List<GroupLog> groupLogs1 = new ArrayList<>();
        groupLogs1.add(new GroupLog(group2, log1, true, true, false));
        groupLogs1.add(new GroupLog(group3, log1, true, false, false));
        groupLogs1.add(new GroupLog(group4, log1, false, false, false));

        List<GroupLog> groupLogs2 = new ArrayList<>();
        groupLogs2.add(new GroupLog(group1, log2, true, true, true));
        groupLogs2.add(new GroupLog(group2, log2, true, true, false));
        groupLogs2.add(new GroupLog(group3, log2, true, false, false));
        groupLogs2.add(new GroupLog(group4, log2, false, false, false));

        Map<Group, AccessType> accessTypes = new HashMap<>();
        accessTypes.put(group1, AccessType.OWNER);
        accessTypes.put(group2, AccessType.EDITOR);
        accessTypes.put(group3, AccessType.VIEWER);
        accessTypes.put(group4, AccessType.RESTRICTED);


        // Mock recording
        expect(authorizationService.getGroupLogs(1)).andReturn(groupLogs1);
        expect(authorizationService.getGroupLogs(2)).andReturn(groupLogs2);
        replayAll();

        // Mock call
        AccessType accessType = authorizationService.getLogsAccessTypeByUser(logs, user);

        // Verify Mock and result
        verifyAll();
        Assert.assertEquals(accessType, AccessType.VIEWER);
    }

    @Test
    @Ignore
    @Rollback
    public void testGetLogsAccessTypeByUser_ReturnEditor() {

        // Set up test data
        Log log1 = createLogWithId(1, user, createFolder("testFolder", null, wp));
        Log log2 = createLogWithId(2, user, createFolder("testFolder", null, wp));
        Set<Log> logs = new HashSet<>();
        logs.add(log1);
        logs.add(log2);

        List<GroupLog> groupLogs1 = new ArrayList<>();
        groupLogs1.add(new GroupLog(group2, log1, true, true, false));

        List<GroupLog> groupLogs2 = new ArrayList<>();
        groupLogs2.add(new GroupLog(group1, log2, true, true, true));
        groupLogs2.add(new GroupLog(group2, log2, true, true, false));
        groupLogs2.add(new GroupLog(group3, log2, true, false, false));
        groupLogs2.add(new GroupLog(group4, log2, false, false, false));

        Map<Group, AccessType> accessTypes = new HashMap<>();
        accessTypes.put(group1, AccessType.OWNER);
        accessTypes.put(group2, AccessType.EDITOR);
        accessTypes.put(group3, AccessType.VIEWER);
        accessTypes.put(group4, AccessType.RESTRICTED);


        // Mock recording
        expect(authorizationService.getGroupLogs(1)).andReturn(groupLogs1);
        expect(authorizationService.getGroupLogs(2)).andReturn(groupLogs2);
        replayAll();

        // Mock call
        AccessType accessType = authorizationService.getLogsAccessTypeByUser(logs, user);

        // Verify Mock and result
        verifyAll();
        Assert.assertEquals(accessType, null);
    }

    @Test
    @Rollback
    public void testGetLogsAccessTypeByUser_LogIds() {

        // Set up test data
        Log log1 = createLogWithId(1, user, createFolder("testFolder", null, wp));
        Log log2 = createLogWithId(2, user, createFolder("testFolder", null, wp));

        List<Integer> logIds = new ArrayList<>();
        logIds.add(1);
        logIds.add(2);

        List<GroupLog> groupLogs1 = new ArrayList<>();
        groupLogs1.add(new GroupLog(group1, log1, true, true, true));
        groupLogs1.add(new GroupLog(group2, log1, true, true, false));
        groupLogs1.add(new GroupLog(group3, log1, true, false, false));
        groupLogs1.add(new GroupLog(group4, log1, false, false, false));

        List<GroupLog> groupLogs2 = new ArrayList<>();
        groupLogs2.add(new GroupLog(group1, log2, true, true, true));
        groupLogs2.add(new GroupLog(group2, log2, true, true, false));
        groupLogs2.add(new GroupLog(group3, log2, true, false, false));
        groupLogs2.add(new GroupLog(group4, log2, false, false, false));

        Map<Group, AccessType> accessTypes = new HashMap<>();
        accessTypes.put(group1, AccessType.OWNER);
        accessTypes.put(group2, AccessType.EDITOR);
        accessTypes.put(group3, AccessType.VIEWER);
        accessTypes.put(group4, AccessType.RESTRICTED);


        // Mock recording
        expect(authorizationService.getGroupLogs(1)).andReturn(groupLogs1);
        expect(authorizationService.getGroupLogs(2)).andReturn(groupLogs2);
        expect(logRepository.findUniqueByID(1)).andReturn(log1);
        expect(logRepository.findUniqueByID(2)).andReturn(log2);
        replayAll();

        // Mock call
        AccessType accessType = authorizationService.getLogsAccessTypeByUser(logIds, user);

        // Verify Mock and result
        verifyAll();
        Assert.assertEquals(accessType, AccessType.OWNER);
    }

    @Test
    @Rollback
    @Ignore
    public void testGetLogsAccessTypeByUser_LogIds_ReturnNone() {

        // Set up test data
        Log log1 = createLogWithId(1, user, createFolder("testFolder", null, wp));
        Log log2 = createLogWithId(2, user, createFolder("testFolder", null, wp));

        List<Integer> logIds = new ArrayList<>();
        logIds.add(1);
        logIds.add(2);

        List<GroupLog> groupLogs1 = new ArrayList<>();
        groupLogs1.add(new GroupLog(group4, log1, false, false, false));

        List<GroupLog> groupLogs2 = new ArrayList<>();
        groupLogs2.add(new GroupLog(group1, log2, true, true, true));
        groupLogs2.add(new GroupLog(group2, log2, true, true, false));
        groupLogs2.add(new GroupLog(group3, log2, true, false, false));
        groupLogs2.add(new GroupLog(group4, log2, false, false, false));

        Map<Group, AccessType> accessTypes = new HashMap<>();
        accessTypes.put(group1, AccessType.OWNER);
        accessTypes.put(group2, AccessType.EDITOR);
        accessTypes.put(group3, AccessType.VIEWER);
        accessTypes.put(group4, AccessType.RESTRICTED);


        // Mock recording
        expect(authorizationService.getGroupLogs(1)).andReturn(groupLogs1);
//        expect(workspaceService.getGroupLogs(2)).andReturn(groupLogs2);
        expect(logRepository.findUniqueByID(1)).andReturn(log1);
        expect(logRepository.findUniqueByID(2)).andReturn(log2);
        replayAll();

        // Mock call
        AccessType accessType = authorizationService.getLogsAccessTypeByUser(logIds, user);

        // Verify Mock and result
        verifyAll();
        Assert.assertEquals(accessType, AccessType.RESTRICTED);
    }

    @Test
    @Rollback
    public void testGetLogAccessTypeByUser_ReturnNull() {

        // Set up test data
        Log log = createLog(user, createFolder("testFolder", null, wp));

        List<GroupLog> groupLogs = new ArrayList<>();

        groupLogs.add(new GroupLog(group4, log, false, false, false));

        // Mock recording
        expect(authorizationService.getGroupLogs(123)).andReturn(groupLogs);
        replayAll();

        // Mock call
        AccessType accessType = authorizationService.getLogAccessTypeByUser(123, user);

        // Verify Mock and result
        verifyAll();
        Assert.assertEquals(accessType, null);
    }

    @Test
    @Rollback
    public void testGetProcessAccessTypeByUser() {

        // Set up test data
        Log log = createLog(user, createFolder("testFolder", null, wp));

        Process process = new Process(123);

        List<GroupProcess> groupProcesses = new ArrayList<>();
        groupProcesses.add(new GroupProcess(process, group1, true, true, true));
        groupProcesses.add(new GroupProcess(process, group2, true, true, false));
        groupProcesses.add(new GroupProcess(process, group3, true, false, false));
        groupProcesses.add(new GroupProcess(process, group4, false, false, false));

        Map<Group, AccessType> accessTypes = new HashMap<>();
        accessTypes.put(group1, AccessType.OWNER);
        accessTypes.put(group2, AccessType.EDITOR);
        accessTypes.put(group3, AccessType.VIEWER);
        accessTypes.put(group4, AccessType.RESTRICTED);


        // Mock recording
        expect(authorizationService.getGroupProcesses(123)).andReturn(groupProcesses);
        replayAll();

        // Mock call
        AccessType accessType = authorizationService.getProcessAccessTypeByUser(123, user);

        // Verify Mock and result
        verifyAll();
        Assert.assertEquals(accessType, AccessType.OWNER);
    }

    @Test
    @Rollback
    public void testGetFolderAccessTypeByUser() {

        // Set up test data
        Folder folder = createFolder("testFolder", null, wp);

        List<GroupFolder> groupFolders = new ArrayList<>();
        groupFolders.add(new GroupFolder(group1, folder, true, true, true));
        groupFolders.add(new GroupFolder(group2, folder, true, true, false));
        groupFolders.add(new GroupFolder(group3, folder, true, false, false));
        groupFolders.add(new GroupFolder(group4, folder, false, false, false));

        Map<Group, AccessType> accessTypes = new HashMap<>();
        accessTypes.put(group1, AccessType.OWNER);
        accessTypes.put(group2, AccessType.EDITOR);
        accessTypes.put(group3, AccessType.VIEWER);
        accessTypes.put(group4, AccessType.RESTRICTED);


        // Mock recording
        expect(authorizationService.getGroupFolders(123)).andReturn(groupFolders);
        replayAll();

        // Mock call
        AccessType accessType = authorizationService.getFolderAccessTypeByUser(123, user);

        // Verify Mock and result
        verifyAll();
        Assert.assertEquals(accessType, AccessType.OWNER);
    }
}
