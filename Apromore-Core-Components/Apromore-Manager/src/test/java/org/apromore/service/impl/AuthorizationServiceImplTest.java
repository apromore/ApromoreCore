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

import junit.framework.Assert;
import org.apromore.AbstractTest;
import org.apromore.dao.GroupUsermetadataRepository;
import org.apromore.dao.LogRepository;
import org.apromore.dao.UsermetadataRepository;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.*;
import org.apromore.service.UserMetadataService;
import org.apromore.service.WorkspaceService;
import org.apromore.util.AccessType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.expect;

public class AuthorizationServiceImplTest extends AbstractTest {

    private AuthorizationServiceImpl authorizationService;
    private WorkspaceService workspaceService;
    private GroupUsermetadataRepository groupUsermetadataRepository;
    private UsermetadataRepository usermetadataRepository;
    private LogRepository logRepository;


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

        workspaceService = createMock(WorkspaceService.class);
        groupUsermetadataRepository = createMock(GroupUsermetadataRepository.class);
        usermetadataRepository = createMock(UsermetadataRepository.class);
        logRepository = createMock(LogRepository.class);
        authorizationService = new AuthorizationServiceImpl(workspaceService, groupUsermetadataRepository,
                usermetadataRepository, logRepository);

        // Set up test data
        group1 = createGroup(1, Group.Type.GROUP);
        group2 = createGroup(2, Group.Type.GROUP);
        group3 = createGroup(3, Group.Type.USER);
        group4 = createGroup(4, Group.Type.USER);

        role = createRole(createSet(createPermission()));
        user = createUser("userName1", group1, createSet(group1), createSet(role));

        wp = createWorkspace(user);

        nativeType = createNativeType();

    }

    @Test
    public void testGetLogAccessType() {

        // Set up test data
        Log log = createLog(user, createFolder("testFolder", null, wp));

        List<GroupLog> groupLogs = new ArrayList<>();
        groupLogs.add(new GroupLog(group1, log, true, true, true));
        groupLogs.add(new GroupLog(group2, log, true, true, false));
        groupLogs.add(new GroupLog(group3, log, true, false, false));
        groupLogs.add(new GroupLog(group4, log, false, false, false));

        // Mock recording
        expect(workspaceService.getGroupLogs(123)).andReturn(groupLogs);
        replayAll();

        // Mock call
        Map<Group, AccessType> groupAccessTypeMap = authorizationService.getLogAccessType(123);

        // Verify Mock and result
        verifyAll();
        Map<Group, AccessType> result = new HashMap<>();
        result.put(group1, AccessType.OWNER);
        result.put(group2, AccessType.EDITOR);
        result.put(group3, AccessType.VIEWER);
        result.put(group4, AccessType.NONE);
        Assert.assertEquals(groupAccessTypeMap, result);

    }

    @Test
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
        expect(workspaceService.getGroupProcesses(123)).andReturn(groupProcesses);
        replayAll();

        // Mock call
        Map<Group, AccessType> groupAccessTypeMap = authorizationService.getProcessAccessType(123);

        // Verify Mock and result
        verifyAll();
        Map<Group, AccessType> result = new HashMap<>();
        result.put(group1, AccessType.OWNER);
        result.put(group2, AccessType.EDITOR);
        result.put(group3, AccessType.VIEWER);
        result.put(group4, AccessType.NONE);
        Assert.assertEquals(groupAccessTypeMap, result);
    }

    @Test
    public void testGetFolderAccessType() {

        // Set up test data
        Folder folder = createFolder("movedFolder", null, wp);

        List<GroupFolder> groupFolders = new ArrayList<>();
        groupFolders.add(new GroupFolder(group1, folder, true, true, true));
        groupFolders.add(new GroupFolder(group2, folder, true, true, false));
        groupFolders.add(new GroupFolder(group3, folder, true, false, false));
        groupFolders.add(new GroupFolder(group4, folder, false, false, false));

        // Mock recording
        expect(workspaceService.getGroupFolders(123)).andReturn(groupFolders);
        replayAll();

        // Mock call
        Map<Group, AccessType> groupAccessTypeMap = authorizationService.getFolderAccessType(123);

        // Verify Mock and result
        verifyAll();
        Map<Group, AccessType> result = new HashMap<>();
        result.put(group1, AccessType.OWNER);
        result.put(group2, AccessType.EDITOR);
        result.put(group3, AccessType.VIEWER);
        result.put(group4, AccessType.NONE);
        Assert.assertEquals(groupAccessTypeMap, result);
    }
}
