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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.apromore.common.ConfigBean;
import org.apromore.dao.GroupProcessRepository;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.NativeRepository;
import org.apromore.dao.ProcessBranchRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.ProcessRepository;
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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit test the UserService Implementation.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ProcessServiceImplUnitTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private ProcessServiceImpl service;

    private NativeRepository natDao;
    private ProcessModelVersionRepository pmvDao;

    @Before
    public final void setUp() throws Exception {
        natDao = createMock(NativeRepository.class);
        GroupRepository grpDao = createMock(GroupRepository.class);
        GroupProcessRepository grpProcDao = createMock(GroupProcessRepository.class);
        ProcessBranchRepository branchDao = createMock(ProcessBranchRepository.class);
        ProcessRepository proDao = createMock(ProcessRepository.class);
        pmvDao = createMock(ProcessModelVersionRepository.class);
        UserService usrSrv = createMock(UserService.class);
        FormatService fmtSrv = createMock(FormatService.class);
        LockService lSrv = createMock(LockService.class);
        UserInterfaceHelper ui = createMock(UserInterfaceHelper.class);
        WorkspaceService workspaceSrv = createMock(WorkspaceService.class);
        ConfigBean config = new ConfigBean();

        service = new ProcessServiceImpl(natDao, grpDao, branchDao, proDao, pmvDao, grpProcDao, lSrv, usrSrv, fmtSrv, ui, workspaceSrv, config);
    }

    @Test
    public void testExportFormatGetAnnotation() throws Exception {
        Integer processId = 123;
        String version = "1.2";
        String name = "processName";
        String format = "EPML 2.0";
        String subStr = "MN";
        Version versionNumber = new Version(1,0);

        NativeType natType = new NativeType();
        natType.setNatType("EPML 2.0");

        Native nat = new Native();
        nat.setContent("<xml/>");

        org.apromore.dao.model.Process process = new org.apromore.dao.model.Process();
        process.setId(processId);
        process.setNativeType(natType);

        ProcessBranch branch = new ProcessBranch();
        branch.setId(processId);
        branch.setBranchName(name);
        branch.setProcess(process);

        ProcessModelVersion pmv = new ProcessModelVersion();
        pmv.setId(processId);
        pmv.setNativeType(natType);
        pmv.setProcessBranch(branch);

        expect(pmvDao.getProcessModelVersion(processId, version, versionNumber.toString())).andReturn(pmv);
        expect(natDao.getNative(processId, version, versionNumber.toString(), format)).andReturn(nat);

        replay(pmvDao, natDao);

        ExportFormatResultType data = service.exportProcess(name, processId, version, versionNumber, format);

        verify(pmvDao, natDao);

        MatcherAssert.assertThat(data, Matchers.notNullValue());
    }

    @Test
    public void testImportProcess() throws Exception {
        String username = "bubba";
        String processName = "TestProcess";
        String cpfURI = "112321234";
        String version = "1.2";
        String natType = "XPDL 2.1";
        String domain = "Airport";
        String created = "12/12/2011";
        String lastUpdate = "12/12/2011";

//        DataHandler stream = new DataHandler(new ByteArrayDataSource(TestData.XPDL.getBytes(), "text/xml"));
//        User user = new User();
//        user.setUsername(username);
//
//        NativeType nativeType = new NativeType();
//        nativeType.setNatType(natType);
//
//        expect(usrSrv.findUser(username)).andReturn(user);
//        expect(fmtSrv.findNativeType(natType)).andReturn(nativeType);
//        proDao.save((Process) anyObject());
//        expectLastCall().atLeastOnce();
//        canDao.save((Canonical) anyObject());
//        expectLastCall().atLeastOnce();
//        natDao.save((Native) anyObject());
//        expectLastCall().atLeastOnce();
//        annDao.save((Annotation) anyObject());
//        expectLastCall().atLeastOnce();
//
//        replayAll();
//
//        ProcessSummaryType procSum = service.importProcess(username, processName, cpfURI, version, natType, stream, domain, "", created, lastUpdate);
//
//        verifyAll();
//
//        assertThat(procSum, notNullValue());
    }


    private Process createProcess() {
        Process process = new Process();
        //process.setProcessId(1234);
        process.setDomain("domain");
        process.setName("name");
        process.setUser(createUser());
        process.setNativeType(createNativeType());
        return process;
    }

    private NativeType createNativeType() {
        NativeType nat = new NativeType();
        nat.setExtension("ext");
        nat.setNatType("nat");
        return nat;
    }

    private User createUser() {
        User usr = new User();
//        usr.setFirstname("first");
//        usr.setLastname("last");
//        usr.setEmail("fl@domain.com");
//        usr.setUsername("user");
//        usr.setPasswd("pass");
        return usr;
    }

    private Native createNative() {
        Native nat = new Native();
        //nat.setUri(1234);
        nat.setNativeType(createNativeType());
        return nat;
    }

}
