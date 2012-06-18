package org.apromore.dao.jpa;

import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.User;
import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

/**
 * Test the Process DAO JPA class.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(PowerMockRunner.class)
public class ProcessDaoJpaUnitTest {

    private ProcessDaoJpa dao;
    private EntityManager manager;

    @Before
    public final void setUp() throws Exception {
        dao = new ProcessDaoJpa();
        EntityManagerFactory factory = createMock(EntityManagerFactory.class);
        manager = createMock(EntityManager.class);
        dao.setEntityManager(manager);
        expect(factory.createEntityManager()).andReturn(manager).anyTimes();
        replay(factory);
    }

    @Test
    public final void testIsAPOJO() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(ProcessDaoJpa.class, "em");
    }

    @Test
    public final void testFindProcess() {
        String id = "1234567890";
        Process p = createProcess();

        expect(manager.find(Process.class, id)).andReturn(p);
        replay(manager);

        Process result = dao.findProcess(id);

        verify(manager);
        assertThat(p, equalTo(result));
    }

    @Test
    public final void testGetProcessByName() {
        String name = "procName1";
        Process p = createProcess();
        List<Process> processes = new ArrayList<Process>();
        processes.add(p);

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_PROCESS_BY_NAME)).andReturn(query);
        expect(query.setParameter("name", name)).andReturn(query);
        expect(query.getResultList()).andReturn(processes);

        replay(manager, query);

        Process result = dao.getProcess(name);

        verify(manager, query);

        assertThat(result, equalTo(p));
    }

    @Test
    public final void testGetProcesses() {
        List<Process> prss = new ArrayList<Process>();
        prss.add(createProcess());
        prss.add(createProcess());

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_All_PROCESSES)).andReturn(query);
        expect(query.getResultList()).andReturn(prss);

        replay(manager, query);

        List<Process> result = dao.getProcesses();

        verify(manager, query);

        assertThat(result, equalTo(prss));
    }

    @Test
    public final void testGetProcessById() {
        int id = 1234;
        Process p = createProcess();
        List<Process> processes = new ArrayList<Process>();
        processes.add(p);

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_PROCESS_BY_ID)).andReturn(query);
        expect(query.setParameter("id", id)).andReturn(query);
        expect(query.getResultList()).andReturn(processes);

        replay(manager, query);

        Process result = dao.getProcess(id);

        verify(manager, query);

        assertThat(result, equalTo(p));
    }

    @Test
    public final void testGetAllProcesses() {
        List<Process> prss = new ArrayList<Process>();
        prss.add(createProcess());
        prss.add(createProcess());

        Query query = createMock(Query.class);
        expect(manager.createQuery(ProcessDaoJpa.GET_ALL_PROCESSES + ProcessDaoJpa.GET_ALL_PRO_SORT)).andReturn(query);
        expect(query.getResultList()).andReturn(prss);

        replay(manager, query);

        List<Process> processes = dao.getAllProcesses(null);

        verify(manager, query);

        assertThat(prss.size(), equalTo(processes.size()));
    }

    @Test
    public final void testGetAllProcessesEmptyCondition() {
        List<Process> procs = new ArrayList<Process>(0);

        Query query = createMock(Query.class);
        expect(manager.createQuery(ProcessDaoJpa.GET_ALL_PROCESSES + ProcessDaoJpa.GET_ALL_PRO_SORT)).andReturn(query);
        expect(query.getResultList()).andReturn(procs);

        replay(manager, query);

        List<Process> processes = dao.getAllProcesses("");

        verify(manager, query);

        assertThat(processes, equalTo(procs));
    }

    @Test
    public final void testGetAllProcessesNonFound() {
        List<Process> procs = new ArrayList<Process>(0);

        Query query = createMock(Query.class);
        expect(manager.createQuery(ProcessDaoJpa.GET_ALL_PROCESSES + ProcessDaoJpa.GET_ALL_PRO_SORT)).andReturn(query);
        expect(query.getResultList()).andReturn(procs);

        replay(manager, query);

        List<Process> processes = dao.getAllProcesses(null);

        verify(manager, query);

        assertThat(processes, equalTo(procs));
    }

//    @Test
//    public final void testGetAllProcessesNonEmptySearch() {
//        String invoicingStr = " and  p.processId in (select k.processId FROM Keyword k WHERE k.word like '%invoicing%' )";
//        List<Object[]> procs = new ArrayList<Object[]>(0);
//
//        Query query = createMock(Query.class);
//        expect(manager.createQuery(ProcessDaoJpa.GET_ALL_PROCESSES + invoicingStr + ProcessDaoJpa.GET_ALL_PRO_SORT)).andReturn(query);
//        expect(query.getResultList()).andReturn(procs);
//
//        replay(manager, query);
//
//        List<Object[]> processes = dao.getAllProcesses(invoicingStr);
//
//        verify(manager, query);
//
//        assertThat(processes, equalTo(procs));
//    }

    @Test
    public final void testGetRootFragmentVersionId() {
        Integer pmvid = 1234;
        String rfvid = "3333";

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_ROOT_FRAGMENT_PROCESS_MODEL)).andReturn(query);
        expect(query.setParameter("id", pmvid)).andReturn(query);
        expect(query.getSingleResult()).andReturn(rfvid);

        replay(manager, query);

        String result = dao.getRootFragmentVersionId(pmvid);

        verify(manager, query);

        assertThat(result, equalTo(rfvid));
    }

    @Test
    @SuppressWarnings("unchecked")
    public final void testGetCurrentProcessModels() {
        List<Object[]> currentModels = new ArrayList<Object[]>();
        Object[] obj1 = new Object[] { 1, 1, 1 };
        Object[] obj2 = new Object[] { 2, 2, 2 };
        currentModels.add(obj1);
        currentModels.add(obj2);

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_CURRENT_PROCESS_MODELS)).andReturn(query);
        expect(query.getResultList()).andReturn(currentModels);

        replay(manager, query);

        Map<Integer, int[]> result = dao.getCurrentProcessModels();

        verify(manager, query);

        assertThat(result, allOf(hasEntry(1, new int[] {1,1}), hasEntry(2, new int[] {2,2})));
    }

    @Test
    public final void testGetAllDomains() {
        List<Object> doms = new ArrayList<Object>();
        doms.add("domain1");
        doms.add("domain2");

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_All_DOMAINS)).andReturn(query);
        expect(query.getResultList()).andReturn(doms);

        replay(manager, query);

        List<String> domains = dao.getAllDomains();

        verify(manager, query);

        assertThat(doms.size(), equalTo(domains.size()));
    }


    @Test
    public final void testGetAllDomainsNonFound() {
        List<String> doms = new ArrayList<String>(0);

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_All_DOMAINS)).andReturn(query);
        expect(query.getResultList()).andReturn(doms);

        replay(manager, query);

        List<String> domains = dao.getAllDomains();

        verify(manager, query);

        assertThat(domains, equalTo(doms));
    }


    @Test
    public final void testSaveProcess() {
        Process usr = createProcess();
        manager.persist(usr);
        replay(manager);
        dao.save(usr);
        verify(manager);
    }

    @Test
    public final void testUpdateProcess() {
        Process usr = createProcess();
        expect(manager.merge(usr)).andReturn(usr);
        replay(manager);
        dao.update(usr);
        verify(manager);
    }


    @Test
    public final void testDeleteProcess() {
        Process usr = createProcess();
        manager.remove(usr);
        replay(manager);
        dao.delete(usr);
        verify(manager);
    }


    private Process createProcess() {
        Process prs = new Process();

        prs.setDomain("airport");
        prs.setProcessId(1234567890);
        prs.setName("testProcess");
        prs.setNativeType(new NativeType());
        prs.setUser(new User());

        return prs;
    }



    private class ModelVersionInfo {
        protected Integer processModelVersionId = 1;
        protected Integer versionNumber = 1;
        protected Integer branchId = 1;

        public Integer getProcessModelVersionId() {
            return processModelVersionId;
        }
        public Integer getVersionNumber() {
            return versionNumber;
        }
        public Integer getBranchId() {
            return branchId;
        }
    }
}
