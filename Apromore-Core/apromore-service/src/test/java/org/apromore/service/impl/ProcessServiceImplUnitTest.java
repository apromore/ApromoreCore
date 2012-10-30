package org.apromore.service.impl;

import java.util.HashSet;
import java.util.Set;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.dao.AnnotationDao;
import org.apromore.dao.NativeDao;
import org.apromore.dao.jpa.AnnotationDaoJpa;
import org.apromore.dao.jpa.NativeDaoJpa;
import org.apromore.dao.jpa.ProcessDaoJpa;
import org.apromore.dao.model.Native;
import org.apromore.graph.canonical.Canonical;
import org.apromore.model.ExportFormatResultType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.CanonicalConverter;
import org.apromore.service.CanoniserService;
import org.apromore.service.FormatService;
import org.apromore.service.RepositoryService;
import org.apromore.service.UserService;
import org.apromore.service.model.DecanonisedProcess;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

/**
 * Unit test the UserService Implementation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"
})
@PrepareForTest({ ProcessDaoJpa.class, NativeDaoJpa.class, AnnotationDaoJpa.class })
public class ProcessServiceImplUnitTest {

    //private static final String CONDITION = " and  p.processId in (select k.id.processId FROM Keyword k WHERE k.id.word like '%invoicing%' )";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Autowired
    private NativeDao natDao;
    @Autowired
    private AnnotationDao annDao;
    @Autowired
    private UserService usrSrv;
    @Autowired
    private CanoniserService canSrv;
    @Autowired
    private FormatService fmtSrv;
    @Autowired
    private RepositoryService rSrv;
    @Autowired
    private CanonicalConverter convertor;

    private ProcessServiceImpl service;

    @Before
    public final void setUp() throws Exception {
        service = new ProcessServiceImpl();
        natDao = createMock(NativeDao.class);
        annDao = createMock(AnnotationDao.class);
        usrSrv = createMock(UserService.class);
        fmtSrv = createMock(FormatService.class);
        rSrv = createMock(RepositoryService.class);
        canSrv = createMock(CanoniserService.class);
        convertor = createMock(CanonicalConverter.class);
        service.setNativeDao(natDao);
        service.setAnnotationDao(annDao);
        service.setUserService(usrSrv);
        service.setFormatService(fmtSrv);
        service.setCanoniserService(canSrv);
        service.setRepositoryService(rSrv);
        service.setConverterAdpater(convertor);
    }


    @Test
    public void testExportFormatGetCanonical() throws Exception {
        Integer processId = 123;
        String version = "1.2";
        String name = "processName";
        String format = "Canonical";

        DataSource result = new ByteArrayDataSource("<xml/>", "text/xml");
        DecanonisedProcess dp = new DecanonisedProcess();
        dp.setNativeFormat(result.getInputStream());
        CanonicalProcessType cpt = new CanonicalProcessType();
        Canonical cpf = new Canonical();

        expect(rSrv.getCurrentProcessModel(name, version, false)).andReturn(cpf);
        expect(convertor.convert(cpf)).andReturn(cpt);
        expect(canSrv.deCanonise(anyObject(Integer.class), anyObject(String.class), anyObject(String.class), anyObject(CanonicalProcessType.class), anyObject(AnnotationsType.class), anyObject(Set.class))).andReturn(dp);

        replayAll();

        ExportFormatResultType data = service.exportProcess(name, processId, version, format, "", false, new HashSet<RequestParameterType<?>>());

        verifyAll();

        MatcherAssert.assertThat(data, Matchers.notNullValue());
    }

    @Test
    public void testExportFormatGetAnnotation() throws Exception {
        Integer processId = 123;
        String version = "1.2";
        String name = "processName";
        String format = "Annotations-BPMN";
        String subStr = "MN";

        Canonical cpf = new Canonical();

        Native nat = new Native();
        nat.setContent("<xml/>");

        DataSource result = new ByteArrayDataSource("<xml/>", "text/xml");

        expect(natDao.getNative(processId, version, format)).andReturn(nat);
        expect(rSrv.getCurrentProcessModel(name, version, false)).andReturn(cpf);
        //expect(annDao.getAnnotation(processId, version, subStr)).andReturn(annotation);

        replayAll();

        ExportFormatResultType data = service.exportProcess(name, processId, version, format, subStr, true, new HashSet<RequestParameterType<?>>());

        verifyAll();

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
