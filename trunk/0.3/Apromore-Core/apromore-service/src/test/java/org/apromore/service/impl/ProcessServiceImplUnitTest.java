package org.apromore.service.impl;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verify;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.ArrayList;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import org.apromore.TestData;
import org.apromore.dao.jpa.AnnotationDaoJpa;
import org.apromore.dao.jpa.CanonicalDaoJpa;
import org.apromore.dao.jpa.NativeDaoJpa;
import org.apromore.dao.jpa.ProcessDaoJpa;
import org.apromore.dao.model.Annotation;
import org.apromore.dao.model.Canonical;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.User;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.NativeFormatNotFoundException;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.service.model.Format;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test the UserService Implementation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"
})
@PrepareForTest({ ProcessDaoJpa.class, CanonicalDaoJpa.class, NativeDaoJpa.class, AnnotationDaoJpa.class })
public class ProcessServiceImplUnitTest {

    private static final String CONDITION = " and  p.processId in (select k.id.processId FROM Keyword k WHERE k.id.word like '%invoicing%' )";

    @Rule
	public ExpectedException exception = ExpectedException.none();

    @Autowired
    private ProcessDaoJpa proDao;
    @Autowired
    private CanonicalDaoJpa canDao;
    @Autowired
    private NativeDaoJpa natDao;
    @Autowired
    private AnnotationDaoJpa annDao;

    @Autowired
    private UserServiceImpl usrSrv;
    @Autowired
    private CanoniserServiceImpl canSrv;
    @Autowired
    private FormatServiceImpl fmtSrv;

    private ProcessServiceImpl service;

    @Before
    public final void setUp() throws Exception {
        service = new ProcessServiceImpl();
        proDao = createMock(ProcessDaoJpa.class);
        canDao = createMock(CanonicalDaoJpa.class);
        natDao = createMock(NativeDaoJpa.class);
        annDao = createMock(AnnotationDaoJpa.class);
        usrSrv = createMock(UserServiceImpl.class);
        fmtSrv = createMock(FormatServiceImpl.class);
        canSrv = new CanoniserServiceImpl();
        service.setProcessDao(proDao);
        service.setCanonicalDao(canDao);
        service.setNativeDao(natDao);
        service.setAnnotationDao(annDao);
        service.setUserService(usrSrv);
        service.setFormatService(fmtSrv);
        service.setCanoniserService(canSrv);
    }

    @Test
    public void getAllProcessesNoneFound() {
        String searchExpression = "";
        List<Object[]> processes = new ArrayList<Object[]>();

        expect(proDao.getAllProcesses(searchExpression)).andReturn(processes);
        replay(proDao);

        ProcessSummariesType processSummary = service.readProcessSummaries(searchExpression);

        verify(proDao);

        assertThat(processSummary.getProcessSummary().size(), equalTo(processes.size()));
    }

    @Test
    public void getAllProcessesWithSearchCriteria() {
        String searchExpression = "invoicing";
        List<Canonical> canonicals = new ArrayList<Canonical>();
        List<Object[]> processes = new ArrayList<Object[]>();

        Object[] procSummary = new Object[2];
        Process process = createProcess();
        procSummary[0] = process;
        procSummary[1] = "2.0";
        processes.add(procSummary);

        expect(proDao.getAllProcesses(CONDITION)).andReturn(processes);
        expect(canDao.findByProcessId(Long.valueOf(process.getProcessId()).intValue())).andReturn(canonicals);
        replay(proDao, canDao);

        ProcessSummariesType processSummary = service.readProcessSummaries(searchExpression);

        verify(proDao, canDao);

        assertThat(processSummary.getProcessSummary().size(), equalTo(processes.size()));
        assertThat(processSummary.getProcessSummary().get(0).getLastVersion(), equalTo(null));
    }

    @Test
    public void getAllProcessesCanonicals() {
        String searchExpression = "";

        // For the Processes
        List<Object[]> processes = new ArrayList<Object[]>();
        Object[] procSummary = new Object[2];
        Process process = createProcess();
        procSummary[0] = process;
        procSummary[1] = "2.0";
        processes.add(procSummary);

        // For the Canonicals
        List<Canonical> canonicals = new ArrayList<Canonical>();

        expect(proDao.getAllProcesses(searchExpression)).andReturn(processes);
        expect(canDao.findByProcessId(Long.valueOf(process.getProcessId()).intValue())).andReturn(canonicals);
        replay(proDao, canDao);

        ProcessSummariesType processSummary = service.readProcessSummaries(searchExpression);

        verify(proDao, canDao);

        assertThat(processSummary.getProcessSummary().size(), equalTo(processes.size()));
        assertThat(processSummary.getProcessSummary().get(0).getLastVersion(), equalTo(null));
    }

    @Test
    public void getAllProcessesNatives() {
        String searchExpression = "";

        // For the Processes
        List<Object[]> processes = new ArrayList<Object[]>();
        Object[] procSummary = new Object[2];
        Process process = createProcess();
        procSummary[0] = process;
        procSummary[1] = "2.0";
        processes.add(procSummary);

        // For the Canonicals
        List<Canonical> canonicals = new ArrayList<Canonical>();
        canonicals.add(createCanonical());

        // For the Natives
        List<Native> natives = new ArrayList<Native>();

        expect(proDao.getAllProcesses(searchExpression)).andReturn(processes);
        expect(canDao.findByProcessId(Long.valueOf(process.getProcessId()).intValue())).andReturn(canonicals);
        expect(natDao.findNativeByCanonical(Long.valueOf(process.getProcessId()).intValue(), "version")).andReturn(natives);
        replay(proDao, canDao, natDao);

        ProcessSummariesType processSummary = service.readProcessSummaries(searchExpression);

        verify(proDao, canDao, natDao);

        assertThat(processSummary.getProcessSummary().size(), equalTo(processes.size()));
        assertThat(processSummary.getProcessSummary().get(0).getName(), equalTo("name"));
        assertThat(processSummary.getProcessSummary().get(0).getVersionSummaries().get(0).getName(), equalTo("version"));
    }

    @Test
    public void getAllProcessesAnnotations() {
        String searchExpression = "";

        // For the Processes
        List<Object[]> processes = new ArrayList<Object[]>();
        Object[] procSummary = new Object[2];
        Process process = createProcess();
        procSummary[0] = process;
        procSummary[1] = "2.0";
        processes.add(procSummary);

        // For the Canonicals
        List<Canonical> canonicals = new ArrayList<Canonical>();
        canonicals.add(createCanonical());

        // For the Natives
        List<Native> natives = new ArrayList<Native>();
        natives.add(createNative());

        // For the Annotations
        List<Annotation> annotations = new ArrayList<Annotation>();

        expect(proDao.getAllProcesses(searchExpression)).andReturn(processes);
        expect(canDao.findByProcessId(Long.valueOf(process.getProcessId()).intValue())).andReturn(canonicals);
        expect(natDao.findNativeByCanonical(Long.valueOf(process.getProcessId()).intValue(), "version")).andReturn(natives);
        expect(annDao.findByUri(1234)).andReturn(annotations);
        replay(proDao, canDao, natDao, annDao);

        ProcessSummariesType processSummary = service.readProcessSummaries(searchExpression);

        verify(proDao, canDao, natDao, annDao);

        assertThat(processSummary.getProcessSummary().size(), equalTo(processes.size()));
        assertThat(processSummary.getProcessSummary().get(0).getName(), equalTo("name"));
        assertThat(processSummary.getProcessSummary().get(0).getVersionSummaries().get(0).getName(), equalTo("version"));
        assertThat(processSummary.getProcessSummary().get(0).getVersionSummaries().get(0).getAnnotations().get(0).getNativeType(), equalTo("nat"));
    }

    @Test
    public void getAllProcessesCompleteData() {
        String searchExpression = "";

        // For the Processes
        List<Object[]> processes = new ArrayList<Object[]>();
        Object[] procSummary = new Object[2];
        Process process = createProcess();
        procSummary[0] = process;
        procSummary[1] = "2.0";
        processes.add(procSummary);

        // For the Canonicals
        List<Canonical> canonicals = new ArrayList<Canonical>();
        canonicals.add(createCanonical());

        // For the Natives
        List<Native> natives = new ArrayList<Native>();
        natives.add(createNative());

        // For the Annotations
        List<Annotation> annotations = new ArrayList<Annotation>();
        annotations.add(createAnnotation());

        expect(proDao.getAllProcesses(searchExpression)).andReturn(processes);
        expect(canDao.findByProcessId(Long.valueOf(process.getProcessId()).intValue())).andReturn(canonicals);
        expect(natDao.findNativeByCanonical(Long.valueOf(process.getProcessId()).intValue(), "version")).andReturn(natives);
        expect(annDao.findByUri(1234)).andReturn(annotations);

        replayAll();

        ProcessSummariesType processSummary = service.readProcessSummaries(searchExpression);

        verifyAll();

        assertThat(processSummary.getProcessSummary().size(), equalTo(processes.size()));
        assertThat(processSummary.getProcessSummary().get(0).getName(), equalTo("name"));
        assertThat(processSummary.getProcessSummary().get(0).getVersionSummaries().get(0).getName(), equalTo("version"));
        assertThat(processSummary.getProcessSummary().get(0).getVersionSummaries().get(0).getAnnotations().get(0).getNativeType(), equalTo("nat"));
        assertThat(processSummary.getProcessSummary().get(0).getVersionSummaries().get(0).getAnnotations().get(0).getAnnotationName().get(0), equalTo("name1"));
    }


    @Test
    public void testExportFormatGetCanonical() throws Exception {
        long processId = 123;
        String version = "1.2";
        String format = "Canonical";

        Canonical canonical = new Canonical();
        canonical.setContent("<xml/>");

        expect(canDao.getCanonical(processId, version)).andReturn(canonical);

        replayAll();

        DataSource data = service.exportFormat(processId, version, format);

        verifyAll();

        MatcherAssert.assertThat(data, Matchers.notNullValue());
    }

    @Test
    public void testExportFormatGetAnnotation() throws Exception {
        long processId = 123;
        String version = "1.2";
        String format = "Annotations-BPMN";
        String subStr = "MN";

        Annotation annotation = new Annotation();
        annotation.setContent("<xml/>");

        expect(annDao.getAnnotation(processId, version, subStr)).andReturn(annotation);

        replayAll();

        DataSource data = service.exportFormat(processId, version, format);

        verifyAll();

        MatcherAssert.assertThat(data, Matchers.notNullValue());
    }

    @Test
    public void testExportFormatGetNative() throws Exception {
        long processId = 123;
        String version = "1.2";
        String format = "WHATEVER";

        Native nat = new Native();
        nat.setContent("<xml/>");

        expect(natDao.getNative(processId, version, format)).andReturn(nat);

        replayAll();

        DataSource data = service.exportFormat(processId, version, format);

        verifyAll();

        MatcherAssert.assertThat(data, Matchers.notNullValue());
    }

    @Test(expected = ExportFormatException.class)
    public void testExportFormatWithException() throws Exception {
        long processId = 123;
        String version = "1.2";
        String format = "WHATEVER";

        expect(natDao.getNative(processId, version, format)).andThrow(new NativeFormatNotFoundException());

        replayAll();

        service.exportFormat(processId, version, format);

        verifyAll();
    }

    @Test
    public void testReadCanonicalAnfWithAnnotation() throws Exception {
        long processId = 123;
        String version = "1.2";
        String name = "Canonical";
        boolean isWith = true;

        Canonical canonical = new Canonical();
        canonical.setContent("<xml/>");
        Annotation annotation = new Annotation();
        annotation.setContent("<xml/>");

        expect(canDao.getCanonical(processId, version)).andReturn(canonical);
        expect(annDao.getAnnotation(processId, version, name)).andReturn(annotation);

        replayAll();

        Format data = service.getCanonicalAnf(processId, version, isWith, name);

        verifyAll();

        MatcherAssert.assertThat(data, Matchers.notNullValue());
        MatcherAssert.assertThat(data.getCpf(), Matchers.notNullValue());
        MatcherAssert.assertThat(data.getAnf(), Matchers.notNullValue());
    }

    @Test
    public void testReadCanonicalAnfWithOutAnnotation() throws Exception {
        long processId = 123;
        String version = "1.2";
        String name = "Canonical";
        boolean isWith = false;

        Canonical canonical = new Canonical();
        canonical.setContent("<xml/>");

        expect(canDao.getCanonical(processId, version)).andReturn(canonical);

        replayAll();

        Format data = service.getCanonicalAnf(processId, version, isWith, name);

        verifyAll();

        assertThat(data, notNullValue());
        assertThat(data.getCpf(), notNullValue());
        assertThat(data.getAnf(), nullValue());
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

        DataHandler stream = new DataHandler(new ByteArrayDataSource(TestData.XPDL.getBytes(), "text/xml"));
        User user = new User();
        user.setUsername(username);

        NativeType nativeType = new NativeType();
        nativeType.setNatType(natType);

        expect(usrSrv.findUser(username)).andReturn(user);
        expect(fmtSrv.findNativeType(natType)).andReturn(nativeType);
        proDao.save((Process) anyObject());
        expectLastCall().atLeastOnce();
        canDao.save((Canonical) anyObject());
        expectLastCall().atLeastOnce();
        natDao.save((Native) anyObject());
        expectLastCall().atLeastOnce();
        annDao.save((Annotation) anyObject());
        expectLastCall().atLeastOnce();

        replayAll();

        ProcessSummaryType procSum = service.importProcess(username, processName, cpfURI, version, natType, stream, domain, "", created, lastUpdate);

        verifyAll();

        assertThat(procSum, notNullValue());
    }
    

    private Process createProcess() {
        Process process = new Process();
        process.setProcessId(1234);
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
        usr.setFirstname("first");
        usr.setLastname("last");
        usr.setEmail("fl@domain.com");
        usr.setUsername("user");
        usr.setPasswd("pass");
        return usr;
    }

    private Canonical createCanonical() {
        Canonical can = new Canonical();
        can.setAuthor("someone");
        can.setContent("content");
        can.setDocumentation("doco");
        can.setVersionName("version");
        return can;
    }

    private Native createNative() {
        Native nat = new Native();
        nat.setUri(1234);
        nat.setNativeType(createNativeType());
        return nat;
    }

    private Annotation createAnnotation() {
        Annotation ann = new Annotation();
        ann.setName("name1");
        ann.setUri(1234);
        return ann;
    }

}
