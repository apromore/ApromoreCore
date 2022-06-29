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

package org.apromore.service.impl;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apromore.AbstractTest;
import org.apromore.TestData;
import org.apromore.commons.config.ConfigBean;
import org.apromore.dao.CustomCalendarRepository;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.GroupFolderRepository;
import org.apromore.dao.GroupLogRepository;
import org.apromore.dao.GroupProcessRepository;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.GroupUsermetadataRepository;
import org.apromore.dao.LogRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.StorageRepository;
import org.apromore.dao.SubprocessProcessRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.UsermetadataRepository;
import org.apromore.dao.WorkspaceRepository;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.GroupFolder;
import org.apromore.dao.model.GroupLog;
import org.apromore.dao.model.GroupProcess;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.SubprocessProcess;
import org.apromore.dao.model.User;
import org.apromore.dao.model.Workspace;
import org.apromore.service.EventLogFileService;
import org.apromore.service.EventLogService;
import org.apromore.service.WorkspaceService;
import org.apromore.storage.StorageClient;
import org.apromore.storage.factory.StorageManagementFactory;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * 
 * @author Bruce Nguyen
 *
 */
class WorkspaceServiceImplTest extends AbstractTest {
    private WorkspaceService workspaceService;
    private WorkspaceRepository workspaceRepo;
    private FolderRepository folderRepo;
    private LogRepository logRepo;
    private UsermetadataRepository usermetadataRepo;
    private EventLogFileService logFileService;
    private FolderServiceImpl folderServiceImpl;
    
    private GroupRepository groupRepo;
    private GroupFolderRepository groupFolderRepo;
    private GroupProcessRepository groupProcessRepo;
    private GroupLogRepository groupLogRepo;
    private GroupUsermetadataRepository groupUsermetadataRepo;
    private CustomCalendarRepository customCalendarRepository;
    private SubprocessProcessRepository subprocessProcessRepo;
    
    private ProcessRepository processRepo;
    private ProcessModelVersionRepository pmvRepo;
    private UserRepository userRepo;
    private StorageRepository storageRepository;
    private EventLogService eventLogService;
    private StorageManagementFactory<StorageClient> storageFactory;

    private ConfigBean config;
    
    @BeforeEach
    final void setUp() throws Exception {
        workspaceRepo = createMock(WorkspaceRepository.class);
        groupRepo = createMock(GroupRepository.class);
        groupFolderRepo = createMock(GroupFolderRepository.class);
        groupProcessRepo = createMock(GroupProcessRepository.class);
        groupLogRepo = createMock(GroupLogRepository.class);
        groupUsermetadataRepo = createMock(GroupUsermetadataRepository.class);
        customCalendarRepository = createMock(CustomCalendarRepository.class);
        subprocessProcessRepo = createMock(SubprocessProcessRepository.class);

        logRepo = createMock(LogRepository.class);
        usermetadataRepo = createMock(UsermetadataRepository.class);
        folderRepo = createMock(FolderRepository.class);
        logFileService = createMock(EventLogFileService.class);
        
        processRepo = createMock(ProcessRepository.class);
        pmvRepo = createMock(ProcessModelVersionRepository.class);
        userRepo = createMock(UserRepository.class);
        folderServiceImpl = createMock(FolderServiceImpl.class);
        storageFactory = createMock(StorageManagementFactory.class);
        eventLogService = createMock(EventLogService.class);
        storageRepository = createMock(StorageRepository.class);

        config = new ConfigBean();

        workspaceService = new WorkspaceServiceImpl(workspaceRepo,
                                                userRepo,
                                                processRepo,
                                                pmvRepo,
                                                logRepo,
                                                usermetadataRepo,
                                                folderRepo,
                                                groupRepo,
                                                groupFolderRepo,
                                                groupProcessRepo,
                                                groupLogRepo,
                                                groupUsermetadataRepo,
                                                customCalendarRepository,
                                                subprocessProcessRepo,
                                                logFileService,
                                                folderServiceImpl,
                                                storageFactory,
                                                eventLogService,
                                                storageRepository,
                                                config);
    }

    @Test
    @Disabled
    void testCopyLog() throws Exception {
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
        logFileService.copyFile(EasyMock.anyObject(InputStream.class), EasyMock.anyObject(OutputStream.class));
        expect(logRepo.save((Log)EasyMock.anyObject())).andReturn(newLog);
        replayAll();
        
        // Mock call
        Log copyLog = workspaceService.copyLog(logId, targetFolderId, userName, madePublic);
        
        // Verify Mock and result
        verifyAll();
        assertEquals(newLog.getFolder(), copyLog.getFolder());
        assertEquals(newLog.getUser(), copyLog.getUser());
        assertEquals(newLog.getName(), copyLog.getName());
        assertEquals(user.getGroup(), copyLog.getGroupLogs().iterator().next().getGroup());
    }
    
    @Test
    void testMoveLog() throws Exception {
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
        
        assertEquals(targetFolder, movedLog.getFolder());
        assertEquals(log, movedLog);
    }
    
    
    @Test
    void testCopyProcessVersions() throws Exception {
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
        branch.getProcessModelVersions().addAll(Arrays.asList(pmv1, pmv2, pmv3));
        branch.setCurrentProcessModelVersion(pmv2);
        process.getProcessBranches().add(branch);
        
        // Parameters
        Integer processId = process.getId();
        Integer targetFolderId = targetFolder.getId();
        String userName = user.getUsername();
        List<String> pmvVersions = Arrays.asList(pmv1.getVersionNumber(), pmv2.getVersionNumber());
        boolean madePublic = false;
        
        // Mock recording
        expect(folderRepo.findUniqueByID(targetFolderId)).andReturn(targetFolder);
        expect(userRepo.findByUsername(userName)).andReturn(user);
        expect(processRepo.findUniqueByID(processId)).andReturn(process);
        expect(subprocessProcessRepo.getLinkedSubProcesses(anyInt())).andReturn(Collections.emptyList());
        expect(processRepo.save((Process)EasyMock.anyObject())).andReturn(null); //ignore return value
        expect(pmvRepo.save((ProcessModelVersion)EasyMock.anyObject())).andReturn(null).anyTimes(); //ignore return value
        expect(subprocessProcessRepo.saveAll(anyObject(Iterable.class))).andReturn(null).anyTimes(); //ignore return value
        replayAll();
        
        // Mock call
        Process copyProcess = workspaceService.copyProcessVersions(processId, pmvVersions, targetFolderId, userName, madePublic);
        
        // Verify Mock and result
        verifyAll();
        assertEquals(targetFolder, copyProcess.getFolder());
        assertEquals(user, copyProcess.getUser());
        assertEquals(process.getName(), copyProcess.getName());
        assertEquals(1, copyProcess.getProcessBranches().size());
        assertEquals(process.getProcessBranches().get(0).getBranchName(), copyProcess.getProcessBranches().get(0).getBranchName());
        assertEquals(copyProcess, copyProcess.getProcessBranches().get(0).getProcess());

        assertEquals(2, copyProcess.getProcessBranches().get(0).getProcessModelVersions().size());
        assertEquals("1.0", copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(0).getVersionNumber());
        assertEquals("1.1", copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(1).getVersionNumber());
        assertEquals("1.1", copyProcess.getProcessBranches().get(0).getCurrentProcessModelVersion().getVersionNumber());
        
        assertEquals(pmv1.getNativeDocument().getContent(), copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(0).getNativeDocument().getContent());
        assertEquals(pmv2.getNativeDocument().getContent(), copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(1).getNativeDocument().getContent());
        assertEquals(copyProcess.getProcessBranches().get(0), copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(0).getProcessBranch());
        assertEquals(copyProcess.getProcessBranches().get(0), copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(1).getProcessBranch());
        
        assertEquals(user.getGroup(), copyProcess.getGroupProcesses().iterator().next().getGroup());
    }
    
    @Test
    void testCopyProcess() throws Exception {
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

        SubprocessProcess subprocessLink = createSubprocessLink(process, "test", process);
        List<SubprocessProcess> subprocessLinks = Arrays.asList(subprocessLink);
        
        // Parameters
        Integer processId = process.getId();
        Integer targetFolderId = targetFolder.getId();
        String userName = user.getUsername();
        boolean madePublic = false;
        
        // Mock recording
        expect(processRepo.findUniqueByID(processId)).andReturn(process).anyTimes();
        expect(folderRepo.findUniqueByID(targetFolderId)).andReturn(targetFolder);
        expect(userRepo.findByUsername(userName)).andReturn(user);
        expect(subprocessProcessRepo.getLinkedSubProcesses(anyInt())).andReturn(subprocessLinks);
        expect(processRepo.save((Process)EasyMock.anyObject())).andReturn(null); //ignore return value
        expect(pmvRepo.save((ProcessModelVersion)EasyMock.anyObject())).andReturn(null).anyTimes(); //ignore return value
        expect(subprocessProcessRepo.saveAll(anyObject(Iterable.class))).andReturn(null).anyTimes(); //ignore return value
        replayAll();
        
        // Mock call
        Process copyProcess = workspaceService.copyProcess(processId, targetFolderId, userName, madePublic);
        
        // Verify Mock and result
        verifyAll();
        assertEquals(targetFolder, copyProcess.getFolder());
        assertEquals(user, copyProcess.getUser());
        assertEquals(process.getName(), copyProcess.getName());
        assertEquals(1, copyProcess.getProcessBranches().size());
        assertEquals(process.getProcessBranches().get(0).getBranchName(), copyProcess.getProcessBranches().get(0).getBranchName());
        assertEquals(3, copyProcess.getProcessBranches().get(0).getProcessModelVersions().size());
        assertEquals("1.0", copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(0).getVersionNumber());
        assertEquals("1.1", copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(1).getVersionNumber());
        assertEquals("1.2", copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(2).getVersionNumber());
        assertEquals(pmv1.getNativeDocument().getContent(), copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(0).getNativeDocument().getContent());
        assertEquals(pmv2.getNativeDocument().getContent(), copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(1).getNativeDocument().getContent());
        assertEquals(pmv3.getNativeDocument().getContent(), copyProcess.getProcessBranches().get(0).getProcessModelVersions().get(2).getNativeDocument().getContent());
        assertEquals("1.2", copyProcess.getProcessBranches().get(0).getCurrentProcessModelVersion().getVersionNumber());
        assertEquals(user.getGroup(), copyProcess.getGroupProcesses().iterator().next().getGroup());
    }
    
    @Test
    void testMoveProcess() throws Exception {
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
        assertEquals(targetFolder, movedProcess.getFolder());
        assertEquals(process, movedProcess);
    }
    
    @Test
    @Disabled
    void testMoveFolder() throws Exception {
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
        assertEquals(folder.getName(), movedFolder.getName());
        assertEquals(newParentFolder, movedFolder.getParentFolder());
    }

    @Test
    void getSingleOwnerFolderByUser() {
        Group group = createGroup(1, Group.Type.USER);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));

        Group group2 = createGroup(2, Group.Type.USER);
        User user2 = createUser("userName1", group2, createSet(group2), createSet(role));

        Workspace wp = createWorkspace(user);
        Folder testFolder = createFolder("movedFolder", null, wp);

        GroupFolder groupFolder = new GroupFolder(group, testFolder,
                true, true, true);

        GroupFolder groupFolder2 = new GroupFolder(group2, testFolder,
                true, true, false);

        // Parameters
        Integer folderId = testFolder.getId();

        // Mock recording
        expect(groupFolderRepo.findByGroupId(group.getId())).andReturn(Arrays.asList(groupFolder));
        expect(groupFolderRepo.findOwnerByFolderId(folderId)).andReturn(Arrays.asList(groupFolder));
        replayAll();

        List<Folder> result = workspaceService.getSingleOwnerFolderByUser(user);

        // Verify mock and result
        verifyAll();
        assertEquals(result, Arrays.asList(testFolder));

    }

    @Test
    void getSingleOwnerFolderByUserReturnEmptyList() {
        Group group = createGroup(1, Group.Type.USER);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));

        Group group2 = createGroup(2, Group.Type.USER);
        User user2 = createUser("userName1", group2, createSet(group2), createSet(role));

        Workspace wp = createWorkspace(user);
        Folder testFolder = createFolder("movedFolder", null, wp);

        GroupFolder groupFolder = new GroupFolder(group, testFolder,
                true, true, true);

        GroupFolder groupFolder2 = new GroupFolder(group2, testFolder,
                true, true, true);

        // Parameters
        Integer folderId = testFolder.getId();

        // Mock recording
        expect(groupFolderRepo.findByGroupId(group.getId())).andReturn(Arrays.asList(groupFolder));
        expect(groupFolderRepo.findOwnerByFolderId(folderId)).andReturn(Arrays.asList(groupFolder, groupFolder2));
        replayAll();

        List<Folder> result = workspaceService.getSingleOwnerFolderByUser(user);

        // Verify mock and result
        verifyAll();
        assertEquals(Arrays.asList(), result);

    }

    @Test
    void getSingleOwnerLogByUser() {

        Group group = createGroup(1, Group.Type.USER);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));

        Group group2 = createGroup(2, Group.Type.USER);
        User user2 = createUser("userName1", group2, createSet(group2), createSet(role));

        Workspace wp = createWorkspace(user);
        Folder testFolder = createFolder("movedFolder", null, wp);

        Log testLog = createLog(user, testFolder);

        GroupLog groupLog = new GroupLog(group, testLog, true, true, true);

        // Parameters
        Integer folderId = testFolder.getId();

        // Mock recording
        expect(groupLogRepo.findByGroupId(group.getId())).andReturn(Arrays.asList(groupLog));
        expect(groupLogRepo.findOwnerByLogId(folderId)).andReturn(Arrays.asList(groupLog));
        replayAll();

        List<Log> result = workspaceService.getSingleOwnerLogByUser(user);

        // Verify mock and result
        verifyAll();
        assertEquals(result, Arrays.asList(testLog));

    }

    @Test
    void getSingleOwnerLogByUserReturnEmptyList() {

        Group group = createGroup(1, Group.Type.USER);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));

        Group group2 = createGroup(2, Group.Type.USER);
        User user2 = createUser("userName1", group2, createSet(group2), createSet(role));

        Workspace wp = createWorkspace(user);
        Folder testFolder = createFolder("movedFolder", null, wp);

        Log testLog = createLog(user, testFolder);

        GroupLog groupLog = new GroupLog(group, testLog, true, true, true);
        GroupLog groupLog2 = new GroupLog(group2, testLog, true, true, true);

        // Parameters
        Integer folderId = testFolder.getId();

        // Mock recording
        expect(groupLogRepo.findByGroupId(group.getId())).andReturn(Arrays.asList(groupLog));
        expect(groupLogRepo.findOwnerByLogId(folderId)).andReturn(Arrays.asList(groupLog, groupLog2));
        replayAll();

        List<Log> result = workspaceService.getSingleOwnerLogByUser(user);

        // Verify mock and result
        verifyAll();
        assertEquals(Arrays.asList(), result);

    }

    @Test
    void getSingleOwnerProcessByUser() {

        Group group = createGroup(1, Group.Type.USER);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));

        Group group2 = createGroup(2, Group.Type.USER);
        User user2 = createUser("userName1", group2, createSet(group2), createSet(role));

        Workspace wp = createWorkspace(user);
        Folder testFolder = createFolder("movedFolder", null, wp);

        Log testLog = createLog(user, testFolder);

        NativeType nativeType = createNativeType();
        Process testProcess = createProcess(user, nativeType, testFolder);

        GroupProcess groupProcess = createGroupProcess(group, testProcess, true, true, true);

        // Parameters
        Integer processId = testProcess.getId();

        // Mock recording
        expect(groupProcessRepo.findByGroupId(group.getId())).andReturn(Arrays.asList(groupProcess));
        expect(groupProcessRepo.findOwnerByProcessId(processId)).andReturn(Arrays.asList(groupProcess));
        replayAll();

        List<Process> result = workspaceService.getSingleOwnerProcessByUser(user);

        // Verify mock and result
        verifyAll();
        assertEquals(result, Arrays.asList(testProcess));

    }

    @Test
    void getSingleOwnerProcessByUserReturnEmptyList() {

        Group group = createGroup(1, Group.Type.USER);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));

        Group group2 = createGroup(2, Group.Type.USER);
        User user2 = createUser("userName1", group2, createSet(group2), createSet(role));

        Workspace wp = createWorkspace(user);
        Folder testFolder = createFolder("movedFolder", null, wp);

        Log testLog = createLog(user, testFolder);

        NativeType nativeType = createNativeType();
        Process testProcess = createProcess(user, nativeType, testFolder);

        GroupProcess groupProcess = createGroupProcess(group, testProcess, true, true, true);
        GroupProcess groupProcess2 = createGroupProcess(group2, testProcess, true, true, true);

        // Parameters
        Integer processId = testProcess.getId();

        // Mock recording
        expect(groupProcessRepo.findByGroupId(group.getId())).andReturn(Arrays.asList(groupProcess));
        expect(groupProcessRepo.findOwnerByProcessId(processId)).andReturn(Arrays.asList(groupProcess, groupProcess2));
        replayAll();

        List<Process> result = workspaceService.getSingleOwnerProcessByUser(user);

        // Verify mock and result
        verifyAll();
        assertEquals(Arrays.asList(), result);

    }

    @Test
    void canDeleteOwnerlessFolderReturnTrue() {

        Group group = createGroup(1, Group.Type.USER);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));

        Group group2 = createGroup(2, Group.Type.USER);
        User user2 = createUser("userName1", group2, createSet(group2), createSet(role));

        Workspace wp = createWorkspace(user);
        Folder testFolder = createFolder("movedFolder", null, wp);

        Log testLog = createLog(user, testFolder);

        GroupLog groupLog = new GroupLog(group, testLog, true, true, true);
        GroupLog groupLog2 = new GroupLog(group2, testLog, true, true, true);

        boolean result = workspaceService.canDeleteOwnerlessFolder(user);
        assertTrue(result);

    }

    @Test
    void canDeleteOwnerlessFolderReturnFalseWithLog() {

        Group group = createGroup(1, Group.Type.USER);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));

        Group group2 = createGroup(2, Group.Type.USER);
        User user2 = createUser("userName2", group2, createSet(group2), createSet(role));

        Workspace wp = createWorkspace(user);
        Folder testFolder = createFolder("testFolder", null, wp);
        Set<GroupFolder> groupFolders = new HashSet<>();
        GroupFolder groupFolder = new GroupFolder(group, testFolder, true, true, true);
        GroupFolder groupFolder1 = new GroupFolder(group2, testFolder, true, true, false);
        groupFolders.add(groupFolder);
        groupFolders.add(groupFolder1);
        testFolder.setGroupFolders(groupFolders);

        Log testLog = createLog(user, testFolder);

        GroupLog groupLog = new GroupLog(group, testLog, true, true, true);
        GroupLog groupLog2 = new GroupLog(group2, testLog, true, true, true);

        Set<GroupLog> groupLogs = new HashSet<>();
        groupLogs.add(groupLog);
        groupLogs.add(groupLog2);
        testLog.setGroupLogs(groupLogs);

        // Mock recording
        expect(groupFolderRepo.findByGroupId(group.getId())).andReturn(Collections.singletonList(groupFolder));
        expect(groupFolderRepo.findByGroupId(group2.getId())).andReturn(Collections.singletonList(groupFolder1));
        expect(groupFolderRepo.findOwnerByFolderId(testFolder.getId())).andReturn(Collections.singletonList(groupFolder));

        expect(workspaceService.getSingleOwnerFolderByUser(user)).andReturn(Collections.singletonList(testFolder));
        expect(logRepo.findByFolderIdIn(Collections.singletonList(testFolder.getId()))).andReturn(Collections.singletonList(testLog));

        expect(processRepo.findByFolderIdIn(Collections.singletonList(testFolder.getId()))).andReturn(Collections.emptyList());
        replayAll();

        boolean result = workspaceService.canDeleteOwnerlessFolder(user);

        assertFalse(result);

    }

    @Test
    void canDeleteOwnerlessFolderReturnFalseWithProcess() {

        Group group = createGroup(1, Group.Type.USER);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));

        Group group2 = createGroup(2, Group.Type.USER);
        User user2 = createUser("userName2", group2, createSet(group2), createSet(role));

        Workspace wp = createWorkspace(user);
        Folder testFolder = createFolder("testFolder", null, wp);
        Set<GroupFolder> groupFolders = new HashSet<>();
        GroupFolder groupFolder = new GroupFolder(group, testFolder, true, true, true);
        GroupFolder groupFolder1 = new GroupFolder(group2, testFolder, true, true, false);
        groupFolders.add(groupFolder);
        groupFolders.add(groupFolder1);
        testFolder.setGroupFolders(groupFolders);

        NativeType nativeType = createNativeType();
        Process testProcess = createProcess(user, nativeType, testFolder);

        GroupProcess groupProcess = new GroupProcess(testProcess, group, true, true, true);
        GroupProcess groupProcess2 = new GroupProcess(testProcess, group2, true, true, true);

        Set<GroupProcess> groupProcesses = new HashSet<>();
        groupProcesses.add(groupProcess);
        groupProcesses.add(groupProcess2);
        testProcess.setGroupProcesses(groupProcesses);

        // Mock recording
        expect(groupFolderRepo.findByGroupId(group.getId())).andReturn(Collections.singletonList(groupFolder));
        expect(groupFolderRepo.findByGroupId(group2.getId())).andReturn(Collections.singletonList(groupFolder1));
        expect(groupFolderRepo.findOwnerByFolderId(testFolder.getId())).andReturn(Collections.singletonList(groupFolder));

        expect(workspaceService.getSingleOwnerFolderByUser(user)).andReturn(Collections.singletonList(testFolder));
        expect(logRepo.findByFolderIdIn(Collections.singletonList(testFolder.getId()))).andReturn(Collections.emptyList());

        expect(processRepo.findByFolderIdIn(Collections.singletonList(testFolder.getId()))).andReturn(Collections.singletonList(testProcess));
        replayAll();

        boolean result = workspaceService.canDeleteOwnerlessFolder(user);

        assertFalse(result);

    }

    @Test
    void canDeleteOwnerlessFolderReturnFalseWithLogAndProcess() {

        Group group = createGroup(1, Group.Type.USER);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));

        Group group2 = createGroup(2, Group.Type.USER);
        User user2 = createUser("userName2", group2, createSet(group2), createSet(role));

        Workspace wp = createWorkspace(user);
        Folder testFolder = createFolder("testFolder", null, wp);
        Set<GroupFolder> groupFolders = new HashSet<>();
        GroupFolder groupFolder = new GroupFolder(group, testFolder, true, true, true);
        GroupFolder groupFolder1 = new GroupFolder(group2, testFolder, true, true, false);
        groupFolders.add(groupFolder);
        groupFolders.add(groupFolder1);
        testFolder.setGroupFolders(groupFolders);

        Log testLog = createLog(user, testFolder);

        GroupLog groupLog = new GroupLog(group, testLog, true, true, true);
        GroupLog groupLog2 = new GroupLog(group2, testLog, true, true, true);

        Set<GroupLog> groupLogs = new HashSet<>();
        groupLogs.add(groupLog);
        groupLogs.add(groupLog2);
        testLog.setGroupLogs(groupLogs);

        NativeType nativeType = createNativeType();
        Process testProcess = createProcess(user, nativeType, testFolder);

        GroupProcess groupProcess = new GroupProcess(testProcess, group, true, true, true);
        GroupProcess groupProcess2 = new GroupProcess(testProcess, group2, true, true, true);

        Set<GroupProcess> groupProcesses = new HashSet<>();
        groupProcesses.add(groupProcess);
        groupProcesses.add(groupProcess2);
        testProcess.setGroupProcesses(groupProcesses);

        // Mock recording
        expect(groupFolderRepo.findByGroupId(group.getId())).andReturn(Collections.singletonList(groupFolder));
        expect(groupFolderRepo.findByGroupId(group2.getId())).andReturn(Collections.singletonList(groupFolder1));
        expect(groupFolderRepo.findOwnerByFolderId(testFolder.getId())).andReturn(Collections.singletonList(groupFolder));

        expect(workspaceService.getSingleOwnerFolderByUser(user)).andReturn(Collections.singletonList(testFolder));
        expect(logRepo.findByFolderIdIn(Collections.singletonList(testFolder.getId()))).andReturn(Collections.singletonList(testLog));

        expect(processRepo.findByFolderIdIn(Collections.singletonList(testFolder.getId()))).andReturn(Collections.singletonList(testProcess));
        replayAll();

        boolean result = workspaceService.canDeleteOwnerlessFolder(user);

        assertFalse(result);

    }

    @Test
    void updateOwnerAfterDeleteUser() {

        Group group = createGroup(1, Group.Type.USER);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        group.setName(user.getUsername());

        Group group2 = createGroup(2, Group.Type.USER);
        User userToBeDeleted = createUser("userName1", group2, createSet(group2), createSet(role));
        group2.setName(userToBeDeleted.getUsername());

        Workspace wp = createWorkspace(user);
        Folder testFolder = createFolder("TestFolder", null, wp);
        testFolder.setCreatedBy(userToBeDeleted);

        GroupFolder groupFolder = new GroupFolder(group, testFolder, true, true, true);
        groupFolder.setId(1);
        GroupFolder groupFolder2 = new GroupFolder(group2, testFolder, true, true, true);
        groupFolder2.setId(2);
        Set<GroupFolder> groupFolderSet = new HashSet<>();
        groupFolderSet.add(groupFolder);
        groupFolderSet.add(groupFolder2);
        testFolder.setGroupFolders(groupFolderSet);

        Log testLog = createLog(userToBeDeleted, testFolder);
        GroupLog groupLog = new GroupLog(group, testLog, true, true, true);
        groupLog.setId(1);
        GroupLog groupLog2 = new GroupLog(group2, testLog, true, true, true);
        groupLog2.setId(2);
        Set<GroupLog> groupLogSet = new HashSet<>();
        groupLogSet.add(groupLog);
        groupLogSet.add(groupLog2);
        testLog.setGroupLogs(groupLogSet);

        Process testProcess = createProcess(userToBeDeleted, new NativeType(), testFolder);
        GroupProcess groupProcess = new GroupProcess(testProcess, group, true, true, true);
        groupProcess.setId(1);
        GroupProcess groupProcess2 = new GroupProcess(testProcess, group2, true, true, true);
        groupProcess2.setId(2);
        Set<GroupProcess> groupProcessSet = new HashSet<>();
        groupProcessSet.add(groupProcess);
        groupProcessSet.add(groupProcess2);
        testProcess.setGroupProcesses(groupProcessSet);

        // Parameters
        Integer folderId = testFolder.getId();

        // Mock recording
        expect(groupFolderRepo.findByGroupId(group2.getId())).andReturn(Arrays.asList(groupFolder,
            groupFolder2)).times(2);
        expect(groupFolderRepo.findOwnerByFolderId(folderId)).andReturn(Arrays.asList(groupFolder,
            groupFolder2)).times(3);
        expect(userRepo.findByUsername(group2.getName())).andReturn(userToBeDeleted).times(1);

        expect(groupLogRepo.findByGroupId(group2.getId())).andReturn(Arrays.asList(groupLog, groupLog2)).times(2);
        expect(groupLogRepo.findOwnerByLogId(testLog.getId())).andReturn(Arrays.asList(groupLog, groupLog2)).times(4);
        expect(userRepo.findByUsername(group2.getName())).andReturn(userToBeDeleted).times(2);

        expect(groupProcessRepo.findByGroupId(group2.getId())).andReturn(Arrays.asList(groupProcess, groupProcess2))
            .times(2);
        expect(groupProcessRepo.findOwnerByProcessId(testProcess.getId())).andReturn(
            Arrays.asList(groupProcess, groupProcess2)).times(4);
        expect(userRepo.findByUsername(group2.getName())).andReturn(userToBeDeleted).times(2);

        replayAll();

        workspaceService.updateOwnerAfterDeleteUser(userToBeDeleted);


        // Verify mock and result
        verifyAll();
        assertEquals(user, testFolder.getCreatedBy());
        assertEquals(user, testLog.getUser());
        assertEquals(user, testProcess.getUser());
    }
}
