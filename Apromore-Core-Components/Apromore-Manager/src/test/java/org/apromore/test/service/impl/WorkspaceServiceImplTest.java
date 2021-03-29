
package org.apromore.test.service.impl;

import static org.easymock.EasyMock.expect;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import org.apromore.AbstractTest;
import org.apromore.TestData;
import org.apromore.commons.config.ConfigBean;
import org.apromore.dao.*;
import org.apromore.dao.model.*;
import org.apromore.dao.model.Process;
import org.apromore.service.EventLogFileService;
import org.apromore.service.EventLogService;
import org.apromore.service.UserMetadataService;
import org.apromore.service.WorkspaceService;
import org.apromore.service.impl.FolderServiceImpl;
import org.apromore.service.impl.WorkspaceServiceImpl;
import org.apromore.storage.StorageClient;
import org.apromore.storage.factory.StorageManagementFactory;
import org.apromore.util.AccessType;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.junit.Assert;

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
    private UsermetadataRepository usermetadataRepo;
    private EventLogFileService logFileService;
    private FolderServiceImpl folderServiceImpl;
    
    private GroupRepository groupRepo;
    private GroupFolderRepository groupFolderRepo;
    private GroupProcessRepository groupProcessRepo;
    private GroupLogRepository groupLogRepo;
    private GroupUsermetadataRepository groupUsermetadataRepo;
    
    private ProcessRepository processRepo;
    private ProcessModelVersionRepository pmvRepo;
    private UserRepository userRepo;
    private StorageRepository storageRepository;
    private EventLogService eventLogService;
    private StorageManagementFactory<StorageClient> storageFactory;

    private ConfigBean config;
    
    @Before
    public final void setUp() throws Exception {
        workspaceRepo = createMock(WorkspaceRepository.class);
        groupRepo = createMock(GroupRepository.class);
        groupFolderRepo = createMock(GroupFolderRepository.class);
        groupProcessRepo = createMock(GroupProcessRepository.class);
        groupLogRepo = createMock(GroupLogRepository.class);
        groupUsermetadataRepo = createMock(GroupUsermetadataRepository.class);

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
                                                logFileService,
                                                folderServiceImpl,
                                                storageFactory,
                                                eventLogService,
                                                storageRepository,
                                                config);
    }

    @Test
    @Ignore
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
        logFileService.copyFile(EasyMock.anyObject(InputStream.class), EasyMock.anyObject(OutputStream.class));
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
    @Ignore
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
    public void getSingleOwnerFolderByUser() {
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
        Assert.assertEquals(result, Arrays.asList(testFolder));

    }

    @Test
    public void getSingleOwnerFolderByUserReturnEmptyList() {
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
        Assert.assertEquals(result, Arrays.asList());

    }

    @Test
    public void getSingleOwnerLogByUser() {

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
        Assert.assertEquals(result, Arrays.asList(testLog));

    }

    @Test
    public void getSingleOwnerLogByUserReturnEmptyList() {

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
        Assert.assertEquals(result, Arrays.asList());

    }

    @Test
    public void getSingleOwnerProcessByUser() {

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
        Assert.assertEquals(result, Arrays.asList(testProcess));

    }

    @Test
    public void getSingleOwnerProcessByUserReturnEmptyList() {

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
        Assert.assertEquals(result, Arrays.asList());

    }

    @Test
    public void canDeleteOwnerlessFolderReturnTrue() {

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
        Assert.assertTrue(result);

    }

    @Test
    public void canDeleteOwnerlessFolderReturnFalseWithLog() {

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

        Assert.assertFalse(result);

    }

    @Test
    public void canDeleteOwnerlessFolderReturnFalseWithProcess() {

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

        Assert.assertFalse(result);

    }

    @Test
    public void canDeleteOwnerlessFolderReturnFalseWithLogAndProcess() {

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

        Assert.assertFalse(result);

    }

}
