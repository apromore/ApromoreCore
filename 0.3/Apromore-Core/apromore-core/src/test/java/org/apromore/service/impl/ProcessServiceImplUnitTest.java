package org.apromore.service.impl;

import org.apromore.dao.jpa.AnnotationDaoJpa;
import org.apromore.dao.jpa.CanonicalDaoJpa;
import org.apromore.dao.jpa.NativeDaoJpa;
import org.apromore.dao.jpa.ProcessDaoJpa;
import org.apromore.dao.model.*;
import org.apromore.dao.model.Process;
import org.apromore.model.ProcessSummariesType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

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

    @Autowired
    private ProcessDaoJpa proDao;
    @Autowired
    private CanonicalDaoJpa canDao;
    @Autowired
    private NativeDaoJpa natDao;
    @Autowired
    private AnnotationDaoJpa annDao;

    private ProcessServiceImpl service;

    @Before
    public final void setUp() throws Exception {
        service = new ProcessServiceImpl();
        proDao = createMock(ProcessDaoJpa.class);
        canDao = createMock(CanonicalDaoJpa.class);
        natDao = createMock(NativeDaoJpa.class);
        annDao = createMock(AnnotationDaoJpa.class);
        service.setProcessDao(proDao);
        service.setCanonicalDao(canDao);
        service.setNativeDao(natDao);
        service.setAnnotationDao(annDao);
    }

    @Test
    public void getAllProcessesNoneFound() {
        String searchExpression = "";
        List<Object[]> processes = new ArrayList<Object[]>();

        expect(proDao.getAllProcesses()).andReturn(processes);
        replay(proDao);

        ProcessSummariesType processSummary = service.readProcessSummaries(searchExpression);

        verify(proDao);

        assertThat(processSummary.getProcessSummary().size(), equalTo(processes.size()));
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

        expect(proDao.getAllProcesses()).andReturn(processes);
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

        expect(proDao.getAllProcesses()).andReturn(processes);
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

        expect(proDao.getAllProcesses()).andReturn(processes);
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

        expect(proDao.getAllProcesses()).andReturn(processes);
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
        assertThat(processSummary.getProcessSummary().get(0).getVersionSummaries().get(0).getAnnotations().get(0).getAnnotationName().get(0), equalTo("name1"));
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
