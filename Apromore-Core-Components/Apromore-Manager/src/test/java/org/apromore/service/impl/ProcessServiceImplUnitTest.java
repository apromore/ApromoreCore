/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.activation.DataHandler;

import org.apromore.TestData;
import org.apromore.common.ConfigBean;
import org.apromore.common.Constants;
import org.apromore.dao.GroupProcessRepository;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.NativeRepository;
import org.apromore.dao.ProcessBranchRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.User;
import org.apromore.helper.Version;
import org.apromore.model.ExportFormatResultType;
import org.apromore.service.FormatService;
import org.apromore.service.LockService;
import org.apromore.service.UserService;
import org.apromore.service.WorkspaceService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.easymock.EasyMockSupport;
import org.eclipse.persistence.internal.oxm.ByteArrayDataSource;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.CharStreams;

import junit.framework.Assert;

public class ProcessServiceImplUnitTest extends EasyMockSupport {

    private ProcessServiceImpl service;
    private UserService usrSrv;
    private FormatService fmtSrv;
    private UserInterfaceHelper ui;
    private WorkspaceService workspaceSrv;
    private LockService lockSrv;
    private ConfigBean config;

    private ProcessRepository processRepo;
    private NativeRepository nativeRepo;
    private ProcessModelVersionRepository processModelVersionRepo;
    private ProcessBranchRepository processBranchRepo;
    private GroupRepository groupRepo;
    private GroupProcessRepository groupProcessRepo;

    @Before
    public final void setUp() throws Exception {
        //nativeRepo = createMock(NativeRepository.class);
        groupRepo = createMock(GroupRepository.class);
        groupProcessRepo = createMock(GroupProcessRepository.class);
        processBranchRepo = createMock(ProcessBranchRepository.class);
        processRepo = createMock(ProcessRepository.class);
        processModelVersionRepo = createMock(ProcessModelVersionRepository.class);
        nativeRepo = createMock(NativeRepository.class);
        usrSrv = createMock(UserService.class);
        fmtSrv = createMock(FormatService.class);
        ui = createMock(UserInterfaceHelper.class);
        workspaceSrv = createMock(WorkspaceService.class);
        lockSrv = createMock(LockService.class);
        config = new ConfigBean();

        service = new ProcessServiceImpl(nativeRepo, groupRepo, processBranchRepo, processRepo, processModelVersionRepo, groupProcessRepo, lockSrv, 
                                        usrSrv, fmtSrv, ui, workspaceSrv, config);
    }
    
    @Test
    public void testImportProcess_createNew() throws Exception {
        //Test data setup
        Folder folder = createFolder();
        User user = createUser();
        Version version = createVersion();
        NativeType nativeType = createNativeType();
        Native nativeDoc = createNative(nativeType);
        
        Process process = createProcess(user, nativeType, folder);
        ProcessBranch branch = createBranch(process);
        ProcessModelVersion pmv = createPMV(branch, nativeDoc, version);

        //Parameter setup
        String userName = user.getUsername();
        String createDate = pmv.getCreateDate();
        String lastUpdateDate = pmv.getLastUpdateDate();
        String processName = process.getName();
        String domainName = process.getDomain();
        InputStream nativeStream = (new DataHandler(new ByteArrayDataSource(nativeDoc.getContent().getBytes(), "text/xml")))
                                    .getInputStream();
        String nativeTypeS = nativeType.getNatType();
        Integer folderId = folder.getId();
        
        
        //MOCK RECORDING
        
        //Insert process
        expect(usrSrv.findUserByLogin(userName)).andReturn(user);
        expect(fmtSrv.findNativeType(nativeTypeS)).andReturn(nativeType);
        expect(workspaceSrv.getFolder((Integer) anyObject())).andReturn(folder);
        expect(processRepo.save((Process) anyObject())).andReturn(process);
        expect(processRepo.saveAndFlush((Process) anyObject())).andReturn(process);
        
        //Insert branch
        expect(processBranchRepo.save((ProcessBranch) anyObject())).andReturn(branch);
        
        //Insert process model version
        expect(processModelVersionRepo.save((ProcessModelVersion) anyObject())).andReturn(pmv);
        
        //Store native
        //expect(nativeRepo.save((Native) anyObject())).andReturn(createMock(Native.class));
        fmtSrv.storeNative(processName, pmv, createDate, lastUpdateDate, user, nativeType, Constants.INITIAL_ANNOTATION, nativeStream);
        
        workspaceSrv.addProcessToFolder(process.getId(), folder.getId());

        replayAll();

        // MOCK CALL AND VERIFY
        ProcessModelVersion pmvResult = service.importProcess(userName, folderId, processName, version, nativeType.getNatType(), 
                                                nativeStream, domainName, "", createDate, lastUpdateDate, false);
        verifyAll();

        // VERIFY RESULT AGAINST TEST DATA
        Assert.assertEquals(pmvResult.getProcessBranch().getProcess().getName(), pmv.getProcessBranch().getProcess().getName());
        Assert.assertEquals(pmvResult.getProcessBranch().getBranchName(), pmv.getProcessBranch().getBranchName());        
        Assert.assertEquals(pmvResult.getNativeType().getNatType(), pmv.getNativeType().getNatType());
        Assert.assertEquals(pmvResult.getVersionNumber(), pmv.getVersionNumber());
        Assert.assertEquals(pmvResult.getNativeDocument().getContent(), pmv.getNativeDocument().getContent());
        Assert.assertEquals(pmvResult.getCreateDate(), pmv.getCreateDate());
        Assert.assertEquals(pmvResult.getLastUpdateDate(), pmv.getLastUpdateDate());
    }

    @Test
    public void testExportFormat() throws Exception {
        // Test Data setup
        Folder folder = createFolder();
        User user = createUser();
        Version version = createVersion();
        NativeType nativeType = createNativeType();
        Native nativeDoc = createNative(nativeType);
        
        Process process = createProcess(user, nativeType, folder);
        ProcessBranch branch = createBranch(process);
        ProcessModelVersion pmv = createPMV(branch, nativeDoc, version);
        
        // Parameter setup
        Integer processId = process.getId();
        String processName = process.getName();
        String branchName = branch.getBranchName();
        String versionNumber = version.toString();
        String nativeTypeS = nativeType.getNatType();
        
        // Mock Recording
        expect(processModelVersionRepo.getProcessModelVersion(processId, branchName, versionNumber)).andReturn(pmv);
        expect(nativeRepo.getNative(processId, branchName, versionNumber, nativeTypeS)).andReturn(nativeDoc);
        
        // Mock call and verify
        replayAll();
        ExportFormatResultType exportResult = service.exportProcess(processName, processId, branchName, version, nativeTypeS);
        verifyAll();

        // Verify result against test data
        String exportResultText = CharStreams.toString(new InputStreamReader(exportResult.getNative().getInputStream()));
        Assert.assertEquals(nativeDoc.getContent(), exportResultText);
    }

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
    
    private ProcessModelVersion createPMV(ProcessBranch branch, Native nativeDoc, Version version) {
        ProcessModelVersion pmv = new ProcessModelVersion();
        pmv.setId(123);
        pmv.setCreateDate("1.1.2020");
        pmv.setLastUpdateDate("1.1.2020");
        pmv.setProcessBranch(branch);
        pmv.setLastUpdateDate("");
        pmv.setNativeType(nativeDoc.getNativeType());
        pmv.setNativeDocument(nativeDoc);
        pmv.setVersionNumber(version.toString());
        pmv.setOriginalId("123");
        pmv.setNumEdges(0);
        pmv.setNumVertices(0);
        return pmv;
    }
    
    private Folder createFolder() {
        Folder folder = new Folder();
        folder.setId(0);
        return folder;
    }
    
    private Version createVersion() {
        Version version = new Version("1.0.0");
        return version;
    }
    
    private Native createNative(NativeType nativeType) {
        Native nat = new Native();
        nat.setNativeType(nativeType);
        nat.setContent(TestData.XPDL);
        nat.setLastUpdateDate("1.1.2020");
        return nat;
    }

    private NativeType createNativeType() {
        NativeType nat = new NativeType();
        nat.setExtension("ext");
        nat.setNatType("nat");
        return nat;
    }

    private User createUser() {
        User usr = new User();
        usr.setFirstName("first");
        usr.setLastName("last");
        usr.setUsername("user");
        return usr;
    }


}
