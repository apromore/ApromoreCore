/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apromore.common.ConfigBean;
import org.apromore.dao.AnnotationRepository;
import org.apromore.dao.FragmentVersionDagRepository;
import org.apromore.dao.FragmentVersionRepository;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.GroupProcessRepository;
import org.apromore.dao.NativeRepository;
import org.apromore.dao.ProcessBranchRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.helper.Version;
import org.apromore.model.ExportFormatResultType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.AnnotationService;
import org.apromore.service.CanonicalConverter;
import org.apromore.service.CanoniserService;
import org.apromore.service.ComposerService;
import org.apromore.service.DecomposerService;
import org.apromore.service.FormatService;
import org.apromore.service.FragmentService;
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
        AnnotationRepository annDao = createMock(AnnotationRepository.class);
        natDao = createMock(NativeRepository.class);
        GroupRepository grpDao = createMock(GroupRepository.class);
        GroupProcessRepository grpProcDao = createMock(GroupProcessRepository.class);
        ProcessBranchRepository branchDao = createMock(ProcessBranchRepository.class);
        ProcessRepository proDao = createMock(ProcessRepository.class);
        FragmentVersionRepository fvDao = createMock(FragmentVersionRepository.class);
        FragmentVersionDagRepository fvdDao = createMock(FragmentVersionDagRepository.class);
        pmvDao = createMock(ProcessModelVersionRepository.class);
        UserService usrSrv = createMock(UserService.class);
        FormatService fmtSrv = createMock(FormatService.class);
        AnnotationService annSrv = createMock(AnnotationService.class);
        CanoniserService canSrv = createMock(CanoniserService.class);
        LockService lSrv = createMock(LockService.class);
        CanonicalConverter convertor = createMock(CanonicalConverter.class);
        ComposerService composerSrv = createMock(ComposerService.class);
        DecomposerService decomposerSrv = createMock(DecomposerService.class);
        UserInterfaceHelper ui = createMock(UserInterfaceHelper.class);
        FragmentService fSrv = createMock(FragmentService.class);
        WorkspaceService workspaceSrv = createMock(WorkspaceService.class);
        ConfigBean config = new ConfigBean();

        service = new ProcessServiceImpl(annDao, natDao, grpDao, branchDao, proDao, fvDao, fvdDao, pmvDao, grpProcDao, convertor, annSrv, canSrv, lSrv, usrSrv, fSrv, fmtSrv, composerSrv, decomposerSrv, ui, workspaceSrv, config);
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

        ExportFormatResultType data = service.exportProcess(name, processId, version, versionNumber, format, subStr, true, new HashSet<RequestParameterType<?>>());

        verify(pmvDao, natDao);

        MatcherAssert.assertThat(data, Matchers.notNullValue());
    }

    //    @Test
    //    public void getAllProcessesWithSearchCriteria() {
    //        String searchExpression = "invoicing";
    //        List<Canonical> canonicals = new ArrayList<Canonical>();
    //        List<Object[]> processes = new ArrayList<Object[]>();
    //
    //        Object[] procSummary = new Object[2];
    //        Process process = createProcess();
    //        procSummary[0] = process;
    //        procSummary[1] = "2.0";
    //        processes.add(procSummary);
    //
    //        expect(proDao.getAllProcesses(CONDITION)).andReturn(processes);
    //        expect(canDao.findByProcessId(Long.valueOf(process.getProcessId()).intValue())).andReturn(canonicals);
    //        replay(proDao, canDao);
    //
    //        ProcessSummariesType processSummary = service.readProcessSummaries(searchExpression);
    //
    //        verify(proDao, canDao);
    //
    //        assertThat(processSummary.getProcessSummary().size(), equalTo(processes.size()));
    //        assertThat(processSummary.getProcessSummary().get(0).getLastVersion(), equalTo(null));
    //    }
    //
    //    @Test
    //    public void getAllProcessesCanonicals() {
    //        String searchExpression = "";
    //
    //        // For the Processes
    //        List<Object[]> processes = new ArrayList<Object[]>();
    //        Object[] procSummary = new Object[2];
    //        Process process = createProcess();
    //        procSummary[0] = process;
    //        procSummary[1] = "2.0";
    //        processes.add(procSummary);
    //
    //        // For the Canonicals
    //        List<Canonical> canonicals = new ArrayList<Canonical>();
    //
    //        expect(proDao.getAllProcesses(searchExpression)).andReturn(processes);
    //        expect(canDao.findByProcessId(Long.valueOf(process.getProcessId()).intValue())).andReturn(canonicals);
    //        replay(proDao, canDao);
    //
    //        ProcessSummariesType processSummary = service.readProcessSummaries(searchExpression);
    //
    //        verify(proDao, canDao);
    //
    //        assertThat(processSummary.getProcessSummary().size(), equalTo(processes.size()));
    //        assertThat(processSummary.getProcessSummary().get(0).getLastVersion(), equalTo(null));
    //    }
    //
    //    @Test
    //    public void getAllProcessesNatives() {
    //        String searchExpression = "";
    //
    //        // For the Processes
    //        List<Object[]> processes = new ArrayList<Object[]>();
    //        Object[] procSummary = new Object[2];
    //        Process process = createProcess();
    //        procSummary[0] = process;
    //        procSummary[1] = "2.0";
    //        processes.add(procSummary);
    //
    //        // For the Canonicals
    //        List<Canonical> canonicals = new ArrayList<Canonical>();
    //        canonicals.add(createCanonical());
    //
    //        // For the Natives
    //        List<Native> natives = new ArrayList<Native>();
    //
    //        expect(proDao.getAllProcesses(searchExpression)).andReturn(processes);
    //        expect(canDao.findByProcessId(Long.valueOf(process.getProcessId()).intValue())).andReturn(canonicals);
    //        expect(natDao.findNativeByCanonical(Long.valueOf(process.getProcessId()).intValue(), "version")).andReturn(natives);
    //        replay(proDao, canDao, natDao);
    //
    //        ProcessSummariesType processSummary = service.readProcessSummaries(searchExpression);
    //
    //        verify(proDao, canDao, natDao);
    //
    //        assertThat(processSummary.getProcessSummary().size(), equalTo(processes.size()));
    //        assertThat(processSummary.getProcessSummary().get(0).getName(), equalTo("name"));
    //        assertThat(processSummary.getProcessSummary().get(0).getVersionSummaries().get(0).getName(), equalTo("version"));
    //    }
    //
    //    @Test
    //    public void getAllProcessesAnnotations() {
    //        String searchExpression = "";
    //
    //        // For the Processes
    //        List<Object[]> processes = new ArrayList<Object[]>();
    //        Object[] procSummary = new Object[2];
    //        Process process = createProcess();
    //        procSummary[0] = process;
    //        procSummary[1] = "2.0";
    //        processes.add(procSummary);
    //
    //        // For the Canonicals
    //        List<Canonical> canonicals = new ArrayList<Canonical>();
    //        canonicals.add(createCanonical());
    //
    //        // For the Natives
    //        List<Native> natives = new ArrayList<Native>();
    //        natives.add(createNative());
    //
    //        // For the Annotations
    //        List<Annotation> annotations = new ArrayList<Annotation>();
    //
    //        expect(proDao.getAllProcesses(searchExpression)).andReturn(processes);
    //        expect(canDao.findByProcessId(Long.valueOf(process.getProcessId()).intValue())).andReturn(canonicals);
    //        expect(natDao.findNativeByCanonical(Long.valueOf(process.getProcessId()).intValue(), "version")).andReturn(natives);
    //        expect(annDao.findByUri(1234)).andReturn(annotations);
    //        replay(proDao, canDao, natDao, annDao);
    //
    //        ProcessSummariesType processSummary = service.readProcessSummaries(searchExpression);
    //
    //        verify(proDao, canDao, natDao, annDao);
    //
    //        assertThat(processSummary.getProcessSummary().size(), equalTo(processes.size()));
    //        assertThat(processSummary.getProcessSummary().get(0).getName(), equalTo("name"));
    //        assertThat(processSummary.getProcessSummary().get(0).getVersionSummaries().get(0).getName(), equalTo("version"));
    //        assertThat(processSummary.getProcessSummary().get(0).getVersionSummaries().get(0).getAnnotations().get(0).getNativeType(), equalTo("nat"));
    //    }
    //
    //    @Test
    //    public void getAllProcessesCompleteData() {
    //        String searchExpression = "";
    //
    //        // For the Processes
    //        List<Object[]> processes = new ArrayList<Object[]>();
    //        Object[] procSummary = new Object[2];
    //        Process process = createProcess();
    //        procSummary[0] = process;
    //        procSummary[1] = "2.0";
    //        processes.add(procSummary);
    //
    //        // For the Canonicals
    //        List<Canonical> canonicals = new ArrayList<Canonical>();
    //        canonicals.add(createCanonical());
    //
    //        // For the Natives
    //        List<Native> natives = new ArrayList<Native>();
    //        natives.add(createNative());
    //
    //        // For the Annotations
    //        List<Annotation> annotations = new ArrayList<Annotation>();
    //        annotations.add(createAnnotation());
    //
    //        expect(proDao.getAllProcesses(searchExpression)).andReturn(processes);
    //        expect(canDao.findByProcessId(Long.valueOf(process.getProcessId()).intValue())).andReturn(canonicals);
    //        expect(natDao.findNativeByCanonical(Long.valueOf(process.getProcessId()).intValue(), "version")).andReturn(natives);
    //        expect(annDao.findByUri(1234)).andReturn(annotations);
    //
    //        replayAll();
    //
    //        ProcessSummariesType processSummary = service.readProcessSummaries(searchExpression);
    //
    //        verifyAll();
    //
    //        assertThat(processSummary.getProcessSummary().size(), equalTo(processes.size()));
    //        assertThat(processSummary.getProcessSummary().get(0).getName(), equalTo("name"));
    //        assertThat(processSummary.getProcessSummary().get(0).getVersionSummaries().get(0).getName(), equalTo("version"));
    //        assertThat(processSummary.getProcessSummary().get(0).getVersionSummaries().get(0).getAnnotations().get(0).getNativeType(), equalTo("nat"));
    //        assertThat(processSummary.getProcessSummary().get(0).getVersionSummaries().get(0).getAnnotations().get(0).getAnnotationName().get(0), equalTo("name1"));
    //    }
    //
    //    @Test
    //    public void testReadCanonicalAnfWithAnnotation() throws Exception {
    //        Integer processId = 123;
    //        String version = "1.2";
    //        String name = "Canonical";
    //        boolean isWith = true;
    //
    //        Canonical canonical = new Canonical();
    //        canonical.setContent("<xml/>");
    //        Annotation annotation = new Annotation();
    //        annotation.setContent("<xml/>");
    //
    //        expect(canDao.getCanonical(processId, version)).andReturn(canonical);
    //        expect(annDao.getAnnotation(processId, version, name)).andReturn(annotation);
    //
    //        replayAll();
    //
    //        Format data = service.getCanonicalAnf(processId, version, isWith, name);
    //
    //        verifyAll();
    //
    //        MatcherAssert.assertThat(data, Matchers.notNullValue());
    //        MatcherAssert.assertThat(data.getCpf(), Matchers.notNullValue());
    //        MatcherAssert.assertThat(data.getAnf(), Matchers.notNullValue());
    //    }
    //
    //    @Test
    //    public void testReadCanonicalAnfWithOutAnnotation() throws Exception {
    //        Integer processId = 123;
    //        String version = "1.2";
    //        String name = "Canonical";
    //        boolean isWith = false;
    //
    //        Canonical canonical = new Canonical();
    //        canonical.setContent("<xml/>");
    //
    //        expect(canDao.getCanonical(processId, version)).andReturn(canonical);
    //
    //        replayAll();
    //
    //        Format data = service.getCanonicalAnf(processId, version, isWith, name);
    //
    //        verifyAll();
    //
    //        assertThat(data, notNullValue());
    //        assertThat(data.getCpf(), notNullValue());
    //        assertThat(data.getAnf(), nullValue());
    //    }


    //    @Test
    //    public void testImportProcess() throws Exception {
    //        String username = "bubba";
    //        String processName = "TestProcess";
    //        String cpfURI = "112321234";
    //        String version = "1.2";
    //        String natType = "XPDL 2.1";
    //        String domain = "Airport";
    //        String created = "12/12/2011";
    //        String lastUpdate = "12/12/2011";
    //
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
    //    }
    //

    //    private Process createProcess() {
    //        Process process = new Process();
    //        process.setProcessId(1234);
    //        process.setDomain("domain");
    //        process.setName("name");
    //        process.setUser(createUser());
    //        process.setNativeType(createNativeType());
    //        return process;
    //    }
    //
    //    private NativeType createNativeType() {
    //        NativeType nat = new NativeType();
    //        nat.setExtension("ext");
    //        nat.setNatType("nat");
    //        return nat;
    //    }
    //
    //    private User createUser() {
    //        User usr = new User();
    //        usr.setFirstname("first");
    //        usr.setLastname("last");
    //        usr.setEmail("fl@domain.com");
    //        usr.setUsername("user");
    //        usr.setPasswd("pass");
    //        return usr;
    //    }
    //
    //    private Canonical createCanonical() {
    //        Canonical can = new Canonical();
    //        can.setAuthor("someone");
    //        can.setContent("content");
    //        can.setDocumentation("doco");
    //        can.setVersionName("version");
    //        return can;
    //    }
    //
    //    private Native createNative() {
    //        Native nat = new Native();
    //        nat.setUri(1234);
    //        nat.setNativeType(createNativeType());
    //        return nat;
    //    }
    //
    //    private Annotation createAnnotation() {
    //        Annotation ann = new Annotation();
    //        ann.setName("name1");
    //        ann.setUri(1234);
    //        return ann;
    //    }

}
