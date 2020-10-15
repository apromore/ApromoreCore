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

import java.util.*;

import org.apromore.AbstractTest;
import org.apromore.TestData;
import org.apromore.common.ConfigBean;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.GroupFolderRepository;
import org.apromore.dao.GroupLogRepository;
import org.apromore.dao.GroupProcessRepository;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.LogRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.WorkspaceRepository;
import org.apromore.dao.model.*;
import org.apromore.dao.model.Process;
import org.apromore.service.EventLogFileService;
import org.apromore.service.UserMetadataService;
import org.apromore.service.WorkspaceService;
import org.apromore.util.AccessType;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

/**
 * 
 * @author Bruce Nguyen
 *
 */
public class WorkspaceServiceImplTest extends AbstractTest {
    private WorkspaceService workspaceService;
    private WorkspaceRepository workspaceRepo;
    private FolderRepository folderRepo;
    private LogRepository logRepo;
    private EventLogFileService logFileService;
    private FolderServiceImpl folderServiceImpl;
    
    private GroupRepository groupRepo;
    private GroupFolderRepository groupFolderRepo;
    private GroupProcessRepository groupProcessRepo;
    private GroupLogRepository groupLogRepo;
    
    private ProcessRepository processRepo;
    private ProcessModelVersionRepository pmvRepo;
    private UserRepository userRepo;
    private UserMetadataService userMetadataServ;
    
    private ConfigBean config;
    
    @Before
    public final void setUp() throws Exception {
        workspaceRepo = createMock(WorkspaceRepository.class);
        groupRepo = createMock(GroupRepository.class);
        groupFolderRepo = createMock(GroupFolderRepository.class);
        groupProcessRepo = createMock(GroupProcessRepository.class);
        groupLogRepo = createMock(GroupLogRepository.class);

        logRepo = createMock(LogRepository.class);
        folderRepo = createMock(FolderRepository.class);
        logFileService = createMock(EventLogFileService.class);
        
        processRepo = createMock(ProcessRepository.class);
        pmvRepo = createMock(ProcessModelVersionRepository.class);
        userRepo = createMock(UserRepository.class);
        userMetadataServ = createMock(UserMetadataService.class);
        folderServiceImpl = createMock(FolderServiceImpl.class);
        
        config = new ConfigBean();

        workspaceService = new WorkspaceServiceImpl(workspaceRepo,
                                                userRepo,
                                                processRepo,
                                                pmvRepo,
                                                logRepo,
                                                folderRepo,
                                                groupRepo,
                                                groupFolderRepo,
                                                groupProcessRepo,
                                                groupLogRepo,
                                                logFileService,
                                                userMetadataServ, folderServiceImpl);
    }

    @Test
    public void testCopyLog() throws Exception {
        // Set up test data
        Group group = createGroup(123, Group.Type.GROUP);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        
        Workspace wp = createWorkspace(user);
        Log log = createLog(user, createFolder("sourceFolder", null, wp));
        Folder targetFolder = createFolder("targetFolder", null, wp);
        Log newLog = log.clone();
        newLog.setFolder(targetFolder);
        
        // Parameters
        Integer logId = log.getId();
        Integer targetFolderId = targetFolder.getId();
        String userName = user.getUsername();
        boolean madePublic = false;
        
        // Mock recording
        expect(logRepo.findUniqueByID(logId)).andReturn(log);
        expect(folderRepo.findUniqueByID(targetFolderId)).andReturn(targetFolder);
        expect(userRepo.findByUsername(userName)).andReturn(user);
        logFileService.copyFile(EasyMock.anyObject(), EasyMock.anyObject());
        expect(logRepo.save((Log)EasyMock.anyObject())).andReturn(newLog);
        replayAll();
        
        // Mock call
        Log copyLog = workspaceService.copyLog(logId, targetFolderId, userName, madePublic);
        
        // Verify Mock and result
        verifyAll();
        Assert.assertEquals(newLog.getFolder(), copyLog.getFolder());
        Assert.assertEquals(newLog.getUser(), copyLog.getUser());
        Assert.assertEquals(newLog.getName(), copyLog.getName());
        Assert.assertEquals(user.getGroup(), copyLog.getGroupLogs().iterator().next().getGroup());
    }
    
    @Test
    public void testMoveLog() throws Exception {
     // Set up test data
        Group group = createGroup(123, Group.Type.GROUP);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        
        Workspace wp = createWorkspace(user);
        Folder sourceFolder = createFolder("sourceFolder", null, wp);
        Folder targetFolder = createFolder("targetFolder", null, wp);
        Log log = createLog(user, sourceFolder);
        
        // Parameters
        Integer logId = log.getId();
        Integer targetFolderId = targetFolder.getId();
        
        // Mock recording
        expect(logRepo.findUniqueByID(logId)).andReturn(log);
        expect(folderRepo.findUniqueByID(targetFolderId)).andReturn(targetFolder);
        expect(logRepo.save((Log)EasyMock.anyObject())).andReturn(log);
        replayAll();
        
        Log movedLog = workspaceService.moveLog(logId, targetFolderId);
        verifyAll();
        
        Assert.assertEquals(targetFolder, movedLog.getFolder());
        Assert.assertEquals(log, movedLog);
    }
    
    
    @Test
    public void testCopyProcessVersions() throws Exception {
        // Set up test data
        Group group = createGroup(123, Group.Type.GROUP);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        NativeType nativeType = createNativeType();
        Native nativeDoc = createNative(nativeType, TestData.XPDL);
        
        Workspace wp = createWorkspace(user);
        Folder targetFolder = createFolder("targetFolder", null, wp);
        
        Folder sourceFolder = createFolder("sourceFolder", null, wp);
        Process process = createProcess(user, nativeType, sourceFolder);
        ProcessBranch branch = createBranch(process);
        ProcessModelVersion pmv1 = createPMV(branch, nativeDoc, createVersion("1.0"));
        ProcessModelVersion pmv2 = createPMV(branch, nativeDoc, createVersion("1.1"));
        ProcessModelVersion pmv3 = createPMV(branch, nativeDoc, createVersion("1.2"));
        branch.getProcessModelVersions().addAll(Arrays.asList(new ProcessModelVersion[] {pmv1, pmv2, pmv3}));
        branch.setCurrentProcessModelVersion(pmv2);
        process.getProcessBranches().add(branch);
        
        // Parameters
        Integer processId = process.getId();
        Integer targetFolderId = targetFolder.getId();
        String userName = user.getUsername();
        List<String> pmvVersions = Arrays.asList(new String[] {pmv1.getVersionNumber(), pmv2.getVersionNumber()});
        boolean madePublic = false;
        
        // Mock recording
        expect(folderRepo.findUniqueByID(targetFolderId)).andReturn(targetFolder);
        expect(userRepo.findByUsername(userName)).andReturn(user);
        expect(processRepo.findUniqueByID(processId)).andReturn(process);
        expect(processRepo.save((Process)EasyMock.anyObject())).andReturn(null); //ignore return value
        expect(pmvRepo.save((ProcessModelVersion)EasyMock.anyObject())).andReturn(null).anyTimes(); //ignore return value
        replayAll();
        
        // Mock call
        Process copyProcess = workspaceService.copyProcessVersions(processId, pmvVersions, targetFolderId, userName, madePublic);
        
        // Verify Mock and result
        verifyAll();
        Assert.assertEquals(targetFolder, copyProcess.getFolder());
        Assert.assertEquals(user, copyProcess.getUser());
        Assert.assertEquals(process.getName(), copyProcess.getName());
        Assert.assertEquals(1, copyProcess.getProcessBranches().size());
        Assert.assertEquals(process.getProcessBranches().get(0).getBranchName(), copyProcess.getProcessBranches().get(0).getBranchName());
        Assert.assertEquals(copyProcess, copyProcess.getProcessBranches().get(0).getProcess());

        Assert.assertEquals(2, copyProcess.getProcessBranches().get(0).getProcessModelVersions().size());
        Assert.assertEquals("1.0", copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(0).getVersionNumber());
        Assert.assertEquals("1.1", copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(1).getVersionNumber());
        Assert.assertEquals("1.1", copyProcess.getProcessBranches().get(0).getCurrentProcessModelVersion().getVersionNumber());
        
        Assert.assertEquals(pmv1.getNativeDocument().getContent(), copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(0).getNativeDocument().getContent());
        Assert.assertEquals(pmv2.getNativeDocument().getContent(), copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(1).getNativeDocument().getContent());
        Assert.assertEquals(copyProcess.getProcessBranches().get(0), copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(0).getProcessBranch());
        Assert.assertEquals(copyProcess.getProcessBranches().get(0), copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(1).getProcessBranch());
        
        Assert.assertEquals(user.getGroup(), copyProcess.getGroupProcesses().iterator().next().getGroup());
    }
    
    @Test
    public void testCopyProcess() throws Exception {
        // Set up test data
        Group group = createGroup(123, Group.Type.GROUP);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        NativeType nativeType = createNativeType();
        Native nativeDoc = createNative(nativeType, TestData.XPDL);
        
        Workspace wp = createWorkspace(user);
        Folder targetFolder = createFolder("targetFolder", null, wp);
        
        Folder sourceFolder = createFolder("sourceFolder", null, wp);
        Process process = createProcess(user, nativeType, sourceFolder);
        ProcessBranch branch = createBranch(process);
        ProcessModelVersion pmv1 = createPMV(branch, nativeDoc, createVersion("1.0"));
        ProcessModelVersion pmv2 = createPMV(branch, nativeDoc, createVersion("1.1"));
        ProcessModelVersion pmv3 = createPMV(branch, nativeDoc, createVersion("1.2"));
        branch.getProcessModelVersions().addAll(Arrays.asList(new ProcessModelVersion[] {pmv1, pmv2, pmv3}));
        branch.setCurrentProcessModelVersion(pmv2);
        process.getProcessBranches().add(branch);
        
        // Parameters
        Integer processId = process.getId();
        Integer targetFolderId = targetFolder.getId();
        String userName = user.getUsername();
        boolean madePublic = false;
        
        // Mock recording
        expect(processRepo.findUniqueByID(processId)).andReturn(process).anyTimes();
        expect(folderRepo.findUniqueByID(targetFolderId)).andReturn(targetFolder);
        expect(userRepo.findByUsername(userName)).andReturn(user);
        expect(processRepo.save((Process)EasyMock.anyObject())).andReturn(null); //ignore return value
        expect(pmvRepo.save((ProcessModelVersion)EasyMock.anyObject())).andReturn(null).anyTimes(); //ignore return value
        replayAll();
        
        // Mock call
        Process copyProcess = workspaceService.copyProcess(processId, targetFolderId, userName, madePublic);
        
        // Verify Mock and result
        verifyAll();
        Assert.assertEquals(targetFolder, copyProcess.getFolder());
        Assert.assertEquals(user, copyProcess.getUser());
        Assert.assertEquals(process.getName(), copyProcess.getName());
        Assert.assertEquals(1, copyProcess.getProcessBranches().size());
        Assert.assertEquals(process.getProcessBranches().get(0).getBranchName(), copyProcess.getProcessBranches().get(0).getBranchName());
        Assert.assertEquals(3, copyProcess.getProcessBranches().get(0).getProcessModelVersions().size());
        Assert.assertEquals("1.0", copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(0).getVersionNumber());
        Assert.assertEquals("1.1", copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(1).getVersionNumber());
        Assert.assertEquals("1.2", copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(2).getVersionNumber());
        Assert.assertEquals(pmv1.getNativeDocument().getContent(), copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(0).getNativeDocument().getContent());
        Assert.assertEquals(pmv2.getNativeDocument().getContent(), copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(1).getNativeDocument().getContent());
        Assert.assertEquals(pmv3.getNativeDocument().getContent(), copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(2).getNativeDocument().getContent());
        Assert.assertEquals("1.2", copyProcess.getProcessBranches().get(0).getCurrentProcessModelVersion().getVersionNumber());
        Assert.assertEquals(user.getGroup(), copyProcess.getGroupProcesses().iterator().next().getGroup());
    }
    
    @Test
    public void testMoveProcess() throws Exception {
        Group group = createGroup(123, Group.Type.GROUP);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        NativeType nativeType = createNativeType();
        
        Workspace wp = createWorkspace(user);
        Folder sourceFolder = createFolder("sourceFolder", null, wp);
        Folder targetFolder = createFolder("targetFolder", null, wp);
        Process process = createProcess(user, nativeType, sourceFolder);
        
        // Parameters
        Integer processId = process.getId();
        Integer targetFolderId = targetFolder.getId();
        
        // Mock recording
        expect(folderRepo.findUniqueByID(targetFolderId)).andReturn(targetFolder);
        expect(processRepo.findUniqueByID(processId)).andReturn(process);
        expect(processRepo.save((Process)EasyMock.anyObject())).andReturn(process);
        replayAll();
        
        Process movedProcess = workspaceService.moveProcess(processId, targetFolderId);

        // Verify mock and result
        verifyAll();
        Assert.assertEquals(targetFolder, movedProcess.getFolder());
        Assert.assertEquals(process, movedProcess);
    }
    
    @Test
    public void testMoveFolder() throws Exception {
        Group group = createGroup(123, Group.Type.GROUP);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        
        Workspace wp = createWorkspace(user);
        Folder folder = createFolder("movedFolder", null, wp);
        Folder newParentFolder = createFolder("newParentFolder", null, wp);
        
        // Parameters
        Integer folderId = folder.getId();
        Integer newParentFolderId = newParentFolder.getId();
        
        // Mock recording
        expect(folderRepo.findUniqueByID(folderId)).andReturn(folder);
        expect(folderRepo.findUniqueByID(newParentFolderId)).andReturn(newParentFolder);
        expect(folderRepo.save((Folder)EasyMock.anyObject())).andReturn(null); //ignore return value
        replayAll();
        
        Folder movedFolder = workspaceService.moveFolder(folderId, newParentFolderId);

        // Verify mock and result
        verifyAll();
        Assert.assertEquals(folder.getName(), movedFolder.getName());
        Assert.assertEquals(newParentFolder, movedFolder.getParentFolder());
    }

    @Test
    public void getLogACL() {

        // Set up test data
        Group group1 = createGroup(1, Group.Type.GROUP);
        Group group2 = createGroup(2, Group.Type.GROUP);
        Group group3 = createGroup(3, Group.Type.USER);
        Group group4 = createGroup(4, Group.Type.USER);

        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group1, createSet(group1), createSet(role));

        Workspace wp = createWorkspace(user);
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
        Map<Group, AccessType> groupAccessTypeMap = workspaceService.getLogACL(123);

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
    public void getProcessACL() {

        // Set up test data
        Group group1 = createGroup(1, Group.Type.GROUP);
        Group group2 = createGroup(2, Group.Type.GROUP);
        Group group3 = createGroup(3, Group.Type.USER);
        Group group4 = createGroup(4, Group.Type.USER);

        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group1, createSet(group1), createSet(role));
        NativeType nativeType = createNativeType();

        Workspace wp = createWorkspace(user);
        Folder folder = createFolder("sourceFolder", null, wp);
        Process process = createProcess(user, nativeType, folder);

        List<GroupProcess> groupProcesses = new ArrayList<>();
        groupProcesses.add(new GroupProcess(process, group1, new AccessRights(true,true,true)));
        groupProcesses.add(new GroupProcess(process, group2, true, true, false));
        groupProcesses.add(new GroupProcess(process, group3, true, false, false));
        groupProcesses.add(new GroupProcess(process, group4, false, false, false));

        // Mock recording
        expect(workspaceService.getGroupProcesses(123)).andReturn(groupProcesses);
        replayAll();

        // Mock call
        Map<Group, AccessType> groupAccessTypeMap = workspaceService.getProcessACL(123);

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
    public void getFolderACL() {

        // Set up test data
        Group group1 = createGroup(1, Group.Type.GROUP);
        Group group2 = createGroup(2, Group.Type.GROUP);
        Group group3 = createGroup(3, Group.Type.USER);
        Group group4 = createGroup(4, Group.Type.USER);

        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group1, createSet(group1), createSet(role));

        Workspace wp = createWorkspace(user);
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
        Map<Group, AccessType> groupAccessTypeMap = workspaceService.getFolderACL(123);

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
