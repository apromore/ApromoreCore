/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
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

package org.apromore.service.impl;

import com.google.common.io.CharStreams;
import org.apromore.TestData;
import org.apromore.common.Constants;
import org.apromore.commons.config.ConfigBean;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.GroupProcessRepository;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.NativeRepository;
import org.apromore.dao.ProcessBranchRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.StorageRepository;
import org.apromore.dao.model.AccessRights;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.GroupProcess;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.Permission;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.Storage;
import org.apromore.dao.model.User;
import org.apromore.exception.ImportException;
import org.apromore.exception.RepositoryException;
import org.apromore.exception.UpdateProcessException;
import org.apromore.exception.UserNotFoundException;
import org.apromore.portal.helper.Version;
import org.apromore.portal.model.ExportFormatResultType;
import org.apromore.service.AuthorizationService;
import org.apromore.service.FormatService;
import org.apromore.service.LockService;
import org.apromore.service.UserService;
import org.apromore.service.WorkspaceService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.model.ProcessData;
import org.apromore.storage.StorageClient;
import org.apromore.storage.factory.StorageManagementFactory;
import org.apromore.storage.factory.StorageManagementFactoryImpl;
import org.apromore.util.AccessType;
import org.apromore.util.StreamUtil;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.expect;

/**
 * @author Bruce Nguyen
 */
public class ProcessServiceImplUnitTest extends EasyMockSupport {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private ProcessServiceImpl processService;
    private UserService usrSrv;
    private FormatService fmtSrv;
    private UserInterfaceHelper ui;
    private WorkspaceService workspaceSrv;
    private AuthorizationService authorizationService;
    private LockService lockSrv;
    private ConfigBean config;

    private ProcessRepository processRepo;
    private NativeRepository nativeRepo;
    private ProcessModelVersionRepository processModelVersionRepo;
    private ProcessBranchRepository processBranchRepo;
    private GroupRepository groupRepo;
    private GroupProcessRepository groupProcessRepo;
    private FolderRepository folderRepository;
    private StorageRepository storageRepo;
    private StorageManagementFactory<StorageClient> storageFactory;

    @Before
    public final void setUp() throws Exception {
        // nativeRepo = createMock(NativeRepository.class);
        groupRepo = createMock(GroupRepository.class);
        groupProcessRepo = createMock(GroupProcessRepository.class);
        processBranchRepo = createMock(ProcessBranchRepository.class);
        processRepo = createMock(ProcessRepository.class);
        processModelVersionRepo = createMock(ProcessModelVersionRepository.class);
        folderRepository = createMock(FolderRepository.class);
        nativeRepo = createMock(NativeRepository.class);
        usrSrv = createMock(UserService.class);
        fmtSrv = createMock(FormatService.class);
        ui = createMock(UserInterfaceHelper.class);
        workspaceSrv = createMock(WorkspaceService.class);
        lockSrv = createMock(LockService.class);
        authorizationService = createMock(AuthorizationService.class);
        config = new ConfigBean();
        storageRepo = createMock(StorageRepository.class);
        storageFactory = createMock(StorageManagementFactory.class);

        processService = new ProcessServiceImpl(nativeRepo, groupRepo, processBranchRepo, processRepo,
                processModelVersionRepo, groupProcessRepo, lockSrv, usrSrv, fmtSrv, ui, workspaceSrv,
                authorizationService, folderRepository, config, storageRepo, storageFactory);
    }

    @Test
    public void testImportProcess_MainPath() throws Exception {
        // Test data setup
        Folder folder = createFolder();
        Group group = createGroup(123, Group.Type.GROUP);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        Version version = createVersion("1.0.0");
        NativeType nativeType = createNativeType();
        Native nativeDoc = createNative(nativeType, TestData.XPDL);

        Process process = createProcess(user, nativeType, folder);
        ProcessBranch branch = createBranch(process);
        Storage storage = createStorage();
        ProcessModelVersion pmv = createPMV(branch, nativeDoc, version, storage);

        // Parameter setup
        String userName = user.getUsername();
        String createDate = pmv.getCreateDate();
        String lastUpdateDate = pmv.getLastUpdateDate();
        String processName = process.getName();
        String domainName = process.getDomain();
        InputStream nativeStream =
                (new DataHandler(new ByteArrayDataSource(nativeDoc.getContent().getBytes(), "text/xml")))
                        .getInputStream();
        String nativeTypeS = nativeType.getNatType();
        Integer folderId = folder.getId();


        // MOCK RECORDING

        // Insert new process
        expect(usrSrv.findUserByLogin(userName)).andReturn(user);
        expect(fmtSrv.findNativeType(nativeTypeS)).andReturn(nativeType);
        expect(workspaceSrv.getFolder((Integer) anyObject())).andReturn(folder);
        expect(processRepo.save((Process) anyObject())).andReturn(process);
        expect(processRepo.saveAndFlush((Process) anyObject())).andReturn(process);
        // Insert branch
        expect(processBranchRepo.save((ProcessBranch) anyObject())).andReturn(branch);
        // Insert process model version
        expect(processModelVersionRepo.save((ProcessModelVersion) anyObject())).andReturn(pmv);
        // Store native
        expect(fmtSrv.storeNative(processName, null, lastUpdateDate, null, nativeType,
                Constants.INITIAL_ANNOTATION, nativeStream)).andReturn(nativeDoc);
        expect(folderRepository.findUniqueByID(folder.getId())).andReturn(folder);
        expect(authorizationService.getFolderAccessTypeByUser(folderId, user))
                .andReturn(AccessType.OWNER);
        expect(workspaceSrv.addProcessToFolder(user, process.getId(), folderId)).andReturn(process);

        replayAll();

        // MOCK CALL AND VERIFY
        ProcessModelVersion pmvResult = processService.importProcess(userName, folderId, processName,
                version, nativeType.getNatType(), nativeStream, domainName, "", createDate, lastUpdateDate,
                false);

        // VERIFY MOCK AND RESULT
        verifyAll();
        Assert.assertEquals(pmvResult.getProcessBranch().getProcess().getName(),
                pmv.getProcessBranch().getProcess().getName());
        Assert.assertEquals(pmvResult.getProcessBranch().getBranchName(),
                pmv.getProcessBranch().getBranchName());
        Assert.assertEquals(pmvResult.getNativeType().getNatType(), pmv.getNativeType().getNatType());
        Assert.assertEquals(pmvResult.getVersionNumber(), pmv.getVersionNumber());
        Assert.assertEquals(pmvResult.getNativeDocument().getContent(),
                pmv.getNativeDocument().getContent());
        Assert.assertEquals(pmvResult.getCreateDate(), pmv.getCreateDate());
        Assert.assertEquals(pmvResult.getLastUpdateDate(), pmv.getLastUpdateDate());
    }

    @Test
    public void testImportProcess_FolderWithoutWriteAccess() throws Exception {
        // Test data setup
        Folder homeFolder = createFolder();
        Folder folder = new Folder();
        folder.setId(1);
        Group group = createGroup(123, Group.Type.GROUP);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        Version version = createVersion("1.0.0");
        NativeType nativeType = createNativeType();
        Native nativeDoc = createNative(nativeType, TestData.XPDL);

        Process process = createProcess(user, nativeType, homeFolder);
        ProcessBranch branch = createBranch(process);
        Storage storage = createStorage();
        ProcessModelVersion pmv = createPMV(branch, nativeDoc, version, storage);

        // Parameter setup
        String userName = user.getUsername();
        String createDate = pmv.getCreateDate();
        String lastUpdateDate = pmv.getLastUpdateDate();
        String processName = process.getName();
        String domainName = process.getDomain();
        InputStream nativeStream =
                (new DataHandler(new ByteArrayDataSource(nativeDoc.getContent().getBytes(), "text/xml")))
                        .getInputStream();
        String nativeTypeS = nativeType.getNatType();
        Integer folderId = folder.getId();


        // MOCK RECORDING

        // Insert new process
        expect(usrSrv.findUserByLogin(userName)).andReturn(user);
        expect(fmtSrv.findNativeType(nativeTypeS)).andReturn(nativeType);
        expect(processRepo.save((Process) anyObject())).andReturn(process);
        expect(processRepo.saveAndFlush((Process) anyObject())).andReturn(process);
        // Insert branch
        expect(processBranchRepo.save((ProcessBranch) anyObject())).andReturn(branch);
        // Insert process model version
        expect(processModelVersionRepo.save((ProcessModelVersion) anyObject())).andReturn(pmv);
        // Store native
        expect(fmtSrv.storeNative(processName, null, lastUpdateDate, null, nativeType,
                Constants.INITIAL_ANNOTATION, nativeStream)).andReturn(nativeDoc);
        expect(folderRepository.findUniqueByID(folder.getId())).andReturn(folder);
        expect(authorizationService.getFolderAccessTypeByUser(folderId, user))
                .andReturn(AccessType.EDITOR);
        expect(workspaceSrv.addProcessToFolder(user, process.getId(), 0)).andReturn(process);

        replayAll();

        // MOCK CALL AND VERIFY
        ProcessModelVersion pmvResult = processService.importProcess(userName, folderId, processName,
                version, nativeType.getNatType(), nativeStream, domainName, "", createDate, lastUpdateDate,
                false);

        // VERIFY MOCK AND RESULT
        verifyAll();
        Assert.assertEquals(homeFolder.getId(), process.getFolder().getId());
        Assert.assertEquals(pmvResult.getProcessBranch().getProcess().getName(),
                pmv.getProcessBranch().getProcess().getName());
        Assert.assertEquals(pmvResult.getProcessBranch().getBranchName(),
                pmv.getProcessBranch().getBranchName());
        Assert.assertEquals(pmvResult.getNativeType().getNatType(), pmv.getNativeType().getNatType());
        Assert.assertEquals(pmvResult.getVersionNumber(), pmv.getVersionNumber());
        Assert.assertEquals(pmvResult.getNativeDocument().getContent(),
                pmv.getNativeDocument().getContent());
        Assert.assertEquals(pmvResult.getCreateDate(), pmv.getCreateDate());
        Assert.assertEquals(pmvResult.getLastUpdateDate(), pmv.getLastUpdateDate());
    }

    @Test(expected = ImportException.class)
    public void testImportProcess_EmptyNativeContent() throws Exception {
        // Test data setup
        Folder folder = createFolder();
        Group group = createGroup(123, Group.Type.GROUP);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        Version version = createVersion("1.0.0");
        NativeType nativeType = createNativeType();
        Native nativeDoc = createNative(nativeType, TestData.XPDL);

        Process process = createProcess(user, nativeType, folder);
        ProcessBranch branch = createBranch(process);
        Storage storage = createStorage();
        ProcessModelVersion pmv = createPMV(branch, nativeDoc, version, storage);

        // Parameter setup
        String userName = user.getUsername();
        String createDate = pmv.getCreateDate();
        String lastUpdateDate = pmv.getLastUpdateDate();
        String processName = process.getName();
        String domainName = process.getDomain();
        InputStream nativeStream = null;
        String nativeTypeS = nativeType.getNatType();
        Integer folderId = folder.getId();

        ProcessModelVersion pmvResult = processService.importProcess(userName, folderId, processName,
                version, nativeType.getNatType(), nativeStream, domainName, "", createDate, lastUpdateDate,
                false);

    }

    @Test
    public void testImportProcess_MakeModelPublic() throws Exception {
        // Test data setup
        Folder folder = createFolder();
        Group group = createGroup(1, Group.Type.GROUP);
        Group publicGroup = createGroup(2, Group.Type.PUBLIC);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        Version version = createVersion("1.0.0");
        NativeType nativeType = createNativeType();
        Native nativeDoc = createNative(nativeType, TestData.XPDL);

        Process process = createProcess(user, nativeType, folder);
        ProcessBranch branch = createBranch(process);
        Storage storage = createStorage();
        ProcessModelVersion pmv = createPMV(branch, nativeDoc, version, storage);

        // Parameter setup
        String userName = user.getUsername();
        String createDate = pmv.getCreateDate();
        String lastUpdateDate = pmv.getLastUpdateDate();
        String processName = process.getName();
        String domainName = process.getDomain();
        InputStream nativeStream =
                (new DataHandler(new ByteArrayDataSource(nativeDoc.getContent().getBytes(), "text/xml")))
                        .getInputStream();
        String nativeTypeS = nativeType.getNatType();
        Integer folderId = folder.getId();


        // MOCK RECORDING

        // Insert new process
        expect(usrSrv.findUserByLogin(userName)).andReturn(user);
        expect(fmtSrv.findNativeType(nativeTypeS)).andReturn(nativeType);
        expect(workspaceSrv.getFolder((Integer) anyObject())).andReturn(folder);
        expect(processRepo.save((Process) anyObject())).andReturn(process);
        expect(groupRepo.findPublicGroup()).andReturn(publicGroup);
        expect(processRepo.saveAndFlush((Process) anyObject())).andReturn(process);
        // Insert branch
        expect(processBranchRepo.save((ProcessBranch) anyObject())).andReturn(branch);
        // Insert process model version
        expect(processModelVersionRepo.save((ProcessModelVersion) anyObject())).andReturn(pmv);
        // Store native
        expect(fmtSrv.storeNative(processName, null, lastUpdateDate, null, nativeType,
                Constants.INITIAL_ANNOTATION, nativeStream)).andReturn(nativeDoc);
        expect(folderRepository.findUniqueByID(folder.getId())).andReturn(folder);
        expect(authorizationService.getFolderAccessTypeByUser(folderId, user))
                .andReturn(AccessType.OWNER);

        // Update workspace
        expect(workspaceSrv.addProcessToFolder(user, process.getId(), folderId)).andReturn(process);

        replayAll();

        // MOCK CALL AND VERIFY
        ProcessModelVersion pmvResult = processService.importProcess(userName, folderId, processName,
                version, nativeType.getNatType(), nativeStream, domainName, "", createDate, lastUpdateDate,
                true);

        // VERIFY MOCK AND RESULT
        verifyAll();
        // Verify that the public group has been added to the created process
        boolean publicGroupAdded = false;
        for (GroupProcess gp : pmvResult.getProcessBranch().getProcess().getGroupProcesses()) {
            if (gp.getGroup() == publicGroup) {
                publicGroupAdded = true;
                break;
            }
        }
        ;
        Assert.assertTrue(publicGroupAdded);
    }

    @Test(expected = ImportException.class)
    public void testImportProcess_NotFoundUsername() throws Exception {
        // Test data setup
        Folder folder = createFolder();
        Group group = createGroup(1, Group.Type.GROUP);
        Group publicGroup = createGroup(2, Group.Type.PUBLIC);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        Version version = createVersion("1.0.0");
        NativeType nativeType = createNativeType();
        Native nativeDoc = createNative(nativeType, TestData.XPDL);

        Process process = createProcess(user, nativeType, folder);
        ProcessBranch branch = createBranch(process);
        Storage storage = createStorage();
        ProcessModelVersion pmv = createPMV(branch, nativeDoc, version, storage);

        // Parameter setup
        String userName = user.getUsername();
        String createDate = pmv.getCreateDate();
        String lastUpdateDate = pmv.getLastUpdateDate();
        String processName = process.getName();
        String domainName = process.getDomain();
        InputStream nativeStream =
                (new DataHandler(new ByteArrayDataSource(nativeDoc.getContent().getBytes(), "text/xml")))
                        .getInputStream();
        String nativeTypeS = nativeType.getNatType();
        Integer folderId = folder.getId();


        // MOCK RECORDING

        // Insert new process
        expect(usrSrv.findUserByLogin(userName)).andThrow(new UserNotFoundException());
        replayAll();

        // MOCK CALL AND VERIFY
        ProcessModelVersion pmvResult = processService.importProcess(userName, folderId, processName,
                version, nativeType.getNatType(), nativeStream, domainName, "", createDate, lastUpdateDate,
                false);

        // VERIFY MOCK AND RESULT
        verifyAll();
    }

    @Test
    public void testCreateProcessModelVersion_MainPath() throws Exception {
        // Test Data setup
        Folder folder = createFolder();
        Group group = createGroup(123, Group.Type.GROUP);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        Version existingVersion = createVersion("1.0");
        Version newVersion = createVersion("1.1");
        NativeType nativeType = createNativeType();
        Native nativeDoc = createNative(nativeType, TestData.XPDL);

        Process process = createProcess(user, nativeType, folder);
        ProcessBranch branch = createBranch(process);
        Storage storage = createStorage();
        ProcessModelVersion newPMV = createPMV(branch, nativeDoc, newVersion, storage);
        ProcessModelVersion existingPMV = createPMV(branch, nativeDoc, existingVersion, storage);
        GroupProcess groupProcess = createGroupProcess(group, process, true, true, true);

        // Parameter setup
        Integer processId = process.getId();
        String processName = process.getName();
        String branchName = branch.getBranchName();
        String existingVersionNumber = existingVersion.toString();
        InputStream nativeStream =
                (new DataHandler(new ByteArrayDataSource(nativeDoc.getContent().getBytes(), "text/xml")))
                        .getInputStream();

        // Mock Recording
        expect(processRepo.findById(processId)).andReturn(Optional.of(process));
        expect(groupProcessRepo.findByProcessAndUser(processId, user.getRowGuid()))
                .andReturn(List.of(groupProcess));
        expect(processModelVersionRepo.getProcessModelVersion(processId, branchName,
                existingVersionNumber)).andReturn(existingPMV);
        expect(processModelVersionRepo.save((ProcessModelVersion) EasyMock.anyObject()))
                .andReturn(newPMV);
        // All matchers are used here to avoid unmatched timestamp
        expect(fmtSrv.storeNative(EasyMock.eq(processName), anyString(), anyString(), anyObject(),
                EasyMock.eq(nativeType),
                anyString(), EasyMock.eq(nativeStream))).andReturn(nativeDoc);


        replayAll();
        // Mock Call
        ProcessModelVersion resultPMV = processService.createProcessModelVersion(processId, branchName,
                newVersion, existingVersion, user, "", nativeType, nativeStream);

        // Verify mock and result
        verifyAll();
        Assert.assertEquals(resultPMV, newPMV);
    }

    @Test(expected = ImportException.class)
    public void testCreateProcessModelVersion_NoWriteAccess() throws Exception {
        // Test Data setup
        Folder folder = createFolder();
        Group group = createGroup(123, Group.Type.GROUP);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        Version existingVersion = createVersion("1.0");
        Version newVersion = createVersion("1.1");
        NativeType nativeType = createNativeType();
        Native nativeDoc = createNative(nativeType, TestData.XPDL);

        Process process = createProcess(user, nativeType, folder);
        ProcessBranch branch = createBranch(process);
        GroupProcess groupProcess = createGroupProcess(group, process, true, true, false);

        // Parameter setup
        Integer processId = process.getId();
        String branchName = branch.getBranchName();
        InputStream nativeStream =
                (new DataHandler(new ByteArrayDataSource(nativeDoc.getContent().getBytes(), "text/xml")))
                        .getInputStream();

        // Mock Recording
        expect(processRepo.findById(processId)).andReturn(Optional.of(process));
        expect(groupProcessRepo.findByProcessAndUser(processId, user.getRowGuid()))
                .andReturn(Arrays.asList(new GroupProcess[]{groupProcess}));
        replayAll();

        // Mock Call
        ProcessModelVersion resultPMV = processService.createProcessModelVersion(processId, branchName,
                newVersion, existingVersion, user, "", nativeType, nativeStream);

        // Verify mock and result
        verifyAll();
    }

    @Test(expected = RepositoryException.class)
    public void testCreateProcessModelVersion_VersionConflict() throws Exception {
        // Test Data setup
        Folder folder = createFolder();
        Group group = createGroup(123, Group.Type.GROUP);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        Version existingVersion = createVersion("1.0");
        Version newVersion = createVersion("1.0");
        NativeType nativeType = createNativeType();
        Native nativeDoc = createNative(nativeType, TestData.XPDL);

        Process process = createProcess(user, nativeType, folder);
        ProcessBranch branch = createBranch(process);
        Storage storage = createStorage();
        ProcessModelVersion existingPMV = createPMV(branch, nativeDoc, existingVersion, storage);
        GroupProcess groupProcess = createGroupProcess(group, process, true, true, true);

        // Parameter setup
        Integer processId = process.getId();
        String branchName = branch.getBranchName();
        String existingVersionNumber = existingVersion.toString();
        InputStream nativeStream =
                (new DataHandler(new ByteArrayDataSource(nativeDoc.getContent().getBytes(), "text/xml")))
                        .getInputStream();

        // Mock Recording
        expect(processRepo.findById(processId)).andReturn(Optional.of(process));
        expect(groupProcessRepo.findByProcessAndUser(processId, user.getRowGuid()))
                .andReturn(Arrays.asList(new GroupProcess[]{groupProcess}));
        expect(processModelVersionRepo.getProcessModelVersion(processId, branchName,
                existingVersionNumber)).andReturn(existingPMV);
        replayAll();

        // Mock Call
        ProcessModelVersion resultPMV = processService.createProcessModelVersion(processId, branchName,
                newVersion, existingVersion, user, "", nativeType, nativeStream);

        // Verify mock and result
        verifyAll();
    }

    @Test(expected = RepositoryException.class)
    public void testCreateProcessModelVersion_NotFoundExistingVersion() throws Exception {
        // Test Data setup
        Folder folder = createFolder();
        Group group = createGroup(123, Group.Type.GROUP);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        Version existingVersion = createVersion("1.0");
        Version newVersion = createVersion("1.0");
        NativeType nativeType = createNativeType();
        Native nativeDoc = createNative(nativeType, TestData.XPDL);

        Process process = createProcess(user, nativeType, folder);
        ProcessBranch branch = createBranch(process);
        GroupProcess groupProcess = createGroupProcess(group, process, true, true, true);

        // Parameter setup
        Integer processId = process.getId();
        String branchName = branch.getBranchName();
        String existingVersionNumber = existingVersion.toString();
        InputStream nativeStream =
                (new DataHandler(new ByteArrayDataSource(nativeDoc.getContent().getBytes(), "text/xml")))
                        .getInputStream();

        // Mock Recording
        expect(processRepo.findById(processId)).andReturn(Optional.of(process));
        expect(groupProcessRepo.findByProcessAndUser(processId, user.getRowGuid()))
                .andReturn(Arrays.asList(new GroupProcess[]{groupProcess}));
        expect(processModelVersionRepo.getProcessModelVersion(processId, branchName,
                existingVersionNumber)).andReturn(null);
        replayAll();

        // Mock Call
        ProcessModelVersion resultPMV = processService.createProcessModelVersion(processId, branchName,
                newVersion, existingVersion, user, "", nativeType, nativeStream);

        // Verify mock and result
        verifyAll();
    }

    @Test
    public void testUpdateProcessModelVersion_withStorage() throws Exception {
        // Test Data setup
        Folder folder = createFolder();
        Group group = createGroup(123, Group.Type.GROUP);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        Version existingVersion = createVersion("1.0");
        NativeType nativeType = createNativeType();
        Native existingNativeDoc = createNative(nativeType, TestData.XPDL);
        Native newNativeDoc = createNative(nativeType, TestData.XPDL2);

        Process process = createProcess(user, nativeType, folder);
        ProcessBranch branch = createBranch(process);
        Storage storage = createStorage();
        String filename = "testUpdateProcessModelVersion_withStorage.bpmn";
        storage.setKey(filename);
        ProcessModelVersion existingPMV = createPMV(branch, null, existingVersion, storage);
        ProcessModelVersion newPMV = createPMV(branch, null, existingVersion, storage);
        GroupProcess groupProcess = createGroupProcess(group, process, true, true, true);

        // Parameter setup
        Integer processId = process.getId();
        String branchName = branch.getBranchName();
        String existingVersionNumber = existingVersion.toString();
        InputStream newNativeStream =
                (new DataHandler(new ByteArrayDataSource(newNativeDoc.getContent().getBytes(), "text/xml")))
                        .getInputStream();

        StorageManagementFactory<StorageClient> storageManagementFactory;
        storageManagementFactory = new StorageManagementFactoryImpl<StorageClient>();
        URL url = getClass().getClassLoader().getResource("file_store_dir/model/" + filename);
        File file = new File(url.getFile());
        StorageClient client = storageManagementFactory.getStorageClient("FILE::" + file.getParent().substring(0,
                file.getParent().length() - 5));
        InputStream stream = client.getInputStream("model", filename);

        // Mock Recording
        expect(processRepo.findById(processId)).andReturn(Optional.of(process));
        expect(groupProcessRepo.findByProcessAndUser(processId, user.getRowGuid()))
                .andReturn(List.of(groupProcess));
        expect(processModelVersionRepo.getProcessModelVersion(processId, branchName,
                existingVersionNumber)).andReturn(existingPMV);
        expect(processModelVersionRepo.save(EasyMock.anyObject()))
                .andReturn(newPMV);
        expect(storageFactory.getStorageClient(storage.getStoragePath()))
                .andReturn(client);
        replayAll();

        // Mock Call
        ProcessModelVersion resultPMV = processService.updateProcessModelVersion(processId, branchName,
                existingVersion, user, "", nativeType, newNativeStream);

        // Verify mock and result
        verifyAll();
        Assert.assertEquals(StreamUtil.convertStreamToString(client.getInputStream("model", filename)),
                newNativeDoc.getContent());
    }

    @Test
    public void testUpdateProcessModelVersion_withNativeDocument() throws Exception {
        // Test Data setup
        Folder folder = createFolder();
        Group group = createGroup(123, Group.Type.GROUP);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        Version existingVersion = createVersion("1.0");
        NativeType nativeType = createNativeType();
        Native existingNativeDoc = createNative(nativeType, TestData.XPDL);
        Native newNativeDoc = createNative(nativeType, TestData.XPDL2);

        Process process = createProcess(user, nativeType, folder);
        ProcessBranch branch = createBranch(process);
        ProcessModelVersion existingPMV = createPMV(branch, existingNativeDoc, existingVersion, null);
        ProcessModelVersion newPMV = createPMV(branch, newNativeDoc, existingVersion, null);
        GroupProcess groupProcess = createGroupProcess(group, process, true, true, true);

        // Parameter setup
        Integer processId = process.getId();
        String branchName = branch.getBranchName();
        String existingVersionNumber = existingVersion.toString();
        InputStream newNativeStream =
                (new DataHandler(new ByteArrayDataSource(newNativeDoc.getContent().getBytes(), "text/xml")))
                        .getInputStream();

        // Mock Recording
        expect(processRepo.findById(processId)).andReturn(Optional.of(process));
        expect(groupProcessRepo.findByProcessAndUser(processId, user.getRowGuid()))
                .andReturn(List.of(groupProcess));
        expect(processModelVersionRepo.getProcessModelVersion(processId, branchName,
                existingVersionNumber)).andReturn(existingPMV);
        expect(processModelVersionRepo.save(EasyMock.anyObject()))
                .andReturn(newPMV);
        replayAll();

        // Mock Call
        ProcessModelVersion resultPMV = processService.updateProcessModelVersion(processId, branchName,
                existingVersion, user, "", nativeType, newNativeStream);

        // Verify mock and result
        verifyAll();
        Assert.assertEquals(resultPMV.getNativeDocument().getContent(), newNativeDoc.getContent());
    }

    @Test(expected = UpdateProcessException.class)
    public void testUpdateProcessModelVersion_MissStorage() throws Exception {
        // Test Data setup
        Folder folder = createFolder();
        Group group = createGroup(123, Group.Type.GROUP);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        Version existingVersion = createVersion("1.0");
        NativeType nativeType = createNativeType();
        Native existingNativeDoc = createNative(nativeType, TestData.XPDL);
        Native newNativeDoc = createNative(nativeType, TestData.XPDL2);

        Process process = createProcess(user, nativeType, folder);
        ProcessBranch branch = createBranch(process);
        ProcessModelVersion existingPMV = createPMV(branch, null, existingVersion, null);
        ProcessModelVersion newPMV = createPMV(branch, null, existingVersion, null);
        GroupProcess groupProcess = createGroupProcess(group, process, true, true, true);

        // Parameter setup
        Integer processId = process.getId();
        String branchName = branch.getBranchName();
        String existingVersionNumber = existingVersion.toString();
        InputStream newNativeStream =
                (new DataHandler(new ByteArrayDataSource(newNativeDoc.getContent().getBytes(), "text/xml")))
                        .getInputStream();

        // Mock Recording
        expect(processRepo.findById(processId)).andReturn(Optional.of(process));
        expect(groupProcessRepo.findByProcessAndUser(processId, user.getRowGuid()))
                .andReturn(List.of(groupProcess));
        expect(processModelVersionRepo.getProcessModelVersion(processId, branchName,
                existingVersionNumber)).andReturn(existingPMV);
        expect(processModelVersionRepo.save(EasyMock.anyObject()))
                .andReturn(newPMV);
        replayAll();

        // Mock Call
        ProcessModelVersion resultPMV = processService.updateProcessModelVersion(processId, branchName,
                existingVersion, user, "", nativeType, newNativeStream);

        // Verify mock and result
        verifyAll();
    }


    @Test
    public void testUpdateProcessMetadata_CurrentPublic_TobeMadePublic() throws Exception {
        // Test Data setup
        Folder folder = createFolder();
        Group group = createGroup(1, Group.Type.GROUP);
        Group publicGroup = createGroup(2, Group.Type.PUBLIC);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        Version existingVersion = createVersion("1.0");
        Version newVersion = createVersion("1.1");
        NativeType nativeType = createNativeType();
        Native existingNativeDoc = createNative(nativeType, TestData.XPDL);

        Process process = createProcess(user, nativeType, folder);
        process.getGroupProcesses().add(createGroupProcess(group, process, true, true, true));
        process.getGroupProcesses().add(createGroupProcess(publicGroup, process, true, true, true));
        ProcessBranch branch = createBranch(process);
        Storage storage = createStorage();
        ProcessModelVersion pmv = createPMV(branch, existingNativeDoc, existingVersion, storage);
        branch.setCurrentProcessModelVersion(pmv);
        branch.getProcessModelVersions().add(pmv);
        process.getProcessBranches().add(branch);

        // Parameter setup
        Integer processId = process.getId();
        String existingVersionNumber = existingVersion.toString();

        String newProcessName = "NEW PROCESS NAME";
        String newProcessDomain = "NEW DOMAIN";
        String newProcessRanking = "NEW RANKING";
        String newProcessVersion = newVersion.toString();
        boolean newProcessPublicStatus = true;
        User newUser = createUser("userName2", group, createSet(group), createSet(role));
        String newUserName = newUser.getUsername();

        // Mock Recording
        expect(processModelVersionRepo.getCurrentProcessModelVersion(processId, existingVersionNumber))
                .andReturn(pmv);
        expect(processRepo.findById(processId)).andReturn(Optional.of(process));
        expect(usrSrv.findUserByLogin(newUserName)).andReturn(newUser);
        expect(groupRepo.findPublicGroup()).andStubReturn(publicGroup);

        expect(processRepo.save((Process) EasyMock.anyObject())).andReturn(process);
        expect(processModelVersionRepo.save((ProcessModelVersion) EasyMock.anyObject())).andReturn(pmv);
        expect(processBranchRepo.save((ProcessBranch) EasyMock.anyObject())).andReturn(branch);

        replayAll();

        // Mock Call
        processService.updateProcessMetaData(processId, newProcessName, newProcessDomain, newUserName,
                existingVersion, newVersion, newProcessRanking, newProcessPublicStatus);

        // Verify mock and result
        verifyAll();
        Assert.assertEquals(newProcessName, process.getName());
        Assert.assertEquals(newProcessDomain, process.getDomain());
        Assert.assertEquals(newProcessRanking, process.getRanking());
        Assert.assertEquals(newUser, process.getUser());
        Assert.assertEquals(newProcessVersion, pmv.getVersionNumber());
        Assert.assertEquals(branch.getCurrentProcessModelVersion(), pmv);
        boolean processHasPublicGroup = false;
        for (GroupProcess gp : process.getGroupProcesses()) {
            if (gp.getGroup().getType() == Group.Type.PUBLIC) {
                processHasPublicGroup = true;
                break;
            }
        }
        Assert.assertEquals(true, processHasPublicGroup);
    }


    @Test
    public void testUpdateProcessMetadata_CurrentNonPublic_TobeMadePublic() throws Exception {
        // Test Data setup
        Folder folder = createFolder();
        Group group = createGroup(1, Group.Type.GROUP);
        Group publicGroup = createGroup(2, Group.Type.PUBLIC);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        Version existingVersion = createVersion("1.0");
        Version newVersion = createVersion("1.1");
        NativeType nativeType = createNativeType();
        Native existingNativeDoc = createNative(nativeType, TestData.XPDL);

        Process process = createProcess(user, nativeType, folder);
        process.getGroupProcesses().add(createGroupProcess(group, process, true, true, true));
        process.getGroupProcesses().add(createGroupProcess(publicGroup, process, true, true, true)); // public
        // process
        ProcessBranch branch = createBranch(process);
        Storage storage = createStorage();
        ProcessModelVersion pmv = createPMV(branch, existingNativeDoc, existingVersion, storage);
        branch.setCurrentProcessModelVersion(pmv);
        branch.getProcessModelVersions().add(pmv);
        process.getProcessBranches().add(branch);

        // Parameter setup
        Integer processId = process.getId();
        String existingVersionNumber = existingVersion.toString();

        String newProcessName = "NEW PROCESS NAME";
        String newProcessDomain = "NEW DOMAIN";
        String newProcessRanking = "NEW RANKING";
        String newProcessVersion = newVersion.toString();
        boolean newProcessPublicStatus = false; // made non-public
        User newUser = createUser("userName2", group, createSet(group), createSet(role));
        String newUserName = newUser.getUsername();

        // Mock Recording
        expect(processModelVersionRepo.getCurrentProcessModelVersion(processId, existingVersionNumber))
                .andReturn(pmv);
        expect(processRepo.findById(processId)).andReturn(Optional.of(process));
        expect(usrSrv.findUserByLogin(newUserName)).andReturn(newUser);
        expect(groupRepo.findPublicGroup()).andReturn(publicGroup);
        expect(groupRepo.findPublicGroup()).andReturn(publicGroup);

        expect(processRepo.save((Process) EasyMock.anyObject())).andReturn(process);
        expect(processModelVersionRepo.save((ProcessModelVersion) EasyMock.anyObject())).andReturn(pmv);
        expect(processBranchRepo.save((ProcessBranch) EasyMock.anyObject())).andReturn(branch);
        workspaceSrv.removePublicStatusForUsers(process);

        replayAll();

        // Mock Call
        processService.updateProcessMetaData(processId, newProcessName, newProcessDomain, newUserName,
                existingVersion, newVersion, newProcessRanking, newProcessPublicStatus);

        // Verify mock and result
        verifyAll();
        Assert.assertEquals(newProcessName, process.getName());
        Assert.assertEquals(newProcessDomain, process.getDomain());
        Assert.assertEquals(newProcessRanking, process.getRanking());
        Assert.assertEquals(newUser, process.getUser());
        Assert.assertEquals(newProcessVersion, pmv.getVersionNumber());
        Assert.assertEquals(branch.getCurrentProcessModelVersion(), pmv);
        boolean processHasPublicGroup = false;
        for (GroupProcess gp : process.getGroupProcesses()) {
            if (gp.getGroup().getType() == Group.Type.PUBLIC) {
                processHasPublicGroup = true;
                break;
            }
        }
        Assert.assertEquals(false, processHasPublicGroup);
    }


    @Test
    public void testUpdateProcessMetadata_CurrentPublic_TobeMadeNonPublic() throws Exception {
        // Test Data setup
        Folder folder = createFolder();
        Group group = createGroup(1, Group.Type.GROUP);
        Group publicGroup = createGroup(2, Group.Type.PUBLIC);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        Version existingVersion = createVersion("1.0");
        Version newVersion = createVersion("1.1");
        NativeType nativeType = createNativeType();
        Native existingNativeDoc = createNative(nativeType, TestData.XPDL);

        Process process = createProcess(user, nativeType, folder);
        process.getGroupProcesses().add(createGroupProcess(group, process, true, true, true)); // No
        // public
        // process
        ProcessBranch branch = createBranch(process);
        Storage storage = createStorage();
        ProcessModelVersion pmv = createPMV(branch, existingNativeDoc, existingVersion, storage);
        branch.setCurrentProcessModelVersion(pmv);
        branch.getProcessModelVersions().add(pmv);
        process.getProcessBranches().add(branch);

        // Parameter setup
        Integer processId = process.getId();
        String existingVersionNumber = existingVersion.toString();

        String newProcessName = "NEW PROCESS NAME";
        String newProcessDomain = "NEW DOMAIN";
        String newProcessRanking = "NEW RANKING";
        String newProcessVersion = newVersion.toString();
        boolean newProcessPublicStatus = true;
        User newUser = createUser("userName2", group, createSet(group), createSet(role));
        String newUserName = newUser.getUsername();

        // Mock Recording
        expect(processModelVersionRepo.getCurrentProcessModelVersion(processId, existingVersionNumber))
                .andReturn(pmv);
        expect(processRepo.findById(processId)).andReturn(Optional.of(process));
        expect(usrSrv.findUserByLogin(newUserName)).andReturn(newUser);
        expect(groupRepo.findPublicGroup()).andReturn(publicGroup);
        expect(groupRepo.findPublicGroup()).andReturn(publicGroup);

        expect(processRepo.save((Process) EasyMock.anyObject())).andReturn(process);
        expect(processModelVersionRepo.save((ProcessModelVersion) EasyMock.anyObject())).andReturn(pmv);
        expect(processBranchRepo.save((ProcessBranch) EasyMock.anyObject())).andReturn(branch);
        workspaceSrv.createPublicStatusForUsers(process);

        replayAll();

        // Mock Call
        processService.updateProcessMetaData(processId, newProcessName, newProcessDomain, newUserName,
                existingVersion, newVersion, newProcessRanking, newProcessPublicStatus);

        // Verify mock and result
        verifyAll();
        Assert.assertEquals(newProcessName, process.getName());
        Assert.assertEquals(newProcessDomain, process.getDomain());
        Assert.assertEquals(newProcessRanking, process.getRanking());
        Assert.assertEquals(newUser, process.getUser());
        Assert.assertEquals(newProcessVersion, pmv.getVersionNumber());
        Assert.assertEquals(branch.getCurrentProcessModelVersion(), pmv);
        boolean processHasPublicGroup = false;
        for (GroupProcess gp : process.getGroupProcesses()) {
            if (gp.getGroup().getType() == Group.Type.PUBLIC) {
                processHasPublicGroup = true;
                break;
            }
        }
        Assert.assertTrue(processHasPublicGroup);
    }

    @Test
    public void testExportProcess() throws Exception {
        // Test Data setup
        Folder folder = createFolder();
        Group group = createGroup(123, Group.Type.GROUP);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        Version version = createVersion("1.0.0");
        NativeType nativeType = createNativeType();
        Native nativeDoc = createNative(nativeType, TestData.XPDL);

        Process process = createProcess(user, nativeType, folder);
        ProcessBranch branch = createBranch(process);
        Storage storage = createStorage();
        String filename = "testUpdateProcessModelVersion_withStorage.bpmn";
        storage.setKey(filename);
        ProcessModelVersion pmv = createPMV(branch, nativeDoc, version, storage);

        // Parameter setup
        Integer processId = process.getId();
        String processName = process.getName();
        String branchName = branch.getBranchName();
        String versionNumber = version.toString();
        String nativeTypeS = nativeType.getNatType();

        StorageManagementFactory<StorageClient> storageManagementFactory;
        storageManagementFactory = new StorageManagementFactoryImpl<StorageClient>();
        URL url = getClass().getClassLoader().getResource("file_store_dir/model/" + filename);
        File file = new File(url.getFile());
        StorageClient client = storageManagementFactory.getStorageClient("FILE::" + file.getParent().substring(0,
                file.getParent().length() - 5));
        InputStream stream = client.getInputStream("model", filename);

        // Mock Recording
        expect(processModelVersionRepo.getProcessModelVersion(processId, branchName, versionNumber))
                .andReturn(pmv).times(2);
        expect(storageFactory.getStorageClient(storage.getStoragePath()))
                .andReturn(client);
        replayAll();

        // Mock call
        ExportFormatResultType exportResult =
                processService.exportProcess(processName, processId, branchName, version, nativeTypeS);

        // Verify mock and result
        verifyAll();
        String exportResultText =
                CharStreams.toString(new InputStreamReader(exportResult.getNative().getInputStream()));
        Assert.assertEquals(StreamUtil.convertStreamToString(client.getInputStream("model", filename)),
                exportResultText);
    }

    @Test
    public void testGetBPMNRepresentation() throws Exception {
        // Test Data setup
        Folder folder = createFolder();
        Group group = createGroup(123, Group.Type.GROUP);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        Version version = createVersion("1.0.0");
        NativeType nativeType = createNativeType();
        Native nativeDoc = createNative(nativeType, TestData.XPDL);

        Process process = createProcess(user, nativeType, folder);
        ProcessBranch branch = createBranch(process);
        Storage storage = createStorage();
        String filename = "testUpdateProcessModelVersion_withStorage.bpmn";
        storage.setKey(filename);
        ProcessModelVersion pmv = createPMV(branch, nativeDoc, version, storage);

        // Parameter setup
        Integer processId = process.getId();
        String processName = process.getName();
        String branchName = branch.getBranchName();
        String versionNumber = version.toString();
        String nativeTypeS = nativeType.getNatType();

        StorageManagementFactory<StorageClient> storageManagementFactory;
        storageManagementFactory = new StorageManagementFactoryImpl<StorageClient>();
        URL url = getClass().getClassLoader().getResource("file_store_dir/model/" + filename);
        File file = new File(url.getFile());
        StorageClient client = storageManagementFactory.getStorageClient("FILE::" + file.getParent().substring(0,
                file.getParent().length() - 5));
        InputStream stream = client.getInputStream("model", filename);

        // Mock Recording
        expect(processModelVersionRepo.getProcessModelVersion(processId, branchName, versionNumber))
                .andReturn(pmv).times(2);
        expect(storageFactory.getStorageClient(storage.getStoragePath()))
                .andReturn(client);
        replayAll();

        // Mock call
        String bpmnResult =
                processService.getBPMNRepresentation(processName, processId, branchName, version);

        // Verify mock and result
        verifyAll();
        Assert.assertEquals(StreamUtil.convertStreamToString(client.getInputStream("model", filename)), bpmnResult);
    }


    @Test
    public void testDeleteProcessModel_BranchHasMoreThanOnePMV() throws Exception {
        // Test Data setup
        Folder folder = createFolder();
        Group group = createGroup(123, Group.Type.GROUP);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        Version version10 = createVersion("1.0");
        Version version11 = createVersion("1.1");
        NativeType nativeType = createNativeType();
        Native existingNativeDoc = createNative(nativeType, TestData.XPDL);
        Native newNativeDoc = createNative(nativeType, TestData.XPDL2);

        Process process = createProcess(user, nativeType, folder);
        ProcessBranch branch = createBranch(process);
        Storage storage = createStorage();
        ProcessModelVersion pmv10 = createPMV(branch, existingNativeDoc, version10, storage); // to delete
        ProcessModelVersion pmv11 = createPMV(branch, newNativeDoc, version11, storage);
        addProcessModelVersions(branch, pmv10, pmv11);
        GroupProcess groupProcess = createGroupProcess(group, process, true, true, true);

        // Parameter setup
        Integer processId = process.getId();
        String versionToDelete = version10.toString();
        ProcessData processDataToDelete = new ProcessData(processId, version10);

        // Mock Recording
        expect(processModelVersionRepo.getCurrentProcessModelVersion(processId, versionToDelete))
                .andReturn(pmv10);
        expect(groupProcessRepo.findByProcessAndUser(processId, user.getRowGuid()))
                .andReturn(Arrays.asList(new GroupProcess[]{groupProcess}));
        expect(processBranchRepo.save((ProcessBranch) EasyMock.anyObject())).andReturn(branch);
        expect(processModelVersionRepo.countByStorageId(storage.getId())).andReturn(1L);
        processModelVersionRepo.delete(pmv10);
        replayAll();

        // Mock Call
        processService.deleteProcessModel(Arrays.asList(new ProcessData[]{processDataToDelete}), user);

        // Verify mock and result
        verifyAll();
        Assert.assertEquals(1, branch.getProcessModelVersions().size());
        Assert.assertEquals(pmv11, branch.getProcessModelVersions().get(0));
    }


    @Test
    public void testDeleteProcessModel_BranchHasOnlyOnePMV() throws Exception {
        // Test Data setup
        Folder folder = createFolder();
        Group group = createGroup(123, Group.Type.GROUP);
        Role role = createRole(createSet(createPermission()));
        User user = createUser("userName1", group, createSet(group), createSet(role));
        Version version10 = createVersion("1.0");
        NativeType nativeType = createNativeType();
        Native existingNativeDoc = createNative(nativeType, TestData.XPDL);

        Process process = createProcess(user, nativeType, folder);
        ProcessBranch branch = createBranch(process);
        Storage storage = createStorage();
        ProcessModelVersion pmv10 = createPMV(branch, existingNativeDoc, version10, storage); // to delete
        addProcessModelVersions(branch, pmv10);
        GroupProcess groupProcess = createGroupProcess(group, process, true, true, true);

        // Parameter setup
        Integer processId = process.getId();
        String versionToDelete = version10.toString();
        ProcessData processDataToDelete = new ProcessData(processId, version10);

        // Mock Recording
        expect(processModelVersionRepo.getCurrentProcessModelVersion(processId, versionToDelete))
                .andReturn(pmv10);
        expect(groupProcessRepo.findByProcessAndUser(processId, user.getRowGuid()))
                .andReturn(Arrays.asList(new GroupProcess[]{groupProcess}));
        expect(processBranchRepo.save(branch)).andReturn(branch);
        expect(processModelVersionRepo.countByStorageId(storage.getId())).andReturn(1L);
        processModelVersionRepo.delete(pmv10);
        processRepo.delete(process);
        replayAll();

        // Mock Call
        processService.deleteProcessModel(Arrays.asList(new ProcessData[]{processDataToDelete}), user);

        // Verify mock only (assume that JPA has done all database work properly)
        verifyAll();
    }


    /**
     * Test the {@link ProcessServiceImpl#sanitizeBPMN} method.
     */
    @Test
    public void testSanitizeBPMN() throws Exception {
        for (String[] s : new String[][]{
                // unsanitized input file expected sanitized output issue description
                {"Cyclic.bpmn", "Cyclic.bpmn", "Innocuous files should be unchanged"},
                {"unsanitized_documentation.bpmn", "sanitized_documentation.bpmn",
                        "bpmn:documentation should have complex content removed"},
                {"unsanitized_naked_script.bpmn", "sanitized_naked_script.bpmn",
                        "Spurious <script> tags should be commented out"},
                {"unsanitized_script.bpmn", "sanitized_script.bpmn",
                        "bpmn:scriptTask must have its script child commented out"},
                {"unsanitized_text.bpmn", "sanitized_text.bpmn",
                        "bpmn:text should have complex content removed"}}) {
            Assert.assertEquals(s[2],
                    CharStreams.toString(new InputStreamReader(getResourceAsStream("BPMN_models/" + s[1])))
                            .trim(),
                    CharStreams.toString(new InputStreamReader(
                            ProcessServiceImpl.sanitizeBPMN(getResourceAsStream("BPMN_models/" + s[0])))));
        }
    }

    /**
     * @param path the classpath of a desired resource within the test JAR
     * @return the content of the resource at <var>path</var>
     * @throws Exception if <var>path</var> doesn't match a resource in the test JAR
     */
    private InputStream getResourceAsStream(String path) throws Exception {
        InputStream result = getClass().getClassLoader().getResourceAsStream(path);
        if (result == null) {
            throw new Exception(path + " is not a resource");
        }

        return result;
    }

    ///////////////////////////////// DATA METHODS ////////////////////////////////////////

    private Process createProcess(User user, NativeType natType, Folder folder) {
        Process process = new Process();
        process.setId(1234);
        process.setFolder(folder);
        process.setCreateDate("1.1.2020");
        process.setDomain("domain");
        process.setName("ProcessName");
        process.setUser(user);
        process.setNativeType(natType);
        return process;
    }

    private ProcessBranch createBranch(Process process) {
        ProcessBranch branch = new ProcessBranch();
        branch.setId(1234);
        branch.setBranchName("BranchName");
        branch.setProcess(process);
        branch.setCreateDate("1.1.2020");
        branch.setLastUpdateDate("1.1.2020");
        return branch;
    }

    private void addProcessModelVersions(ProcessBranch branch, ProcessModelVersion... pmvs) {
        if (pmvs != null && pmvs.length > 0) {
            branch.setProcessModelVersions(new ArrayList<>(Arrays.asList(pmvs)));
        }
    }

    private ProcessModelVersion createPMV(ProcessBranch branch, Native nativeDoc, Version version, Storage storage) {
        ProcessModelVersion pmv = new ProcessModelVersion();
        pmv.setId(123);
        pmv.setOriginalId("123");
        pmv.setCreateDate("1.1.2020");
        pmv.setLastUpdateDate("1.1.2020");
        pmv.setProcessBranch(branch);
        pmv.setLastUpdateDate("");
        if (nativeDoc != null) {
            pmv.setNativeType(nativeDoc.getNativeType());
            pmv.setNativeDocument(nativeDoc);
            nativeDoc.setProcessModelVersion(pmv);
        }
        pmv.setVersionNumber(version.toString());
        pmv.setNumEdges(0);
        pmv.setNumVertices(0);
        pmv.setLockStatus(Constants.NO_LOCK);
        pmv.setStorage(storage);
        return pmv;
    }

    private Storage createStorage() {
        Storage storage = new Storage();
        storage.setId(1L);
        storage.setStoragePath("FILE::/Path");
        storage.setCreated(new Date().toString());
        storage.setUpdated(new Date().toString());
        storage.setKey("model");
        storage.setPrefix("model");

        return storage;
    }

    private Folder createFolder() {
        Folder folder = new Folder();
        folder.setId(1);
        return folder;
    }

    private Version createVersion(String versionNumber) {
        Version version = new Version(versionNumber);
        return version;
    }

    private Native createNative(NativeType nativeType, String nativeContent) {
        Native nat = new Native();
        nat.setNativeType(nativeType);
        nat.setContent(nativeContent);
        nat.setLastUpdateDate("1.1.2020");
        return nat;
    }

    private NativeType createNativeType() {
        NativeType nat = new NativeType();
        nat.setExtension("ext");
        nat.setNatType("BPMN 2.0");
        nat.setExtension("bpmn");
        return nat;
    }

    private User createUser(String userName, Group group, Set<Group> groups, Set<Role> roles) {
        User user = new User();
        user.setId(123);
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setUsername(userName);
        user.setOrganization("Apromore");
        user.setCountry("Australia");
        user.setSubscription("UserSubscription");
        user.setRowGuid("UserRowGuid");
        user.setGroup(group);
        user.setRole("UserRole");
        user.setRoles(roles);
        user.setGroups(groups);
        return user;
    }

    private Permission createPermission() {
        Permission p = new Permission();
        p.setId(123);
        p.setDescription("PermissionDesc");
        p.setName("PermissionName");
        p.setRowGuid("PermissionRowGuid");
        return p;
    }

    private Role createRole(Set<Permission> permissions) {
        Role role = new Role();
        role.setId(123);
        role.setName("RoleName");
        role.setRowGuid("RoleGuid");
        role.setPermissions(permissions);
        role.setDescription("RoleDescription");
        return role;
    }

    private Group createGroup(Integer groupId, Group.Type groupType) {
        Group group = new Group();
        group.setRowGuid("groupGUID");
        group.setName("GroupName");
        group.setId(groupId);
        group.setType(groupType);
        return group;
    }

    private <E> Set<E> createSet(E... arrayT) {
        return new HashSet<E>(Arrays.asList(arrayT));
    }

    private GroupProcess createGroupProcess(Group group, Process process, boolean hasOwnership,
                                            boolean hasRead, boolean hasWrite) {
        GroupProcess gp = new GroupProcess();
        gp.setId(123);
        gp.setGroup(group);
        gp.setProcess(process);
        AccessRights accessRights = new AccessRights(hasRead, hasWrite, hasOwnership);
        gp.setAccessRights(accessRights);
        return gp;
    }


}
